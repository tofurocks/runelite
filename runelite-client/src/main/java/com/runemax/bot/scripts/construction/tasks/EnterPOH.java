package com.runemax.bot.scripts.construction.tasks;

import com.runemax.bot.api.entities.actors.npcs.NPCs;
import com.runemax.bot.api.entities.tile.objects.TileObjects;
import com.runemax.bot.api.script.Task;

import static com.runemax.bot.api.commons.Rand.gaussian;

public class EnterPOH extends Task {
    @Override
    public boolean activate() {
        return NPCs.closest("Phials").isPresent();
    }

    @Override
    public int execute() {
        TileObjects.closest("Portal").interact("Build mode");
        return gaussian(50, 15000, 2000, 1250);
    }
}
