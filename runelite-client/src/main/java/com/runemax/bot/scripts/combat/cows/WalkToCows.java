package com.runemax.bot.scripts.combat.cows;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.coords.RectangularArea;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.itemcontainer.equipment.Equipment;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.skill.Skills;
import com.runemax.bot.tasks.subtasks.WalkTask;
import com.runemax.webwalker.localpath.LocalPath;
import com.runemax.webwalker.spatial.TraversableWorldPoint;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

import static com.runemax.bot.Constants.LUM_COWS;

@Slf4j
public class WalkToCows extends Task {
    TraversableWorldPoint LUM_COWS_POINT = new TraversableWorldPoint(3178, 3322, 0);
    TraversableWorldPoint LUM_BRIDGE_POINT = new TraversableWorldPoint(3243, 3262, 0);
    RectangularArea BIG_VARROCK_AREA = new RectangularArea(3140, 3514, 3281, 3349);
    RectangularArea HALFWAY_VARROCK_AREA = new RectangularArea(3204, 3351, 3269, 3309);


    WalkTask walkToCows = new WalkTask("walk to lumbridge cows", LUM_COWS_POINT);
    WalkTask walkToLumbridge = new WalkTask("walk to lumbridge bridge", LUM_BRIDGE_POINT);


    @Override
    public boolean activate() {
        if (Skills.getLevel(Skill.ATTACK) < 30 || Skills.getLevel(Skill.DEFENCE) < 30){
            return false; //Not done training melee yet
        }
        if(!Equipment.isOccupied(EquipmentInventorySlot.WEAPON)){
            return false; //We have no weapon
        }
        if(!Equipment.inSlot(EquipmentInventorySlot.WEAPON).getName().contains("Staff")){
            return false;
        }
        return !LUM_COWS.contains(Players.getLocal());
                //&& (!LUM_COWS_POINT.isInScene(Client.getInstance()) || !LocalPath.reachable(LUM_COWS_POINT.getLocalPoint()));
    }

    @Override
    public int execute() {
        if(BIG_VARROCK_AREA.contains(Players.getLocal()) || HALFWAY_VARROCK_AREA.contains(Players.getLocal())){
            log.info("Walking to lumbridge first so we don't get fucked up by the draynor trees");
            walkToLumbridge.execute(null);
            return Rand.nextInt(1000, 2000);
        }
        log.info("Walking to lumbridge cows");
        walkToCows.execute(null);
        return Rand.nextInt(1000, 2000);
    }
}
