package com.runemax.bot.api.interact;

import com.runemax.bot.api.game.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.eventbus.Subscribe;

public class MenuDriver {

    public volatile MenuEntry[] replacedMenu = null;

    public void startReplacing(MenuEntry[] replacedMenu) {
        this.replacedMenu = replacedMenu;
    }

    public void stopReplacing() {
        replacedMenu = null;
    }

    public boolean isReplacing() {
        return replacedMenu != null;
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded e) {
        if (replacedMenu != null) {
            Client.setMenuEntries(replacedMenu);
        }
    }
}
