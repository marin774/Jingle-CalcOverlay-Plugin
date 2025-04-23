package me.marin.calcoverlay.gui;

import com.google.gson.JsonObject;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.marin.calcoverlay.io.AllAdvancementsSettings;
import me.marin.calcoverlay.io.CalcOverlaySettings;
import me.marin.calcoverlay.util.CalcOverlayUtil;
import me.marin.calcoverlay.util.OverlayUtil;
import me.marin.calcoverlay.util.UpdateUtil;
import org.apache.logging.log4j.Level;
import org.drjekyll.fontchooser.FontDialog;
import xyz.duncanruns.jingle.gui.JingleGUI;
import xyz.duncanruns.jingle.util.ExceptionUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
import java.util.Locale;

import static me.marin.calcoverlay.CalcOverlay.*;
import static me.marin.calcoverlay.ninjabrainapi.NinjabrainBotEventSubscriber.GSON;

public class ConfigGUI extends JPanel {

    public JPanel mainPanel;
    public JCheckBox enabledCheckbox;

    private JCheckBox showCoordsBasedOnCheckbox;
    private JComboBox<String> overworldCoordsTypeCombobox;
    private JPanel eyeThrowsColumnsPanel;
    private JComboBox<String> overlayPositionCombobox;
    private JCheckBox showAngleDirectionCheckbox;
    private JButton checkForUpdatesButton;
    private JPanel enabledPanel;
    private JButton previewEyeThrowsOverlayButton;
    private JSpinner shownMeasurementsSpinner;
    private JLabel fontLabel;
    private JButton changeFontButton;
    private JButton copyScriptPathButton;
    private JButton OBSScriptSetupButton;
    private JTabbedPane settingsPane;
    private JButton previewAAOverlayButton;
    private JPanel aaColumnsPanel;
    private JPanel aaRowsPanel;
    private JSpinner outlineWidthSpinner;
    private JButton previewBlindCoordsOverlayButton;

    private JFrame previewFrame;
    private JPanel previewPanel;
    private PreviewType previewType;

    @AllArgsConstructor
    @Getter
    public enum PreviewType {
        EYE_THROWS(GSON.fromJson(
                "{\"eyeThrows\":[{\"xInOverworld\":1199.63,\"angleWithoutCorrection\":-161.14926034190884,\"zInOverworld\":-139.09,\"angle\":-161.13926034190885,\"correction\":0.01,\"error\":0.0014111816929869292,\"type\":\"NORMAL\"}],\"resultType\":\"TRIANGULATION\",\"playerPosition\":{\"xInOverworld\":1199.63,\"isInOverworld\":true,\"isInNether\":false,\"horizontalAngle\":-161.15,\"zInOverworld\":-139.09},\"predictions\":[{\"overworldDistance\":523.3899550048701,\"certainty\":0.5147413124532876,\"chunkX\":85,\"chunkZ\":-40},{\"overworldDistance\":1216.5659558774444,\"certainty\":0.2674146623130985,\"chunkX\":99,\"chunkZ\":-81},{\"overworldDistance\":1859.1560464361241,\"certainty\":0.1252834863035146,\"chunkX\":112,\"chunkZ\":-119},{\"overworldDistance\":1909.7519223710706,\"certainty\":0.07908349382318092,\"chunkX\":113,\"chunkZ\":-122},{\"overworldDistance\":1165.9697787678717,\"certainty\":0.012493314849953712,\"chunkX\":98,\"chunkZ\":-78}]}",
                JsonObject.class
        )),
        ALL_ADVANCEMENTS(GSON.fromJson(
                "{\"generalLocation\":{},\"spawn\":{\"overworldDistance\":4340,\"xInOverworld\":-94,\"zInOverworld\":-236,\"travelAngle\":-130.84419338173197},\"cityQuery\":{},\"monument\":{\"overworldDistance\":0,\"xInOverworld\":-3378,\"zInOverworld\":2602,\"travelAngle\":132.08916217383447},\"shulkerTransport\":{},\"stronghold\":{\"overworldDistance\":5381,\"xInOverworld\":1764,\"zInOverworld\":1012,\"travelAngle\":-107.19020650452967},\"deepDark\":{},\"isAllAdvancementsModeEnabled\":true,\"outpost\":{\"overworldDistance\":4149,\"xInOverworld\":-347,\"zInOverworld\":-232,\"travelAngle\":-133.0877053812539}}",
                JsonObject.class
        )),
        BLIND_COORDS(GSON.fromJson(
                "{\"isBlindModeEnabled\":true,\"hasDivine\":false,\"blindResult\":{\"evaluation\":\"HIGHROLL_GOOD\",\"xInNether\":-217.82,\"improveDistance\":8.071372233935255,\"zInNether\":6.88,\"averageDistance\":1086.9952915836398,\"improveDirection\":1.5392211114431098,\"highrollProbability\":0.10072320582001268,\"highrollThreshold\":400}}",
                JsonObject.class
        ));

