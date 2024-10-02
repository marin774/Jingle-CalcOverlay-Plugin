package me.marin.calcoverlay.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Getter
    private static CalcOverlaySettings instance = null;

    @SerializedName("calc overlay enabled")
    public boolean calcOverlayEnabled;

    @SerializedName("version")
    public String version;

    @SerializedName("overlay overlayPosition")
    public Position overlayPosition;

    @SerializedName("columns")
    public List<ColumnData> columnData;

    @SerializedName("show angle direction")
    public boolean showAngleDirection;

    @SerializedName("show coords based on dimension")
    public boolean onlyShowCurrentDimensionCoords;

    @SerializedName("overworld coords")
    public OverworldsCoords overworldCoords;

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
        instance.columnData.add(new ColumnData(ColumnType.OVERWORLD_COORDS, true, true));
        instance.columnData.add(new ColumnData(ColumnType.CERTAINTY, true, true));
        instance.columnData.add(new ColumnData(ColumnType.DISTANCE, true, true));
        instance.columnData.add(new ColumnData(ColumnType.NETHER_COORDS, true, true));
        instance.columnData.add(new ColumnData(ColumnType.ANGLE, true, true));
        instance.showAngleDirection = true;
        instance.onlyShowCurrentDimensionCoords = false;
        instance.overworldCoords = OverworldsCoords.CHUNK;
    }

    @AllArgsConstructor @Getter
    public enum Position {
        @SerializedName("top left")
        TOP_LEFT("Top left"),
        @SerializedName("top right")
        TOP_RIGHT("Top right"),
        @SerializedName("bottom left")
        BOTTOM_LEFT("Bottom left"),
        @SerializedName("bottom right")
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
    public static class ColumnData {
        @SerializedName("name")
        private final ColumnType columnType;
        @SerializedName("show icon")
        private boolean showIcon;
        @SerializedName("visible")
        private boolean isVisible;

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
    public enum ColumnType {
        @SerializedName("overworld coords")
        OVERWORLD_COORDS("Overworld Coords", OverlayUtil.overworldIconImage),
        @SerializedName("certainty")
        CERTAINTY("Certainty", OverlayUtil.certaintyIconImage),
        @SerializedName("distance")
        DISTANCE("Distance", OverlayUtil.distanceIconImage),
        @SerializedName("nether coords")
        NETHER_COORDS("Nether Coords", OverlayUtil.netherIconImage),
        @SerializedName("angle")
        ANGLE("Angle", OverlayUtil.angleIconImage);

        private final String display;
        private final Image icon;
    }

    @AllArgsConstructor @Getter
    public enum OverworldsCoords {
        @SerializedName("chunk")
        CHUNK("Chunk"),
        @SerializedName("(8, 8)")
        EIGHT_EIGHT("(8, 8)"),
        @SerializedName("(4, 4)")
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