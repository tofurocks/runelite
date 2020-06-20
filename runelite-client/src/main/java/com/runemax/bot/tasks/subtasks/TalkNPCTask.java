package com.runemax.bot.tasks.subtasks;

import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.coords.RectangularArea;
import com.runemax.bot.api.entities.actors.npcs.NPCs;
import com.runemax.bot.api.exception.BotException;
import com.runemax.bot.api.widget.Dialog;
import com.runemax.bot.api.wrappers.entity.actor.npc.NPC;
import com.runemax.bot.interfaces.RequiresStartLocation;
import com.runemax.bot.tasks.framework.Task;
import com.runemax.bot.tasks.framework.TaskSet;
import com.runemax.webwalker.localpath.LocalPath;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

@Slf4j
public class TalkNPCTask extends Task implements RequiresStartLocation {

    private final String npcName;
    private final WorldPoint point;
    private final String[] chatOptions;
    private boolean haveSpoken = false;

    public TalkNPCTask(String taskDescription, String npcName) {
        this(taskDescription, npcName, new String[0]);
    }

    public TalkNPCTask(String taskDescription, String npcName, String[] chatOptions) {
        super(taskDescription);
        this.npcName = npcName;
        this.chatOptions = chatOptions;
        this.point = null;
    }

    public TalkNPCTask(String taskDescription, String npcName, RectangularArea area, String[] chatOptions) {
        this(taskDescription, npcName, area.getRandomPoint(), chatOptions);
    }

    public TalkNPCTask(String taskDescription, String npcName, RectangularArea area) {
        this(taskDescription, npcName, area.getRandomPoint(), new String[0]);
    }


    public TalkNPCTask(String taskDescription, String npcName, WorldPoint point, String[] chatOptions) {
        super(taskDescription);
        this.npcName = npcName;
        this.point = point;
        this.chatOptions = chatOptions;
    }

    @Override
    public Result execute(TaskSet parentQueue) {

        NPC npc =  NPCs.closest(npcName);

        if (!npc.isPresent() || !LocalPath.reachable(npc.getLocalLocation())) {
            log.info("Unable to reach target, adding additional walk task");
            parentQueue.addFirst(new WalkTask("Walking to " + npc.getName(), npc.getWorldLocation()));
            return Result.RUNNING;
        }

        // Not spoken to yet, and not in dialog
        if (!haveSpoken && !Dialog.isOpen()) {
            npc.interact(0);
            Sleep.until(Dialog::isOpen, 15000);
            haveSpoken = true;
            return Result.RUNNING;
        }

        // Can continue chat
        else if (Dialog.canContinue()) {
            Dialog.continueSpace();
            return Result.RUNNING;
        }

        // Do we have a chat option presented?
        else if (Dialog.hasOption(chatOptions)) {
            Dialog.chooseOption(chatOptions);
            return Result.RUNNING;
        }

        // Spoken to, and not in dialog
        else if (haveSpoken && !Dialog.isOpen()) {
            return Result.SUCCESS;
        }

        // Weird edge case?!
        else {
            throw new BotException("Chat somehow hit edge-case");
        }
    }

    @Override
    public WorldPoint getStartLocation() {
        return this.point;
    }
}
