package me.marin.calcoverlay.util;

import lombok.Data;

@Data
public class AngleToCoords {

    private final double actualAngle;
    private final double neededAngleCorrection;

    public static AngleToCoords from(Prediction prediction, PlayerPosition playerPosition) {
        double x;
        double z;
        double strongholdX;
        double strongholdZ;
        if (!playerPosition.isInNether()) {
            x = playerPosition.getXInOverworld();
            z = playerPosition.getZInOverworld();

            strongholdX = prediction.getChunkX() * 16 + 4.5;
            strongholdZ = prediction.getChunkZ() * 16 + 4.5;
        } else {
            x = playerPosition.getXInOverworld() / 8;
            z = playerPosition.getZInOverworld() / 8;

            strongholdX = prediction.getChunkX() * 2 + 0.5;
            strongholdZ = prediction.getChunkZ() * 2 + 0.5;
        }

        double actualAngle = Math.toDegrees(Math.atan2(strongholdX - x, z - strongholdZ)) + 180;
        while (actualAngle > 180) {
            actualAngle -= 360;
        }
        while (actualAngle < -180) {
            actualAngle += 360;
        }

        double playerAngle = playerPosition.getHorizontalAngle();
        while (playerAngle > 180) {
            playerAngle -= 360;
        }
        while (playerAngle < -180) {
            playerAngle += 360;
        }

        double neededAngleCorrection = actualAngle - playerAngle;
        while (neededAngleCorrection > 180) {
            neededAngleCorrection -= 360;
        }
        while (neededAngleCorrection < -180) {
            neededAngleCorrection += 360;
        }

        return new AngleToCoords(actualAngle, neededAngleCorrection);
    }

}
