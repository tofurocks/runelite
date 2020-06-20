package com.runemax.bot.api.coords;

import com.runemax.bot.api.wrappers.Locatable;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nullable;

public interface Area {
    boolean contains(WorldPoint point);

    default boolean contains(Locatable locatable){
        return contains(locatable.getWorldLocation());
    }

    @Nullable
    Integer getPlane();
}
