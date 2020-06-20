package com.runemax.bot.api.script.blockingevent.events;

import com.runemax.bot.api.widget.Widgets;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.WidgetID;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.movement.Movement;
import com.runemax.bot.api.script.blockingevent.BlockingEvent;

@Slf4j
public class WelcomeEvent extends BlockingEvent {
    @Override
    public boolean validate() {
        return check();
    }

    @Override
    public int execute() {
        log.info("welcome event");
        Movement.walk(Players.getLocal());//fuck it
        return 500;
    }

    public static boolean check(){
        return Widgets.get(WidgetID.LOGIN_CLICK_TO_PLAY_GROUP_ID, 81).isPresent() || Widgets.get(413, 77).isPresent();
    }
}
