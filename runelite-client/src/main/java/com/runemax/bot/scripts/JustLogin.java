package com.runemax.bot.scripts;

import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.script.BotScript;
import com.runemax.bot.api.script.ScriptMeta;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;

@ScriptMeta("JustLogin")
@Slf4j
public class JustLogin extends BotScript {
    @Override
    public void loop() {
        if(Client.getGameState() == GameState.LOGGED_IN){
            log.info("Logged in, stopping");
            System.clearProperty("script"); //Unset property so we don't end up stuck logging in over and over
            this.stopLooping();
            return;
        }
        Sleep.sleep(500);
    }

}

