package me.marin.calcoverlay;

import com.google.common.io.Resources;
import me.marin.calcoverlay.gui.ConfigGUI;
import me.marin.calcoverlay.io.CalcOverlaySettings;
import me.marin.calcoverlay.ninjabrainapi.NinjabrainBotEventSubscriber;
import me.marin.calcoverlay.util.OverlayUtil;
import me.marin.calcoverlay.util.UpdateUtil;
import me.marin.calcoverlay.util.VersionUtil;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.JingleAppLaunch;
import xyz.duncanruns.jingle.gui.JingleGUI;
import xyz.duncanruns.jingle.plugin.PluginManager;
import xyz.duncanruns.jingle.util.ExceptionUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static me.marin.calcoverlay.util.VersionUtil.CURRENT_VERSION;

public class CalcOverlay {

    public static final Path CALC_OVERLAY_FOLDER_PATH = Jingle.FOLDER.resolve("calc-overlay-plugin");

    public static final Path PLUGINS_PATH = Jingle.FOLDER.resolve("plugins");
    public static final Path SETTINGS_PATH = CALC_OVERLAY_FOLDER_PATH.resolve("settings.json");

    public static final Path OVERLAY_PATH = CALC_OVERLAY_FOLDER_PATH.resolve("calc-overlay.png");

    public static ConfigGUI CONFIG_GUI = null;
    public static final NinjabrainBotEventSubscriber NINJABRAIN_BOT_EVENT_SUBSCRIBER = new NinjabrainBotEventSubscriber();

    public static void main(String[] args) throws IOException {
        JingleAppLaunch.launchWithDevPlugin(args, PluginManager.JinglePluginData.fromString(
                Resources.toString(Resources.getResource(CalcOverlay.class, "/jingle.plugin.json"), Charset.defaultCharset())
        ), CalcOverlay::initialize);
    }

    public static void log(Level level, String message) {
        Jingle.log(level, "(CalcOverlay) " + message);
    }

    public static void initialize() {
        CalcOverlay.log(Level.INFO, "Running CalcOverlay Plugin v" + CURRENT_VERSION + "!");

        OverlayUtil.loadImagesAndStyles();
        CALC_OVERLAY_FOLDER_PATH.toFile().mkdirs();
        createOverlayFile();

        CalcOverlaySettings.load();

        if (CalcOverlaySettings.getInstance().calcOverlayEnabled) {
            NINJABRAIN_BOT_EVENT_SUBSCRIBER.connect();
        }

        VersionUtil.deleteOldVersionJars();
        UpdateUtil.checkForUpdatesAndUpdate(true);

        CONFIG_GUI = new ConfigGUI();
        JingleGUI.addPluginTab("Calc Overlay", CONFIG_GUI);
    }

    private static void createOverlayFile() {
        try {
            BufferedImage image = new BufferedImage(1250, 400, BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.createGraphics();
            g.drawRect(0, 0, 1250, 400);
            try {
                ImageIO.write(image, "png", CalcOverlay.OVERLAY_PATH.toFile());
            } catch (Exception e) {
                log(Level.ERROR, "Error while writing overlay file:\n" + ExceptionUtil.toDetailedString(e));
            }
        } catch (Exception e) {
            log(Level.INFO, "Failed to create overlay image:\n" + ExceptionUtil.toDetailedString(e));
        }
    }

}
