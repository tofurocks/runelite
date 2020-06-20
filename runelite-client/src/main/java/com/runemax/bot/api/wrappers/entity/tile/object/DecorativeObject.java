package com.runemax.bot.api.wrappers.entity.tile.object;

import lombok.experimental.Delegate;

import javax.annotation.Nonnull;

public final class DecorativeObject extends TileObject<net.runelite.api.DecorativeObject> implements net.runelite.api.DecorativeObject {
    public DecorativeObject(net.runelite.api.DecorativeObject rl) {
        super(rl);
    }

    @Nonnull
    @Override
    @Delegate
    public net.runelite.api.DecorativeObject getRl() {
        return super.getRl();
    }
}
