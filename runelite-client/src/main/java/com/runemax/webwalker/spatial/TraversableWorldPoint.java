package com.runemax.webwalker.spatial;

import com.runemax.bot.api.game.Client;
import lombok.Getter;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class TraversableWorldPoint extends WorldPoint {

    @Getter
    final boolean interactive;

    @Getter
    final boolean ladder_ascending;

    @Getter
    final boolean ladder_descending;

    public TraversableWorldPoint(int x, int y, int plane, boolean interactive, boolean ladder_ascending, boolean ladder_descending) {
        super(x, y, plane);
        this.interactive = interactive;
        this.ladder_ascending = ladder_ascending;
        this.ladder_descending = ladder_descending;
    }

    public TraversableWorldPoint(int x, int y, int plane) {
        super(x, y, plane);
        this.interactive = false;
        this.ladder_ascending = false;
        this.ladder_descending = false;
    }

    public LocalPoint getLocalPoint() {
        return LocalPoint.fromWorld(Client.getInstance(), this);
    }

}
