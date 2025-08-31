package me.marin.calcoverlay.gui;

import me.marin.calcoverlay.CalcOverlay;
import me.marin.calcoverlay.io.CalcOverlaySettings;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.gui.JingleGUI;
import xyz.duncanruns.jingle.util.ExceptionUtil;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.function.Consumer;

public class HSVColorChooser {


    public HSVColorChooser(String title, Consumer<Color> onColorChange) {
        JColorChooser chooser = new JColorChooser(CalcOverlaySettings.getInstance().netherCoordsColor);
        for (AbstractColorChooserPanel p : chooser.getChooserPanels()) {
            if (!p.getDisplayName().equals("HSV")) {
                chooser.removeChooserPanel(p);
            } else {
                removeTransparencySlider(p);
            }
        }
        chooser.setPreviewPanel(getPreviewPanel(chooser));
        JDialog d = JColorChooser.createDialog(JingleGUI.get(), title, true, chooser, e -> onColorChange.accept(chooser.getColor()), null);
        d.setVisible(true);
    }


    private JPanel getPreviewPanel(JColorChooser chooser) {
        JPanel previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(chooser.getSelectionModel().getSelectedColor());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        previewPanel.setPreferredSize(new Dimension(250, 50));
        return previewPanel;
    }

    /**
     * Removes the transparency slider from ColorPanel. Code from
     * <a href="https://stackoverflow.com/questions/12026767/java-7-jcolorchooser-disable-transparency-slider">StackOverflow</a>.
     */
    private void removeTransparencySlider(AbstractColorChooserPanel panel) {
        try {
            Field f = panel.getClass().getDeclaredField("panel");
            f.setAccessible(true);

            Object colorPanel = f.get(panel);
            Field f2 = colorPanel.getClass().getDeclaredField("spinners");
            f2.setAccessible(true);
            Object spinners = f2.get(colorPanel);

            Object transpSlispinner = Array.get(spinners, 3);
            Field f3 = transpSlispinner.getClass().getDeclaredField("slider");
            f3.setAccessible(true);
            JSlider slider = (JSlider) f3.get(transpSlispinner);
            slider.setEnabled(false);
            slider.setVisible(false);
            Field f4 = transpSlispinner.getClass().getDeclaredField("spinner");
            f4.setAccessible(true);
            JSpinner spinner = (JSpinner) f4.get(transpSlispinner);
            spinner.setEnabled(false);
            spinner.setVisible(false);

            Field f5 = transpSlispinner.getClass().getDeclaredField("label");
            f5.setAccessible(true);
            JLabel label = (JLabel) f5.get(transpSlispinner);
            label.setVisible(false);
        } catch (Exception e) {
            CalcOverlay.log(Level.ERROR, "Could not remove transparency slider:\n" + ExceptionUtil.toDetailedString(e));
        }

    }

}
