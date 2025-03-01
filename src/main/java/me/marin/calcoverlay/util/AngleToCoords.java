package me.marin.calcoverlay.util;

import lombok.Data;

@Data
public class AngleToCoords {

    private final double actualAngle;
    private final double neededAngleCorrection;

    public static AngleToCoords from(Prediction prediction, PlayerPosition playerPos) {
        //boolean playerIsInNether = playerPos.isInNether();
        double xDiff = prediction.getChunkX()*16+4 - playerPos.getXInOverworld();
        double zDiff = prediction.getChunkZ()*16+4 - playerPos.getZInOverworld();
        double angleToStructure = -Math.atan2(xDiff, zDiff) * 180 / Math.PI;
        double angleDifference = (angleToStructure - playerPos.getHorizontalAngle()) % 360;
        angleDifference = CalcOverlayUtil.normalizeAngle(angleDifference);

        return new AngleToCoords(angleToStructure, angleDifference);
    }

}
