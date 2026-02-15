package me.marin.calcoverlay.util.data;

import lombok.Data;
import lombok.ToString;
import me.marin.calcoverlay.CalcOverlay;
import org.apache.logging.log4j.Level;

import java.util.Map;

@Data @ToString
public class EyeThrow {

    private final double xInOverworld;
    private final double zInOverworld;
    private final double angle;
    private final double angleWithoutCorrection;
    private final double correction;
    private final int correctionIncrements; // not available in 1.5.1
    private final double error;
    private final String type;

    public int getAngleCorrectionIncrements(Map<String, Object> settings) {
        double beta = -31; // ESTIMATED, MIGHT NOT WORK ALL THE TIME
        double change;
        switch ((int) settings.getOrDefault("angle_adjustment_type", 0)) {
            case 1:
                final double toRad = Math.PI / 180.0;
                change = Math.atan(2 * Math.tan(15 * toRad) / (double) settings.getOrDefault("resolution_height", 16384)) / Math.cos(beta * toRad) / toRad;
                break;
            case 2:
                change = (double) settings.get("custom_adjustment");
                break;
            default:
                change = 0.01;
        }

        return (int) Math.round(correction / change);
    }


}
