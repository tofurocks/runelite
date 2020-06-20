package com.runemax.bot.scripts.fishing.tasks;

import com.runemax.bot.api.entities.tile.objects.TileObjects;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.scripts.util.DepositBox;
import lombok.extern.slf4j.Slf4j;

import static com.runemax.bot.api.commons.Rand.gaussian;

@Slf4j
public class BankFish extends Task {
    @Override
    public boolean activate() {
        return Inventory.isFull();
    }

    @Override
    public int execute() {
        if(DepositBox.isOpen()){
            if(Inventory.first("Raw shrimps").isPresent()) {
                DepositBox.depositAll("Raw shrimps");
                return gaussian(50, 15000, 200, 150);
            }
            if(Inventory.first("Raw anchovies").isPresent()) {
                DepositBox.depositAll("Raw anchovies");
                return gaussian(50, 15000, 200, 150);
            }
        }
        log.info("Opening bank");
        TileObjects.closest("Bank deposit box").interact("Deposit");
        return gaussian(50, 15000, 2*1000, 1*1000);
    }
}
