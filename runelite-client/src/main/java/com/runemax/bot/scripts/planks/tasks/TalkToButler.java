package com.runemax.bot.scripts.planks.tasks;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.actors.npcs.NPCs;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.entities.tile.objects.TileObjects;
import com.runemax.bot.api.input.Keyboard;
import com.runemax.bot.api.interact.Interact;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.widget.Dialog;
import com.runemax.bot.api.widget.WidgetQuery;
import com.runemax.bot.api.wrappers.entity.actor.npc.NPC;
import com.runemax.bot.scripts.Store;
import lombok.extern.slf4j.Slf4j;

import static com.runemax.bot.api.commons.Rand.gaussian;
import static com.runemax.bot.scripts.planks.PlankUtil.teleportCamelot;

@Slf4j
public class TalkToButler extends Task {
    private final WidgetQuery CLOSE_HOUSE_OPTIONS = new WidgetQuery(370, w -> w.hasAction("Close"));

    @Override
    public boolean activate() {
        return TileObjects.closest("Portal").isPresent();
    }

    @Override
    public int execute() {
        if (Inventory.count("Coins") < 39000) {
            log.info("Not enough coins to continue, stopping");
            return -1;
        }
        if (Inventory.first("Law rune").isEmpty()) {
            log.info("Out of law runes, stopping");
            return -1;
        }
        if(Dialog.isEnterAmountOpen()){
            Dialog.enterAmount(26);
            Sleep.sleep(gaussian(25, 15*1000, 100, 50));
            Keyboard.enter();
            Sleep.sleep(gaussian(25, 15*1000, 100, 50));
            return 0;
        }
        if (Inventory.first(item -> item.getName().toLowerCase().contains("plank")).isPresent()) {
            log.info("Accidentally ended up with planks in our POH, teleporting to camelot");
            teleportCamelot();
            return 0;
        }
        if (Inventory.first(item -> item.getName().toLowerCase().contains("logs")).isPresent()
                && !Inventory.isFull()) {
            log.info("We have a semi-full inventory of logs, teleporting to camelot");
            teleportCamelot();
            return 0;
        }
        if (Dialog.canContinue()) {
            Dialog.continueSpace();
            return gaussian(50, 15 * 1000, 200, 150);
        }
        if (!Inventory.first(Store.getPlankLogName()).isPresent()) {
            teleportCamelot();
            return 0;
        }
        if (!Dialog.isOpen()) {
            NPC butler = NPCs.closest("Demon butler");
            if (butler.isPresent() && butler.getWorldLocation().distanceTo(Players.getLocal().getWorldLocation()) <= 2) {
                log.info("Butler is here, talking to him");
                butler.interact("Talk-to");
                Sleep.until(() -> Interact.isReady() && Dialog.isOpen(), Rand.nextInt(2 * 1000, 4 * 1000));
                return 0;
            }
            if (Store.isButlerBugged()) {
                log.info("Butler bugged, closing house options");
                Store.setButlerBugged(false);
                CLOSE_HOUSE_OPTIONS.get().interact(0);
                return gaussian(50, 15 * 1000, 100, 40);

            }
            log.info("Calling servant");
            Interact.interact("", "View House", 1, 57, -1, 17104996);
            Sleep.sleep(gaussian(50, 15 * 1000, 100, 40));
            Sleep.until(Interact::isReady, Rand.nextInt(5 * 1000, 10 * 1000));
            Interact.interact("", "Call Servant", 1, 57, -1, 24248339);
            Sleep.until(() -> Interact.isReady() && Dialog.isOpen()
                            || Store.isButlerBugged()
                            || NPCs.closest(npc -> npc.getName().equals("Demon butler") && npc.getWorldLocation().distanceTo(Players.getLocal().getWorldLocation()) <= 2).isPresent(),
                    Rand.nextInt(4 * 1000, 6 * 1000));
            return 0;
        }
        if (Dialog.hasOption("Take to sawmill")) {
            log.info("Trying fast path");
            Dialog.chooseOption("Take to sawmill");
            Sleep.until(() -> Interact.isReady() && Dialog.canContinue(), Rand.nextInt(2 * 1000, 4 * 1000));
            Dialog.continueSpace();
            Sleep.until(() -> Dialog.hasOption("Yes") && Interact.isReady(), Rand.nextInt(1000, 2000));
            Dialog.chooseOption("Yes");
            Sleep.until(() -> Interact.isReady() && Dialog.canContinue(), Rand.nextInt(2 * 1000, 4 * 1000));
            Dialog.continueSpace();
            Sleep.until(() -> Interact.isReady() && !Dialog.canContinue(), Rand.nextInt(2 * 1000, 4 * 1000)); //TODO: Can we get away without !Dialog.canContinue here without bugging anything?
            log.info("Finished fast path, teleporting to camelot");
            teleportCamelot();
            return 0;
        } else if (Dialog.hasOption("Yes")) {
            Dialog.chooseOption("Yes");
        } else if (Dialog.hasOption("Okay, here's 10,000 coins")) {
            Dialog.chooseOption("Okay, here's 10,000 coins");
        } else if (Dialog.hasOption("Take them back to the bank")) {
            Dialog.chooseOption("Take them back to the bank");
        } else if (Dialog.hasOption("Greet guests")) {
            log.info("Setting up butler for log type");
            NPC butler = NPCs.closest("Demon butler");
            Inventory.useOn(item -> item.getName().equals(Store.getPlankLogName()), butler);
            Sleep.until(() -> Interact.isReady() && Dialog.canContinue(), Rand.nextInt(2 * 1000, 4 * 1000));
            Dialog.continueSpace();
            Sleep.until(() -> Interact.isReady() && Dialog.hasOption("Sawmill"), Rand.nextInt(2 * 1000, 4 * 1000));
            Dialog.chooseOption("Sawmill");
            Sleep.until(() -> Interact.isReady() && Dialog.isEnterAmountOpen(), Rand.nextInt(2 * 1000, 4 * 1000));
            Dialog.enterAmount(26);
            Sleep.until(() -> Interact.isReady(), Rand.nextInt(2 * 1000, 4 * 1000));
            Sleep.sleep(gaussian(25, 15*1000, 100, 50));
            Keyboard.enter();
            Sleep.until(() -> Dialog.hasOption("Yes") && Dialog.canContinue(), Rand.nextInt(2 * 1000, 4 * 1000));
            Dialog.chooseOption("Yes");
            Sleep.until(() -> Interact.isReady() && Dialog.canContinue(), Rand.nextInt(2 * 1000, 4 * 1000));
            Dialog.continueSpace();
            Sleep.until(() -> Interact.isReady() && !Dialog.canContinue(), Rand.nextInt(2 * 1000, 4 * 1000)); //TODO: Can we get away without !Dialog.canContinue here without bugging anything?
            log.info("Set up butler for log type, teleporting to camelot");
            teleportCamelot();
        }
        return 0;
    }

}
