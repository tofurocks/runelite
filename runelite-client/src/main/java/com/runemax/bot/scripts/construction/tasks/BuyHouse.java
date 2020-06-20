package com.runemax.bot.scripts.construction.tasks;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.actors.npcs.NPCs;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.varps.Varps;
import com.runemax.bot.api.widget.Dialog;
import com.runemax.bot.api.wrappers.entity.actor.npc.NPC;
import com.runemax.bot.tasks.subtasks.WalkTask;
import com.runemax.webwalker.localpath.LocalPath;
import com.runemax.webwalker.spatial.TraversableWorldPoint;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

import static com.runemax.bot.api.commons.Rand.gaussian;

@Slf4j
public class BuyHouse extends Task {
    int HOUSE_VARP = 738;
    TraversableWorldPoint POINT_ESTATE_AGENT = new TraversableWorldPoint(3242, 3474, 0);
    WalkTask walkToAgentSpot = new WalkTask("estate agent", POINT_ESTATE_AGENT);

    @Override
    public boolean activate() {
        return (Varps.get(HOUSE_VARP) & 32) == 0;
    }

    @Override
    public int execute() {
        log.info("We need to buy a house");
        if(Dialog.canContinue()){
            Dialog.continueSpace();
            return Rand.nextInt(1000, 2000);
        }
        if(Dialog.isViewingOptions()) {
            if (Dialog.hasOption("How can I get a house?", "Yes please!")) {
                Dialog.chooseOption("How can I get a house?", "Yes please!");
                return gaussian(50, 15000, 100, 50);
            }
        }
        if (!POINT_ESTATE_AGENT.isInScene(Client.getInstance()) || !LocalPath.reachable(POINT_ESTATE_AGENT.getLocalPoint()) || !NPCs.closest("Estate agent").isPresent()) {
            log.info("Walking to estate agent");
            walkToAgentSpot.execute(null);
            return Rand.nextInt(1000, 2000);
        }
        log.info("Talking to estate agent");
        NPC agent = NPCs.closest("Estate agent");
        if(agent.isPresent()){
            agent.interact("Talk-to");
            Sleep.until(Dialog::isOpen, 10*1000, 15*1000);
        }
        return Rand.nextInt(1000, 2000);
    }
}
