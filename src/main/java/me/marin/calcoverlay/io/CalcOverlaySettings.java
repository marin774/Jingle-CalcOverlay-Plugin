package me.marin.calcoverlay.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import me.marin.calcoverlay.util.OverlayUtil;
import me.marin.calcoverlay.util.VersionUtil;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.util.ExceptionUtil;
import xyz.duncanruns.jingle.util.FileUtil;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static me.marin.calcoverlay.CalcOverlay.SETTINGS_PATH;
import static me.marin.calcoverlay.CalcOverlay.log;

@ToString
public class CalcOverlaySettings {

    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    @Getter
    private static CalcOverlaySettings instance = null;

    /************************** General settings *****************************/

    @Expose @SerializedName("calc overlay enabled")
    public boolean calcOverlayEnabled;

    @Expose @SerializedName("version")
    public String version;

    @Expose @SerializedName(value = "overlay position", alternate = {"overlay overlayPosition"})
    public Position overlayPosition;

    @Expose @SerializedName("font")
    public FontData fontData;

    @Expose @SerializedName("outline width")
    public int outlineWidth;

    /* ********************************************************************* */



    /****************** Eye throws (stronghold endpoint) *********************/
    /* For now, I'm leaving it here, even though it should be its own class. */

    @Expose @SerializedName("columns")
    public List<ColumnData> columnData;

    @Expose @SerializedName("show angle direction")
    public boolean showAngleDirection;

    @Expose @SerializedName("show coords based on dimension")
    public boolean onlyShowCurrentDimensionCoords;

    @Expose @SerializedName("overworld coords")
    public OverworldsCoords overworldCoords;

    @Expose @SerializedName("shown measurements")
    public int shownMeasurements = -1;

    /* ********************************************************************* */



    /********** All Advancements (all-advancements endpoint) *****************/

    @Expose @SerializedName("all advancements")
    public AllAdvancementsSettings aaSettings;

    /* ********************************************************************* */

    public static void load() {
        if (!Files.exists(SETTINGS_PATH)) {
            loadDefaultSettings();
            save();
            return;
        }

        try {
            instance = GSON.fromJson(FileUtil.readString(SETTINGS_PATH), CalcOverlaySettings.class);
        } catch (IOException e) {
            log(Level.ERROR, "Error while reading settings, resetting back to default:\n" + ExceptionUtil.toDetailedString(e));
            loadDefaultSettings();
        }

    }

    public static void save() {
        try {
            FileUtil.writeString(SETTINGS_PATH, GSON.toJson(instance));
        } catch (IOException e) {
            log(Level.ERROR, "Failed to save CalcOverlay settings: " + ExceptionUtil.toDetailedString(e));
        }
    }

    private static void loadDefaultSettings() {
        instance = new CalcOverlaySettings();
        instance.calcOverlayEnabled = false;
        instance.version = VersionUtil.CURRENT_VERSION.toString();
        instance.overlayPosition = Position.TOP_LEFT;
        instance.columnData = new ArrayList<>();
        instance.columnData.add(new ColumnData(ColumnType.OVERWORLD_COORDS, HeaderRow.ICON, true));
        instance.columnData.add(new ColumnData(ColumnType.CERTAINTY, HeaderRow.ICON, true));
        instance.columnData.add(new ColumnData(ColumnType.DISTANCE, HeaderRow.ICON, true));
        instance.columnData.add(new ColumnData(ColumnType.NETHER_COORDS, HeaderRow.ICON, true));
        instance.columnData.add(new ColumnData(ColumnType.ANGLE, HeaderRow.ICON, true));
        instance.showAngleDirection = true;
        instance.onlyShowCurrentDimensionCoords = false;
        instance.overworldCoords = OverworldsCoords.CHUNK;
        instance.shownMeasurements = 3;
        instance.aaSettings = AllAdvancementsSettings.loadDefaultSettings();
        instance.outlineWidth = 3;
    }

