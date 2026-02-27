package me.marin.calcoverlay;

import com.google.common.io.Resources;
import me.marin.calcoverlay.gui.ConfigGUI;
import me.marin.calcoverlay.io.AllAdvancementsSettings;
import me.marin.calcoverlay.io.CalcOverlaySettings;
import me.marin.calcoverlay.ninjabrainapi.NinjabrainBotEventSubscriber;
import me.marin.calcoverlay.util.CalcOverlayUtil;
import me.marin.calcoverlay.util.OverlayUtil;
import me.marin.calcoverlay.util.UpdateUtil;
import me.marin.calcoverlay.util.VersionUtil;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.JingleAppLaunch;
import xyz.duncanruns.jingle.gui.JingleGUI;
import xyz.duncanruns.jingle.plugin.PluginManager;
import xyz.duncanruns.jingle.util.ExceptionUtil;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Objects;

import static me.marin.calcoverlay.util.VersionUtil.CURRENT_VERSION;
import static me.marin.calcoverlay.util.VersionUtil.version;

public class CalcOverlay {

    public static final Path CALC_OVERLAY_FOLDER_PATH = Jingle.FOLDER.resolve("calc-overlay-plugin");

    public static final Path PLUGINS_PATH = Jingle.FOLDER.resolve("plugins");
    public static final Path SETTINGS_PATH = CALC_OVERLAY_FOLDER_PATH.resolve("settings.json");

    public static final Path OVERLAY_PATH = CALC_OVERLAY_FOLDER_PATH.resolve("calc-overlay.png");
    public static final Path OBS_LINK_STATE_PATH = CALC_OVERLAY_FOLDER_PATH.resolve("obs-link-state");
    public static final Path OBS_SCRIPT_PATH = CALC_OVERLAY_FOLDER_PATH.resolve("calc-overlay-obs-link.lua");

    public static final NinjabrainBotEventSubscriber NINJABRAIN_BOT_EVENT_SUBSCRIBER = new NinjabrainBotEventSubscriber();

    public static void main(String[] args) throws IOException {
        JingleAppLaunch.launchWithDevPlugin(args, PluginManager.JinglePluginData.fromString(
                Resources.toString(Resources.getResource(CalcOverlay.class, "/jingle.plugin.json"), Charset.defaultCharset())
        ), CalcOverlay::initialize);
    }

    public static void log(Level level, String message) {
        Jingle.log(level, "(CalcOverlay) " + message);
    }

    public static void initialize() {
        CalcOverlay.log(Level.INFO, "Running CalcOverlay Plugin v" + CURRENT_VERSION + "!");

        OverlayUtil.loadImagesAndStyles();
        CALC_OVERLAY_FOLDER_PATH.toFile().mkdirs();
        createObsLinkStateFile();
        createObsScriptFile();

        CalcOverlaySettings.load();
        VersionUtil.Version version = version(CalcOverlaySettings.getInstance().version);
        if (version.isOlderThan(CURRENT_VERSION)) {
            updateData(version);
        }

        createOverlayFile();

        NINJABRAIN_BOT_EVENT_SUBSCRIBER.startConnectJob();
        NINJABRAIN_BOT_EVENT_SUBSCRIBER.updateClearOverlayTime();

        VersionUtil.deleteOldVersionJars();
        UpdateUtil.checkForUpdatesAndUpdate(true);

        JPanel configGUI = new ConfigGUI().mainPanel;
        JingleGUI.addPluginTab("Calc Overlay", configGUI);
    }

