package com.runemax.bot.api.entities.actors;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.runemax.bot.api.entities.Entities;
import com.runemax.bot.api.wrappers.entity.actor.Actor;
import net.runelite.api.Point;

import javax.annotation.Nonnull;

public final class Actors<A extends Actor> extends Entities<A> {
    private final Supplier<List<A>> actorsSupplier;
    private final Supplier<A> emptySupplier;

    public Actors(Supplier<List<A>> actorsSupplier, Supplier<A> emptySupplier) {
        this.actorsSupplier = actorsSupplier;
        this.emptySupplier = emptySupplier;
    }

    @Nonnull
    @Override
    protected A closestUnsafe(Predicate<? super A> filter, Point from) {
        return actorsSupplier
                .get()
                .stream()
                .filter(a -> a.getName() != null)
                .filter(filter)
                .min(Comparator.comparingInt(npc -> npc.getSceneLocation().distanceTo(from)))
                .orElse(emptySupplier.get());
    }

    @Nonnull
    @Override
    protected List<A> allUnsafe(Predicate<? super A> filter) {
        return actorsSupplier
                .get()
                .stream()
                .filter(a -> a.getName() != null)
                .filter(filter)
                .collect(Collectors.toList());
    }
}
