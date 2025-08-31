package me.marin.calcoverlay.util;

import me.marin.calcoverlay.CalcOverlay;
import me.marin.calcoverlay.gui.LTRPaintOrderJPanel;
import me.marin.calcoverlay.gui.OutlinedJLabel;
import me.marin.calcoverlay.io.CalcOverlaySettings;
import org.apache.logging.log4j.Level;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CalcOverlayUtil {

    public static void runAsync(String threadName, Runnable runnable) {
        new Thread(runnable, threadName).start();
    }

    public static void runTimerAsync(Runnable runnable, int delayMs) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(runnable, 0, delayMs, TimeUnit.MILLISECONDS);
    }

    private static final Font DEFAULT_FONT = new Font("Calibri", Font.PLAIN, 48);
    public static Font getFont() {
        Font font = DEFAULT_FONT;

        CalcOverlaySettings.FontData fontData = CalcOverlaySettings.getInstance().fontData;
        try {
            font = fontData.toFont();
        } catch (Exception ignored) {
            CalcOverlay.log(Level.WARN, "Could not load font: " + fontData);
        }
        return font;
    }

    public static double normalizeAngle(double angle) {
        while (angle > 180)
            angle -= 360;
        while (angle < -180)
            angle += 360;
        return angle;
    }

    public static JLabel setupJLabel(String text) {
        JLabel jLabel = new OutlinedJLabel();
        jLabel.setText(text);
        jLabel.setFont(CalcOverlayUtil.getFont());
        jLabel.setForeground(Color.WHITE);
        return jLabel;
    }

    public static JPanel setupCoordsLabel(int x, int z, boolean isNetherCoords, Color netherCoordsColor, CalcOverlaySettings.NegativeCoords negativeCoords) {
        JPanel panel = new LTRPaintOrderJPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JLabel label1 = setupJLabel("(");
        label1.setForeground(isNetherCoords ? netherCoordsColor : Color.WHITE);
        JLabel label2 = setupJLabel(String.valueOf(x));
        label2.setForeground(x < 0 && negativeCoords.use ? negativeCoords.color : (isNetherCoords ? netherCoordsColor : Color.WHITE));
        JLabel label3 = setupJLabel(", ");
        //label3.setBorder(BorderFactory.createEmptyBorder(0, 5, 0 ,0));
        label3.setForeground(isNetherCoords ? netherCoordsColor : Color.WHITE);
        JLabel label4 = setupJLabel(String.valueOf(z));
        label4.setForeground(z < 0 && negativeCoords.use ? negativeCoords.color : (isNetherCoords ? netherCoordsColor : Color.WHITE));
        JLabel label5 = setupJLabel(")");
        label5.setForeground(isNetherCoords ? netherCoordsColor : Color.WHITE);

        panel.add(label1);
        panel.add(label2);
        panel.add(label3);
        panel.add(label4);
        panel.add(label5);

        for (Component c : panel.getComponents()) {
            System.out.println(((JLabel) c).getText());
        }

        return panel;
    }

}
