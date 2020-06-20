package com.runemax.bot.scripts;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.wrappers.entity.tile.object.TileObject;
import com.runemax.bot.scripts.construction.tasks.*;
import com.runemax.bot.scripts.fishing.tasks.BankFish;
import com.runemax.bot.scripts.fishing.tasks.CatchFish;
import com.runemax.bot.scripts.generic.ToggleRun;
import lombok.extern.slf4j.Slf4j;
import com.runemax.bot.api.entities.tile.items.TileItems;
import com.runemax.bot.api.entities.tile.objects.TileObjects;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.script.BotScript;
import com.runemax.bot.api.script.ScriptMeta;

import java.util.ArrayList;
import java.util.List;

import static com.runemax.bot.api.commons.Sleep.sleep;

@ScriptMeta("DraynorFishing")
@Slf4j
public class DraynorFishing extends BotScript {

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
        tasks.add(new BankFish());
        tasks.add(new CatchFish());
    }

}

