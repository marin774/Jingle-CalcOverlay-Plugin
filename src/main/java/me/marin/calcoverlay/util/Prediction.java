package me.marin.calcoverlay.util;

import lombok.Data;
import lombok.ToString;

@Data @ToString
public class Prediction {

    private final int chunkX;
    private final int chunkZ;
    private final double certainty;
    private final double overworldDistance;

}
