package com.runemax.bot.scripts.construction.tasks;

import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.itemcontainer.equipment.Equipment;
import com.runemax.bot.api.itemcontainer.equipment.EquipmentItem;
import com.runemax.bot.api.varps.Varps;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.skill.Skills;

import static com.runemax.bot.Constants.GRAND_EXCHANGE;

@Slf4j
public class StopIfDoneConstruction extends Task {
    int BUTLER_VARPBIT = 2191;

    @Override
    public boolean activate() {
        if (Skills.getLevel(Skill.CONSTRUCTION) == 50) {
            if ( (Varps.getBit(BUTLER_VARPBIT) & 16) == 0) {
                log.info("Butler varp: " + Varps.get(BUTLER_VARPBIT));
                log.info("We need to purchase a butler");
                return false;
            }
        }
        if (Skills.getLevel(Skill.CONSTRUCTION) < 16) {
            return !Inventory.first(item -> item.getName().toLowerCase().equals("plank") && item.isNoted()).isPresent()
                    && Inventory.count(item -> !item.isNoted() && item.getName().toLowerCase().equals("plank")) < 2;

        }
        return !Inventory.first(item -> item.isNoted()
                && item.getName().toLowerCase().equals("oak plank")).isPresent()
                && Inventory.count(item -> !item.isNoted()
                && item.getName().toLowerCase().equals("oak plank")) < 8;
    }

    @Override
    public int execute() {
        //log.info("Noted oak planks: " + Inventory.count(item -> !item.isNoted()
        //        && item.getName().toLowerCase().equals("oak plank")));
        log.info("We are done!");
        EquipmentItem ring = Equipment.first("Ring of wealth (");
        if(!GRAND_EXCHANGE.contains(Players.getLocal()) && ring.isPresent()){
            log.info("Teleporting to grand exchange and stopping");
            ring.interact(2);
        }
        return -1;
    }
}
