package me.marin.calcoverlay.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Code from <a href="https://github.com/DuncanRuns/Julti/blob/main/src/main/java/xyz/duncanruns/julti/gui/DownloadProgressFrame.java">Julti</a>
 * @author DuncanRuns
 */
public class UpdateProgressFrame extends JFrame {

    private final JProgressBar bar;

    public UpdateProgressFrame(Point location) {
        this.setLayout(new GridBagLayout());
        JLabel text = new JLabel("Downloading CalcOverlay Plugin...");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        this.add(text, gbc);
        this.bar = new JProgressBar(0, 100);
        this.add(this.bar, gbc);


        this.setSize(300, 100);
        this.setTitle("CalcOverlay update");
        this.setLocation(location);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setVisible(true);
    }

    public JProgressBar getBar() {
        return this.bar;
    }

}