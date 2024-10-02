package me.marin.calcoverlay.gui;

import lombok.Getter;
import me.marin.calcoverlay.io.CalcOverlaySettings;
import me.marin.calcoverlay.util.AngleToCoords;
import me.marin.calcoverlay.util.OverlayUtil;
import me.marin.calcoverlay.util.PlayerPosition;
import me.marin.calcoverlay.util.Prediction;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public class MeasurementsGUI {

    @Getter
    private JPanel mainPanel;
    private JPanel measurementsPanel;

    private final GridBagConstraints gbc = new GridBagConstraints();

    public MeasurementsGUI(List<Pair<Prediction, AngleToCoords>> predictions, PlayerPosition playerPosition) {
        $$$setupUI$$$();

        gbc.gridy = 0;
        gbc.insets = new Insets(18, 18, 0, 18);

        gbc.gridx = 0;
        for (CalcOverlaySettings.ColumnData columnData : CalcOverlaySettings.getInstance().columnData) {
            if (columnData.shouldShow(playerPosition.isInNether())) {
                if (columnData.isShowIcon()) {
                    JLabel overworldIcon = new JLabel();
                    overworldIcon.setIcon(new ImageIcon(columnData.getColumnType().getIcon()));
                    gbc.anchor = GridBagConstraints.CENTER;
                    measurementsPanel.add(overworldIcon, gbc);
                }
                gbc.gridx += 1;
            }
        }

        gbc.gridy = 1;
        final int vGap = 12;
        final int hGap = 30;
        for (Pair<Prediction, AngleToCoords> pair : predictions) {
            if (gbc.gridy > 3) {
                // only show the first 3 results
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
                            String text = "";
                            switch (CalcOverlaySettings.getInstance().overworldCoords) {
                                case CHUNK:
                                    text = "(" + prediction.getChunkX() + ", " + prediction.getChunkZ() + ")";
                                    break;
                                case FOUR_FOUR:
                                    text = "(" + (prediction.getChunkX() * 16 + 4) + ", " + (prediction.getChunkZ() * 16 + 4) + ")";
                                    break;
                                case EIGHT_EIGHT:
                                    text = "(" + (prediction.getChunkX() * 16 + 8) + ", " + (prediction.getChunkZ() * 16 + 8) + ")";
                                    break;
                            }
                            JLabel coords = setupJLabel(text);
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
                            JLabel netherCoords = setupJLabel("(" + prediction.getChunkX() * 2 + ", " + prediction.getChunkZ() * 2 + ")");
                            netherCoords.setForeground(new Color(0xFFB4B4));
                            gbc.insets = new Insets(gbc.gridy == 1 ? vGap : 0, hGap, vGap, hGap);
                            gbc.anchor = GridBagConstraints.CENTER;
                            measurementsPanel.add(netherCoords, gbc);
                            break;
                        case ANGLE:
                            JPanel anglePanel = new JPanel();
                            BorderLayout layout = new BorderLayout();
                            layout.setHgap(hGap);
                            anglePanel.setLayout(layout);

                            JLabel angleLabel = setupJLabel(String.format(Locale.US, "%.2f", angleToCoords.getActualAngle()));
                            anglePanel.add(angleLabel, BorderLayout.CENTER);

                            if (CalcOverlaySettings.getInstance().showAngleDirection) {
                                double correction = angleToCoords.getNeededAngleCorrection();
                                Color correctionColor = OverlayUtil.getColor(1 - Math.abs(correction) / 180);
                                JLabel angleAdjustmentLabel = setupJLabel(String.format(Locale.US, "(%s%.1f)",
                                        Math.abs(correction) < 0.05 ? "" : (correction > 0 ? "-> " : "<- "),
                                        Math.abs(correction)
                                ));
                                angleAdjustmentLabel.setForeground(correctionColor);
                                anglePanel.add(angleAdjustmentLabel, BorderLayout.EAST);
                            }

                            if (displayedDistance == 0) {
                                anglePanel.setVisible(false);
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

    private JLabel setupJLabel(String text) {
        JLabel jLabel = new JLabel();
        jLabel.setText(text);
        jLabel.setFont(new Font("Calibri", Font.PLAIN, 48));
        jLabel.setForeground(Color.WHITE);
        return jLabel;
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
