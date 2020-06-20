package com.runemax.bot.scripts;


import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.script.BotScript;
import com.runemax.bot.api.script.ScriptMeta;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.scripts.construction.tasks.*;
import com.runemax.bot.scripts.generic.ToggleRun;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.runemax.bot.api.commons.Sleep.sleep;

@ScriptMeta("Construction")
@Slf4j
public class Construction extends BotScript {
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
        sleep(Rand.nextInt(250, 1000));
    }

    @Override
    public void onStart(String launchArg) {
        super.onStart(launchArg);
        tasks.add(new StopIfDoneConstruction());
        tasks.add(new BuyHouse());
        tasks.add(new TeleportToPOH());
        tasks.add(new BuildFurniture());
        tasks.add(new OperateFurnitureWidget());
        tasks.add(new ToggleRun());
        tasks.add(new RemoveFurniture());
        tasks.add(new ExitPOH());
        tasks.add(new ExchangePhials());
        tasks.add(new EnterPOH());
        tasks.add(new HireServant());
    }
}
