package com.runemax.bot.api.script.blockingevent;


public abstract class BlockingEvent   {
    public abstract boolean validate();
    public abstract int execute();
}
