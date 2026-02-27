package me.marin.calcoverlay.gui;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import lombok.Getter;
import me.marin.calcoverlay.io.CalcOverlaySettings;
import me.marin.calcoverlay.util.OverlayUtil;
import xyz.duncanruns.jingle.win32.User32;

import javax.swing.*;
import java.awt.*;

/**
 * A window that displays the current overlay panel.
 * Intended use is a window capture in Toolscreen for instant updates.
 */
public class OverlayCaptureWindow extends JFrame {

    private static final int SHOW_FLAGS = User32.SWP_NOACTIVATE | User32.SWP_NOSENDCHANGING;

    @Getter
    private static final OverlayCaptureWindow instance = new OverlayCaptureWindow();

    private JPanel currentPanel;

    private OverlayCaptureWindow() {
        super("Calc Overlay [TOOLSCREEN CAPTURE WINDOW]");

        setUndecorated(true);
        setType(Window.Type.UTILITY);

        setFocusable(false);
        setFocusableWindowState(false);
        setResizable(false);

        setSize(OverlayUtil.IMAGE_WIDTH, OverlayUtil.IMAGE_HEIGHT);
        setLocation(0, 0);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());

        setOpacity(0f);
        setVisible(true);

        WinDef.HWND hwnd = new WinDef.HWND(Native.getWindowPointer(this));
        long style = User32.INSTANCE.GetWindowLongA(hwnd, User32.GWL_EXSTYLE);
        style |= 0x00080000; // WS_EX_LAYERED
        style |= 0x00000020; // WS_EX_TRANSPARENT
        User32.INSTANCE.SetWindowLongA(hwnd, User32.GWL_EXSTYLE, style);

        this.setVisible(true);
        this.setAlwaysOnTop(true);
        User32.INSTANCE.SetWindowPos(
                hwnd,
                new WinDef.HWND(new Pointer(0)),
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                SHOW_FLAGS
        );
    }


    public void updateOverlay(JPanel overlayPanel) {
        if (!CalcOverlaySettings.getInstance().enableWindowOverlay) {
            this.setVisible(false);
            return;
        }
        this.setVisible(true);

        if (currentPanel != null) {
            getContentPane().remove(currentPanel);
        }

        currentPanel = overlayPanel;
        getContentPane().add(currentPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

}
