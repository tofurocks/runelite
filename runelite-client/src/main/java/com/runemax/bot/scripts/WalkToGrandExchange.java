package com.runemax.bot.scripts;

import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.script.BotScript;
import com.runemax.bot.api.script.ScriptMeta;
import com.runemax.bot.tasks.subtasks.WalkTask;
import lombok.extern.slf4j.Slf4j;

import static com.runemax.bot.Constants.POINT_GRAND_EXCHANGE;

@ScriptMeta("WalkToGrandExchange")
@Slf4j
public class WalkToGrandExchange extends BotScript {
    //WorldPoint AGENT_SPOT = new WorldPoint(3242, 3474, 0);
    WalkTask walkToGrandExchange = new WalkTask("grand exchange", POINT_GRAND_EXCHANGE);

    @Override
    public void loop() {
        if (POINT_GRAND_EXCHANGE.distanceTo(Players.getLocal().getWorldLocation()) > 2) {
            walkToGrandExchange.execute(null);
        }
        Sleep.sleep(1000, 2000);
    }
}