        private final JsonObject response;
    }

    public static void main(String[] args) {
        // I run this to force intellij to update gui code
    }

    public ConfigGUI() {
        CalcOverlaySettings settings = CalcOverlaySettings.getInstance();

        add(mainPanel);

        updateGUI();

        checkForUpdatesButton.addActionListener(a -> {
            UpdateUtil.checkForUpdatesAndUpdate(false);
        });

        enabledCheckbox.setSelected(settings.calcOverlayEnabled);
        enabledCheckbox.addActionListener(a -> {
            if (!settings.calcOverlayEnabled) {
                if (!NINJABRAIN_BOT_EVENT_SUBSCRIBER.ping()) {
                    enabledCheckbox.setSelected(false);
                    JOptionPane.showMessageDialog(null, "Couldn't connect to Ninjabrain Bot API. Make sure that Ninjabrain Bot is open, and API is enabled in its settings.");
                    return;
                }
            } else {
                previewFrame.setVisible(false);
                NINJABRAIN_BOT_EVENT_SUBSCRIBER.disconnect();
            }

            settings.calcOverlayEnabled = enabledCheckbox.isSelected();
            CalcOverlaySettings.save();

            updateGUI();
        });

        previewFrame = new JFrame();

        previewFrame.setTitle("Overlay Preview");
        previewFrame.setResizable(false);
        previewFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        previewEyeThrowsOverlayButton.addActionListener(a -> {
            previewType = PreviewType.EYE_THROWS;
            updatePreview();
            previewFrame.setVisible(true);
            previewFrame.requestFocus();
        });
        previewAAOverlayButton.addActionListener(a -> {
            previewType = PreviewType.ALL_ADVANCEMENTS;
            updatePreview();
            previewFrame.setVisible(true);
            previewFrame.requestFocus();
        });
        previewBlindCoordsOverlayButton.addActionListener(a -> {
            previewType = PreviewType.BLIND_COORDS;
            updatePreview();
            previewFrame.setVisible(true);
            previewFrame.requestFocus();
        });

        showAngleDirectionCheckbox.setSelected(settings.showAngleDirection);
        showAngleDirectionCheckbox.addActionListener(a -> {
            settings.showAngleDirection = showAngleDirectionCheckbox.isSelected();
            CalcOverlaySettings.save();
            updatePreview();
        });

        showCoordsBasedOnCheckbox.setSelected(settings.onlyShowCurrentDimensionCoords);
        showCoordsBasedOnCheckbox.addActionListener(a -> {
            settings.onlyShowCurrentDimensionCoords = showCoordsBasedOnCheckbox.isSelected();
            CalcOverlaySettings.save();
            updatePreview();
        });


        for (CalcOverlaySettings.OverworldsCoords value : CalcOverlaySettings.OverworldsCoords.values()) {
            overworldCoordsTypeCombobox.addItem(value.getDisplay());
        }
        overworldCoordsTypeCombobox.setSelectedItem(settings.overworldCoords.getDisplay());
        overworldCoordsTypeCombobox.addActionListener(a -> {
            settings.overworldCoords = CalcOverlaySettings.OverworldsCoords.match((String) overworldCoordsTypeCombobox.getSelectedItem());
            CalcOverlaySettings.save();
            updatePreview();
        });

        for (CalcOverlaySettings.Position value : CalcOverlaySettings.Position.values()) {
            overlayPositionCombobox.addItem(value.getDisplay());
        }
        overlayPositionCombobox.setSelectedItem(settings.overlayPosition.getDisplay());
        overlayPositionCombobox.addActionListener(a -> {
            settings.overlayPosition = CalcOverlaySettings.Position.match((String) overlayPositionCombobox.getSelectedItem());
            CalcOverlaySettings.save();
            updatePreview();
        });

        copyScriptPathButton.addActionListener(a -> {
            StringSelection stringSelection = new StringSelection(OBS_SCRIPT_PATH.toAbsolutePath().toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            JOptionPane.showMessageDialog(null, "Copied to clipboard.");
        });

        OBSScriptSetupButton.addActionListener(a -> {
            try {
                Desktop.getDesktop().browse(URI.create("https://github.com/marin774/Jingle-CalcOverlay-Plugin/blob/main/setup.md#setup-obs-script--overlay"));
            } catch (Exception e) {
                log(Level.ERROR, "Failed to open:\n" + ExceptionUtil.toDetailedString(e));
            }
        });

        shownMeasurementsSpinner.setModel(new SpinnerNumberModel(settings.shownMeasurements, 1, 5, 1));
        ((JSpinner.DefaultEditor) shownMeasurementsSpinner.getEditor()).getTextField().setEditable(false);
        shownMeasurementsSpinner.addChangeListener(a -> {
            settings.shownMeasurements = (int) shownMeasurementsSpinner.getValue();
            CalcOverlaySettings.save();
            updatePreview();
        });

        outlineWidthSpinner.setModel(new SpinnerNumberModel(settings.outlineWidth, 0, 20, 1));
        ((JSpinner.DefaultEditor) outlineWidthSpinner.getEditor()).getTextField().setEditable(false);
        outlineWidthSpinner.addChangeListener(a -> {
            settings.outlineWidth = (int) outlineWidthSpinner.getValue();
            CalcOverlaySettings.save();
            updatePreview();
        });

        changeFontButton.addActionListener(a -> {
            try {
                FontDialog dialog = new FontDialog(JingleGUI.get(), "CalcOverlay Font Chooser", true);
                dialog.setSelectedFont(CalcOverlayUtil.getFont());
                dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                dialog.setVisible(true);
                if (!dialog.isCancelSelected()) {
                    Font font = dialog.getSelectedFont();
                    settings.fontData = new CalcOverlaySettings.FontData(font.getFontName(), font.getSize());
                    CalcOverlaySettings.save();
                    updatePreview();
                    updateGUI();
                }
            } catch (Exception e) {
                log(Level.ERROR, "Exception while choosing font:\n" + ExceptionUtil.toDetailedString(e));
            }
        });

    }

    private void updatePreview() {
        if (previewPanel != null) {
            previewFrame.remove(previewPanel);
        }

        // Update OBS
        NINJABRAIN_BOT_EVENT_SUBSCRIBER.updateImage();

        if (previewType != null) {
            // Update preview panel
            previewPanel = OverlayUtil.getFinalOverlayPanel(OverlayUtil.getPreviewPanel(previewType));

            previewFrame.add(previewPanel);
            previewFrame.setSize(previewPanel.getSize());
            previewFrame.revalidate();
            previewFrame.repaint();
        }
    }

    public void updateGUI() {
        CalcOverlaySettings settings = CalcOverlaySettings.getInstance();

        enabledPanel.setVisible(settings.calcOverlayEnabled);

        Font font = CalcOverlayUtil.getFont();
        fontLabel.setText(String.format("%s, %dpt", font.getName(), font.getSize()));

        setupEyeThrowsPanel();
        setupAARowsPanel();
        setupAAColumnsPanel();

        revalidate();
        repaint();
    }

    private void setupEyeThrowsPanel() {
        CalcOverlaySettings settings = CalcOverlaySettings.getInstance();

        eyeThrowsColumnsPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        final int hGap = 5;
        final int vGap = 5;
        gbc.gridy = 0;

        gbc.insets = new Insets(0, hGap, vGap, hGap);

        JLabel showColumnLabel = new JLabel("<html>Show<br>column<html>");
        showColumnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        eyeThrowsColumnsPanel.add(showColumnLabel, gbc);

        JLabel showIconLabel = new JLabel("Header");
        showIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.SOUTH;
        eyeThrowsColumnsPanel.add(showIconLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(vGap, 0, vGap, hGap);
        for (CalcOverlaySettings.ColumnData columnData : settings.columnData) {
            JLabel columnNameLabel = new JLabel(columnData.getColumnType().getConfigDisplay());
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.EAST;
            eyeThrowsColumnsPanel.add(columnNameLabel, gbc);

            JCheckBox showColumnCheckbox = new JCheckBox();
            showColumnCheckbox.setSelected(columnData.isVisible());
            showColumnCheckbox.addActionListener(a -> {
                columnData.setVisible(showColumnCheckbox.isSelected());
                CalcOverlaySettings.save();
                updatePreview();
            });
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            eyeThrowsColumnsPanel.add(showColumnCheckbox, gbc);

            JComboBox<String> headerCombobox = newEyeThrowsHeaderCombobox(columnData);
            gbc.gridx = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            eyeThrowsColumnsPanel.add(headerCombobox, gbc);

            JButton moveUpButton = new JButton("Move left");
            gbc.gridx = 3;
            gbc.anchor = GridBagConstraints.CENTER;
            moveUpButton.addActionListener(a -> {
                int idx = settings.columnData.indexOf(columnData);
                settings.columnData.remove(idx);
                settings.columnData.add(idx - 1, columnData);
                updateGUI();
                updatePreview();
            });
            moveUpButton.setEnabled(gbc.gridy > 1);
            eyeThrowsColumnsPanel.add(moveUpButton, gbc);

            JButton moveDownButton = new JButton("Move right");
            gbc.gridx = 4;
            gbc.anchor = GridBagConstraints.CENTER;
            moveDownButton.addActionListener(a -> {
                int idx = settings.columnData.indexOf(columnData);
                settings.columnData.remove(idx);
                settings.columnData.add(idx + 1, columnData);
                updateGUI();
                updatePreview();
            });
            eyeThrowsColumnsPanel.add(moveDownButton, gbc);
            moveDownButton.setEnabled(gbc.gridy < settings.columnData.size());

            gbc.gridy += 1;
        }
    }

    private void setupAAColumnsPanel() {
        AllAdvancementsSettings settings = CalcOverlaySettings.getInstance().aaSettings;

        aaColumnsPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        final int hGap = 5;
        final int vGap = 5;
        gbc.gridy = 0;

        gbc.insets = new Insets(0, hGap, vGap, hGap);

        JLabel showColumnLabel = new JLabel("<html>Show<br>column<html>");
        showColumnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        aaColumnsPanel.add(showColumnLabel, gbc);

        JLabel showIconLabel = new JLabel("Header");
        showIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.SOUTH;
        aaColumnsPanel.add(showIconLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(vGap, 0, vGap, hGap);
        for (AllAdvancementsSettings.ColumnData columnData : settings.columnData) {
            JLabel columnNameLabel = new JLabel(columnData.getColumnType().getConfigDisplay());
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.EAST;
            aaColumnsPanel.add(columnNameLabel, gbc);

            JCheckBox showColumnCheckbox = new JCheckBox();
            showColumnCheckbox.setSelected(columnData.isVisible());
            showColumnCheckbox.addActionListener(a -> {
                columnData.setVisible(showColumnCheckbox.isSelected());
                CalcOverlaySettings.save();
                updatePreview();
            });
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            aaColumnsPanel.add(showColumnCheckbox, gbc);

            JComboBox<String> headerCombobox = newAAHeaderCombobox(columnData);
            gbc.gridx = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            aaColumnsPanel.add(headerCombobox, gbc);

            JButton moveUpButton = new JButton("Move left");
            gbc.gridx = 3;
            gbc.anchor = GridBagConstraints.CENTER;
            moveUpButton.addActionListener(a -> {
                int idx = settings.columnData.indexOf(columnData);
                settings.columnData.remove(idx);
                settings.columnData.add(idx - 1, columnData);
                updateGUI();
                updatePreview();
            });
            moveUpButton.setEnabled(gbc.gridy > 1);
            aaColumnsPanel.add(moveUpButton, gbc);

            JButton moveDownButton = new JButton("Move right");
            gbc.gridx = 4;
            gbc.anchor = GridBagConstraints.CENTER;
            moveDownButton.addActionListener(a -> {
                int idx = settings.columnData.indexOf(columnData);
                settings.columnData.remove(idx);
                settings.columnData.add(idx + 1, columnData);
                updateGUI();
                updatePreview();
            });
            aaColumnsPanel.add(moveDownButton, gbc);
            moveDownButton.setEnabled(gbc.gridy < settings.columnData.size());

            gbc.gridy += 1;
        }
    }

    private void setupAARowsPanel() {
        AllAdvancementsSettings settings = CalcOverlaySettings.getInstance().aaSettings;

        aaRowsPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        final int hGap = 5;
        final int vGap = 5;
        gbc.gridy = 0;

        gbc.insets = new Insets(0, hGap, vGap, hGap);

        JLabel showRowLabel = new JLabel("<html>Show<br>row<html>");
        showRowLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        aaRowsPanel.add(showRowLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(vGap, 0, vGap, hGap);
        for (AllAdvancementsSettings.RowData rowData : settings.rowData) {
            JLabel rowNameLabel = new JLabel(rowData.getRowType().getConfigDisplay());
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.EAST;
            aaRowsPanel.add(rowNameLabel, gbc);

            JCheckBox showRowCheckbox = new JCheckBox();
            showRowCheckbox.setSelected(rowData.isVisible());
            showRowCheckbox.addActionListener(a -> {
                rowData.setVisible(showRowCheckbox.isSelected());
                CalcOverlaySettings.save();
                updatePreview();
            });
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            aaRowsPanel.add(showRowCheckbox, gbc);

            JButton moveUpButton = new JButton("Move up");
            gbc.gridx = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            moveUpButton.addActionListener(a -> {
                int idx = settings.rowData.indexOf(rowData);
                settings.rowData.remove(idx);
                settings.rowData.add(idx - 1, rowData);
                updateGUI();
                updatePreview();
            });
            moveUpButton.setEnabled(gbc.gridy > 1);
            aaRowsPanel.add(moveUpButton, gbc);

            JButton moveDownButton = new JButton("Move down");
            gbc.gridx = 3;
            gbc.anchor = GridBagConstraints.CENTER;
            moveDownButton.addActionListener(a -> {
                int idx = settings.rowData.indexOf(rowData);
                settings.rowData.remove(idx);
                settings.rowData.add(idx + 1, rowData);
                updateGUI();
                updatePreview();
            });
            moveDownButton.setEnabled(gbc.gridy < settings.rowData.size());
            aaRowsPanel.add(moveDownButton, gbc);

            gbc.gridy += 1;
        }
    }

    private JComboBox<String> newEyeThrowsHeaderCombobox(CalcOverlaySettings.ColumnData columnData) {
        JComboBox<String> headerCombobox = new JComboBox<>();
        for (CalcOverlaySettings.HeaderRow value : CalcOverlaySettings.HeaderRow.values()) {
            headerCombobox.addItem(value.getDisplay());
        }
        headerCombobox.setSelectedItem(columnData.getHeaderRow().getDisplay());
        headerCombobox.addActionListener(a -> {
            columnData.setHeaderRow(CalcOverlaySettings.HeaderRow.match((String) headerCombobox.getSelectedItem()));
            CalcOverlaySettings.save();
            updatePreview();
        });
        return headerCombobox;
    }

    private JComboBox<String> newAAHeaderCombobox(AllAdvancementsSettings.ColumnData columnData) {
        JComboBox<String> headerCombobox = new JComboBox<>();
        if (columnData.getColumnType() == AllAdvancementsSettings.ColumnType.ICONS) {
            headerCombobox.addItem(AllAdvancementsSettings.HeaderRow.NOTHING.getDisplay());
            headerCombobox.setSelectedItem(AllAdvancementsSettings.HeaderRow.NOTHING.getDisplay());
            headerCombobox.setEnabled(false);
            return headerCombobox;
        }

        for (AllAdvancementsSettings.HeaderRow value : AllAdvancementsSettings.HeaderRow.values()) {
            headerCombobox.addItem(value.getDisplay());
        }
        headerCombobox.setSelectedItem(columnData.getHeaderRow().getDisplay());
        headerCombobox.addActionListener(a -> {
            columnData.setHeaderRow(AllAdvancementsSettings.HeaderRow.match((String) headerCombobox.getSelectedItem()));
            CalcOverlaySettings.save();
            updatePreview();
        });
        return headerCombobox;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
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
        mainPanel.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        enabledCheckbox = new JCheckBox();
        enabledCheckbox.setText("Enable overlay");
        mainPanel.add(enabledCheckbox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 10, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("OBS Script:");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        copyScriptPathButton = new JButton();
        copyScriptPathButton.setText("Copy Script Path");
        panel1.add(copyScriptPathButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        OBSScriptSetupButton = new JButton();
        OBSScriptSetupButton.setText("OBS Script Setup");
        panel1.add(OBSScriptSetupButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkForUpdatesButton = new JButton();
        checkForUpdatesButton.setText("Check for updates");
        mainPanel.add(checkForUpdatesButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        enabledPanel = new JPanel();
        enabledPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(enabledPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        enabledPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Overlay Config", TitledBorder.LEFT, TitledBorder.TOP, this.$$$getFont$$$(null, Font.BOLD, 14, enabledPanel.getFont()), null));
        settingsPane = new JTabbedPane();
        settingsPane.setTabLayoutPolicy(0);
        settingsPane.setTabPlacement(1);
        enabledPanel.add(settingsPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 420), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(31);
        settingsPane.addTab("General", scrollPane1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 1, new Insets(5, 5, 5, 5), -1, -1));
        scrollPane1.setViewportView(panel2);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Font:");
        panel3.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        changeFontButton = new JButton();
        changeFontButton.setText("Change font");
        panel3.add(changeFontButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fontLabel = new JLabel();
        fontLabel.setText("<font>");
        panel3.add(fontLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        overlayPositionCombobox = new JComboBox();
        panel4.add(overlayPositionCombobox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Overlay position:");
        label3.setToolTipText("<html>Overlay position in the image. Image is always the same size,<br>and this setting lets you choose which corner overlay anchors to.</html>");
        panel4.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(89, 31), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Outline width:");
        panel5.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        outlineWidthSpinner = new JSpinner();
        panel5.add(outlineWidthSpinner, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setHorizontalScrollBarPolicy(31);
        settingsPane.addTab("Eye Throws Overlay", scrollPane2);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(7, 1, new Insets(5, 5, 5, 5), -1, -1));
        scrollPane2.setViewportView(panel6);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        shownMeasurementsSpinner = new JSpinner();
        panel7.add(shownMeasurementsSpinner, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Shown measurements:");
        panel7.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel8, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Overworld coords:");
        panel8.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        overworldCoordsTypeCombobox = new JComboBox();
        panel8.add(overworldCoordsTypeCombobox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showAngleDirectionCheckbox = new JCheckBox();
        showAngleDirectionCheckbox.setText("Show angle direction");
        panel6.add(showAngleDirectionCheckbox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showCoordsBasedOnCheckbox = new JCheckBox();
        showCoordsBasedOnCheckbox.setText("Show Overworld/Nether coords based on dimension");
        showCoordsBasedOnCheckbox.setToolTipText("<html>\nIf enabled, only overworld coords will be shown while you're in overworld,<br>and nether coords will be hidden.<br> Once you F3+C in the nether, only nether coords will be shown,<br>and overworld coords will be hidden.\n<br><br>\nIf disabled, both coords will always be shown.\n</html>");
        panel6.add(showCoordsBasedOnCheckbox, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        panel6.add(panel9, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel9.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Columns", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        eyeThrowsColumnsPanel = new JPanel();
        eyeThrowsColumnsPanel.setLayout(new GridBagLayout());
        panel9.add(eyeThrowsColumnsPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        previewEyeThrowsOverlayButton = new JButton();
        previewEyeThrowsOverlayButton.setLabel("Preview Eye Throws Overlay");
        previewEyeThrowsOverlayButton.setText("Preview Eye Throws Overlay");
        panel6.add(previewEyeThrowsOverlayButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel6.add(spacer2, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane3 = new JScrollPane();
        scrollPane3.setHorizontalScrollBarPolicy(31);
        scrollPane3.setName("");
        settingsPane.addTab("Blind Coords Overlay", scrollPane3);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(3, 1, new Insets(5, 5, 5, 5), -1, -1));
        scrollPane3.setViewportView(panel10);
        previewBlindCoordsOverlayButton = new JButton();
        previewBlindCoordsOverlayButton.setLabel("Preview Blind Coords Overlay");
        previewBlindCoordsOverlayButton.setText("Preview Blind Coords Overlay");
        panel10.add(previewBlindCoordsOverlayButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel10.add(spacer3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("No config options yet.");
        panel10.add(label7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane4 = new JScrollPane();
        scrollPane4.setHorizontalScrollBarPolicy(31);
        settingsPane.addTab("All Advancements Overlay", scrollPane4);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(3, 3, new Insets(5, 5, 5, 5), -1, -1));
        scrollPane4.setViewportView(panel11);
        previewAAOverlayButton = new JButton();
        previewAAOverlayButton.setText("Preview All Advancements Overlay");
        panel11.add(previewAAOverlayButton, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel11.add(spacer4, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel11.add(panel12, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        panel12.add(panel13, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel13.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Columns", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        aaColumnsPanel = new JPanel();
        aaColumnsPanel.setLayout(new GridBagLayout());
        panel13.add(aaColumnsPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        panel12.add(panel14, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel14.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Rows", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        aaRowsPanel = new JPanel();
        aaRowsPanel.setLayout(new GridBagLayout());
        panel14.add(aaRowsPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel12.add(spacer5, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        mainPanel.add(spacer6, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
