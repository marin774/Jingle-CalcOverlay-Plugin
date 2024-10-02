package me.marin.calcoverlay.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.marin.calcoverlay.CalcOverlay;
import me.marin.calcoverlay.gui.HomePortalGUI;
import me.marin.calcoverlay.gui.MeasurementsGUI;
import me.marin.calcoverlay.io.CalcOverlaySettings;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.util.ExceptionUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.marin.calcoverlay.CalcOverlay.NINJABRAIN_BOT_EVENT_SUBSCRIBER;
import static me.marin.calcoverlay.CalcOverlay.log;
import static me.marin.calcoverlay.ninjabrainapi.NinjabrainBotEventSubscriber.GSON;

public class OverlayUtil {

    public static Image homeIconImage = null;
    public static Image overworldIconImage = null;
    public static Image netherIconImage = null;
    public static Image distanceIconImage = null;
    public static Image certaintyIconImage = null;
    public static Image angleIconImage = null;

    public static void loadImagesAndStyles() {
        try {
            homeIconImage = ImageIO.read(Objects.requireNonNull(OverlayUtil.class.getResource("/icons/home.png")));
            overworldIconImage = ImageIO.read(Objects.requireNonNull(OverlayUtil.class.getResource("/icons/overworld.png")));
            netherIconImage = ImageIO.read(Objects.requireNonNull(OverlayUtil.class.getResource("/icons/nether.png")));
            distanceIconImage = ImageIO.read(Objects.requireNonNull(OverlayUtil.class.getResource("/icons/distance.png")));
            certaintyIconImage = ImageIO.read(Objects.requireNonNull(OverlayUtil.class.getResource("/icons/certainty.png")));
            angleIconImage = ImageIO.read(Objects.requireNonNull(OverlayUtil.class.getResource("/icons/angle.png")));
        } catch (Exception e) {
           log(Level.ERROR, "Error while reading images from resources:\n" + ExceptionUtil.toDetailedString(e));
        }
    }

    public static JPanel homePortal(double xNether, double zNether, String evauluation, double probability) {
        return new HomePortalGUI(
                (int) Math.floor(xNether),
                (int) Math.floor(zNether),
                evauluation,
                probability
        ).getMainPanel();
    }

    public static JPanel measurements(List<Pair<Prediction, AngleToCoords>> predictions, PlayerPosition playerPosition) {
        return new MeasurementsGUI(
                predictions,
                playerPosition
        ).getMainPanel();
    }

    public static JPanel empty() {
        return null;
    }

    public static void writeImage(JPanel panel) {
        JPanel wrapper = getFinalOverlayPanel(panel);

        BufferedImage image = ScreenImage.createImage(wrapper);

        try {
            ImageIO.write(image, "png", CalcOverlay.OVERLAY_PATH.toFile());
        } catch (Exception e) {
            log(Level.ERROR, "Error while writing overlay:\n" + ExceptionUtil.toDetailedString(e));
        }
    }

    public static JPanel getFinalOverlayPanel(JPanel panel) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.setPreferredSize(new Dimension(1250, 400));
        wrapper.setSize(1250, 400);

        if (panel != null) {
            JPanel verticalWrapper = new JPanel();
            verticalWrapper.setLayout(new BorderLayout());
            verticalWrapper.add(panel, CalcOverlaySettings.getInstance().overlayPosition.isLeft() ? BorderLayout.WEST : BorderLayout.EAST);

            wrapper.add(verticalWrapper, CalcOverlaySettings.getInstance().overlayPosition.isTop() ? BorderLayout.NORTH : BorderLayout.SOUTH);
        }

        SwingUtil.transparency(wrapper);