    public static void updateData(VersionUtil.Version fromVersion) {
        log(Level.INFO, "Updating data from version " + fromVersion + ".");
        CalcOverlaySettings instance = CalcOverlaySettings.getInstance();
        if (fromVersion.isOlderThan(version("1.1.0"))) {
            CalcOverlaySettings.getInstance().shownMeasurements = 3;
            log(Level.INFO, "[1.1.0] 'shown measurements' set to 3");
        }

        if (fromVersion.isOlderThan(version("1.3.0"))) {
            for (CalcOverlaySettings.ColumnData cd : CalcOverlaySettings.getInstance().columnData) {
                if (cd.isShowIcon()) {
                    cd.setHeaderRow(CalcOverlaySettings.HeaderRow.ICON);
                } else {
                    cd.setHeaderRow(CalcOverlaySettings.HeaderRow.NOTHING);
                }
            }
        }

        if (fromVersion.isOlderThan(version("2.0.0"))) {
            CalcOverlaySettings.getInstance().aaSettings = AllAdvancementsSettings.loadDefaultSettings();
            CalcOverlaySettings.getInstance().outlineWidth = 3;
            if (CalcOverlaySettings.getInstance().fontData == null) {
                CalcOverlaySettings.getInstance().fontData = new CalcOverlaySettings.FontData("Calibri", 48);
            }

            Jingle.log(Level.INFO, "\n\n\t\tCALC OVERLAY v2.0.0 UPDATE\n\n" +
                    "\tCalcOverlay v2.0.0 now uses new 'Calc Overlay' OBS source.\n" +
                    "\tYou should no longer use an Image source in OBS.\n\n" +
                    "\tAdd the script to your OBS (script path can be found in Plugins -> Calc Overlay), then add 'Calc Overlay' source to your scene in OBS.\n\n" +
                    "\tFor a step-by-step setup, go here: https://github.com/marin774/Jingle-CalcOverlay-Plugin/blob/main/setup.md#setup-obs-script--overlay\n\n.");

            CalcOverlayUtil.runAsync("notify", () -> {
                JOptionPane.showMessageDialog(null,
                        "CalcOverlay v2.0.0 now uses new 'Calc Overlay' OBS source.\n" +
                                "You should no longer use an Image source in OBS.\n\n" +
                                "** Check Jingle Logs or Github for more information. **",
                        "CalcOverlay v2.0.0", JOptionPane.INFORMATION_MESSAGE);
            });
        }

        if (fromVersion.isOlderThan(version("2.2.0"))) {
            instance.clearOverlayAfter = new CalcOverlaySettings.ClearOverlayAfter(CalcOverlaySettings.ClearOverlayTimeUnit.NEVER, 1);
            instance.netherCoordsColor = OverlayUtil.DEFAULT_NETHER_COORDS_COLOR;
            instance.negativeCoords = new CalcOverlaySettings.NegativeCoords(true, OverlayUtil.DEFAULT_NEGATIVE_COORDS_COLOR);

            instance.displayOverlayMap = new HashMap<>();
            instance.displayOverlayMap.put(CalcOverlaySettings.PreviewType.EYE_THROWS, true);
            instance.displayOverlayMap.put(CalcOverlaySettings.PreviewType.ALL_ADVANCEMENTS, true);
            instance.displayOverlayMap.put(CalcOverlaySettings.PreviewType.BLIND_COORDS, true);
        }

        if (fromVersion.isOlderThan(version("2.3.0"))) {
            instance.angleDisplay = instance.showAngleDirection ? CalcOverlaySettings.AngleDisplay.ALL : CalcOverlaySettings.AngleDisplay.ONLY_ANGLE;
            instance.showInfoBar = false;
        }

        if (fromVersion.isOlderThan(version("2.4.0"))) {
            instance.enableWindowOverlay = false;
            instance.showDirectionAndDistance = false;
        }

        CalcOverlaySettings.getInstance().version = CURRENT_VERSION.toString();
        CalcOverlaySettings.save();
        log(Level.INFO, "Updated data to v" + CURRENT_VERSION);
    }

    private static void createObsLinkStateFile() {
        try {
            OBS_LINK_STATE_PATH.toFile().createNewFile();
        } catch (Exception e) {
            log(Level.ERROR, "Failed to create obs-link-state file:\n" + ExceptionUtil.toDetailedString(e));
        }
    }

    private static void createObsScriptFile() {
        try {
            Files.copy(Objects.requireNonNull(CalcOverlay.class.getResourceAsStream("/calc-overlay-obs-link.lua")), OBS_SCRIPT_PATH, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log(Level.ERROR, "Failed to write calc-overlay-obs-link.lua:\n" + ExceptionUtil.toDetailedString(e));
        }
    }

    private static void createOverlayFile() {
        try {
            OverlayUtil.writeImage(OverlayUtil.empty());
        } catch (Exception e) {
            log(Level.INFO, "Failed to create overlay image:\n" + ExceptionUtil.toDetailedString(e));
        }
    }

}
