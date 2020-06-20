package com.runemax.bot.api.itemcontainer;

import com.runemax.bot.api.wrappers.Interactable;
import com.runemax.bot.api.wrappers.item.Item;
import lombok.Getter;

public abstract class ContainerItem extends Item implements Interactable {
    @Getter
    private final int index;

    public ContainerItem(net.runelite.api.Item delegate, int index) {
        super(delegate);
        this.index = index;
    }
}
