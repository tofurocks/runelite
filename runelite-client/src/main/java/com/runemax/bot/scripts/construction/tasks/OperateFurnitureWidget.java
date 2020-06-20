package com.runemax.bot.scripts.construction.tasks;

import net.runelite.api.Skill;
import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.tile.objects.TileObjects;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.skill.Skills;
import com.runemax.bot.api.widget.Widgets;
import com.runemax.bot.api.wrappers.widget.Widget;

import static com.runemax.bot.scripts.construction.util.ConstructionUtil.COMPLETED_FURNITURE_NAMES;

public class OperateFurnitureWidget extends Task {
    //first slot widget (crude wooden chair, wooden bookcase): 458, 4

    @Override
    public boolean activate() {
        Widget conWidget = Widgets.get(458, 4);
        return conWidget.isPresent() && !conWidget.isHidden();
    }

    @Override
    public int execute() {
        if (Skills.getLevel(Skill.CONSTRUCTION) < 33) {
            Widgets.get(458, 4).interact("Build");
        } else {
            Widgets.get(458, 5).interact("Build"); //Second option in build menu - works for bell-pull
        }
        Sleep.sleep(1000);
        Sleep.until(() -> TileObjects.closest(COMPLETED_FURNITURE_NAMES).isPresent(), Rand.nextInt(10 * 1000, 15 * 1000));
        return 0;
    }
}
