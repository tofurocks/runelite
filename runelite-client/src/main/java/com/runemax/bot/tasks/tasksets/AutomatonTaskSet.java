package com.runemax.bot.tasks.tasksets;

import com.runemax.bot.MapPoints;
import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.tasks.framework.TaskSet;
import com.runemax.bot.tasks.subtasks.WalkTask;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

@Slf4j
public class AutomatonTaskSet extends TaskSet {

    public AutomatonTaskSet(String taskDescription) {
        super(taskDescription);
    }

    @Override
    public void ascertainTasks() {
        WorldPoint randomPoint = getRandomLocation();
        this.add(new WalkTask("Walk to random point", randomPoint.getX(), randomPoint.getY(), 0));
    }

    private WorldPoint getRandomLocation() {
        int index = Rand.nextInt(0, MapPoints.values().length - 1);
        MapPoints point = MapPoints.values()[index];

        log.info("Picked location {} ", point.name());
        return point.getPoint();
    }

}
