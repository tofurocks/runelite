package com.runemax.bot.scripts.combat.seagulls;


import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.tile.items.TileItems;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.wrappers.entity.tile.item.TileItem;

import static com.runemax.bot.Constants.PORT_SARIM_DOCKS;

public class LootSeagulls extends Task {
    @Override
    public boolean activate() {
        return !Inventory.isFull() && TileItems.closest(item->item instanceof TileItem
                && item.getName().equals("Bones")
                && PORT_SARIM_DOCKS.contains(item)).isPresent();
    }

    @Override
    public int execute() {
        int count = Inventory.distinctCount();
        TileItems.closest(item->item.getName().equals("Bones") && PORT_SARIM_DOCKS.contains(item)).interact("Take");
        Sleep.until(()->Inventory.distinctCount() > count || Inventory.isFull(), Rand.nextInt(5*1000, 10*1000));
        return 0;
    }
}
