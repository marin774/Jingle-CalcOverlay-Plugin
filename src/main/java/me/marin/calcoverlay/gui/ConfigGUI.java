package me.marin.calcoverlay.gui;

import com.google.gson.JsonObject;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import me.marin.calcoverlay.io.AllAdvancementsSettings;
import me.marin.calcoverlay.io.CalcOverlaySettings;
import me.marin.calcoverlay.util.CalcOverlayUtil;
import me.marin.calcoverlay.util.OverlayUtil;
import me.marin.calcoverlay.util.UpdateUtil;
import org.apache.logging.log4j.Level;
import org.drjekyll.fontchooser.FontDialog;
import xyz.duncanruns.jingle.Jingle;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static me.marin.calcoverlay.CalcOverlay.*;
import static me.marin.calcoverlay.ninjabrainapi.NinjabrainBotEventSubscriber.GSON;

public class ConfigGUI extends JPanel {

    public JPanel mainPanel;
    public JCheckBox enabledCheckbox;

    private JCheckBox showCoordsBasedOnCheckbox;
    private JComboBox<String> overworldCoordsTypeCombobox;
    private JPanel eyeThrowsColumnsPanel;
    private JComboBox<String> overlayPositionCombobox;
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
    private JSpinner clearOverlayTime;
    private JComboBox<String> clearOverlayCombobox;
    private JButton changeNetherCoordsColorButton;
    private JPanel netherCoordsColorPanel;
    private JCheckBox useNegativeCoordsColor;
    private JButton changeNegativeCoordsColorButton;
    private JPanel negativeCoordsColorPanel;
    private JCheckBox enableBlindCoordsOverlay;
    private JCheckBox enableAllAdvancementsOverlay;
    private JComboBox<String> angleDisplayCombobox;
    private JCheckBox showInfoBarCheckbox;
    private JButton windowOverlaySetupButton;
    private JButton copyColorKeyButton;
    private JCheckBox showDirectionAndDistanceCheckbox;

    private JFrame previewFrame;
    private JPanel previewPanel;
    private CalcOverlaySettings.PreviewType previewType;

    private final Map<CalcOverlaySettings.PreviewType, JsonObject> dummyResponseMap = new HashMap<>();

    public static void main(String[] args) {
        // I run this to force intellij to update gui code
    }

