package com.runemax.bot.api.wrappers.entity.actor;

import com.runemax.bot.api.wrappers.entity.actor.npc.NPC;
import com.runemax.bot.api.wrappers.entity.actor.player.Player;
import com.runemax.bot.api.exception.BotException;
import com.runemax.bot.api.wrappers.entity.Entity;

import javax.annotation.Nullable;

public interface Actor<A extends net.runelite.api.Actor> extends Entity, net.runelite.api.Actor {
    @Override
    default int getMenuParam0() {
        return 0;
    }

    @Override
    default int getMenuParam1() {
        return 0;
    }

    @Override
    default int getMenuIdentifier(Integer actionIndex) {
        return getIndex();
    }

    A getRl();

    int getIndex();

    @Override
    default int getX() {
        return getWorldLocation().getX();
    }

    @Override
    default int getY() {
        return getWorldLocation().getY();
    }

    @Override
    default int getPlane() {
        return getWorldLocation().getPlane();
    }

    @Override
    @Nullable
    default net.runelite.api.Actor getInteracting() {
        net.runelite.api.Actor interacting = getRl().getInteracting();

        if(interacting == null){
            return null;
        }

        if (interacting instanceof net.runelite.api.NPC) {
            return new NPC((net.runelite.api.NPC) interacting);
        }

        if (interacting instanceof net.runelite.api.Player) {
            return new Player((net.runelite.api.Player) interacting);
        }

        throw new BotException("interacting isnt a player or npc: " + interacting.getClass().getName());
    }
}
