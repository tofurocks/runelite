package com.runemax.bot.scripts.planks.tasks;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.entities.tile.objects.TileObjects;
import com.runemax.bot.api.interact.Interact;
import com.runemax.bot.api.itemcontainer.bank.Bank;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.widget.WidgetQuery;
import com.runemax.bot.scripts.Store;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.WidgetID;

import static com.runemax.bot.Constants.CAMELOT_CASTLE_OUTSIDE;
import static com.runemax.bot.scripts.planks.PlankUtil.openCamelotBankChest;


@Slf4j
public class BankPlanks extends Task {
    private final WidgetQuery TELEPORT_HOUSE = new WidgetQuery(WidgetID.SPELLBOOK_GROUP_ID, w -> w.getName().contains("Teleport to House"));
    long startTime;

    public BankPlanks() {
        Store.setPlankName("unset");
        Store.setLootBankedCount(-1);
        Store.setInitialLootBankedCount(-1);
    }

    @Override
    public boolean activate() {
        return CAMELOT_CASTLE_OUTSIDE.contains(Players.getLocal())
                && (!Inventory.isFull() || Inventory.first(obj -> obj.getName().toLowerCase().contains("plank") || obj.isNoted()).isPresent()) //If we have planks or noted items we need to bank them
                && Interact.isReady();
    }

    @Override
    public int execute() {
        if (!Bank.isOpen()) {
            openCamelotBankChest();
            Sleep.until(Bank::isOpen, Rand.nextInt(2 * 1000, 4 * 1000));
        }
        return operateBank();
    }

    private int operateBank() {
        if (Inventory.first(item -> item.getName().toLowerCase().contains("plank") || item.isNoted()).isPresent()) { //Deposit any planks or noted items
            Bank.depositAll(item -> item.getName().toLowerCase().contains("plank") || item.getNote() == 799);
            Sleep.until(() -> Interact.isReady() && Inventory.first(item -> item.getName().toLowerCase().contains("plank") || item.isNoted()).isEmpty(), Rand.nextInt(5 * 1000, 10 * 1000));
            return 0;
        }

        if (Store.getPlankName().equals("unset")) {
            if (Bank.count("Teak logs") >= 26) {
                log.info("We are making teak planks");
                Store.setPlankLogName("Teak logs");
                Store.setPlankName("Teak plank");
            } else if (Bank.count("Mahogany logs") >= 26) {
                log.info("We are making mahogany planks");
                Store.setPlankLogName("Mahogany logs");
                Store.setPlankName("Mahogany plank");
            } else if (Bank.count("Oak logs") >= 26) {
                log.info("We are making oak planks");
                Store.setPlankLogName("Oak logs");
                Store.setPlankName("Oak plank");
            } else if (Bank.count("Logs") >= 26) {
                log.info("We are making regular plank");
                Store.setPlankLogName("Logs");
                Store.setPlankName("Plank");
            } else {
                if (Bank.isOpen()) { //TODO: is this extra check needed? trying to stop false quits
                    log.info("No logs left in bank, setting plank reload needed to true");
                    //Store.setPlankReloadNeeded(true);
                    //return 0;
                    return -1;
                } else {
                    log.info("Bank became closed during scan somehow");
                    return 0;
                }
            }
        }
        int lootBankedCount = Bank.count(Store.getPlankName());
        if (Store.getLootBankedCount() == -1) {
            startTime = System.currentTimeMillis();
            log.info("Setting initial loot banked count to " + lootBankedCount);
            Store.setInitialLootBankedCount(lootBankedCount);
        }
        log.info("Setting loot banked count to " + lootBankedCount);
        Store.setLootBankedCount(lootBankedCount);
        int lootGained = Store.getLootBankedCount() - Store.getInitialLootBankedCount();
        //long perHour = CommonUtils.perHour(Store.getInitialLootBankedCount(), Store.getLootBankedCount(), startTime); //TODO: add back in
        //log.info("Made " + lootGained + " planks in " + CommonUtils.formatTime(System.currentTimeMillis() - startTime) + " (" + perHour + " per hour)");
        if (!Inventory.isFull()) {
            if (Bank.count(Store.getPlankLogName()) < 26) {
                log.info("Out of current log (" + Store.getPlankName() + "), setting plank to 'unset'");
                Store.setPlankName("unset");
                return 0;
            }
            Bank.withdrawAll(Store.getPlankLogName());
            Sleep.until(() -> Interact.isReady() && Inventory.first(Store.getPlankLogName()).isPresent(), Rand.nextInt(5 * 1000, 10 * 1000));
        }
        log.info("Teleporting to house");
        TELEPORT_HOUSE.get().interact(0);
        Sleep.until(() -> TileObjects.closest("Portal").isPresent(), Rand.nextInt(5 * 1000, 10 * 1000));
        return 0;
    }

}
