package com.runemax.bot.api.tabs.quest;

import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.widget.WidgetQuery;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

@Slf4j
public class QuestTab {

    public static void setMinigame(Minigame minigame) {
        log.info("Setting minigame teleport to {}", minigame.name());
        Client.runScript(124, minigame.getId());
    }

    public static void teleMinigame(Minigame minigame) {
        setMinigame(minigame);
        //idk if need sleep here
        WidgetInfo teleInfo = WidgetInfo.MINIGAME_TELEPORT_BUTTON;
        new WidgetQuery(teleInfo.getGroupId(), teleInfo.getChildId()).withGrandChild(w -> w.hasAction("teleport to")).get().interact("teleport to");
        log.info("Teleporting to minigame");
        Sleep.until(() -> Players.getLocal().getAnimation() == 4847, 5000);
        WorldPoint startLoc = Players.getLocal().getWorldLocation();
        Sleep.until(() -> !Players.getLocal().getWorldLocation().equals(startLoc), 30 * 1000);
    }
}
