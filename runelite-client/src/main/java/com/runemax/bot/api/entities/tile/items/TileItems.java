package com.runemax.bot.api.entities.tile.items;

import com.runemax.bot.api.entities.tile.TileEntities;
import com.runemax.bot.api.wrappers.Locatable;
import com.runemax.bot.api.wrappers.entity.tile.item.TileItem;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TileItems {
    private static TileEntities<TileItem> INNER = new TileEntities<TileItem>() {
        @Nonnull
        @Override
        protected List<TileItem> extractFrom(Tile tile) {
            List<net.runelite.api.TileItem> tileItems = tile.getGroundItems();

            if (tileItems == null) return Collections.emptyList();

            return tileItems.stream()
                    .map(item -> new TileItem(item, tile))
                    .collect(Collectors.toList());
        }

        @Nonnull
        @Override
        protected TileItem supplyDefault() {
            return new TileItem(null, null);
        }
    };

    //generated delegates, move up if modified
    @Nonnull
    public static List<TileItem> getAt(Point scenePoint) {
        return INNER.getAt(scenePoint);
    }

    @Nonnull
    public static List<TileItem> getAt(WorldPoint worldPoint) {
        return INNER.getAt(worldPoint);
    }

    @Nonnull
    public static List<TileItem> getAt(WorldPoint worldPoint, Predicate<? super TileItem> predicate) {
        return INNER.getAt(worldPoint, predicate);
    }

    @Nonnull
    public static TileItem getFirstAt(WorldPoint worldPoint, Predicate<? super TileItem> predicate) {
        return INNER.getFirstAt(worldPoint, predicate);
    }

    @Nonnull
    public static TileItem getFirstAt(Point scenePoint, Predicate<? super TileItem> predicate) {
        return INNER.getFirstAt(scenePoint, predicate);
    }

    @Nonnull
    public static List<TileItem> all(Predicate<? super TileItem> filter) {
        return INNER.all(filter);
    }

    @Nonnull
    public static List<TileItem> all() {
        return INNER.all();
    }

    @Nonnull
    public static TileItem closest(Predicate<? super TileItem> filter, Locatable from) {
        return INNER.closest(filter, from);
    }

    @Nonnull
    public static TileItem closest(Predicate<? super TileItem> filter) {
        return INNER.closest(filter);
    }

    @Nonnull
    public static TileItem closest(int... ids) {
        return INNER.closest(ids);
    }

    @Nonnull
    public static TileItem closest(String... names) {
        return INNER.closest(names);
    }
}
