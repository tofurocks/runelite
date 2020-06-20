package com.runemax.bot.scripts;

import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.script.BotScript;
import com.runemax.bot.api.script.ScriptMeta;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.scripts.planks.tasks.BankPlanks;
import com.runemax.bot.scripts.planks.tasks.PlanksTeleportToPOH;
import com.runemax.bot.scripts.planks.tasks.TalkToButler;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.runemax.bot.api.commons.Rand.gaussian;
import static com.runemax.bot.api.commons.Sleep.sleep;


@ScriptMeta("Make Planks")
@Slf4j
public class MakePlanks extends BotScript {
    final List<Task> tasks = new ArrayList<>();

    @Override
    protected void loop() {
        for (Task task : tasks) {
            if (task.activate()) {
                log.info("Executing " + task.getClass().getSimpleName());
                int wait = task.execute();
                if (wait < 0) {
                    log.info("Stopping script");
                    stopLooping();
                    return;
                }
                sleep(wait);
                return;
            }
        }
        Sleep.sleep(gaussian(50, 15 * 1000, 1000, 500));
    }

    @Override
    public void onStart(String launchArg) {
        super.onStart(launchArg);
        tasks.add(new BankPlanks());
        tasks.add(new TalkToButler());
        tasks.add(new PlanksTeleportToPOH());
    }


}
