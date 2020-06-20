package com.runemax.webwalker.localpath;

import net.runelite.api.CollisionDataFlag;

import java.util.ArrayList;
import java.util.List;

import static net.runelite.api.CollisionDataFlag.*;

public final class Graph {
    private final int offX, offY;
    private final Node[][] nodes;
    private final int width, height;

    Graph(final int[][] flags, final int offX, final int offY) {
        this.offX = offX;
        this.offY = offY;
        nodes = new Node[flags.length][];
        width = flags.length;
        int height = flags.length;
        for (int x = 0; x < flags.length; x++) {
            final int[] arr = flags[x];
            nodes[x] = new Node[arr.length];
            height = Math.min(height, arr.length);
            for (int y = 0; y < arr.length; y++) {
                nodes[x][y] = new Node(x, y, flags[x][y]);
            }
        }
        this.height = height;
    }

    public Node getNode(final int x, final int y) {
        final int ox = x - offX, oy = y - offY;
        if (ox >= 0 && oy >= 0 && ox < nodes.length && oy < nodes[ox].length) {
            return nodes[ox][oy];
        }
        return null;
    }

    public List<Node> neighbors(final Node node) {
        final List<Node> list = new ArrayList<>(8);
        final int curr_x = node.x;
        final int curr_y = node.y;
        final int BLOCKED = CollisionDataFlag.BLOCK_MOVEMENT_FULL;
        if (curr_x < 0 || curr_y < 0 ||
            curr_x >= width || curr_y >= height) {
            return list;
        }
        if (curr_y > 0 &&
            (nodes[curr_x][curr_y].flag & BLOCK_MOVEMENT_SOUTH) == 0 &&
            (nodes[curr_x][curr_y - 1].flag & BLOCKED) == 0) {
            list.add(nodes[curr_x][curr_y - 1]);
        }
        if (curr_x > 0 &&
            (nodes[curr_x][curr_y].flag & BLOCK_MOVEMENT_WEST) == 0 &&
            (nodes[curr_x - 1][curr_y].flag & BLOCKED) == 0) {
            list.add(nodes[curr_x - 1][curr_y]);
        }
        if (curr_y < height - 1 &&
            (nodes[curr_x][curr_y].flag & BLOCK_MOVEMENT_NORTH) == 0 &&
            (nodes[curr_x][curr_y + 1].flag & BLOCKED) == 0) {
            list.add(nodes[curr_x][curr_y + 1]);
        }
        if (curr_x < width - 1 &&
            (nodes[curr_x][curr_y].flag & BLOCK_MOVEMENT_EAST) == 0 &&
            (nodes[curr_x + 1][curr_y].flag & BLOCKED) == 0) {
            list.add(nodes[curr_x + 1][curr_y]);
        }
        if (curr_x > 0 && curr_y > 0 &&
            (nodes[curr_x][curr_y].flag & (BLOCK_MOVEMENT_SOUTH_WEST | BLOCK_MOVEMENT_SOUTH | BLOCK_MOVEMENT_WEST)) == 0 &&
            (nodes[curr_x - 1][curr_y - 1].flag & BLOCKED) == 0 &&
            (nodes[curr_x][curr_y - 1].flag & (BLOCK_MOVEMENT_WEST | BLOCKED)) == 0 &&
            (nodes[curr_x - 1][curr_y].flag & (BLOCK_MOVEMENT_SOUTH | BLOCKED)) == 0) {
            list.add(nodes[curr_x - 1][curr_y - 1]);
        }
        if (curr_x > 0 && curr_y < height - 1 &&
            (nodes[curr_x][curr_y].flag & (BLOCK_MOVEMENT_NORTH_WEST | BLOCK_MOVEMENT_NORTH | BLOCK_MOVEMENT_WEST)) == 0 &&
            (nodes[curr_x - 1][curr_y + 1].flag & BLOCKED) == 0 &&
            (nodes[curr_x][curr_y + 1].flag & (BLOCK_MOVEMENT_WEST | BLOCKED)) == 0 &&
            (nodes[curr_x - 1][curr_y].flag & (BLOCK_MOVEMENT_NORTH | BLOCKED)) == 0) {
            list.add(nodes[curr_x - 1][curr_y + 1]);
        }
        if (curr_x < height - 1 && curr_y > 0 &&
            (nodes[curr_x][curr_y].flag & (BLOCK_MOVEMENT_SOUTH_EAST | BLOCK_MOVEMENT_SOUTH | BLOCK_MOVEMENT_EAST)) == 0 &&
            (nodes[curr_x + 1][curr_y - 1].flag & BLOCKED) == 0 &&
            (nodes[curr_x][curr_y - 1].flag & (BLOCK_MOVEMENT_EAST | BLOCKED)) == 0 &&
            (nodes[curr_x + 1][curr_y].flag & (BLOCK_MOVEMENT_SOUTH | BLOCKED)) == 0) {
            list.add(nodes[curr_x + 1][curr_y - 1]);
        }
        if (curr_x < width - 1 && curr_y < height - 1 &&
            (nodes[curr_x][curr_y].flag & (BLOCK_MOVEMENT_NORTH_EAST | BLOCK_MOVEMENT_NORTH | BLOCK_MOVEMENT_EAST)) == 0 &&
            (nodes[curr_x + 1][curr_y + 1].flag & BLOCKED) == 0 &&
            (nodes[curr_x][curr_y + 1].flag & (BLOCK_MOVEMENT_EAST | BLOCKED)) == 0 &&
            (nodes[curr_x + 1][curr_y].flag & (BLOCK_MOVEMENT_NORTH | BLOCKED)) == 0) {
            list.add(nodes[curr_x + 1][curr_y + 1]);
        }
        return list;
    }
}
