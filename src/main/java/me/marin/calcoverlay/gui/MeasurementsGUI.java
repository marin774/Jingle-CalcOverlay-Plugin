package me.marin.calcoverlay.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.Getter;
import me.marin.calcoverlay.io.CalcOverlaySettings;
import me.marin.calcoverlay.util.*;
import me.marin.calcoverlay.util.data.AngleToCoords;
import me.marin.calcoverlay.util.data.EyeThrow;
import me.marin.calcoverlay.util.data.PlayerPosition;
import me.marin.calcoverlay.util.data.Prediction;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static me.marin.calcoverlay.util.CalcOverlayUtil.*;

public class MeasurementsGUI {

    @Getter
    private JPanel mainPanel;
    private JPanel measurementsPanel;
    private JPanel errorPanel;
    private JLabel highErrorLabel;
    private JLabel portalLinkLabel;

    public static void main(String[] args) {

    }

    public MeasurementsGUI(List<Pair<Prediction, AngleToCoords>> predictions, List<EyeThrow> eyeThrows, PlayerPosition playerPosition) {
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

                            switch (CalcOverlaySettings.getInstance().angleDisplay) {
                                case ALL: {
                                    JLabel angleLabel = setupJLabel(String.format(Locale.US, "%.2f", angleToCoords.getActualAngle()));
                                    anglePanel.add(angleLabel, BorderLayout.CENTER);

                                    if (displayedDistance == 0) {
                                        angleLabel.setText("-HERE-");
                                        angleLabel.setForeground(OverlayUtil.COLOR_GRADIENT_100);
                                    } else {
                                        double correction = angleToCoords.getNeededAngleCorrection();
                                        Color correctionColor = OverlayUtil.getColor(1 - Math.abs(correction) / 180);
                                        JLabel angleAdjustmentLabel = setupJLabel(String.format(Locale.US, "(%s%.1f)",
                                                Math.abs(correction) < 0.05 ? "" : (correction > 0 ? "-> " : "<- "),
                                                Math.abs(correction)
                                        ));
                                        angleAdjustmentLabel.setForeground(correctionColor);
                                        anglePanel.add(angleAdjustmentLabel, BorderLayout.EAST);
                                    }
                                    break;
                                }
                                case ONLY_ANGLE: {
                                    JLabel angleLabel = setupJLabel(String.format(Locale.US, "%.2f", angleToCoords.getActualAngle()));
                                    if (displayedDistance == 0) {
                                        angleLabel.setText("-HERE-");
                                        angleLabel.setForeground(OverlayUtil.COLOR_GRADIENT_100);
                                    }
                                    anglePanel.add(angleLabel, BorderLayout.CENTER);
                                    break;
                                }
                                case ONLY_ANGLE_CHANGE: {
                                    JLabel angleLabel;
                                    if (displayedDistance == 0) {
                                        angleLabel = setupJLabel("-HERE-");
                                        angleLabel.setForeground(OverlayUtil.COLOR_GRADIENT_100);
                                    } else {
                                        double correction = angleToCoords.getNeededAngleCorrection();
                                        Color correctionColor = OverlayUtil.getColor(1 - Math.abs(correction) / 180);
                                        angleLabel = setupJLabel(String.format(Locale.US, "%s%.1f",
                                                correction > 0 ? "-> " : "<- ",
                                                Math.abs(correction)
                                        ));
                                        angleLabel.setForeground(correctionColor);
                                    }

                                    anglePanel.add(angleLabel, BorderLayout.CENTER);
                                    break;
                                }
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

        errorPanel.setVisible(CalcOverlaySettings.getInstance().showInfoBar);

        if (CalcOverlaySettings.getInstance().showInfoBar) {
            Map<String, Object> settings = NinjabrainBotSettingsUtil.getSettings();

            boolean hasError = InformationUtil.hasError(predictions.get(0).getKey(), eyeThrows, settings);
            highErrorLabel.setVisible(hasError);

            boolean canLink = InformationUtil.canLink(predictions.get(0).getKey(), eyeThrows.get(0));
            portalLinkLabel.setVisible(canLink);

            JPanel anglePanel = new JPanel();
            BorderLayout layout = new BorderLayout();
            layout.setHgap(6);
            anglePanel.setLayout(layout);

            JLabel angleLabel = setupJLabel(String.format(Locale.US, "%.2f°", eyeThrows.get(eyeThrows.size() - 1).getAngleWithoutCorrection()));
            anglePanel.add(angleLabel, BorderLayout.CENTER);

            JLabel angleAdjustmentLabel;

            int correctionIncrements = eyeThrows.get(0).getCorrectionIncrements();
            if (correctionIncrements == 0) {
                // Fix for pre-1.5.2 (correctionIncrements is always 0)
                correctionIncrements = eyeThrows.get(eyeThrows.size() - 1).getAngleCorrectionIncrements(settings);
            }

            if (correctionIncrements != 0) {
                angleAdjustmentLabel = setupJLabel(String.format(Locale.US, "%s%d",
                        correctionIncrements > 0 ? "+" : "",
                        correctionIncrements
                ));
                angleAdjustmentLabel.setForeground(correctionIncrements > 0 ? OverlayUtil.ADJUSTMENT_POSITIVE : OverlayUtil.ADJUSTMENT_NEGATIVE);

                anglePanel.add(angleAdjustmentLabel, BorderLayout.EAST);
            }

            errorPanel.add(anglePanel, BorderLayout.EAST);
        }


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
        errorPanel = new JPanel();
        errorPanel.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 20, 0, 20);
        mainPanel.add(errorPanel, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 20, -1));
        errorPanel.add(panel1, BorderLayout.WEST);
        highErrorLabel = new JLabel();
        highErrorLabel.setIcon(new ImageIcon(getClass().getResource("/icons/high_error.png")));
        highErrorLabel.setText("");
        panel1.add(highErrorLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        portalLinkLabel = new JLabel();
        portalLinkLabel.setIcon(new ImageIcon(getClass().getResource("/icons/portal_link.png")));
        portalLinkLabel.setText("");
        panel1.add(portalLinkLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        errorPanel.add(panel2, BorderLayout.CENTER);
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
