package com.runemax.bot.scripts.construction.tasks;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.entities.actors.npcs.NPCs;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.varps.Varps;
import com.runemax.bot.api.widget.WidgetQuery;
import net.runelite.api.widgets.WidgetID;

public class TeleportToPOH extends Task {
    int HOUSE_VARP = 738;
    private final WidgetQuery TELEPORT_TO_HOUSE = new WidgetQuery(WidgetID.SPELLBOOK_GROUP_ID, w -> w.getName().contains("Teleport to House"));

    @Override
    public boolean activate() {
        return (Varps.get(HOUSE_VARP) & 32) == 32 && NPCs.closest("Estate agent").isPresent();
    }

    @Override
    public int execute() {
        if (Inventory.first("Construction guide").isPresent()) {
            Inventory.first("Construction guide").interact("Drop");
        } else {
            TELEPORT_TO_HOUSE.get().interact(0);
        }
        return Rand.nextInt(2000, 5000);
    }
}
