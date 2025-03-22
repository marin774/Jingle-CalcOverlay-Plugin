package me.marin.calcoverlay.util.data;

import lombok.Data;
import lombok.ToString;

@Data @ToString
public class PlayerPosition {

    private final double xInOverworld;
    private final double zInOverworld;
    private final double horizontalAngle;
    private final boolean isInOverworld;
    private final boolean isInNether;

}
