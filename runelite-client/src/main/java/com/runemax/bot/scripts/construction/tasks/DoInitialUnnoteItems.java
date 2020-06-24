package com.runemax.bot.scripts.construction.tasks;

import com.runemax.bot.api.commons.Pair;
import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.itemcontainer.bank.Bank;
import com.runemax.bot.api.itemcontainer.bank.BankInvItem;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.itemcontainer.inventory.InventoryItem;
import com.runemax.bot.api.script.Task;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DoInitialUnnoteItems extends Task {
    List<Pair<String, Integer>> requiredItems = new ArrayList<>();

    public DoInitialUnnoteItems(){
        requiredItems.add(new Pair("Teak plank", 1));
        requiredItems.add(new Pair("Law rune", 1));
        requiredItems.add(new Pair("Bolt of cloth", 6));
        requiredItems.add(new Pair("Dust battlestaff", 1));
        requiredItems.add(new Pair("Saw", 1));
        requiredItems.add(new Pair("Hammer", 1));
        requiredItems.add(new Pair("Steel nails", 200));
        requiredItems.add(new Pair("Ring of wealth (5)", 1));
        requiredItems.add(new Pair("Skills necklace(4)", 1));
    }
    @Override
    public boolean activate() {
        if(Bank.isOpen()){
            return true; //Handle bank stuff
        }
        return Inventory.first(item->item.isNoted() && !(item.getName().equals("Plank") || item.getName().equals("Oak plank"))).isPresent();
    }

    @Override
    public int execute() {
        if(!Bank.isOpen()){
            Bank.open();
            Sleep.until(Bank::isOpen, Rand.nextInt(5*1000, 10*1000));
            return 0;
        }
        //Deposit noted crap that we want to be unnoted
        InventoryItem notedCrap = Inventory.first(item -> item.isNoted() && !(item.getName().equals("Plank") || item.getName().equals("Oak plank")));
        if(notedCrap.isPresent()){
            log.info("Depositing noted " + notedCrap.getName());
            Bank.depositAll(notedCrap.getId());
            return Rand.nextInt(500, 1000);
        }
        for(Pair<String, Integer> requiredItem : requiredItems){
            String itemName = requiredItem.getLeft();
            int itemQuantity = requiredItem.getRight();
            if(Inventory.count(itemName) < itemQuantity){
                if(Bank.first(itemName).isEmpty()){
                    log.info("We don't have a " + itemName + ", stopping");
                    return -1;
                }
                log.info("withdrawing " + itemName);
                Bank.withdrawAll(itemName);
                return Rand.nextInt(500, 1000);
            }
        }
        log.info("Done withdrawing stuff, closing bank");
        Bank.close();
        return Rand.nextInt(500, 1000);
    }
}
