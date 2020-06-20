package com.runemax.bot.api.wrappers.entity.tile.object;

import lombok.experimental.Delegate;
import net.runelite.api.Point;

import javax.annotation.Nonnull;

public final class GameObject extends TileObject<net.runelite.api.GameObject> implements net.runelite.api.GameObject {
    public GameObject(net.runelite.api.GameObject rl) {
        super(rl);
    }

    @Nonnull
    @Override
    @Delegate
    public net.runelite.api.GameObject getRl() {
        return super.getRl();
    }

    @Override
    public Point getSceneLocation(){
        return getSceneMinLocation();
    }
}
