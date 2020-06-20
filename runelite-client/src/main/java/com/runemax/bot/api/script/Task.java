package com.runemax.bot.api.script;

public abstract class Task {
    public abstract boolean activate();
    //return number of ms to sleep for after execute finishes
    public abstract int execute();
}