    public ConfigGUI() {
        $$$setupUI$$$();

        dummyResponseMap.put(CalcOverlaySettings.PreviewType.EYE_THROWS, GSON.fromJson(
                "{\"eyeThrows\":[{\"xInOverworld\":1199.63,\"angleWithoutCorrection\":-161.14926034190884,\"zInOverworld\":-139.09,\"angle\":-161.13926034190885,\"correction\":0.01,\"error\":0.0014111816929869292,\"type\":\"NORMAL\"}],\"resultType\":\"TRIANGULATION\",\"playerPosition\":{\"xInOverworld\":1199.63,\"isInOverworld\":true,\"isInNether\":false,\"horizontalAngle\":-161.15,\"zInOverworld\":-139.09},\"predictions\":[{\"overworldDistance\":523.3899550048701,\"certainty\":0.5147413124532876,\"chunkX\":85,\"chunkZ\":-40},{\"overworldDistance\":1216.5659558774444,\"certainty\":0.2674146623130985,\"chunkX\":99,\"chunkZ\":-81},{\"overworldDistance\":1859.1560464361241,\"certainty\":0.1252834863035146,\"chunkX\":112,\"chunkZ\":-119},{\"overworldDistance\":1909.7519223710706,\"certainty\":0.07908349382318092,\"chunkX\":113,\"chunkZ\":-122},{\"overworldDistance\":1165.9697787678717,\"certainty\":0.012493314849953712,\"chunkX\":98,\"chunkZ\":-78}]}",
                JsonObject.class
        ));
        dummyResponseMap.put(CalcOverlaySettings.PreviewType.ALL_ADVANCEMENTS, GSON.fromJson(
                "{\"generalLocation\":{},\"spawn\":{\"overworldDistance\":4340,\"xInOverworld\":-94,\"zInOverworld\":-236,\"travelAngle\":-130.84419338173197},\"cityQuery\":{},\"monument\":{\"overworldDistance\":0,\"xInOverworld\":-3378,\"zInOverworld\":2602,\"travelAngle\":132.08916217383447},\"shulkerTransport\":{},\"stronghold\":{\"overworldDistance\":5381,\"xInOverworld\":1764,\"zInOverworld\":1012,\"travelAngle\":-107.19020650452967},\"deepDark\":{},\"isAllAdvancementsModeEnabled\":true,\"outpost\":{\"overworldDistance\":4149,\"xInOverworld\":-347,\"zInOverworld\":-232,\"travelAngle\":-133.0877053812539}}",
                JsonObject.class
        ));
        dummyResponseMap.put(CalcOverlaySettings.PreviewType.BLIND_COORDS, GSON.fromJson(
                "{\"isBlindModeEnabled\":true,\"hasDivine\":false,\"blindResult\":{\"evaluation\":\"HIGHROLL_GOOD\",\"xInNether\":-217.82,\"improveDistance\":8.071372233935255,\"zInNether\":6.88,\"averageDistance\":1086.9952915836398,\"improveDirection\":1.5392211114431098,\"highrollProbability\":0.10072320582001268,\"highrollThreshold\":400}}",
                JsonObject.class
        ));

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
            previewType = CalcOverlaySettings.PreviewType.EYE_THROWS;
            updatePreview();
            previewFrame.setVisible(true);
            previewFrame.requestFocus();
        });
        previewAAOverlayButton.addActionListener(a -> {
            previewType = CalcOverlaySettings.PreviewType.ALL_ADVANCEMENTS;
            updatePreview();
            previewFrame.setVisible(true);
            previewFrame.requestFocus();
        });
        previewBlindCoordsOverlayButton.addActionListener(a -> {
            previewType = CalcOverlaySettings.PreviewType.BLIND_COORDS;
            updatePreview();
            previewFrame.setVisible(true);
            previewFrame.requestFocus();
        });

        showCoordsBasedOnCheckbox.setSelected(settings.onlyShowCurrentDimensionCoords);
        showCoordsBasedOnCheckbox.addActionListener(a -> {
            settings.onlyShowCurrentDimensionCoords = showCoordsBasedOnCheckbox.isSelected();
            CalcOverlaySettings.save();
            updatePreview();
        });

        showDirectionAndDistanceCheckbox.setSelected(settings.showDirectionAndDistance);
        showDirectionAndDistanceCheckbox.addActionListener(a -> {
            settings.showDirectionAndDistance = showDirectionAndDistanceCheckbox.isSelected();
            CalcOverlaySettings.save();
            updatePreview();
        });

        showInfoBarCheckbox.setSelected(settings.showInfoBar);
        showInfoBarCheckbox.addActionListener(a -> {
            settings.showInfoBar = showInfoBarCheckbox.isSelected();
            CalcOverlaySettings.save();
            updatePreview();

            if (settings.showInfoBar) {
                JOptionPane.showMessageDialog(null,
                        "Number of adjustments is an **estimate** because\nNinjabrain Bot API doesn't provide the exact number of adjustments.\n\n" +
                        "It should work correctly for all purposes (<20 adjustments),\njust be aware that it's not 100% perfect.\n\n" +
                        "This will be fixed in Ninjabrain Bot v1.5.2+.",
                        "CalcOverlay - Info Bar", JOptionPane.INFORMATION_MESSAGE);
            }
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

        for (CalcOverlaySettings.AngleDisplay value : CalcOverlaySettings.AngleDisplay.values()) {
            angleDisplayCombobox.addItem(value.getDisplay());
        }
        angleDisplayCombobox.setSelectedItem(settings.angleDisplay.getDisplay());
        angleDisplayCombobox.addActionListener(a -> {
            Jingle.log(Level.INFO, "a");
            settings.angleDisplay = CalcOverlaySettings.AngleDisplay.match((String) angleDisplayCombobox.getSelectedItem());
            CalcOverlaySettings.save();
            updatePreview();
        });

        copyColorKeyButton.addActionListener(a -> {
            StringSelection stringSelection = new StringSelection(CalcOverlayUtil.getColorKey());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            JOptionPane.showMessageDialog(null, "Copied to clipboard.\nSet color similarity to 0.001 in Toolscreen!");
        });

        windowOverlaySetupButton.addActionListener(a -> {
            try {
                Desktop.getDesktop().browse(URI.create("https://github.com/marin774/Jingle-CalcOverlay-Plugin/blob/main/setup.md#toolscreen-window-overlay"));
            } catch (Exception e) {
                log(Level.ERROR, "Failed to open:\n" + ExceptionUtil.toDetailedString(e));
            }
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

        for (CalcOverlaySettings.ClearOverlayTimeUnit value : CalcOverlaySettings.ClearOverlayTimeUnit.values()) {
            clearOverlayCombobox.addItem(value.getDisplay());
        }
        clearOverlayCombobox.setSelectedItem(settings.clearOverlayAfter.getTimeUnit().getDisplay());
        clearOverlayCombobox.addActionListener(a -> {
            settings.clearOverlayAfter.timeUnit = CalcOverlaySettings.ClearOverlayTimeUnit.match((String) clearOverlayCombobox.getSelectedItem());

            updateClearOverlaySeconds();

            CalcOverlaySettings.save();
            updatePreview();
            this.updateGUI();
        });

        clearOverlayTime.setModel(new SpinnerNumberModel(settings.clearOverlayAfter.getAmount(), 1, 60, 1));
        //((JSpinner.DefaultEditor) clearOverlayTime.getEditor()).getTextField().setEditable(false);
        clearOverlayTime.addChangeListener(a -> {
            updateClearOverlaySeconds();
        });

        changeNetherCoordsColorButton.addActionListener(a -> {
            new HSVColorChooser("Nether Coords Color", c -> {
                CalcOverlaySettings.getInstance().netherCoordsColor = c;
                CalcOverlaySettings.save();
                updatePreview();
                updateGUI();
            });
        });

        useNegativeCoordsColor.addActionListener(a -> {
            CalcOverlaySettings.getInstance().negativeCoords.use = useNegativeCoordsColor.isSelected();
            CalcOverlaySettings.save();
            updatePreview();
            updateGUI();
        });

        changeNegativeCoordsColorButton.addActionListener(a -> {
            new HSVColorChooser("Negative Coords Color", c -> {
                CalcOverlaySettings.getInstance().negativeCoords.color = c;
                CalcOverlaySettings.save();
                updatePreview();
                updateGUI();
            });
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

        enableBlindCoordsOverlay.addActionListener(a -> {
            CalcOverlaySettings.getInstance().displayOverlayMap.put(CalcOverlaySettings.PreviewType.BLIND_COORDS, enableBlindCoordsOverlay.isSelected());
            CalcOverlaySettings.save();
            updatePreview();
            //updateGUI();
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
            previewPanel = OverlayUtil.getFinalOverlayPanel(OverlayUtil.getPreviewPanel(previewType, dummyResponseMap.get(previewType)));

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

        clearOverlayTime.setVisible(settings.clearOverlayAfter.getTimeUnit() != CalcOverlaySettings.ClearOverlayTimeUnit.NEVER);
        clearOverlayTime.getParent().revalidate();

        useNegativeCoordsColor.setSelected(CalcOverlaySettings.getInstance().negativeCoords.use);
        useNegativeCoordsColor.setText(CalcOverlaySettings.getInstance().negativeCoords.use ? "Enabled" : "Disabled");
        changeNegativeCoordsColorButton.setEnabled(CalcOverlaySettings.getInstance().negativeCoords.use);

        netherCoordsColorPanel.setBackground(CalcOverlaySettings.getInstance().netherCoordsColor);
        netherCoordsColorPanel.revalidate();

        negativeCoordsColorPanel.setBackground(CalcOverlaySettings.getInstance().negativeCoords.use ? CalcOverlaySettings.getInstance().negativeCoords.color : new Color(0, 0, 0, 0));
        negativeCoordsColorPanel.revalidate();

        enableBlindCoordsOverlay.setSelected(CalcOverlaySettings.PreviewType.BLIND_COORDS.isEnabled());
        enableAllAdvancementsOverlay.setSelected(CalcOverlaySettings.PreviewType.ALL_ADVANCEMENTS.isEnabled());

        setupEyeThrowsPanel();
        setupAARowsPanel();
        setupAAColumnsPanel();

        revalidate();
        repaint();
    }

    private void updateClearOverlaySeconds() {
        CalcOverlaySettings.getInstance().clearOverlayAfter.amount = ((int) clearOverlayTime.getValue());
        CalcOverlaySettings.save();
        NINJABRAIN_BOT_EVENT_SUBSCRIBER.updateClearOverlayTime();
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
                CalcOverlaySettings.save();
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
                CalcOverlaySettings.save();
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
                CalcOverlaySettings.save();
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
                CalcOverlaySettings.save();
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
                CalcOverlaySettings.save();
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
                CalcOverlaySettings.save();
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
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(6, 1, new Insets(5, 5, 5, 5), -1, -1));
        scrollPane1.setViewportView(panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        overlayPositionCombobox = new JComboBox();
        panel2.add(overlayPositionCombobox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Overlay position:");
        label1.setToolTipText("<html>Overlay position in the image. Image is always the same size,<br>and this setting lets you choose which corner overlay anchors to.</html>");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Outline width:");
        panel3.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        outlineWidthSpinner = new JSpinner();
        panel3.add(outlineWidthSpinner, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Clear overlay after:");
        panel4.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearOverlayTime = new JSpinner();
        panel4.add(clearOverlayTime, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel4.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        clearOverlayCombobox = new JComboBox();
        panel4.add(clearOverlayCombobox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(3, 1, new Insets(5, 5, 5, 5), -1, -1));
        panel1.add(panel5, new GridConstraints(3, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Font & Colors", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(panel6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Nether coords color:");
        panel6.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        netherCoordsColorPanel = new JPanel();
        netherCoordsColorPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(netherCoordsColorPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(20, 18), null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel6.add(spacer3, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        changeNetherCoordsColorButton = new JButton();
        changeNetherCoordsColorButton.setText("Change color");
        panel6.add(changeNetherCoordsColorButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 5, 0), -1, -1));
        panel5.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        changeFontButton = new JButton();
        changeFontButton.setText("Change font");
        panel7.add(changeFontButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fontLabel = new JLabel();
        fontLabel.setText("<font>");
        panel7.add(fontLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Font:");
        panel7.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel7.add(spacer4, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(panel8, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Negative coords color:");
        panel8.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        negativeCoordsColorPanel = new JPanel();
        negativeCoordsColorPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel8.add(negativeCoordsColorPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(20, 18), null, 0, false));
        changeNegativeCoordsColorButton = new JButton();
        changeNegativeCoordsColorButton.setText("Change color");
        panel8.add(changeNegativeCoordsColorButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel8.add(spacer5, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        useNegativeCoordsColor = new JCheckBox();
        useNegativeCoordsColor.setText("Disabled");
        panel8.add(useNegativeCoordsColor, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setHorizontalScrollBarPolicy(31);
        settingsPane.addTab("Eye Throws Overlay", scrollPane2);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(8, 1, new Insets(5, 5, 5, 5), -1, -1));
        scrollPane2.setViewportView(panel9);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel9.add(panel10, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        shownMeasurementsSpinner = new JSpinner();
        panel10.add(shownMeasurementsSpinner, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Shown measurements:");
        panel10.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel9.add(panel11, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Overworld coords:");
        panel11.add(label8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        overworldCoordsTypeCombobox = new JComboBox();
        panel11.add(overworldCoordsTypeCombobox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showCoordsBasedOnCheckbox = new JCheckBox();
        showCoordsBasedOnCheckbox.setText("Show Overworld/Nether coords based on dimension");
        showCoordsBasedOnCheckbox.setToolTipText("<html>\nIf enabled, only overworld coords will be shown while you're in overworld,<br>and nether coords will be hidden.<br> Once you F3+C in the nether, only nether coords will be shown,<br>and overworld coords will be hidden.\n<br><br>\nIf disabled, both coords will always be shown.\n</html>");
        panel9.add(showCoordsBasedOnCheckbox, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        panel9.add(panel12, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel12.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Columns", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        eyeThrowsColumnsPanel = new JPanel();
        eyeThrowsColumnsPanel.setLayout(new GridBagLayout());
        panel12.add(eyeThrowsColumnsPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        previewEyeThrowsOverlayButton = new JButton();
        previewEyeThrowsOverlayButton.setLabel("Preview Eye Throws Overlay");
        previewEyeThrowsOverlayButton.setText("Preview Eye Throws Overlay");
        panel9.add(previewEyeThrowsOverlayButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel9.add(spacer6, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel9.add(panel13, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Angle display:");
        panel13.add(label9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        angleDisplayCombobox = new JComboBox();
        panel13.add(angleDisplayCombobox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showInfoBarCheckbox = new JCheckBox();
        showInfoBarCheckbox.setText("Show information bar (warnings, angle adjustments)");
        panel9.add(showInfoBarCheckbox, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane3 = new JScrollPane();
        scrollPane3.setHorizontalScrollBarPolicy(31);
        scrollPane3.setName("");
        settingsPane.addTab("Blind Coords Overlay", scrollPane3);
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(4, 1, new Insets(5, 5, 5, 5), -1, -1));
        scrollPane3.setViewportView(panel14);
        previewBlindCoordsOverlayButton = new JButton();
        previewBlindCoordsOverlayButton.setLabel("Preview Blind Coords Overlay");
        previewBlindCoordsOverlayButton.setText("Preview Blind Coords Overlay");
        panel14.add(previewBlindCoordsOverlayButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel14.add(spacer7, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        enableBlindCoordsOverlay = new JCheckBox();
        enableBlindCoordsOverlay.setText("Enable Blind Coords on Overlay");
        panel14.add(enableBlindCoordsOverlay, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showDirectionAndDistanceCheckbox = new JCheckBox();
        showDirectionAndDistanceCheckbox.setText("Show direction and distance to good blind coords");
        panel14.add(showDirectionAndDistanceCheckbox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane4 = new JScrollPane();
        scrollPane4.setHorizontalScrollBarPolicy(31);
        settingsPane.addTab("All Advancements Overlay", scrollPane4);
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(4, 3, new Insets(5, 5, 5, 5), -1, -1));
        scrollPane4.setViewportView(panel15);
        previewAAOverlayButton = new JButton();
        previewAAOverlayButton.setText("Preview All Advancements Overlay");
        panel15.add(previewAAOverlayButton, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        panel15.add(spacer8, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel15.add(panel16, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        panel16.add(panel17, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel17.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Columns", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        aaColumnsPanel = new JPanel();
        aaColumnsPanel.setLayout(new GridBagLayout());
        panel17.add(aaColumnsPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        panel16.add(panel18, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel18.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Rows", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        aaRowsPanel = new JPanel();
        aaRowsPanel.setLayout(new GridBagLayout());
        panel18.add(aaRowsPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer9 = new Spacer();
        panel16.add(spacer9, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        enableAllAdvancementsOverlay = new JCheckBox();
        enableAllAdvancementsOverlay.setText("Enable All Advancements on Overlay");
        panel15.add(enableAllAdvancementsOverlay, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer10 = new Spacer();
        mainPanel.add(spacer10, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel19 = new JPanel();
        panel19.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 10, 0), -1, 10));
        mainPanel.add(panel19, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel20 = new JPanel();
        panel20.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel19.add(panel20, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Toolscreen window:");
        panel20.add(label10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        copyColorKeyButton = new JButton();
        copyColorKeyButton.setText("Copy Color Key");
        panel20.add(copyColorKeyButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        windowOverlaySetupButton = new JButton();
        windowOverlaySetupButton.setText("Setup Tutorial");
        panel20.add(windowOverlaySetupButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel21 = new JPanel();
        panel21.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel19.add(panel21, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        copyScriptPathButton = new JButton();
        copyScriptPathButton.setText("Copy Script Path");
        panel21.add(copyScriptPathButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        OBSScriptSetupButton = new JButton();
        OBSScriptSetupButton.setText("OBS Script Setup");
        panel21.add(OBSScriptSetupButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("OBS Script:");
        panel21.add(label11, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
