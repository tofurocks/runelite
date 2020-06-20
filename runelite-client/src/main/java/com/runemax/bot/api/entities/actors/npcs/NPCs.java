package com.runemax.bot.api.entities.actors.npcs;

import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.wrappers.Locatable;
import com.runemax.bot.api.wrappers.entity.actor.npc.NPC;
import com.runemax.bot.api.entities.actors.Actors;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NPCs {
    private static final Actors<NPC> INNER = new Actors<>(() -> Client.getNpcs().stream().filter(Objects::nonNull).map(NPC::new).collect(Collectors.toList()), () -> new NPC(null));

    @Nonnull
    public static NPC getIndex(int index) {
        if (index < 0 || index > 32767) throw new IllegalArgumentException();

        return new NPC(Client.getCachedNPCs()[index]);
    }

    @Nonnull
    public static NPC getHintArrowed(){
        return new NPC(Client.getHintArrowNpc());
    }

    //generated delegates, move up if modified
    @Nonnull
    public static List<NPC> all(Predicate<? super NPC> filter) {
        return INNER.all(filter);
    }

    @Nonnull
    public static List<NPC> all() {
        return INNER.all();
    }

    @Nonnull
    public static NPC closest(Predicate<? super NPC> filter, Locatable from) {
        return INNER.closest(filter, from);
    }

    @Nonnull
    public static NPC closest(Predicate<? super NPC> filter) {
        return INNER.closest(filter);
    }

    @Nonnull
    public static NPC closest(int... ids) {
        return INNER.closest(ids);
    }

    @Nonnull
    public static NPC closest(String... names) {
        return INNER.closest(names);
    }
}