    @AllArgsConstructor @Getter
    public enum Position {
        @Expose @SerializedName("top left")
        TOP_LEFT("Top left"),
        @Expose @SerializedName("top right")
        TOP_RIGHT("Top right"),
        @Expose @SerializedName("bottom left")
        BOTTOM_LEFT("Bottom left"),
        @Expose @SerializedName("bottom right")
        BOTTOM_RIGHT("Bottom right");

        public boolean isTop() {
            return this == TOP_LEFT || this == TOP_RIGHT;
        }

        public boolean isLeft() {
            return this == TOP_LEFT || this == BOTTOM_LEFT;
        }

        private final String display;

        public static Position match(String s) {
            for (Position value : Position.values()) {
                if (value.display.equals(s)) {
                    return value;
                }
            }
            return null;
        }

    }

    @Data @AllArgsConstructor
    public static class FontData {
        @Expose @SerializedName("name")
        private final String name;
        @Expose @SerializedName("size")
        private final int size;

        public Font toFont() {
            return new Font(name, Font.PLAIN, size);
        }
    }

    @Data
    public static class ColumnData {
        @Expose @SerializedName("name")
        private final ColumnType columnType;
        @Expose @SerializedName("header row")
        private HeaderRow headerRow;

        @Expose(serialize = false)
        @SerializedName("show icon")
        private boolean showIcon; // old setting

        @Expose @SerializedName("visible")
        private boolean isVisible;

        public ColumnData(ColumnType columnType, HeaderRow headerRow, boolean isVisible) {
            this.columnType = columnType;
            this.headerRow = headerRow;
            this.isVisible = isVisible;
        }

        public boolean shouldShow(boolean isInNether) {
            if (columnType == ColumnType.NETHER_COORDS && getInstance().onlyShowCurrentDimensionCoords) {
                return isInNether;
            }
            if (columnType == ColumnType.OVERWORLD_COORDS && getInstance().onlyShowCurrentDimensionCoords) {
                return !isInNether;
            }
            return isVisible;
        }
    }

    @AllArgsConstructor @Getter
    public enum HeaderRow {
        @Expose @SerializedName("nothing")
        NOTHING("Nothing"),
        @Expose @SerializedName("show icon")
        ICON("Icon"),
        @Expose @SerializedName("show text")
        TEXT("Text");

        private final String display;

        public static HeaderRow match(String s) {
            for (HeaderRow value : HeaderRow.values()) {
                if (value.display.equals(s)) {
                    return value;
                }
            }
            return null;
        }
    }

    @AllArgsConstructor
    public enum ColumnType {
        @Expose @SerializedName("overworld coords")
        OVERWORLD_COORDS("Overworld Coords", "Location", OverlayUtil.overworldIconImage),
        @Expose @SerializedName("certainty")
        CERTAINTY("Certainty", "%", OverlayUtil.certaintyIconImage),
        @Expose @SerializedName("distance")
        DISTANCE("Distance", "Dist.", OverlayUtil.distanceIconImage),
        @Expose @SerializedName("nether coords")
        NETHER_COORDS("Nether Coords", "Nether", OverlayUtil.netherIconImage),
        @Expose @SerializedName("angle")
        ANGLE("Angle", "Angle", OverlayUtil.angleIconImage);

        @Getter
        private final String configDisplay;
        private final String overlayDisplay;
        @Getter
        private final Image icon;

        public String getOverlayDisplay(OverworldsCoords coords) {
            switch (this) {
                default:
                    return this.overlayDisplay;
                case OVERWORLD_COORDS:
                    switch (coords) {
                        case CHUNK:
                            return "Chunk";
                        default:
                            return "Location";
                    }
            }
        }
    }

    @AllArgsConstructor @Getter
    public enum OverworldsCoords {
        @Expose @SerializedName("chunk")
        CHUNK("Chunk"),
        @Expose @SerializedName("(8, 8)")
        EIGHT_EIGHT("(8, 8)"),
        @Expose @SerializedName("(4, 4)")
        FOUR_FOUR("(4, 4)");

        private final String display;

        public static OverworldsCoords match(String s) {
            for (OverworldsCoords value : OverworldsCoords.values()) {
                if (value.display.equals(s)) {
                    return value;
                }
            }
            return null;
        }

    }

}