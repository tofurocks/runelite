package com.runemax.bot.scripts.combat.seagulls;

import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.entities.tile.objects.TileObjects;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.movement.Movement;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.wrappers.entity.tile.object.TileObject;
import com.runemax.bot.scripts.util.DepositBox;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetID;

import static com.runemax.bot.api.commons.Rand.gaussian;

@Slf4j
public class BankGullLoot extends Task {
    WorldPoint DEPOSIT_BOX_SPOT = new WorldPoint(3045, 3235, 0);

    @Override
    public boolean activate() {
        return Inventory.isFull();
    }

    @Override
    public int execute() {
        if (DepositBox.isOpen()) {
            log.info("Depositing inventory");
            DepositBox.depositInventory();
        }
        //Open deposit box
        if (DEPOSIT_BOX_SPOT.distanceTo(Players.getLocal().getWorldLocation()) < 35 && DEPOSIT_BOX_SPOT.isInScene(Client.getInstance())) {
            log.info("Deposit box is in scene");
            TileObject box = TileObjects.closest("Bank deposit box");
            if (box.isPresent()) {
                log.info("Interacting with deposit box");
                box.interact("Deposit");
                return gaussian(25, 15000, 2000, 1500);
            }
        }
        log.info("Walking towards deposit box spot");
        Movement.walk(DEPOSIT_BOX_SPOT);
        return gaussian(25, 15000, 2000, 1500);
    }
}
