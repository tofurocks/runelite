package com.runemax.bot.api.wrappers.entity.tile.item;

import com.runemax.bot.api.exception.RickkPointerException;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.wrappers.entity.tile.TileEntity;
import com.runemax.bot.api.wrappers.item.Item;
import lombok.experimental.Delegate;
import net.runelite.api.Entity;
import net.runelite.api.MenuAction;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TileItem extends Item implements TileEntity, net.runelite.api.TileItem {

    private interface Excludes{
        net.runelite.api.TileItem getHash();
    }

    private interface ExcludesTwo {
        net.runelite.api.Locatable getLocalLocation();
        net.runelite.api.Locatable getWorldLocation();
    }

    @Nullable
    private final net.runelite.api.TileItem tileItemDelegate;

    @Override
    public LocalPoint getLocalLocation()
    {
        return new LocalPoint(getX(), getY());
    }

    @Override
    public WorldPoint getWorldLocation()
    {
        return WorldPoint.fromLocal(Client.getInstance(), getX(), getY(), getPlane());
    }

    @Nullable
    private final Tile tileDelegate;

    public TileItem(@Nullable net.runelite.api.TileItem delegate, @Nullable Tile tileDelegate){
        super(delegate == null ? null : new net.runelite.api.Item(delegate.getId(), delegate.getQuantity()));
        this.tileItemDelegate = delegate;
        this.tileDelegate = tileDelegate;
    }

    @Delegate(types = Entity.class, excludes = Excludes.class)
    @Nonnull
    public net.runelite.api.TileItem getTileItemDelegate(){
        if (tileItemDelegate == null) throw new RickkPointerException();
        return tileItemDelegate;
    }

    @Delegate(excludes = ExcludesTwo.class)
    @Nonnull
    public Tile getTileDelegate() {
        if(tileDelegate == null) throw new RickkPointerException();
        return tileDelegate;
    }

    @Override
    public int getMenuType(int actionIndex) {
        switch (actionIndex){
            case 2:
                return MenuAction.GROUND_ITEM_THIRD_OPTION.getId();
            default:
                throw new IllegalArgumentException("no opcode for " + actionIndex);
        }
    }

    @Override
    public String[] getActions() {
        String[] out = new String[3];
        out[2] = "Take";
        return out;
    }

    @Override
    public int getMenuTypeForUseItemOn() {
        return MenuAction.ITEM_USE_ON_GROUND_ITEM.getId();
    }

    @Override
    public int getMenuTypeForCastSpellOn(){
        return MenuAction.SPELL_CAST_ON_GROUND_ITEM.getId();
    }

}
