package com.runemax.webwalker.spatial;

import net.runelite.api.coords.WorldPoint;

public class InteractiveMovePoint implements MovePoint {

    private final WorldPoint worldPoint;

    public InteractiveMovePoint(int x, int y, int plane) {
        this(new WorldPoint(x, y , plane));
    }

    public InteractiveMovePoint(WorldPoint worldPoint) {
        this.worldPoint = worldPoint;
    }

    @Override
    public WorldPoint getWorldPoint() {
        return worldPoint;
    }
}
