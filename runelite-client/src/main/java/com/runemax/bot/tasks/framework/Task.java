package com.runemax.bot.tasks.framework;

import lombok.Getter;

public abstract class Task {

    @Getter
    private final String taskDescription;

    public Task(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public abstract Result execute(TaskSet parentQueue);
    public enum Result {
        SUCCESS,
        RUNNING,
        FAILED
    }
}
