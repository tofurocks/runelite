package com.runemax.bot.api.tabs.magic;

import com.runemax.bot.api.widget.WidgetQuery;
import com.runemax.bot.api.wrappers.widget.Widget;
import lombok.Getter;
import net.runelite.api.widgets.WidgetID;

//tried to put this in Spell interface but wouldnt show up somehow when improt jar as script dependency even tho it showd up here.  i think intellij/maven assemlby bug because it was still in the jar if decompiled
public enum Modern implements Spell{
    WIND_STRIKE(1, "Wind Strike"),
    CONFUSE(3, "Confuse"),
    WEAKEN(11, "Weaken"),
    CURSE(19, "Curse"),
    LOW_LEVEL_ALCHEMY(21, "Low Level Alchemy"),
    TELEKINETIC_GRAB(33, "Telekinetic Grab"),
    HIGH_LEVEL_ALCHEMY(55, "High Level Alchemy"),
    ;

    @Getter
    private final int level;
    @Getter
    private final String name;
    private final WidgetQuery widgetQuery;

    Modern(int level, String name) {
        this.level = level;
        this.name = name;
        widgetQuery = new WidgetQuery(WidgetID.SPELLBOOK_GROUP_ID, w->w.getName().contains(name));
    }

    @Override
    public Widget getWidget() {
        return widgetQuery.get();
    }
}
