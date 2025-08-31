package me.marin.calcoverlay.gui;

import javax.swing.*;
import java.awt.*;

public class LTRPaintOrderJPanel extends JPanel {

    @Override
    protected void paintChildren(Graphics g) {
        for (int i = 0; i < getComponentCount(); i++) {
            Component comp = getComponent(i);
            Graphics cg = g.create(comp.getX(), comp.getY(), comp.getWidth(), comp.getHeight());
            comp.paint(cg);
            cg.dispose();
        }
    }
}
