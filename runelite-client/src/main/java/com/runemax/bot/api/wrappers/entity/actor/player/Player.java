package com.runemax.bot.api.wrappers.entity.actor.player;


import com.runemax.bot.api.exception.BotException;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.wrappers.RlWrapper;
import com.runemax.bot.api.wrappers.entity.actor.Actor;
import lombok.experimental.Delegate;
import net.runelite.api.Entity;
import net.runelite.api.MenuOpcode;

import javax.annotation.Nonnull;

public final class Player extends RlWrapper<net.runelite.api.Player> implements Actor, net.runelite.api.Player {

    @Delegate(types = {net.runelite.api.Player.class, Entity.class})
    @Nonnull
    @Override
    public net.runelite.api.Player getRl() {
        return super.getRl();
    }

    public Player(net.runelite.api.Player rl){
        super(rl);
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIndex() {
        net.runelite.api.Player rl = getRl();
        net.runelite.api.Player[] players = Client.getCachedPlayers();
        for (int i = 0; i < players.length; i++) {
            if(rl == players[i]) return i;
        }
        throw new BotException("player wasn't in the array");
    }

    @Override
    public int getMenuType(int actionIndex) {
        switch (actionIndex) {
            case 0:
                return MenuOpcode.PLAYER_FIRST_OPTION.getId();
            case 1:
                return MenuOpcode.PLAYER_SECOND_OPTION.getId();
            case 2:
                return MenuOpcode.PLAYER_THIRD_OPTION.getId();
            case 3:
                return MenuOpcode.PLAYER_FOURTH_OPTION.getId();
            case 4:
                return MenuOpcode.PLAYER_FIFTH_OPTION.getId();
            case 5:
                return MenuOpcode.PLAYER_SIXTH_OPTION.getId();
        }

        throw new IllegalArgumentException("no MenuOpcode for index" + actionIndex);
    }

    @Override
    public int getMenuTypeForUseItemOn() {
        return MenuOpcode.ITEM_USE_ON_PLAYER.getId();
    }

    @Override
    public int getMenuTypeForCastSpellOn(){
        return MenuOpcode.SPELL_CAST_ON_PLAYER.getId();
    }

    @Override
    public String[] getActions() {
        return Client.getPlayerOptions();
    }
}
