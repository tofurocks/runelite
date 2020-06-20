package com.runemax.bot.api.entities.actors.players;

import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.wrappers.Locatable;
import com.runemax.bot.api.wrappers.entity.actor.player.Player;
import com.runemax.bot.api.entities.actors.Actors;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Players {
    private static Actors<Player> INNER = new Actors<>(()-> Client.getPlayers().stream().filter(Objects::nonNull).map(Player::new).collect(Collectors.toList()), ()->new Player(null));

    @Nonnull
    public static Player getLocal(){
        return new Player(Client.getLocalPlayer());
    }

    @Nonnull
    public static Player getHintArrowed(){
        return new Player(Client.getHintArrowPlayer());
    }

    @Nonnull
    public static Player atIndex(int index) {
        net.runelite.api.Player[] cachedPlayers = Client.getCachedPlayers();
        if (index < 0 || index >= cachedPlayers.length) throw new IllegalArgumentException();

        return new Player(cachedPlayers[index]);
    }

    //generated delegates, move up if modified
    @Nonnull
    public static List<Player> all(Predicate<? super Player> filter) {
        return INNER.all(filter);
    }

    @Nonnull
    public static List<Player> all() {
        return INNER.all();
    }

    @Nonnull
    public static Player closest(Predicate<? super Player> filter, Locatable from) {
        return INNER.closest(filter, from);
    }

    @Nonnull
    public static Player closest(Predicate<? super Player> filter) {
        return INNER.closest(filter);
    }

    @Nonnull
    public static Player closest(int... ids) {
        return INNER.closest(ids);
    }

    @Nonnull
    public static Player closest(String... names) {
        return INNER.closest(names);
    }
}
