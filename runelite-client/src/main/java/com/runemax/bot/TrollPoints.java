package com.runemax.bot;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

public enum TrollPoints {
    CABBAGES(new WorldPoint(3053, 3292, 0)),
    DRAYNOR_ROPE_GUY(new WorldPoint(3099, 3259, 0)),
    PORT_SARIM_FISH_GUY(new WorldPoint(3013, 3226, 0)),
    WITHCHES_POTION(new WorldPoint(2966, 3204, 0)),
    WITHCHES_POTION_SOUTH(new WorldPoint(2955, 3203, 0)),
    WIZARDS_TOWER_TOP_FLOOR(new WorldPoint(3106, 3160, 2)),
    RANDOM_HOUSE_1(new WorldPoint(3099, 3269, 0)),
    RANDOM_HOUSE_2(new WorldPoint(3091, 3267, 0)),
    RANDOM_HOUSE_3(new WorldPoint(3090, 3274, 0)),
    RANDOM_HOUSE_4(new WorldPoint(3100, 3278, 0)),
    RANDOM_HOUSE_5(new WorldPoint(3087, 3258, 0));

    @Getter
    private final WorldPoint point;

    TrollPoints(WorldPoint worldPoint) {
        this.point = worldPoint;
    }
}
