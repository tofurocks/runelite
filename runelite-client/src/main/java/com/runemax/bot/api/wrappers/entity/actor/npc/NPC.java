package com.runemax.bot.api.wrappers.entity.actor.npc;

import lombok.experimental.Delegate;
import net.runelite.api.MenuAction;
import net.runelite.api.NPCDefinition;
import net.runelite.api.Entity;
import com.runemax.bot.api.game.OnGameThread;
import com.runemax.bot.api.exception.RickkPointerException;
import com.runemax.bot.api.wrappers.Identifiable;
import com.runemax.bot.api.wrappers.Nameable;
import com.runemax.bot.api.wrappers.RlWrapper;
import com.runemax.bot.api.wrappers.entity.actor.Actor;

import javax.annotation.Nonnull;

public final class NPC extends RlWrapper<net.runelite.api.NPC> implements Actor, net.runelite.api.NPC, NPCDefinition {

    private interface Excludes{
        net.runelite.api.Actor getInteracting();
    }

    private interface CompositionExcludes {
        int getCombatLevel();
    }

    private NPCDefinition composition = null;

    public NPC(net.runelite.api.NPC rl) {
        super(rl);
    }

    @Delegate(types = {net.runelite.api.NPC.class, Entity.class}, excludes = Excludes.class)
    @Override
    @Nonnull
    public net.runelite.api.NPC getRl() {
        return super.getRl();
    }

    @Delegate(excludes = {CompositionExcludes.class, Nameable.class, Identifiable.class})
    @Nonnull
    public NPCDefinition getCachedComposition(){
        if(composition == null) composition = OnGameThread.invokeAndWait(this::getTransformedDefinition);

        if(composition == null) throw new RickkPointerException();

        return composition;
    }

    @Override
    public int getMenuType(int actionIndex) {
        switch (actionIndex) {
            case 0:
                return MenuAction.NPC_FIRST_OPTION.getId();
            case 1:
                return MenuAction.NPC_SECOND_OPTION.getId();
            case 2:
                return MenuAction.NPC_THIRD_OPTION.getId();
            case 3:
                return MenuAction.NPC_FOURTH_OPTION.getId();
            case 4:
                return MenuAction.NPC_FIFTH_OPTION.getId();
        }

        throw new IllegalArgumentException("no menuaction for index" + actionIndex);
    }

    @Override
    public int getMenuTypeForUseItemOn() {
        return MenuAction.ITEM_USE_ON_NPC.getId();
    }

    @Override
    public int getMenuTypeForCastSpellOn(){
        return MenuAction.SPELL_CAST_ON_NPC.getId();
    }
}
