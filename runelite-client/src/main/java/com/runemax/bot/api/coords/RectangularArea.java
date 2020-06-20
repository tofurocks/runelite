package com.runemax.bot.api.coords;

import com.runemax.bot.api.commons.Rand;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nullable;

public class RectangularArea implements Area {
    @Getter
    private final int xMin, yMin, xMax, yMax;
    @Getter
    @Nullable
    private final Integer plane;

    public RectangularArea(int x1, int y1, int x2, int y2, @Nullable Integer plane) {
        if (x1 <= x2) {
            xMin = x1;
            xMax = x2;
        } else {
            xMin = x2;
            xMax = x1;
        }

        if (y1 <= y2) {
            yMin = y1;
            yMax = y2;
        } else {
            yMin = y2;
            yMax = y1;
        }

        this.plane = plane;
    }

    public RectangularArea(int x1, int y1, int x2, int y2) {
        this(x1, y1, x2, y2, null);
    }

    public RectangularArea(WorldPoint t1, WorldPoint t2, @Nullable Integer plane) {
        this(t1.getX(), t1.getY(), t2.getX(), t2.getY(), plane);
    }

    public RectangularArea(WorldPoint t1, WorldPoint t2) {
        this(t1, t2, null);
    }

    @Override
    public boolean contains(WorldPoint point) {
        int pointPlane = point.getPlane();

        if (pointPlane != -1 && this.plane != null && pointPlane != this.plane) return false;

        int x = point.getX();
        int y = point.getY();
        return x >= xMin && y >= yMin && x <= xMax && y <= yMax;
    }

    public WorldPoint getRandomPoint() {
        return new WorldPoint(Rand.nextInt(xMin, xMax), Rand.nextInt(yMin, yMax), this.plane != null ? this.plane : 0);
    }
}
