package com.runemax.bot.api.wrappers.item;

import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.wrappers.Identifiable;
import com.runemax.bot.api.wrappers.Nameable;
import com.runemax.bot.api.wrappers.RlWrapper;
import lombok.experimental.Delegate;
import net.runelite.api.ItemDefinition;

import javax.annotation.Nonnull;

//runelite made item a class instead of interface so its kinda fucked
public class Item extends RlWrapper<net.runelite.api.Item> implements  Identifiable, Nameable, ItemDefinition {

    private ItemDefinition itemDefinition = null;

    public Item(net.runelite.api.Item rl){
        super(rl);
    }

    public Item(int id, int quantity){
        super(new net.runelite.api.Item(id, quantity));
    }

    @Nonnull
    @Delegate(excludes = {Identifiable.class})
    public ItemDefinition getItemDefinition(){
        if(itemDefinition == null) itemDefinition = Client.getItemDefinition(getId());

        return itemDefinition;
    }

    @Nonnull
    @Delegate(types = {net.runelite.api.Item.class})
    @Override
    public net.runelite.api.Item getRl() {
        return super.getRl();
    }


}
