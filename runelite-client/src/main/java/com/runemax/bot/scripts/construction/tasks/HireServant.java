package com.runemax.bot.scripts.construction.tasks;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.coords.RectangularArea;
import com.runemax.bot.api.entities.actors.npcs.NPCs;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.itemcontainer.equipment.Equipment;
import com.runemax.bot.api.itemcontainer.equipment.EquipmentItem;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.skill.Skills;
import com.runemax.bot.api.varps.Varps;
import com.runemax.bot.api.widget.Dialog;
import com.runemax.bot.api.wrappers.entity.actor.npc.NPC;
import com.runemax.bot.tasks.subtasks.WalkTask;
import com.runemax.webwalker.localpath.LocalPath;
import com.runemax.webwalker.spatial.TraversableWorldPoint;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;

import static com.runemax.bot.api.commons.Rand.gaussian;

@Slf4j
public class HireServant extends Task {
    int BUTLER_VARP = 2191;
    RectangularArea FISHING_GUILD_TO_ARDOUGNE = new RectangularArea(2592, 3400, 2675, 3324);
    TraversableWorldPoint POINT_OUTSIDE_SERVANTS_GUILD_WORLDPOINT = new TraversableWorldPoint(2659, 3330, 0);
    WalkTask walkToOutsideServantsGuild = new WalkTask("outside servant's guild", POINT_OUTSIDE_SERVANTS_GUILD_WORLDPOINT);

    @Override
    public boolean activate() {
        if (Skills.getLevel(Skill.CONSTRUCTION) == 50) {
            if ((Varps.get(BUTLER_VARP) & 16) == 0) {
                log.info("We need to purchase a butler");
                return true;
            }
        }
        return false;
    }

    @Override
    public int execute() {
        log.info("Purchasing a butler");
        if(Dialog.canContinue()){
            Dialog.continueSpace();
            return Rand.nextInt(1000, 2*1000);
        }
        if(Dialog.isViewingOptions()) {
            if (Dialog.hasOption("You're hired!")) {
                Dialog.chooseOption("You're hired!");
                return gaussian(500, 15*1000, 2*1000, 750);
            }
        }
        if (!FISHING_GUILD_TO_ARDOUGNE.contains(Players.getLocal())) {
            log.info("We need to teleport to the fishing guild / ardougne area");
            EquipmentItem necklace = Equipment.first(item -> item.getName().contains("Skills necklace("));
            if (!necklace.isPresent()) {
                log.info("We have no skills necklace to teleport to fishing guild, stopping");
                return -1;
            }
            log.info("Teleporting to fishing guild");
            necklace.interact(1);
            return Rand.nextInt(3*1000, 6*1000);
        } else {
            log.info("Walking to servant's quarters");
            if (!POINT_OUTSIDE_SERVANTS_GUILD_WORLDPOINT.isInScene(Client.getInstance()) || !LocalPath.reachable(POINT_OUTSIDE_SERVANTS_GUILD_WORLDPOINT.getLocalPoint()) || !NPCs.closest("Demon butler").isPresent()) {
                log.info("Walking to outside servant's guild");
                walkToOutsideServantsGuild.execute(null);
                return Rand.nextInt(1000, 2*1000);
            }
            log.info("We should talk to demon butler");
            NPC butler = NPCs.closest("Demon butler");
            if (!LocalPath.reachable(butler.getLocalLocation())) {
                log.info("Butler is not reachable, web walking to him");
                WalkTask walkToDemonButler = new WalkTask("demon butler", butler.getWorldLocation());
                walkToDemonButler.execute(null);
                return Rand.nextInt(1000, 2*1000);
            }
            log.info("Talking to the demon butler");
            butler.interact("Talk-to");
            return Rand.nextInt(1000, 2*1000);
        }
    }
}
