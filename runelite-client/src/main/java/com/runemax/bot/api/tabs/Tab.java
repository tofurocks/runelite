package com.runemax.bot.api.tabs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.widgets.WidgetInfo;

@AllArgsConstructor
public enum Tab {
    COMBAT(WidgetInfo.FIXED_VIEWPORT_COMBAT_TAB.getChildId()),
    SKILLS(WidgetInfo.FIXED_VIEWPORT_STATS_TAB.getChildId()),
    QUESTS(WidgetInfo.FIXED_VIEWPORT_QUESTS_TAB.getChildId()),
    INVENTORY(WidgetInfo.FIXED_VIEWPORT_INVENTORY_TAB.getChildId()),
    EQUIPMENT(WidgetInfo.FIXED_VIEWPORT_EQUIPMENT_TAB.getChildId()),
    PRAYER(WidgetInfo.FIXED_VIEWPORT_PRAYER_TAB.getChildId()),
    MAGIC(WidgetInfo.FIXED_VIEWPORT_MAGIC_TAB.getChildId()),
    //CLAN_CHAT(WidgetInfo.FIXED_VIEWPORT_CLAN_CHAT_TAB.getChildId()), //Outdated
    FRIENDS(WidgetInfo.FIXED_VIEWPORT_FRIENDS_TAB.getChildId()),
    ACCOUNT(WidgetInfo.FIXED_VIEWPORT_IGNORES_TAB.getChildId()),
    OPTIONS(WidgetInfo.FIXED_VIEWPORT_OPTIONS_TAB.getChildId()),
    ;

    @Getter
    private int childId;
}
