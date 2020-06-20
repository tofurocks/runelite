package com.runemax.bot.api.widget;

import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.wrappers.widget.Widget;
import net.runelite.api.widgets.WidgetInfo;

public class Widgets {

    public static Widget get(WidgetInfo widgetInfo) {
        return new Widget(Client.getWidget(widgetInfo));
    }

    public static Widget get(int group, int child) {
        return new Widget((Client.getWidget(group, child)));
    }

    public static Widget get(int group, int child, int grandChild) {
        return get(group, child).getChild(grandChild);
    }

    public static Widget[] getGroup(int id) {
        return Widget.wrap(Client.getWidgets()[id]);
    }
}
