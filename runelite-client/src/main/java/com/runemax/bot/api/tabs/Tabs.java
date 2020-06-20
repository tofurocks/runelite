package com.runemax.bot.api.tabs;

import com.runemax.bot.api.widget.Widgets;
import net.runelite.api.widgets.WidgetID;

public class Tabs {
    public static void open(Tab tab){
        Widgets.get(WidgetID.FIXED_VIEWPORT_GROUP_ID, tab.getChildId()).interact(a->true);
    }
}
