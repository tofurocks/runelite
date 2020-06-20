package com.runemax.bot.api.wrappers.entity.tile.object;

import lombok.experimental.Delegate;

import javax.annotation.Nonnull;

public final class WallObject extends TileObject<net.runelite.api.WallObject> implements net.runelite.api.WallObject {
    public WallObject(net.runelite.api.WallObject delegate) {
        super(delegate);
    }

    @Delegate
    @Nonnull
    @Override
    public net.runelite.api.WallObject getRl() {
        return super.getRl();
    }
}
