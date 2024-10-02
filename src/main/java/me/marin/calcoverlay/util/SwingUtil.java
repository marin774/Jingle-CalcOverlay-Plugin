package me.marin.calcoverlay.util;

import javax.swing.*;
import java.awt.*;

public class SwingUtil {

    public static void addStroke(JComponent c, Color color, int width) {
        Graphics2D g = (Graphics2D) c.getGraphics();

        Color originalColor = g.getColor();
        Stroke originalStroke = g.getStroke();

        Stroke stroke = new BasicStroke(width);
        g.setColor(color);
        g.setStroke(stroke);

        g.setColor(originalColor);
        g.setStroke(originalStroke);
    }

    public static void transparency(JComponent component) {
        component.setOpaque(false);
        component.setBackground(new Color(0, 0, 0, 0));
        for (Component c : component.getComponents()) {
            if (c instanceof JComponent) {
                transparency((JComponent) c);
            }
        }
    }

}
