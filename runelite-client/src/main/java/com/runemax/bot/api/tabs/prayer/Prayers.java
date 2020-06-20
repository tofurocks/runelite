package com.runemax.bot.api.tabs.prayer;

import com.runemax.bot.api.widget.Widgets;
import com.runemax.bot.api.wrappers.widget.Widget;
import net.runelite.api.Skill;
import com.runemax.bot.api.skill.Skills;
import com.runemax.bot.api.varps.Varps;

public class Prayers {
    public static boolean isEnabled(Prayer prayer) {
        return Varps.getBit(prayer.getRl().getVarbit()) == 1;
    }

    public static void togglePrayer(Prayer prayer, boolean on) {
        Widget prayerWidget = Widgets.get(541, prayer.getChildId());
        if (prayerWidget.isPresent()) {
            prayerWidget.interact(s-> s.equalsIgnoreCase(on ? "activate": "deactivate"));
        }
    }

    public static int getPoints() {
        return Skills.getBoostedLevel(Skill.PRAYER);
    }

    public static boolean isQuickPrayerActive() {
        return Varps.getBit(4103) == 1;
    }

    public static void toggleQuickPrayer(boolean on) {
        Widget quickPrayOrb = Widgets.get(160, 14);
        if (quickPrayOrb.isPresent()) {
            quickPrayOrb.interact(s-> s.equalsIgnoreCase(on ? "activate": "deactivate"));
        }
    }
}
