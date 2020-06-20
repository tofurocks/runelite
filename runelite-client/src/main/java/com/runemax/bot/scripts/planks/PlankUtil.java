package com.runemax.bot.scripts.planks;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.interact.Interact;
import com.runemax.bot.api.itemcontainer.bank.Bank;
import com.runemax.bot.api.widget.WidgetQuery;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetID;

import static com.runemax.bot.Constants.CAMELOT_CASTLE_OUTSIDE;


@Slf4j
public class PlankUtil {
    private static final WidgetQuery TELEPORT_CAMELOT = new WidgetQuery(WidgetID.SPELLBOOK_GROUP_ID, w -> w.getName().contains("Camelot Teleport"));
    private static final WorldPoint BANK_CHEST_SPOT = new WorldPoint(2755, 3479, 0);

    public static void teleportCamelot() {
        log.info("Teleporting to Camelot");
        TELEPORT_CAMELOT.get().interact(0);
        Sleep.until(() -> CAMELOT_CASTLE_OUTSIDE.contains(Players.getLocal()), Rand.nextInt(5 * 1000, 10 * 1000));
    }

    public static void openCamelotBankChest(){
        log.info("Opening bank chest");
        LocalPoint bankChestPoint = LocalPoint.fromWorld(Client.getRl(), BANK_CHEST_SPOT);
        Interact.interact("<col=ffff>Bank chest", "Use", 10777, 3, bankChestPoint.getSceneX(), bankChestPoint.getSceneY());
        Sleep.until(Bank::isOpen, Rand.nextInt(10000, 20000));
    }
}
