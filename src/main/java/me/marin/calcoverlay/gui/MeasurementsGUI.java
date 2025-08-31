package me.marin.calcoverlay.gui;

import lombok.Getter;
import me.marin.calcoverlay.io.CalcOverlaySettings;
import me.marin.calcoverlay.io.ColorSerializer;
import me.marin.calcoverlay.util.*;
import me.marin.calcoverlay.util.data.AngleToCoords;
import me.marin.calcoverlay.util.data.PlayerPosition;
import me.marin.calcoverlay.util.data.Prediction;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;

import static me.marin.calcoverlay.util.CalcOverlayUtil.*;

public class MeasurementsGUI {

    @Getter
    private JPanel mainPanel;
    private JPanel measurementsPanel;

    public MeasurementsGUI(List<Pair<Prediction, AngleToCoords>> predictions, PlayerPosition playerPosition) {
        $$$setupUI$$$();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 20, 5, 20);

        gbc.gridx = 0;
        // Icons
        for (CalcOverlaySettings.ColumnData columnData : CalcOverlaySettings.getInstance().columnData) {
            if (columnData.shouldShow(playerPosition.isInNether())) {
                switch (columnData.getHeaderRow()) {
                    case ICON:
                        JLabel overworldIcon = new JLabel();
                        overworldIcon.setIcon(new ImageIcon(columnData.getColumnType().getIcon()));
                        gbc.anchor = GridBagConstraints.CENTER;
                        measurementsPanel.add(overworldIcon, gbc);
                        break;
                    case TEXT:
                        JLabel text = setupJLabel(columnData.getColumnType().getOverlayDisplay(CalcOverlaySettings.getInstance().overworldCoords));
                        gbc.anchor = GridBagConstraints.SOUTH;
                        measurementsPanel.add(text, gbc);
                }
                gbc.gridx += 1;
            }
        }

        Color netherCoordsColor = CalcOverlaySettings.getInstance().netherCoordsColor;
        CalcOverlaySettings.NegativeCoords negativeCoords = CalcOverlaySettings.getInstance().negativeCoords;

        gbc.gridy = 1;
        final int vGap = 5;
        final int hGap = 20;
        for (Pair<Prediction, AngleToCoords> pair : predictions) {
            if (gbc.gridy > CalcOverlaySettings.getInstance().shownMeasurements) {
                break;
            }

            Prediction prediction = pair.getLeft();
            AngleToCoords angleToCoords = pair.getRight();

            gbc.gridx = 0;
            for (CalcOverlaySettings.ColumnData columnData : CalcOverlaySettings.getInstance().columnData) {
                if (columnData.shouldShow(playerPosition.isInNether())) {
                    int displayedDistance = (int) Math.floor(prediction.getOverworldDistance() / (playerPosition.isInNether() ? 8 : 1));
                    switch (columnData.getColumnType()) {
                        case OVERWORLD_COORDS:
                            JPanel coords = null;
                            switch (CalcOverlaySettings.getInstance().overworldCoords) {
                                case CHUNK:
                                    coords = setupCoordsLabel(prediction.getChunkX(), prediction.getChunkZ(), false, netherCoordsColor, negativeCoords);
                                    break;
                                case FOUR_FOUR:
                                    coords = setupCoordsLabel(prediction.getChunkX() * 16 + 4, prediction.getChunkZ() * 16 + 4, false, netherCoordsColor, negativeCoords);
                                    break;
                                case EIGHT_EIGHT:
                                    coords = setupCoordsLabel(prediction.getChunkX() * 16 + 8, prediction.getChunkZ() * 16 + 8, false, netherCoordsColor, negativeCoords);
                                    break;
                            }

                            gbc.insets = new Insets(gbc.gridy == 1 ? vGap : 0, hGap, vGap, hGap);
                            gbc.anchor = GridBagConstraints.CENTER;

                            measurementsPanel.add(coords, gbc);
                            break;
                        case CERTAINTY:
                            JLabel certainty = setupJLabel(String.format(Locale.US, "%.1f%%", prediction.getCertainty() * 100));
                            certainty.setMinimumSize(new Dimension(400, certainty.getHeight()));
                            certainty.setForeground(OverlayUtil.getColor(prediction.getCertainty()));

                            gbc.anchor = GridBagConstraints.CENTER;
                            gbc.insets = new Insets(gbc.gridy == 1 ? vGap : 0, hGap, vGap, hGap);

                            measurementsPanel.add(certainty, gbc);
                            break;
                        case DISTANCE:
                            JLabel distance = setupJLabel(String.valueOf(displayedDistance));
                            gbc.anchor = GridBagConstraints.CENTER;
                            gbc.insets = new Insets(gbc.gridy == 1 ? vGap : 0, hGap, vGap, hGap);
                            measurementsPanel.add(distance, gbc);
                            break;
                        case NETHER_COORDS:
                            JPanel netherCoords = setupCoordsLabel(prediction.getChunkX() * 2, prediction.getChunkZ() * 2, true, netherCoordsColor, negativeCoords);
                            gbc.insets = new Insets(gbc.gridy == 1 ? vGap : 0, hGap, vGap, hGap);
                            gbc.anchor = GridBagConstraints.CENTER;
                            measurementsPanel.add(netherCoords, gbc);
                            break;
                        case ANGLE:
                            JPanel anglePanel = new JPanel();
                            BorderLayout layout = new BorderLayout();
                            layout.setHgap(12);
                            anglePanel.setLayout(layout);

                            JLabel angleLabel = setupJLabel(String.format(Locale.US, "%.2f", angleToCoords.getActualAngle()));
                            anglePanel.add(angleLabel, BorderLayout.CENTER);

                            if (displayedDistance == 0) {
                                angleLabel.setText("-HERE-");
                                angleLabel.setForeground(OverlayUtil.COLOR_GRADIENT_100);
                            } else if (CalcOverlaySettings.getInstance().showAngleDirection) {
                                double correction = angleToCoords.getNeededAngleCorrection();
                                Color correctionColor = OverlayUtil.getColor(1 - Math.abs(correction) / 180);
                                JLabel angleAdjustmentLabel = setupJLabel(String.format(Locale.US, "(%s%.1f)",
                                        Math.abs(correction) < 0.05 ? "" : (correction > 0 ? "-> " : "<- "),
                                        Math.abs(correction)
                                ));
                                angleAdjustmentLabel.setForeground(correctionColor);
                                anglePanel.add(angleAdjustmentLabel, BorderLayout.EAST);
                            }


                            gbc.insets = new Insets(gbc.gridy == 1 ? vGap : 0, 0, vGap, hGap);
                            gbc.anchor = GridBagConstraints.CENTER;
                            measurementsPanel.add(anglePanel, gbc);
                            break;
                    }
                    gbc.gridx += 1;
                }
            }
            gbc.gridy += 1;
        }
    }



    public static void main(String[] args) {

    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        measurementsPanel = new JPanel();
        measurementsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(measurementsPanel, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
