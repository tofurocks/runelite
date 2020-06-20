package com.runemax.bot.scripts.construction.tasks;

import com.runemax.bot.api.coords.RectangularArea;
import com.runemax.bot.api.skill.Skills;
import lombok.extern.slf4j.Slf4j;
import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.actors.npcs.NPCs;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.entities.tile.objects.TileObjects;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.widget.Dialog;
import com.runemax.bot.api.widget.Widgets;
import com.runemax.bot.api.wrappers.entity.tile.object.TileObject;
import com.runemax.bot.api.wrappers.widget.Widget;
import net.runelite.api.Skill;

import static com.runemax.bot.api.commons.Rand.gaussian;
import static com.runemax.bot.scripts.construction.util.ConstructionUtil.COMPLETED_FURNITURE_NAMES;

@Slf4j
public class RemoveFurniture extends Task {
    RectangularArea FISHING_GUILD_TO_ARDOUGNE = new RectangularArea(2592, 3400, 2675, 3324);

    @Override
    public boolean activate() {
        if (FISHING_GUILD_TO_ARDOUGNE.contains(Players.getLocal())) {
            return false; //Prevent triggering when near furniture in fishing guild / ardougne area
        }
        if (NPCs.closest("Phials").isPresent()) {
            log.info("We are not in our house");
            return false; //We are not in our house

        }
        Widget conWidget = Widgets.get(458, 4);
        if (conWidget.isPresent() && !conWidget.isHidden()) {
            log.info("Construction widget is already open");
            return false; //We should finish with our open interface first
        }
        return TileObjects.closest(COMPLETED_FURNITURE_NAMES).isPresent();
    }

    @Override
    public int execute() {
        if (Dialog.canContinue()) {
            Dialog.continueSpace();
            return gaussian(50, 15000, 500, 350);
        }
        if (Dialog.hasOption("Remove")) {
            Dialog.chooseOption("Remove");
            return gaussian(50, 15000, 500, 350);
        }
        if (Dialog.hasOption("Yes")) {
            Dialog.chooseOption("Yes");
            return gaussian(50, 15000, 500, 350);
        }
        TileObject completedItem = TileObjects.closest(COMPLETED_FURNITURE_NAMES);
        if (completedItem.isPresent()) {
            completedItem.interact("Remove");
            Sleep.until(() -> Players.getLocal().getAnimation() == -1, Rand.nextInt(5 * 1000, 10 * 1000));
        }
        return gaussian(50, 15 * 1000, 1000, 750);
    }
}
