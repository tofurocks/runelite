package com.runemax.bot.scripts.construction.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.actors.npcs.NPCs;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.skill.Skills;
import com.runemax.bot.api.widget.Dialog;
import com.runemax.bot.api.wrappers.entity.actor.npc.NPC;

import static com.runemax.bot.api.commons.Rand.gaussian;

@Slf4j
public class ExchangePhials extends Task {
    @Override
    public boolean activate() {
        if (Skills.getLevel(Skill.CONSTRUCTION) < 16) {
            return !Inventory.isFull() && Inventory.first(item -> item.isNoted() && item.getName().equalsIgnoreCase("plank")).isPresent();
        }
        return !Inventory.isFull() && Inventory.first(item -> item.isNoted() && item.getName().equalsIgnoreCase("oak plank")).isPresent();
    }

    @Override
    public int execute() {
        if(Dialog.isViewingOptions()){
            if(Dialog.hasOption("Go to your house")){
                Dialog.chooseOption("Cancel");
                return gaussian(50, 15000, 100, 50);
            }
            log.info("Exchanging notes");
            if(Dialog.hasOption("Exchange All")) {
                Dialog.chooseOption("Exchange All");
                return gaussian(50, 15000, 100, 50);
            }
            Dialog.chooseOption(1); //Exchange the rest of our planks (eg. "Exchange: 5: 25 coins")
        }
        log.info("Using notes on phials");
        NPC phials = NPCs.closest("Phials");
        if (Skills.getLevel(Skill.CONSTRUCTION) < 16) {
            Inventory.useOn(item->item.isNoted() && item.getName().toLowerCase().equals("plank"), phials);
        } else  {
            Inventory.useOn(item->item.isNoted() && item.getName().toLowerCase().equals("oak plank"), phials);
        }
        Sleep.until(Dialog::isOpen, Rand.nextInt(10*1000, 20*1000));
        return 0;
    }
}
