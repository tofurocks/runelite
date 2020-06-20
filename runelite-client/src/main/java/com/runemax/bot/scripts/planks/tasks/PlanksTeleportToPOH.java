package com.runemax.bot.scripts.planks.tasks;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.entities.tile.objects.TileObjects;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.widget.WidgetQuery;
import com.runemax.bot.scripts.Store;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.WidgetID;

import static com.runemax.bot.Constants.CAMELOT_CASTLE_OUTSIDE;

@Slf4j
public class PlanksTeleportToPOH extends Task {
    private final WidgetQuery TELEPORT_HOUSE = new WidgetQuery(WidgetID.SPELLBOOK_GROUP_ID, w -> w.getName().contains("Teleport to House"));

    @Override
    public boolean activate() {
        return Inventory.first(Store.getPlankLogName()).isPresent() && CAMELOT_CASTLE_OUTSIDE.contains(Players.getLocal());
    }

    @Override
    public int execute() {
        if(Inventory.first("Law rune").isEmpty()){
            log.info("Out of law runes, stopping");
            return -1;
        }
        TELEPORT_HOUSE.get().interact(0);
        Sleep.until(()-> TileObjects.closest("Portal").isPresent(), Rand.nextInt(5*1000, 10*1000));
        return 0;
    }
}
