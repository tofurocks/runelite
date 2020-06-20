package com.runemax.bot.api.tabs.magic;

import com.runemax.bot.api.wrappers.widget.Widget;

public interface Spell {
    int getLevel();
    String getName();
    Widget getWidget();
}
