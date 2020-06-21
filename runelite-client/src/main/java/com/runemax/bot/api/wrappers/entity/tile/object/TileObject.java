package com.runemax.bot.api.wrappers.entity.tile.object;

import com.runemax.bot.api.exception.BotException;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.wrappers.Identifiable;
import com.runemax.bot.api.wrappers.RlWrapper;
import com.runemax.bot.api.wrappers.entity.tile.TileEntity;
import lombok.experimental.Delegate;
import net.runelite.api.MenuOpcode;
import net.runelite.api.ObjectDefinition;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public abstract class TileObject<T extends net.runelite.api.TileObject> extends RlWrapper<T> implements TileEntity, net.runelite.api.TileObject, ObjectDefinition {
    private ObjectDefinition composition = null;

    TileObject(T rl) {
        super(rl);
    }

    @Delegate(excludes = Identifiable.class)
    public ObjectDefinition getComposition() {
        if (composition == null) {
            int id = getRl().getId();
            composition = Client.getObjectDefinition(id);

            if (composition == null) throw new BotException("no def for id " + id);
        }

        return composition;
    }

    @Override
    public int getMenuType(int actionIndex) {
        switch (actionIndex) {
            case 0:
                return MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId();
            case 1:
                return MenuOpcode.GAME_OBJECT_SECOND_OPTION.getId();
            case 2:
                return MenuOpcode.GAME_OBJECT_THIRD_OPTION.getId();
            case 3:
                return MenuOpcode.GAME_OBJECT_FOURTH_OPTION.getId();
            case 4:
                return MenuOpcode.GAME_OBJECT_FIFTH_OPTION.getId();
            default:
                throw new IllegalArgumentException("no opcode for actionIndex " + actionIndex);
        }
    }

    @Override
    public WorldPoint getWorldLocation()
    {
        return WorldPoint.fromLocal(Client.getInstance(), getX(), getY(), getPlane());
    }

    @Override
    public LocalPoint getLocalLocation()
    {
        return new LocalPoint(getX(), getY());
    }

    @Override
    public int getMenuTypeForUseItemOn() {
        return MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId();
    }

    @Override
    public int getMenuTypeForCastSpellOn() {
        return MenuOpcode.SPELL_CAST_ON_GAME_OBJECT.getId();
    }
}
