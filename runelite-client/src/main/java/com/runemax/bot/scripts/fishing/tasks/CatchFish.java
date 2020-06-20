package com.runemax.bot.scripts.fishing.tasks;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.entities.actors.npcs.NPCs;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.movement.Movement;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.widget.Dialog;
import com.runemax.bot.api.wrappers.entity.actor.npc.NPC;
import com.runemax.bot.tasks.subtasks.WalkTask;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

import static com.runemax.bot.api.commons.Rand.gaussian;
import static com.runemax.bot.api.commons.Sleep.sleep;

@Slf4j
public class CatchFish extends Task {
    WorldPoint DRAYNOR_FISHING_SPOT = new WorldPoint(2517, 3571, 0);
    WalkTask walkToDraynorFishingSpot = new WalkTask("Draynor fishing spot", DRAYNOR_FISHING_SPOT);

    @Override
    public boolean activate() {
        if(Dialog.canContinue() && !Inventory.isFull()){
            return true;
        }
        return Players.getLocal().getInteracting() == null
                && Players.getLocal().getAnimation() == -1
                && !Inventory.isFull();
    }

    @Override
    public int execute() {
        if (Rand.nextInt(0, 2) == 0) {
            sleep(gaussian(50, 15000, 2500, 2000));
        }
        if (Dialog.canContinue()) {
            Dialog.continueSpace();
            if (Rand.nextInt(0, 2) == 0) {
                sleep(gaussian(50, 15000, 2500, 2000));
            }
        }
        NPC spot = NPCs.closest("Fishing spot");
        if (spot == null || !spot.isPresent()) {
            log.info("No spot was present! Walking towards draynor fishing spot");
            walkToDraynorFishingSpot.execute(null);
            return gaussian(50, 15000, 2500, 1500);
        }
        log.info("Netting fish spot");
        spot.interact("Net");
        return gaussian(50, 15000, 2500, 2000);
    }
}
