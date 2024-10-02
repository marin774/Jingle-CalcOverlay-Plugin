package me.marin.calcoverlay.gui;

import me.marin.calcoverlay.io.CalcOverlaySettings;
import me.marin.calcoverlay.util.OverlayUtil;
import me.marin.calcoverlay.util.UpdateUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import static me.marin.calcoverlay.CalcOverlay.*;

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

    public ConfigGUI() {
        CalcOverlaySettings settings = CalcOverlaySettings.getInstance();

        add(mainPanel);

        updateGUI();

        checkForUpdatesButton.addActionListener(a -> {
            UpdateUtil.checkForUpdatesAndUpdate(false);
        });

        enabledCheckbox.setSelected(settings.calcOverlayEnabled);
        enabledCheckbox.addActionListener(a -> {
            settings.calcOverlayEnabled = enabledCheckbox.isSelected();
            CalcOverlaySettings.save();

            if (settings.calcOverlayEnabled) {
                if (!NINJABRAIN_BOT_EVENT_SUBSCRIBER.ping()) {
                    JOptionPane.showMessageDialog(null, "Couldn't connect to Ninjabrain Bot API. Make sure that Ninjabrain Bot is open, and API is enabled in it's settings.");
                    return;
                }

                NINJABRAIN_BOT_EVENT_SUBSCRIBER.connect();
            } else {
                OverlayUtil.empty();
                NINJABRAIN_BOT_EVENT_SUBSCRIBER.disconnect();
            }
        });

        showAngleDirectionCheckbox.setSelected(settings.showAngleDirection);
        showAngleDirectionCheckbox.addActionListener(a -> {
            settings.showAngleDirection = showAngleDirectionCheckbox.isSelected();
            CalcOverlaySettings.save();
            updateImage();
        });

        showCoordsBasedOnCheckBox.setSelected(settings.onlyShowCurrentDimensionCoords);
        showCoordsBasedOnCheckBox.addActionListener(a -> {
            settings.onlyShowCurrentDimensionCoords = showCoordsBasedOnCheckBox.isSelected();
            CalcOverlaySettings.save();
            updateImage();
        });


        for (CalcOverlaySettings.OverworldsCoords value : CalcOverlaySettings.OverworldsCoords.values()) {
            overworldCoordsTypeCombobox.addItem(value.getDisplay());
        }
        overworldCoordsTypeCombobox.setSelectedItem(settings.overworldCoords.getDisplay());
        overworldCoordsTypeCombobox.addActionListener(a -> {
            settings.overworldCoords = CalcOverlaySettings.OverworldsCoords.match((String) overworldCoordsTypeCombobox.getSelectedItem());
            CalcOverlaySettings.save();
            updateImage();
        });

        for (CalcOverlaySettings.Position value : CalcOverlaySettings.Position.values()) {
            overlayPositionCombobox.addItem(value.getDisplay());
        }
        overlayPositionCombobox.setSelectedItem(settings.overlayPosition.getDisplay());
        overlayPositionCombobox.addActionListener(a -> {
            settings.overlayPosition = CalcOverlaySettings.Position.match((String) overlayPositionCombobox.getSelectedItem());
            CalcOverlaySettings.save();
            updateImage();
        });

        folderPathTextPane.setText(OVERLAY_PATH.toAbsolutePath().toString());
        copyFilePathButton.addActionListener(a -> {
            StringSelection stringSelection = new StringSelection(OVERLAY_PATH.toAbsolutePath().toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            JOptionPane.showMessageDialog(null, "Copied to clipboard.");
        });
    }

    private void updateImage() {
        NINJABRAIN_BOT_EVENT_SUBSCRIBER.showDummyMeasurement();
    }

    public void updateGUI() {
        CalcOverlaySettings settings = CalcOverlaySettings.getInstance();

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
                updateImage();
            });
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            columnsPanel.add(showColumnCheckbox, gbc);

            JCheckBox showIconCheckbox = new JCheckBox();
            showIconCheckbox.setSelected(columnData.isShowIcon());
            showIconCheckbox.addActionListener(a -> {
                columnData.setShowIcon(showIconCheckbox.isSelected());
                CalcOverlaySettings.save();
                updateImage();
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
                updateImage();
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
                updateImage();
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
        showCoordsBasedOnCheckBox = new JCheckBox();
        showCoordsBasedOnCheckBox.setText("Show coords based on dimension");
        showCoordsBasedOnCheckBox.setToolTipText("<html>\nIf enabled, only overworld coords will be shown while you're in overworld,<br>and nether coords will be hidden.<br> Once you F3+C in the nether, only nether coords will be shown,<br>and overworld coords will be hidden.\n<br><br>\nIf disabled, both coords will always be shown.\n</html>");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(showCoordsBasedOnCheckBox, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(panel1, gbc);
        columnsPanel = new JPanel();
        columnsPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(columnsPanel, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(panel2, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Overworld coords:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 5);
        panel2.add(label1, gbc);
        overworldCoordsTypeCombobox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(overworldCoordsTypeCombobox, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(panel3, gbc);
        overlayPositionCombobox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(overlayPositionCombobox, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Overlay position:");
        label2.setToolTipText("<html>Overlay position in the image. Image is always the same size,<br>and this setting lets you choose which corner overlay anchors to.</html>");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 5);
        panel3.add(label2, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 30, 0);
        mainPanel.add(panel4, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("OBS Overlay file:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(label3, gbc);
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
        panel4.add(folderPathTextPane, gbc);
        copyFilePathButton = new JButton();
        copyFilePathButton.setText("Copy Image Path");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(copyFilePathButton, gbc);
        showAngleDirectionCheckbox = new JCheckBox();
        showAngleDirectionCheckbox.setText("Show angle direction");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(showAngleDirectionCheckbox, gbc);
        checkForUpdatesButton = new JButton();
        checkForUpdatesButton.setText("Check for updates");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(checkForUpdatesButton, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
