package com.runemax.bot;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

public enum MapPoints {
    RIMMINGTON(new WorldPoint(2956, 3216, 0)),
    FALADOR(new WorldPoint(2965, 3381, 0)),
    EDGEVILLE(new WorldPoint(3087, 3493, 0)),
    GRAND_EXCHANGE(new WorldPoint(3164, 3478, 0)),
    VARROCK(new WorldPoint(3211, 3423, 0)),
    LUMBRIDGE(new WorldPoint(3235, 3219, 0)),
    DRAYNOR(new WorldPoint(3081, 3250, 0)),
    CRAFTING_GUILD(new WorldPoint(2933, 2939, 0)),
    WIZARDS_TOWER(new WorldPoint(3112, 3169, 0)),
    AL_KHARID(new WorldPoint(3293, 3188, 0)),
    LUMBER_YARD(new WorldPoint(3302, 3485, 0)),
    CHAMPIONS_GUILD(new WorldPoint(3191, 3367, 0)),
    BARBARIAN_VILLAGE(new WorldPoint(3077, 3420, 0)),
    MONASTRY(new WorldPoint(3051, 3496, 0)),
    GOBLIN_VILLAGE(new WorldPoint(2956, 3503, 0)),
    DORICS_HUT(new WorldPoint(2946, 3450, 0)),
    LUMBRIDGE_SWAMP(new WorldPoint(3198, 3179, 0)),
    PORT_SARIM(new WorldPoint(3014, 3215, 0));

    @Getter
    private final WorldPoint point;

    MapPoints(WorldPoint worldPoint) {
        this.point = worldPoint;
    }
}
