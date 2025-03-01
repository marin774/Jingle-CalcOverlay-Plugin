package me.marin.calcoverlay.gui;

import com.google.gson.JsonObject;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
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
import java.util.Locale;

import static me.marin.calcoverlay.CalcOverlay.*;
import static me.marin.calcoverlay.ninjabrainapi.NinjabrainBotEventSubscriber.GSON;

public class ConfigGUI extends JPanel {

    public JPanel mainPanel;
    public JCheckBox enabledCheckbox;

    private JCheckBox showCoordsBasedOnCheckBox;
    private JComboBox<String> overworldCoordsTypeCombobox;
    private JPanel columnsPanel;
    private JComboBox<String> overlayPositionCombobox;
    private JButton copyFilePathButton;
    private JCheckBox showAngleDirectionCheckbox;
    private JButton checkForUpdatesButton;
    private JPanel enabledPanel;
    private JButton showDummyMeasurementButton;
    private JSpinner shownMeasurementsSpinner;
    private JLabel fontLabel;
    private JButton changeFontButton;

    private JFrame testFrame;
    private JPanel testPanel;
    private final JsonObject dummyMeasurement = GSON.fromJson(
            "{\"eyeThrows\":[{\"xInOverworld\":1199.63,\"angleWithoutCorrection\":-161.14926034190884,\"zInOverworld\":-139.09,\"angle\":-161.13926034190885,\"correction\":0.01,\"error\":0.0014111816929869292,\"type\":\"NORMAL\"}],\"resultType\":\"TRIANGULATION\",\"playerPosition\":{\"xInOverworld\":1199.63,\"isInOverworld\":true,\"isInNether\":false,\"horizontalAngle\":-161.15,\"zInOverworld\":-139.09},\"predictions\":[{\"overworldDistance\":523.3899550048701,\"certainty\":0.5147413124532876,\"chunkX\":85,\"chunkZ\":-40},{\"overworldDistance\":1216.5659558774444,\"certainty\":0.2674146623130985,\"chunkX\":99,\"chunkZ\":-81},{\"overworldDistance\":1859.1560464361241,\"certainty\":0.1252834863035146,\"chunkX\":112,\"chunkZ\":-119},{\"overworldDistance\":1909.7519223710706,\"certainty\":0.07908349382318092,\"chunkX\":113,\"chunkZ\":-122},{\"overworldDistance\":1165.9697787678717,\"certainty\":0.012493314849953712,\"chunkX\":98,\"chunkZ\":-78}]}",
            JsonObject.class
    );

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
                if (testFrame.isVisible()) {
                    testFrame.setVisible(false);
                }
                NINJABRAIN_BOT_EVENT_SUBSCRIBER.disconnect();
            }

            settings.calcOverlayEnabled = enabledCheckbox.isSelected();
            CalcOverlaySettings.save();

            enabledPanel.setVisible(settings.calcOverlayEnabled);
        });

        enabledPanel.setVisible(settings.calcOverlayEnabled);

        testFrame = new JFrame();

        testFrame.setTitle("Test Overlay");
        testFrame.setResizable(false);
        testFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        updateTestGUI();
        showDummyMeasurementButton.addActionListener(a -> {
            testFrame.setVisible(true);
            testFrame.requestFocus();
        });

        showAngleDirectionCheckbox.setSelected(settings.showAngleDirection);
        showAngleDirectionCheckbox.addActionListener(a -> {
            settings.showAngleDirection = showAngleDirectionCheckbox.isSelected();
            CalcOverlaySettings.save();
            updateTestGUI();
        });

        showCoordsBasedOnCheckBox.setSelected(settings.onlyShowCurrentDimensionCoords);
        showCoordsBasedOnCheckBox.addActionListener(a -> {
            settings.onlyShowCurrentDimensionCoords = showCoordsBasedOnCheckBox.isSelected();
            CalcOverlaySettings.save();
            updateTestGUI();
        });


        for (CalcOverlaySettings.OverworldsCoords value : CalcOverlaySettings.OverworldsCoords.values()) {
            overworldCoordsTypeCombobox.addItem(value.getDisplay());
        }
        overworldCoordsTypeCombobox.setSelectedItem(settings.overworldCoords.getDisplay());
        overworldCoordsTypeCombobox.addActionListener(a -> {
            settings.overworldCoords = CalcOverlaySettings.OverworldsCoords.match((String) overworldCoordsTypeCombobox.getSelectedItem());
            CalcOverlaySettings.save();
            updateTestGUI();
        });

        for (CalcOverlaySettings.Position value : CalcOverlaySettings.Position.values()) {
            overlayPositionCombobox.addItem(value.getDisplay());
        }
        overlayPositionCombobox.setSelectedItem(settings.overlayPosition.getDisplay());
        overlayPositionCombobox.addActionListener(a -> {
            settings.overlayPosition = CalcOverlaySettings.Position.match((String) overlayPositionCombobox.getSelectedItem());
            CalcOverlaySettings.save();
            updateTestGUI();
        });

        copyFilePathButton.addActionListener(a -> {
            StringSelection stringSelection = new StringSelection(OVERLAY_PATH.toAbsolutePath().toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            JOptionPane.showMessageDialog(null, "Copied to clipboard.");
        });

        shownMeasurementsSpinner.setModel(new SpinnerNumberModel(settings.shownMeasurements, 1, 5, 1));
        ((JSpinner.DefaultEditor) shownMeasurementsSpinner.getEditor()).getTextField().setEditable(false);
        shownMeasurementsSpinner.addChangeListener(a -> {
            settings.shownMeasurements = (int) shownMeasurementsSpinner.getValue();
            CalcOverlaySettings.save();
            updateTestGUI();
        });

        changeFontButton.addActionListener(a -> {
            try {
                FontDialog dialog = new FontDialog(JingleGUI.get(), "CalcOverlay Font Chooser", true);
                dialog.setSelectedFont(CalcOverlayUtil.getFont());
                dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                dialog.setVisible(true);
                if (!dialog.isCancelSelected()) {
                    Font font = dialog.getSelectedFont();
                    settings.fontData = new CalcOverlaySettings.FontData(font.getName(), font.getStyle(), font.getSize());
                    CalcOverlaySettings.save();
                    updateTestGUI();
                    updateGUI();
                }
            } catch (Exception e) {
                log(Level.ERROR, "Exception while choosing font:\n" + ExceptionUtil.toDetailedString(e));
            }
        });

    }

    private void updateTestGUI() {
        if (testPanel != null) {
            testFrame.remove(testPanel);
        }
        testPanel = OverlayUtil.getFinalOverlayPanel(OverlayUtil.getPanelForStronghold(dummyMeasurement));
        // OverlayUtil.writeImage(OverlayUtil.getPanelForStronghold(dummyMeasurement));
        testFrame.add(testPanel);
        testFrame.setSize(testPanel.getSize());
        testFrame.revalidate();
        testFrame.repaint();
    }

    public void updateGUI() {
        CalcOverlaySettings settings = CalcOverlaySettings.getInstance();

        Font font = CalcOverlayUtil.getFont();
        String style = font.isPlain() ? "Plain, " : ((font.isBold() ? "Bold, " : "") + (font.isItalic() ? "Italic, " : ""));
        fontLabel.setText(font.getFamily() + ", " + style + font.getSize() + "pt");

        columnsPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        final int hGap = 5;
        final int vGap = 5;
        gbc.gridy = 0;

        gbc.insets = new Insets(0, hGap, vGap, hGap);

        JLabel showColumnLabel = new JLabel("<html>Show<br>column<html>");
        showColumnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        columnsPanel.add(showColumnLabel, gbc);

        JLabel showIconLabel = new JLabel("Header");
        showIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        columnsPanel.add(showIconLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(vGap, 0, vGap, hGap);
        for (CalcOverlaySettings.ColumnData columnData : settings.columnData) {
            JLabel columnNameLabel = new JLabel(columnData.getColumnType().getConfigDisplay());
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.EAST;
            columnsPanel.add(columnNameLabel, gbc);

            JCheckBox showColumnCheckbox = new JCheckBox();
            showColumnCheckbox.setSelected(columnData.isVisible());
            showColumnCheckbox.addActionListener(a -> {
                columnData.setVisible(showColumnCheckbox.isSelected());
                CalcOverlaySettings.save();
                updateTestGUI();
            });
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            columnsPanel.add(showColumnCheckbox, gbc);

            JComboBox<String> headerCombobox = newHeaderCombobox(columnData);
            gbc.gridx = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            columnsPanel.add(headerCombobox, gbc);

            JButton moveUpButton = new JButton("Move up");
            gbc.gridx = 3;
            gbc.anchor = GridBagConstraints.CENTER;
            moveUpButton.addActionListener(a -> {
                int idx = settings.columnData.indexOf(columnData);
                settings.columnData.remove(idx);
                settings.columnData.add(idx - 1, columnData);
                updateGUI();
                updateTestGUI();
            });
            moveUpButton.setEnabled(gbc.gridy > 1);
            columnsPanel.add(moveUpButton, gbc);

            JButton moveDownButton = new JButton("Move down");
            gbc.gridx = 4;
            gbc.anchor = GridBagConstraints.CENTER;
            moveDownButton.addActionListener(a -> {
                int idx = settings.columnData.indexOf(columnData);
                settings.columnData.remove(idx);
                settings.columnData.add(idx + 1, columnData);
                updateGUI();
                updateTestGUI();
            });
            columnsPanel.add(moveDownButton, gbc);
            moveDownButton.setEnabled(gbc.gridy < settings.columnData.size());

            gbc.gridy += 1;
        }

        revalidate();
        repaint();
    }

    private JComboBox<String> newHeaderCombobox(CalcOverlaySettings.ColumnData columnData) {
        JComboBox<String> headerCombobox = new JComboBox<>();
        for (CalcOverlaySettings.HeaderRow value : CalcOverlaySettings.HeaderRow.values()) {
            headerCombobox.addItem(value.getDisplay());
        }
        headerCombobox.setSelectedItem(columnData.getHeaderRow().getDisplay());
        headerCombobox.addActionListener(a -> {
            columnData.setHeaderRow(CalcOverlaySettings.HeaderRow.match((String) headerCombobox.getSelectedItem()));
            CalcOverlaySettings.save();
            updateTestGUI();
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
        mainPanel.setLayout(new GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        enabledCheckbox = new JCheckBox();
        enabledCheckbox.setText("Enable overlay");
        mainPanel.add(enabledCheckbox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        enabledPanel = new JPanel();
        enabledPanel.setLayout(new GridLayoutManager(8, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(enabledPanel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        enabledPanel.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        overlayPositionCombobox = new JComboBox();
        panel1.add(overlayPositionCombobox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Overlay position:");
        label1.setToolTipText("<html>Overlay position in the image. Image is always the same size,<br>and this setting lets you choose which corner overlay anchors to.</html>");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        enabledPanel.add(panel2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Overworld coords:");
        panel2.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        overworldCoordsTypeCombobox = new JComboBox();
        panel2.add(overworldCoordsTypeCombobox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showAngleDirectionCheckbox = new JCheckBox();
        showAngleDirectionCheckbox.setText("Show angle direction");
        enabledPanel.add(showAngleDirectionCheckbox, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showCoordsBasedOnCheckBox = new JCheckBox();
        showCoordsBasedOnCheckBox.setText("Show coords based on dimension");
        showCoordsBasedOnCheckBox.setToolTipText("<html>\nIf enabled, only overworld coords will be shown while you're in overworld,<br>and nether coords will be hidden.<br> Once you F3+C in the nether, only nether coords will be shown,<br>and overworld coords will be hidden.\n<br><br>\nIf disabled, both coords will always be shown.\n</html>");
        enabledPanel.add(showCoordsBasedOnCheckBox, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        enabledPanel.add(panel3, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        columnsPanel = new JPanel();
        columnsPanel.setLayout(new GridBagLayout());
        panel3.add(columnsPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        enabledPanel.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        shownMeasurementsSpinner = new JSpinner();
        panel4.add(shownMeasurementsSpinner, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Shown measurements:");
        panel4.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        enabledPanel.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        showDummyMeasurementButton = new JButton();
        Font showDummyMeasurementButtonFont = this.$$$getFont$$$(null, -1, 16, showDummyMeasurementButton.getFont());
        if (showDummyMeasurementButtonFont != null) showDummyMeasurementButton.setFont(showDummyMeasurementButtonFont);
        showDummyMeasurementButton.setLabel("Preview Overlay");
        showDummyMeasurementButton.setText("Preview Overlay");
        panel5.add(showDummyMeasurementButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        Font label4Font = this.$$$getFont$$$(null, Font.BOLD, 20, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        label4.setText("Overlay Settings");
        panel5.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        enabledPanel.add(panel6, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Font:");
        panel6.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        changeFontButton = new JButton();
        changeFontButton.setText("Change font");
        panel6.add(changeFontButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fontLabel = new JLabel();
        fontLabel.setText("<font>");
        panel6.add(fontLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        mainPanel.add(separator1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkForUpdatesButton = new JButton();
        checkForUpdatesButton.setText("Check for updates");
        mainPanel.add(checkForUpdatesButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        copyFilePathButton = new JButton();
        copyFilePathButton.setText("Copy Image Path");
        panel7.add(copyFilePathButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Overlay image for OBS:");
        panel7.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
