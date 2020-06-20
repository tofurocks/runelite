package com.runemax.bot.scripts.combat.seagulls;

import com.runemax.bot.api.entities.actors.npcs.NPCs;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.skill.Skills;
import com.runemax.bot.tasks.subtasks.WalkTask;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;

import static com.runemax.bot.Constants.POINT_PORT_SARIM;
import static com.runemax.bot.api.commons.Rand.gaussian;

@Slf4j
public class WalkToSeagulls extends Task {
    WalkTask walkToSeagulls = new WalkTask("Walk to seagulls", POINT_PORT_SARIM);

    @Override
    public boolean activate() {
        if (Skills.getLevel(Skill.ATTACK) >= 30 && Skills.getLevel(Skill.DEFENCE) >= 30){
            return false; //Done training melee
        }
        return !NPCs.closest("Seagull").isPresent() && POINT_PORT_SARIM.distanceTo(Players.getLocal().getWorldLocation()) > 10;
    }

    @Override
    public int execute() {
        log.info("Walking to seagulls");
        walkToSeagulls.execute(null);
        return gaussian(50, 15000, 1500, 750);
    }
}
