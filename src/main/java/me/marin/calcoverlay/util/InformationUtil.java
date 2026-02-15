package me.marin.calcoverlay.util;

import me.marin.calcoverlay.CalcOverlay;
import me.marin.calcoverlay.util.data.EyeThrow;
import me.marin.calcoverlay.util.data.Prediction;
import org.apache.logging.log4j.Level;

import java.util.List;
import java.util.Map;

public class InformationUtil {

    public static final boolean PRE1_19 = true;

    public static boolean hasError(Prediction bestPrediction, List<EyeThrow> eyeThrows, Map<String, Object> settings) {
        double likelihood = 1;
        double expectedLikelihood = 1;
        for (EyeThrow t : eyeThrows) {
            double error = t == null ? -1 : getAngleError(bestPrediction, t);
            double sigma = getStdDev(t, settings);
            likelihood *= Math.exp(-0.5 * (error / sigma) * (error / sigma));
            expectedLikelihood *= 1.0 / Math.sqrt(2);
        }

        return (likelihood / expectedLikelihood) < 0.01;
    }

    public static boolean canLink(Prediction bestPrediction, EyeThrow eyeThrow) {
        double approximatePortalNetherX = eyeThrow.getXInOverworld() / 8;
        double approximatePortalNetherZ = eyeThrow.getZInOverworld() / 8;

        double maxAxisDistance = Math.max(Math.abs(approximatePortalNetherX - ((double) bestPrediction.getChunkX() * 2 + 0.5)), Math.abs(approximatePortalNetherZ - ((double) bestPrediction.getChunkZ() * 2 + 0.5)));

        return maxAxisDistance < 24;
    }

    private static double getStdDev(EyeThrow eyeThrow, Map<String, Object> settings) {
        switch (eyeThrow.getType()) {
            case "NORMAL": {
                return (double) settings.get("sigma");
            }
            case "NORMAL_WITH_ALT_STD": {
                return (double) settings.get("sigma_alt");
            }
            case "MANUAL": {
                return (double) settings.get("sigma_manual");
            }
            case "BOAT": {
                return (double) settings.get("sigma_boat");
            }
        }
        return 0;
    }

    private static double getAngleError(Prediction p, EyeThrow t) {
        double deltaX = p.getChunkX() * 16 + getStrongholdChunkCoord() - t.getXInOverworld();
        double deltaZ = p.getChunkZ() * 16 + getStrongholdChunkCoord() - t.getZInOverworld();
        double gamma = -180 / Math.PI * Math.atan2(deltaX, deltaZ);
        double delta = (t.getAngle() - gamma) % 360.0;
        return CalcOverlayUtil.normalizeAngle(delta);
    }

    private static int getStrongholdChunkCoord() {
        return PRE1_19 ? 8 : 0;
    }

}
