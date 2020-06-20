package com.runemax.bot.tasks.framework;

import com.runemax.bot.interfaces.*;
import com.runemax.bot.tasks.subtasks.WalkTask;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
public abstract class TaskSet extends Task {

    private final ArrayDeque<Task> queue = new ArrayDeque<>();

    public TaskSet(String taskDescription) {
        super(taskDescription);
    }

    public abstract void ascertainTasks();

    public Result execute(TaskSet parentQueue) {

        Task currentTask;

        if ((currentTask = queue.peek()) == null) {
            // If there were no tasks available, try and get some more
            this.ascertainTasks();
            if ((currentTask = queue.peek()) == null) {
                // Tried to get more tasks, but there were none available. So we've finished.
                return Result.SUCCESS;
            }
        }

        // Get the task name
        String currentTaskName = currentTask.getClass().getSimpleName();

        // Execute the task
        Result result;
        try {
            result = currentTask.execute(this);
        } catch (Exception e) {
            result = Result.FAILED;
            e.printStackTrace();
        }

        switch (result) {
            case RUNNING:
                log.debug("Task {} {} returned RUNNING", currentTaskName, currentTask.getTaskDescription());
                return Result.RUNNING;

            case SUCCESS:
                log.info("Task {} {} returned SUCCESS", currentTaskName, currentTask.getTaskDescription());
                this.queue.poll();
                return Result.RUNNING;

            case FAILED:
                log.error("Task {} {} returned FAILED, ditching task queue", currentTaskName, currentTask.getTaskDescription());
                queue.clear();
                return Result.FAILED;

            default:
                log.error("Task {} {} somehow returned {}", currentTaskName, currentTask.getTaskDescription(), result);
                queue.clear();
                return Result.FAILED;
        }
    }

    /**
     * Some tasks have optional dependencies, which we'll add into the queue using this method
     *
     * @param task
     * @return
     */
    private Collection<? extends Task> expandTasks(Task task) {

        ArrayList<Task> tasks = new ArrayList<>();

        if(task instanceof TaskSet) {
            // Don't expand tasksets
        } else {
            if (task instanceof RequiresEquipment) {
                log.info("Task has equipment requirements");
            }

            if (task instanceof RequiresItems) {
                log.info("Task has inventory requirements");
            }

            if (task instanceof RequiresEmptyEquipment) {
                log.info("Task requires empty equipment slots");
            }

            if (task instanceof RequiresEmptyInventory) {
                log.info("Task requires empty inventory");
            }

            if (task instanceof RequiresStartLocation) {
                WorldPoint startPoint = ((RequiresStartLocation) task).getStartLocation();
                if(startPoint != null) {
                    log.info("Task requires a specific start location, traveling to x:{} y:{} z:{}", startPoint.getX(), startPoint.getY(), startPoint.getPlane());
                    tasks.add(new WalkTask("Walking to required start location", startPoint));
                }
            }
        }

        tasks.add(task);
        return tasks;
    }

    public boolean add(Task task) {
        return queue.addAll(this.expandTasks(task));
    }

    public void addFirst(Task task) {
        queue.addFirst(task);
    }

    public boolean addAll(Collection<? extends Task> tasks) {
        for (Task task : tasks) {
            if (!queue.addAll(this.expandTasks(task))) {
                return false;
            }
        }
        return true;
    }
}
