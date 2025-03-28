package me.marin.calcoverlay.gui;

import me.marin.calcoverlay.io.CalcOverlaySettings;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextLayout;

public class OutlinedJLabel extends JLabel {

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        TextLayout textLayout = new TextLayout(this.getText(), this.getFont(), g2d.getFontRenderContext());
        Shape outline = textLayout.getOutline(null);

        Color color = g2d.getColor();
        g2d.setColor(Color.BLACK);

        Stroke stroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(CalcOverlaySettings.getInstance().outlineWidth));

        // idk why 8, but it works
        g2d.translate(0, 8 + this.getFont().getStringBounds(this.getText(), g2d.getFontRenderContext()).getHeight() / 2);

        expandClip(g2d, CalcOverlaySettings.getInstance().outlineWidth);

        // Anti aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Draw outline
        g2d.draw(outline);

        // Fill inside
        g2d.setColor(this.getForeground());
        g2d.fill(outline);

        // Set original values
        g2d.setStroke(stroke);
        g2d.setColor(color);
    }

    private static void expandClip(Graphics2D g2d, int width) {
        Shape clip = g2d.getClip();
        Rectangle clipRect = clip.getBounds();

        g2d.setClip(clipRect.x - width, clipRect.y - width, clipRect.width + width*2, clipRect.height + width*2);
    }
}
