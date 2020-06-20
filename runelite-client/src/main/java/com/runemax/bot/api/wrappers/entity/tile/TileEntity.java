package com.runemax.bot.api.wrappers.entity.tile;

import com.runemax.bot.api.wrappers.entity.Entity;

public interface TileEntity extends Entity {
    @Override
    default int getMenuIdentifier( Integer actionIndex) {
        return getId();
    }

    @Override
    default int getMenuParam0() {
        return getSceneX();
    }

    @Override
    default int getMenuParam1() {
        return getSceneY();
    }
}
