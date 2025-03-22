package me.marin.calcoverlay.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import me.marin.calcoverlay.util.OverlayUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class AllAdvancementsSettings {

    @Expose
    @SerializedName("columns")
    public List<ColumnData> columnData;

    @Expose
    @SerializedName("rows")
    public List<RowData> rowData;

    public static AllAdvancementsSettings loadDefaultSettings() {
        AllAdvancementsSettings instance = new AllAdvancementsSettings();

        instance.columnData = new ArrayList<>();
        instance.columnData.add(new ColumnData(ColumnType.ICONS, HeaderRow.NOTHING, true));
        instance.columnData.add(new ColumnData(ColumnType.LOCATION, HeaderRow.TEXT, true));
        instance.columnData.add(new ColumnData(ColumnType.NETHER_COORDS, HeaderRow.TEXT, true));
        instance.columnData.add(new ColumnData(ColumnType.ANGLE, HeaderRow.TEXT, true));

        instance.rowData = new ArrayList<>();
        instance.rowData.add(new RowData(RowType.STRONGHOLD, true));
        instance.rowData.add(new RowData(RowType.SPAWN, true));
        instance.rowData.add(new RowData(RowType.OUTPOST, true));
        instance.rowData.add(new RowData(RowType.MONUMENT, true));

        return instance;
    }


    @Data @AllArgsConstructor
    public static class ColumnData {
        @Expose @SerializedName("name")
        private final ColumnType columnType;
        @Expose @SerializedName("header row")
        private HeaderRow headerRow;

        @Expose @SerializedName("visible")
        private boolean isVisible;
    }

    @AllArgsConstructor
    @Getter
    public enum HeaderRow {
        @Expose @SerializedName("nothing")
        NOTHING("Nothing"),
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
    @Getter
    public enum ColumnType {
        @Expose @SerializedName("icons")
        ICONS("Icons", ""),
        @Expose @SerializedName("location")
        LOCATION("Location", "Location"),
        @Expose @SerializedName("nether coords")
        NETHER_COORDS("Nether Coords", "Nether"),
        @Expose @SerializedName("angle")
        ANGLE("Angle", "Angle");

        private final String configDisplay;
        private final String overlayDisplay;
    }

    @Data @AllArgsConstructor
    public static class RowData {
        @Expose @SerializedName("name")
        private final RowType rowType;

        @Expose @SerializedName("visible")
        private boolean isVisible;
    }

    @AllArgsConstructor
    @Getter
    public enum RowType {
        STRONGHOLD("Stronghold", OverlayUtil.strongholdIconImage),
        SPAWN("Shulker", OverlayUtil.spawnIconImage),
        OUTPOST("Outpost", OverlayUtil.outpostIconImage),
        MONUMENT("Monument", OverlayUtil.monumentIconImage);

        private final String configDisplay;
        private final Image icon;
    }


}
