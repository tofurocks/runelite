package com.runemax.bot.api.game;

import com.runemax.bot.api.widget.WidgetQuery;
import com.runemax.bot.api.widget.Widgets;
import com.runemax.bot.api.wrappers.widget.Widget;
import net.runelite.api.widgets.WidgetInfo;

public class Game {
    public static boolean isFixedMode(){
        Widget widget = Widgets.get(WidgetInfo.FIXED_VIEWPORT);
        return !widget.isEmpty() && !widget.isHidden();
    }

    private static final WidgetQuery LOGOUT = new WidgetQuery(182, widget -> widget.hasAction("logout"));
    private static final WidgetQuery WORLD_HOPPER_LOGOUT = new WidgetQuery(69, widget -> widget.hasAction("logout"));

    public static void logout(){
        Widget logout = LOGOUT.get();
        if(logout.isEmpty()){
            WORLD_HOPPER_LOGOUT.get().interact("logout");
        }else {
            logout.interact("logout");
        }
    }
}
