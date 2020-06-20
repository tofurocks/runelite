package com.runemax.bot.tasks.subtasks;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.tile.objects.TileObjects;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.wrappers.entity.tile.object.TileObject;
import com.runemax.bot.tasks.framework.Task;
import com.runemax.bot.tasks.framework.TaskSet;
import com.runemax.webwalker.WebWalker;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Player;

@Slf4j
public class InteractTask extends Task {

    private final String tileItemName;
    private final String interaction;

    public InteractTask(String taskDescription, String tileItemName, String interaction) {
        super(taskDescription);
        this.tileItemName = tileItemName;
        this.interaction = interaction;
    }

    @Override
    public Result execute(TaskSet parentQueue) {
        TileObject object = TileObjects.closest(tileItemName);

        // Interact with our object
        object.interact(interaction);

        // sleep one tick
        Sleep.sleep(Rand.nextDuration(1.2f, 1.8f));

        // sleep until we're no longer interacting
        Sleep.until(() -> {
            Player player = Client.getLocalPlayer();
            if (player == null) return false;
            return player.getInteracting() == null;
        }, 15000);

        Sleep.until(() -> !WebWalker.isMoving(), 15000);

        // sleep one tick
        Sleep.sleep(Rand.nextDuration(1.2f, 1.8f));

        return Result.SUCCESS;
    }

}
