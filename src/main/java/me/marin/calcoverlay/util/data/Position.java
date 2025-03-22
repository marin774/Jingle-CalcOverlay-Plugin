package me.marin.calcoverlay.util.data;

import lombok.Data;
import lombok.ToString;

@Data @ToString
public class Position {

    private final int xInOverworld;
    private final int zInOverworld;
    private final double travelAngle;
    private final int overworldDistance;

}
