package com.runemax.bot.api.script;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.exception.RSDedException;
import com.runemax.bot.api.game.Client;
import com.runemax.webwalker.WebWalker;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.bot.BotOverlay;

@Slf4j
public class ScriptThread extends Thread {
    @Getter
    private final BotScript script;

    public ScriptThread(BotScript script) {
        this.script = script;
    }

    private int consecutiveExceptionCount = 0;

    @Setter
    @Getter
    private int maxConsecutiveExceptionCount = 20;

    @Override
    public void run() {
        try {
//            Events.clear();
            BotOverlay.setRenderable(null);
            script.onStart(null);

            WebWalker.initialise();

            while (script.isLooping()) {
                if (!Client.getClientThread().isAlive()) {
                    throw new RSDedException();
                }

                /** Player player = Client.getLocalPlayer();
                if (player != null) {
                    // or interacting
                    if (player.getInteracting() != null) {
                        log.info("Skip poll - Interacting with {}", player.getInteracting());
                        continue;
                    }
                } */

//                if (Client.getGameState() == GameState.LOGGED_IN && Game.isFixedMode()) {
//                     force fixed mode
//                }

                try {
                    script.outerLoop();
                    consecutiveExceptionCount = 0;
                } catch (Exception e) {
                    log.warn("Exception in loop, consecutiveExceptionCount: {}", ++consecutiveExceptionCount, e);
                    if (consecutiveExceptionCount > 10) {
                        log.error("Script failed too many times. Stopping.");
                        script.stopLooping();
                    }
                }

                Integer nextReturn = BotScript.getNextLoopDelay();
                if (nextReturn == null) {
                    Sleep.sleep(Rand.nextInt(1000, 1500));
                } else if (nextReturn < 0) {
                    script.stopLooping();
                } else {
                    Sleep.sleep(nextReturn);
                }
            }

            script.onFinish();
//            Events.clear();
            BotOverlay.setRenderable(null);
        } catch (RSDedException rsDed) {
            log.error("rs ded");
            System.exit(420);
        } catch (Exception e) {
            log.error("fatal exception in script", e);
        }
    }
}
