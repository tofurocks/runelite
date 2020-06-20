package com.runemax.bot.api.wrappers;

import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public interface Locatable {
    WorldPoint getWorldLocation();
    LocalPoint getLocalLocation();
    int getX();
    int getY();
    int getPlane();

    default Point getSceneLocation(){
        LocalPoint localLocation = getLocalLocation();
        return new Point(localLocation.getSceneX(), localLocation.getSceneY());
    }

    default int getSceneX() {
        return getSceneLocation().getX();
    }

    default int getSceneY() {
        return getSceneLocation().getY();
    }
}
