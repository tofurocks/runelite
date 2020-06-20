package com.runemax.bot.api.entities;

import java.util.List;
import java.util.function.Predicate;

import net.runelite.api.Point;
import com.runemax.bot.api.game.OnGameThread;
import com.runemax.bot.api.commons.By;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.wrappers.entity.Entity;
import com.runemax.bot.api.wrappers.Locatable;

import javax.annotation.Nonnull;

public abstract class Entities<E extends Entity> {
    protected abstract List<E> allUnsafe(Predicate<? super E> filter);

    protected abstract E closestUnsafe(Predicate<? super E> filter, Point from);

    @Nonnull
    public List<E> all(Predicate<? super E> filter){
        return OnGameThread.invokeAndWait(()-> allUnsafe(filter));
    }

    @Nonnull
    public List<E> all(){
        return all(x -> true);
    }

    @Nonnull
    public E closest(Predicate<? super E> filter, Locatable from){
        Point fromLoc = from.getSceneLocation();
        return OnGameThread.invokeAndWait(()-> closestUnsafe(filter, fromLoc));
    }

    @Nonnull
    public E closest(Predicate<? super E> filter){
        return closest(filter, Players.getLocal());
    }

    @Nonnull
    public E closest(int... ids){
        return closest(By.id(ids));
    }

    @Nonnull
    public E closest(String... names){
        return closest(By.name(names));
    }
}
