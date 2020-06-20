package com.runemax.bot.scripts;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.script.BotScript;
import com.runemax.bot.api.script.ScriptMeta;
import com.runemax.bot.tasks.framework.TaskSet;
import com.runemax.bot.tasks.tasksets.AutomatonTaskSet;
import lombok.extern.slf4j.Slf4j;

@ScriptMeta("Automaton")
@Slf4j
public class Automaton extends BotScript {

    /**
     * The limit of how long we're going to allow this bot to run for
     */
    private final long timeLimit = System.currentTimeMillis() + Rand.nextDuration(3600, 3600 * 4);
    private TaskSet queue;

    @Override
    public void onStart(String launchArg) {
        queue = new TaskSet("Automaton task set") {
            @Override
            public void ascertainTasks() {
//                queue.add(new RomeoAndJulietQuestTask("Romeo and Juliet task set"));
                queue.add(new AutomatonTaskSet("Random walking"));
            }
        };
    }

    @Override
    public void onFinish() {
//        Game.logout();
//        Sleep.until(() -> Client.getGameState() == GameState.LOGIN_SCREEN, 30000);
//        System.exit(1);
    }

    @Override
    protected void loop() {

        // Check to see if we've overrun our time limit
        if (System.currentTimeMillis() > timeLimit) {
            this.stopLooping();
            return;
        }

        // Run the queue
        queue.execute(queue);

    }
}

