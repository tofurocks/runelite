package com.runemax.bot.api.tabs.magic;

import com.runemax.bot.api.widget.WidgetQuery;
import com.runemax.bot.api.wrappers.widget.Widget;
import lombok.Getter;
import net.runelite.api.widgets.WidgetID;

public enum Arceuus implements Spell{
    LUMBRIDGE_GRAVEYARD_TELEPORT(6, "Lumbridge Graveyard Teleport"),
    SALVE_GRAVEYARD_TELEPORT(40, "Salve Graveyard Teleport")
    ;

    @Getter
    private final int level;
    @Getter
    private final String name;
    private final WidgetQuery widgetQuery;

    Arceuus(int level, String name) {
        this.level = level;
        this.name = name;
        widgetQuery = new WidgetQuery(WidgetID.SPELLBOOK_GROUP_ID, w->w.getName().contains(name));
    }

    @Override
    public Widget getWidget() {
        return widgetQuery.get();
    }
}
