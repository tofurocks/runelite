package com.runemax.bot.scripts.construction.tasks;

import lombok.extern.slf4j.Slf4j;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.entities.tile.objects.TileObjects;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.wrappers.entity.tile.object.TileObject;

import static com.runemax.bot.api.commons.Rand.gaussian;

@Slf4j
public class ExitPOH extends Task {
    int BUILT_BED = 13149;

    @Override
    public boolean activate() {
        if (TileObjects.all(obj -> obj.getId() == BUILT_BED).size() >= 8) {
            log.info("Done building beds");
            return false;
        }
        return Players.getLocal().getWorldLocation().getY() > 4500 || Players.getLocal().getWorldLocation().getX() > 3900;
    }

    @Override
    public int execute() {
        TileObject portal = TileObjects.closest("Portal");
        if (portal.isPresent()) {
            portal.interact("Enter");
        }
        return gaussian(50, 15000, 2000, 1250);
    }
}
