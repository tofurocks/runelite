package com.runemax.bot.api.entities.tile;

import com.runemax.bot.api.commons.Pair;
import com.runemax.bot.api.entities.Entities;
import com.runemax.bot.api.exception.BotException;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.game.OnGameThread;
import com.runemax.bot.api.wrappers.entity.tile.TileEntity;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class TileEntities<T extends TileEntity> extends Entities<T> {
    public static final Predicate<TileEntity> NON_NULL_NAME = tileEntity -> tileEntity != null && !tileEntity.getName().equalsIgnoreCase("null");

    @Nonnull
    protected abstract List<T> extractFrom(Tile tile);

    @Nonnull
    protected abstract T supplyDefault();

    @Override
    @Nonnull
    protected T closestUnsafe(Predicate<? super T> filter, Point from) {
        List<Tile> tiles = Arrays.stream(Client.getScene().getTiles()[Client.getPlane()])
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());

        List<Pair<T, Integer>> objPairedDist = new ArrayList<>();
        for (Tile tile : tiles) {
            if (tile == null) continue;

            List<T> objs = extractFrom(tile);
            if (objs.isEmpty()) continue;

            int dist = tile.getSceneLocation().distanceTo(from);

            for (T obj : objs) {
                if (NON_NULL_NAME.test(obj) && filter.test(obj)) {
                    objPairedDist.add(new Pair<>(obj, dist));
                }
            }
        }

        return objPairedDist.stream()
                .min(Comparator.comparingInt(Pair::getRight))
                .map(Pair::getLeft)
                .orElse(supplyDefault());
    }

    @Override
    @Nonnull
    protected List<T> allUnsafe(Predicate<? super T> filter) {
        return Arrays
                .stream(Client.getInstance().getScene().getTiles()[Client.getInstance().getPlane()])
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .map(this::extractFrom)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .filter(t -> !t.getName().equals("null"))
                .filter(filter)
                .collect(Collectors.toList());
    }

    @Nonnull
    public List<T> getAt(WorldPoint worldPoint) {
        LocalPoint localPoint = LocalPoint.fromWorld(Client.getInstance(), worldPoint);
        if (localPoint == null) {
            throw new BotException("tryna get from a point outside the scene");
        }

        return getAt(new Point(localPoint.getSceneX(), localPoint.getSceneY()));
    }

    @Nonnull
    public List<T> getAt(Point scenePoint) {
        return OnGameThread.invokeAndWait(() -> extractFrom(Client.getScene().getTiles()[Client.getPlane()][scenePoint.getX()][scenePoint.getY()]))
            .stream()
            .filter(Objects::nonNull)
//            .filter(t -> !t.getName().equals("null"))
            .collect(Collectors.toList());
    }

    @Nonnull
    public List<T> getAt(WorldPoint worldPoint, Predicate<? super T> predicate) {
        LocalPoint localPoint = LocalPoint.fromWorld(Client.getInstance(), worldPoint);
        Point point = new Point(localPoint.getSceneX(), localPoint.getSceneY());
        return OnGameThread.invokeAndWait(() -> extractFrom(Client.getScene().getTiles()[Client.getPlane()][point.getX()][point.getY()]))
            .stream()
            .filter(Objects::nonNull)
            .filter(t -> !t.getName().equals("null"))
            .filter(predicate)
            .collect(Collectors.toList());
    }

    @Nonnull
    public T getFirstAt(WorldPoint worldPoint, Predicate<? super T> predicate) {
        LocalPoint localPoint = LocalPoint.fromWorld(Client.getInstance(), worldPoint.getX(), worldPoint.getY());
        if (localPoint == null) {
            throw new BotException("tryna get from a point outside the scene");
        }

        return getFirstAt(new Point(localPoint.getSceneX(), localPoint.getSceneY()), predicate);
    }

    @Nonnull
    public T getFirstAt(Point scenePoint, Predicate<? super T> predicate) {
        return getAt(scenePoint).stream().filter(predicate).findFirst().orElse(supplyDefault());
    }
}
