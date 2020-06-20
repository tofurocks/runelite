package com.runemax.bot.api.entities.tile.objects;

import com.runemax.bot.api.entities.tile.TileEntities;
import com.runemax.bot.api.wrappers.Locatable;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import com.runemax.bot.api.wrappers.entity.tile.object.DecorativeObject;
import com.runemax.bot.api.wrappers.entity.tile.object.GameObject;
import com.runemax.bot.api.wrappers.entity.tile.object.GroundObject;
import com.runemax.bot.api.wrappers.entity.tile.object.TileObject;
import com.runemax.bot.api.wrappers.entity.tile.object.WallObject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TileObjects {
    private static TileEntities<TileObject> INNER = new TileEntities<TileObject>() {
        @Nonnull
        @Override
        protected List<TileObject> extractFrom(Tile tile) {
            List<TileObject> out = new ArrayList<>();

            net.runelite.api.DecorativeObject decorativeObject = tile.getDecorativeObject();
            if (decorativeObject != null) {
                out.add(new DecorativeObject(decorativeObject));
            }

            net.runelite.api.WallObject wallObject = tile.getWallObject();
            if (wallObject != null) {
                out.add(new WallObject(wallObject));
            }

            net.runelite.api.GroundObject groundObject = tile.getGroundObject();
            if (groundObject != null) {
                out.add(new GroundObject(groundObject));
            }

            net.runelite.api.GameObject[] gameObjects = tile.getGameObjects();
            if (gameObjects != null && gameObjects.length > 0) {
                out.addAll(
                        Arrays.stream(gameObjects)
                                .filter(Objects::nonNull)
                                .map(GameObject::new)
                                .collect(Collectors.toList())
                );
            }

            return out;
        }

        @Nonnull
        @Override
        protected TileObject supplyDefault() {
            return new GameObject(null);
        }
    };

    //generated delegates, move up if modified
    @Nonnull
    public static List<TileObject> getAt(WorldPoint worldPoint) {
        return INNER.getAt(worldPoint);
    }

    @Nonnull
    public static List<TileObject> getAt(Point scenePoint) {
        return INNER.getAt(scenePoint);
    }

    @Nonnull
    public static TileObject getFirstAt(WorldPoint worldPoint, Predicate<? super TileObject> predicate) {
        return INNER.getFirstAt(worldPoint, predicate);
    }

    @Nonnull
    public static TileObject getFirstAt(Point scenePoint, Predicate<? super TileObject> predicate) {
        return INNER.getFirstAt(scenePoint, predicate);
    }

    @Nonnull
    public static List<TileObject> all(Predicate<? super TileObject> filter) {
        return INNER.all(filter);
    }

    @Nonnull
    public static List<TileObject> all() {
        return INNER.all();
    }

    @Nonnull
    public static TileObject closest(Predicate<? super TileObject> filter, Locatable from) {
        return INNER.closest(filter, from);
    }

    @Nonnull
    public static TileObject closest(Predicate<? super TileObject> filter) {
        return INNER.closest(filter);
    }

    @Nonnull
    public static TileObject closest(int... ids) {
        return INNER.closest(ids);
    }

    @Nonnull
    public static TileObject closest(String... names) {
        return INNER.closest(names);
    }
}
