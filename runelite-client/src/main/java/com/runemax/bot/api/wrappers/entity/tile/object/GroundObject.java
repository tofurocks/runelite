package com.runemax.bot.api.wrappers.entity.tile.object;

import lombok.experimental.Delegate;

import javax.annotation.Nonnull;

public final class GroundObject extends TileObject<net.runelite.api.GroundObject> implements net.runelite.api.GroundObject {
    public GroundObject(net.runelite.api.GroundObject delegate) {
        super(delegate);
    }

    @Nonnull
    @Override
    @Delegate
    public net.runelite.api.GroundObject getRl() {
        return super.getRl();
    }

}
