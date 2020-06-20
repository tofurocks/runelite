package com.runemax.bot.tasks.subtasks;

import com.runemax.bot.api.skill.Skills;
import com.runemax.bot.skills.construction.ConstructionObjective;
import com.runemax.bot.tasks.framework.Task;
import com.runemax.bot.tasks.framework.TaskSet;
import net.runelite.api.Skill;

public class TrainConstructionTask extends Task {

    private static final int STEEL_NAILS_NEEDED = 300;
    private static final int WOODEN_PLANKS_NEEDED = 110;
    private static final int OAK_PLANKS_NEEDED = 1670;

    ConstructionObjective currentObjective = null;

    public TrainConstructionTask(String taskDescription) {
        super(taskDescription);
    }

    @Override
    public Result execute(TaskSet parentQueue) {

        int constructionLevel = Skills.getLevel(Skill.CONSTRUCTION);
        currentObjective = ConstructionObjective.getConstructionObjective(constructionLevel);

        return Result.SUCCESS;
    }
}
