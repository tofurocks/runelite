package com.runemax.bot.scripts.util;

import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.actors.npcs.NPCs;
import com.runemax.bot.api.widget.Dialog;
import com.runemax.bot.api.wrappers.entity.actor.npc.NPC;
import com.runemax.webwalker.localpath.LocalPath;
import com.runemax.webwalker.spatial.TraversableWorldPoint;
import lombok.extern.slf4j.Slf4j;

import static com.runemax.bot.api.commons.Rand.gaussian;

@Slf4j
public class CommonUtil {
    public static void walkToRSTileAndTalkToNpc(TraversableWorldPoint tile, String npcName, boolean isObject) {
        //Talk to npc if they are interactable
        NPC npc = NPCs.closest(npcName);
        if (npc.isPresent() && LocalPath.reachable(npc.getLocalLocation()) && npc.getWorldLocation().distanceTo(tile) < 10) {
            npc.interact("Talk-to");
            Sleep.until(() -> Dialog.canContinue() || Dialog.isViewingOptions(), gaussian(50, 15000, 8000, 5000));
            Sleep.sleep(gaussian(50, 15000, 1000, 900));
            return;
        }
        //Npc is loaded but not interactable, walk towards them (might be behind a door etc.)
        if (npc.isPresent() && !LocalPath.reachable(npc.getLocalLocation())) {
            log.info("Npc is present but not reachable, walk towards them (might be behind a door etc.)");
               //TODO: walk towards npc
            return;
        }
        log.info("NPC is null? " + (npc == null));
        //Npc is null and not interactable, walk towards RSTile
        log.info("Npc is null and not interactable, walk towards RSTile");
//TODO: walk towards npc
    }
}
