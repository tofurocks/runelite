package com.runemax.webwalker.spatial;

import net.runelite.api.coords.WorldPoint;

public class StandardMovePoint implements MovePoint {

    private final WorldPoint worldPoint;

    public StandardMovePoint(WorldPoint worldPoint) {
        this.worldPoint = worldPoint;
    }

    public StandardMovePoint(int x, int y, int plane) {
        this(new WorldPoint(x, y, plane));
    }

    @Override
    public WorldPoint getWorldPoint() {
        return worldPoint;
    }
}
