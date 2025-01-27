package me.marin.calcoverlay.gui;

import com.google.gson.JsonObject;
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
    private JTextPane folderPathTextPane;
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

        folderPathTextPane.setText(OVERLAY_PATH.toAbsolutePath().toString());
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
        final int vGap = 3;
        gbc.gridy = 0;

        gbc.insets = new Insets(0, hGap, vGap, hGap);

        JLabel showColumnLabel = new JLabel("<html>Show<br>column<html>");
        showColumnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        columnsPanel.add(showColumnLabel, gbc);

        JLabel showIconLabel = new JLabel("<html>Show<br>icon<html>");
        showIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        columnsPanel.add(showIconLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(vGap, 0, vGap, hGap);
        for (CalcOverlaySettings.ColumnData columnData : settings.columnData) {
            JLabel columnNameLabel = new JLabel(columnData.getColumnType().getDisplay());
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

            JCheckBox showIconCheckbox = new JCheckBox();
            showIconCheckbox.setSelected(columnData.isShowIcon());
            showIconCheckbox.addActionListener(a -> {
                columnData.setShowIcon(showIconCheckbox.isSelected());
                CalcOverlaySettings.save();
                updateTestGUI();
            });
            gbc.gridx = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            columnsPanel.add(showIconCheckbox, gbc);

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

    public static void main(String[] args) {

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
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        enabledCheckbox = new JCheckBox();
        enabledCheckbox.setText("Enable overlay");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(enabledCheckbox, gbc);
        checkForUpdatesButton = new JButton();
        checkForUpdatesButton.setText("Check for updates");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(checkForUpdatesButton, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(panel1, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("OBS Overlay file:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label1, gbc);
        folderPathTextPane = new JTextPane();
        folderPathTextPane.setAlignmentX(0.0f);
        folderPathTextPane.setEditable(false);
        folderPathTextPane.setMaximumSize(new Dimension(200, 2147483647));
        folderPathTextPane.setMinimumSize(new Dimension(200, 22));
        folderPathTextPane.setPreferredSize(new Dimension(200, 22));
        folderPathTextPane.setText("folder path");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 5);
        panel1.add(folderPathTextPane, gbc);
        copyFilePathButton = new JButton();
        copyFilePathButton.setText("Copy Image Path");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(copyFilePathButton, gbc);
        enabledPanel = new JPanel();
        enabledPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(enabledPanel, gbc);
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$(null, Font.BOLD, 20, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("Overlay settings");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        enabledPanel.add(label2, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        enabledPanel.add(panel2, gbc);
        overlayPositionCombobox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(overlayPositionCombobox, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Overlay position:");
        label3.setToolTipText("<html>Overlay position in the image. Image is always the same size,<br>and this setting lets you choose which corner overlay anchors to.</html>");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 5);
        panel2.add(label3, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        enabledPanel.add(panel3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Overworld coords:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 5);
        panel3.add(label4, gbc);
        overworldCoordsTypeCombobox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(overworldCoordsTypeCombobox, gbc);
        showAngleDirectionCheckbox = new JCheckBox();
        showAngleDirectionCheckbox.setText("Show angle direction");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        enabledPanel.add(showAngleDirectionCheckbox, gbc);
        showCoordsBasedOnCheckBox = new JCheckBox();
        showCoordsBasedOnCheckBox.setText("Show coords based on dimension");
        showCoordsBasedOnCheckBox.setToolTipText("<html>\nIf enabled, only overworld coords will be shown while you're in overworld,<br>and nether coords will be hidden.<br> Once you F3+C in the nether, only nether coords will be shown,<br>and overworld coords will be hidden.\n<br><br>\nIf disabled, both coords will always be shown.\n</html>");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        enabledPanel.add(showCoordsBasedOnCheckBox, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.BOTH;
        enabledPanel.add(panel4, gbc);
        columnsPanel = new JPanel();
        columnsPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(columnsPanel, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        enabledPanel.add(panel5, gbc);
        shownMeasurementsSpinner = new JSpinner();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(shownMeasurementsSpinner, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Shown measurements:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 5);
        panel5.add(label5, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 5);
        enabledPanel.add(panel6, gbc);
        panel6.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        showDummyMeasurementButton = new JButton();
        showDummyMeasurementButton.setLabel("Open test overlay");
        showDummyMeasurementButton.setText("Open test overlay");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 5);
        panel6.add(showDummyMeasurementButton, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        enabledPanel.add(panel7, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Font:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 5);
        panel7.add(label6, gbc);
        changeFontButton = new JButton();
        changeFontButton.setText("Change font");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 5);
        panel7.add(changeFontButton, gbc);
        fontLabel = new JLabel();
        fontLabel.setText("<font>");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 5);
        panel7.add(fontLabel, gbc);
        final JSeparator separator1 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(separator1, gbc);
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
