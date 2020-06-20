package com.runemax.bot.api.script.blockingevent.events;

import com.runemax.bot.api.game.Game;
import com.runemax.bot.api.script.blockingevent.BlockingEvent;
import com.runemax.bot.api.widget.Widgets;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ResizeableChanged;
import net.runelite.client.eventbus.Subscribe;

@Slf4j
public class ResizableEvent extends BlockingEvent {
    private volatile boolean isResized = true;

    public ResizableEvent(){
//        Events.register(this);
    }

    @Override
    public boolean validate() {
        return isResized;
    }

    @Override
    public int execute() {
        //todo re-add to blocking event manager
        log.info("resizable event");
        if (!Game.isFixedMode()) {
            Widgets.get(261, 33).interact("Fixed mode");
            return 1000;
        }
        isResized = false;
        return 100;
    }

    @Subscribe
    public void onResizeableChanged(ResizeableChanged e){
        log.info("setting isResized: " + e.isResized());
        isResized = e.isResized();
    }
}