        return wrapper;
    }


    public static JPanel getPanelForStronghold(JsonObject response) {
        String resultType = response.get("resultType").getAsString();

        switch (resultType) {
            default:
            case "NONE":
            case "FAILED":
                return OverlayUtil.empty();
            // case "BLIND":
            //     getPanelForBlindCoords();
            //     break;
            case "TRIANGULATION":
                JsonArray predictions = response.get("predictions").getAsJsonArray();

                PlayerPosition playerPosition = GSON.fromJson(response.get("playerPosition"), PlayerPosition.class);

                List<Pair<Prediction, AngleToCoords>> predictionsList = new ArrayList<>();
                for (JsonElement predictionJson : predictions) {
                    Prediction prediction = GSON.fromJson(predictionJson, Prediction.class);
                    AngleToCoords angleToCoords = AngleToCoords.from(prediction, playerPosition);
                    predictionsList.add(Pair.of(prediction, angleToCoords));
                }

                return OverlayUtil.measurements(predictionsList, playerPosition);
        }
    }

    public static JPanel getPanelForBlindCoords() {
        JsonObject response = NINJABRAIN_BOT_EVENT_SUBSCRIBER.getSseClient().get("blind");
        if (response == null) {
            return OverlayUtil.empty();
        }

        boolean isBlindModeEnabled = response.get("isBlindModeEnabled").getAsBoolean();
        if (!isBlindModeEnabled) {
            return OverlayUtil.empty();
        }
        JsonObject blindResult = response.get("blindResult").getAsJsonObject();

        double xNether = blindResult.get("xInNether").getAsDouble();
        double zNether = blindResult.get("zInNether").getAsDouble();
        String evaluation = blindResult.get("evaluation").getAsString();
        double probability = blindResult.get("highrollProbability").getAsDouble();

        return OverlayUtil.homePortal(xNether, zNether, evaluation, probability);
    }


    private static final Color COLOR_GRADIENT_0 = Color.RED;
    private static final Color COLOR_GRADIENT_50 = Color.YELLOW;
    public static final Color COLOR_GRADIENT_100 = Color.decode("#00CE29");
    private static final Color[] colors = new Color[]{COLOR_GRADIENT_0, COLOR_GRADIENT_50, COLOR_GRADIENT_100};

    /**
     * From <a href="https://github.com/Ninjabrain1/Ninjabrain-Bot/blob/1.5.1/src/main/java/ninjabrainbot/gui/style/theme/ColorMap.java">NinjabrainBot</a>
     */
    public static Color getColor(double probability) {
        int n = colors.length - 1;
        probability *= n;
        // Find colors to interpolate between
        int i0 = (int) Math.floor(probability);
        int i1 = (int) Math.ceil(probability);
        // truncate to allowed range
        i0 = Math.max(Math.min(i0, n), 0);
        i1 = Math.max(Math.min(i1, n), 0);
        return getInterpolatedColor((float) (probability - Math.floor(probability)), colors[i0], colors[i1]);
    }

    /**
     * From <a href="https://github.com/Ninjabrain1/Ninjabrain-Bot/blob/1.5.1/src/main/java/ninjabrainbot/gui/style/theme/ColorMap.java">NinjabrainBot</a>
     */
    private static Color getInterpolatedColor(float t, Color c0, Color c1) {
        float[] hsv0 = Color.RGBtoHSB(c0.getRed(), c0.getGreen(), c0.getBlue(), null);
        float[] hsv1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
        float h;
        if (Math.abs(hsv1[0] - hsv0[0]) < 0.5f) {
            h = hsv1[0] * t + hsv0[0] * (1.0f - t);
        } else {
            if (hsv1[0] < hsv0[0]) {
                hsv1[0]++;
            } else {
                hsv0[0]++;
            }
            h = hsv1[0] * t + hsv0[0] * (1.0f - t);
            if (h > 1)
                h--;
        }
        float s = hsv1[1] * t + hsv0[1] * (1.0f - t);
        float v = hsv1[2] * t + hsv0[2] * (1.0f - t);
        return Color.getHSBColor(h, s, v);
    }

}
