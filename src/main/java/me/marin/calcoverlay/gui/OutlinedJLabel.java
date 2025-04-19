package me.marin.calcoverlay.gui;

import me.marin.calcoverlay.io.CalcOverlaySettings;

import javax.swing.*;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;

public class OutlinedJLabel extends JLabel {

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        TextLayout textLayout = new TextLayout(this.getText(), this.getFont(), g2d.getFontRenderContext());
        Shape outline = textLayout.getOutline(null);

        Paint paint = g2d.getPaint();

        Stroke stroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(CalcOverlaySettings.getInstance().outlineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // idk why 8, but it works
        g2d.translate(0, 8 + this.getFont().getStringBounds(this.getText(), g2d.getFontRenderContext()).getHeight() / 2);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        expandClip(g2d, CalcOverlaySettings.getInstance().outlineWidth);

        GlyphVector glyphVector = this.getFont().createGlyphVector(g2d.getFontRenderContext(), this.getText());
        for (int i = 0; i < this.getText().length(); i++) {
            Shape glyphOutline = glyphVector.getGlyphOutline(i);

            // Draw black outline
            g2d.setPaint(Color.BLACK);
            g2d.draw(glyphOutline);

            // Fill inside
            g2d.setPaint(this.getForeground());
            g2d.fill(outline);
        }

        // Set original values
        g2d.setStroke(stroke);
        g2d.setPaint(paint);
    }

    private static void expandClip(Graphics2D g2d, int width) {
        Shape clip = g2d.getClip();
        Rectangle clipRect = clip.getBounds();

        g2d.setClip(clipRect.x - width, clipRect.y - width, clipRect.width + width*2, clipRect.height + width*2);
    }
}
