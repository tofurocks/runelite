package com.runemax.bot.scripts.construction.tasks;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.itemcontainer.bank.Bank;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.itemcontainer.inventory.InventoryItem;
import com.runemax.bot.api.script.Task;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EquipConstructionGear extends Task {
    @Override
    public boolean activate() {
        if(Bank.isOpen()) {
            return false; //Finish banking first
        }
        return Inventory.first("Ring of wealth (5)", "Skills necklace(4)", "Dust battlestaff").isPresent();
    }

    @Override
    public int execute() {
        InventoryItem staffOfAir = Inventory.first("Staff of air");
        if(staffOfAir.isPresent()){
            staffOfAir.interact("Drop");
            return Rand.nextInt(500, 1000);
        }
        InventoryItem item = Inventory.first("Ring of wealth (5)", "Skills necklace(4)", "Dust battlestaff");
        log.info("Equipping " + item.getName());
        if(item.getName().equals("Dust battlestaff")){
            item.interact("Wield");
        } else {
            item.interact("Wear");
        }
        return Rand.nextInt(500, 1000);
    }
}
