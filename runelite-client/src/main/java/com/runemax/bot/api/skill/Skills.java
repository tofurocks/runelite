package com.runemax.bot.api.skill;

import com.runemax.bot.api.game.Client;
import net.runelite.api.Skill;

public class Skills {
    public static int getLevel(Skill skill){
        return Client.getRealSkillLevel(skill);
    }

    public static int getBoostedLevel(Skill skill){
        return Client.getBoostedSkillLevel(skill);
    }

    public static int getMissing(Skill skill){
        return getLevel(skill) - getBoostedLevel(skill);
    }

    public static int getMissingHealth(){
        return getMissing(Skill.HITPOINTS);
    }

    public static int getHealth(){
        return getBoostedLevel(Skill.HITPOINTS);
    }
}
