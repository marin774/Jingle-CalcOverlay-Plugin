package me.marin.calcoverlay.gui;

import lombok.Getter;
import me.marin.calcoverlay.util.OverlayUtil;

import javax.swing.*;
import java.awt.*;

/**
 * A window that displays the current overlay panel.
 * Intended use is a window capture in Toolscreen for instant updates.
 */
public class OverlayCaptureWindow extends JFrame {

    @Getter
    private static final OverlayCaptureWindow instance = new OverlayCaptureWindow();

    private JPanel currentPanel;

    private OverlayCaptureWindow() {
        super("Calc Overlay [TOOLSCREEN CAPTURE WINDOW]");
        setUndecorated(true);
        setType(Window.Type.UTILITY);
        setFocusableWindowState(false);
        setFocusable(false);
        setResizable(false);
        setSize(OverlayUtil.IMAGE_WIDTH, OverlayUtil.IMAGE_HEIGHT);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        getContentPane().setBackground(new Color(0, 0, 0));
        ((JPanel) getContentPane()).setOpaque(false);
        getContentPane().setLayout(new BorderLayout());

        setLocation(0, 0);

        setVisible(true);

        toBack();
    }

    public void updateOverlay(JPanel overlayPanel) {
        if (currentPanel != null) {
            getContentPane().remove(currentPanel);
        }

        currentPanel = overlayPanel;
        getContentPane().add(currentPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

}
