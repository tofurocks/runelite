package com.runemax.bot.api.script;

import com.runemax.bot.api.script.blockingevent.BlockingEvent;
import com.runemax.bot.api.script.blockingevent.BlockingEventManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BotScript {

    @Getter
    private final BlockingEventManager blockingEventManager = new BlockingEventManager();

    @Getter
    private volatile boolean looping = true;

    @Setter
    @Getter
    private static volatile Integer nextLoopDelay = null;

    protected abstract void loop();
    public void onStart(String launchArg) {}
    public void onFinish() {}

    public final void outerLoop(){
        nextLoopDelay = null;

        BlockingEvent blocking = blockingEventManager.getBlocking();
        if(blocking != null){
            nextLoopDelay = blocking.execute();
            return;
        }

        loop();
    }

    public final void stopLooping(){
        looping = false;
    }
}
