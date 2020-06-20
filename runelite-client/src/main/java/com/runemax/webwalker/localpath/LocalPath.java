package com.runemax.webwalker.localpath;

import com.runemax.bot.api.game.Client;
import net.runelite.api.CollisionData;
import net.runelite.api.coords.LocalPoint;

import java.util.*;

public final class LocalPath {

    /**
     * Returns the amount of steps between two locations. This will return -1 if
     * the amount of steps was indeterminate (for example, one of the locations wasn't loaded or
     * they are both not on the same floor).
     * Note: this can be resource intensive, as it generates a path between these
     * two locations to find the distance. This should only be used if you need an accurate
     * measurement for the amount of steps required.
     * @return The amount of steps required to traverse between these two locations.
     */
    public static int distance(final LocalPoint l1, final LocalPoint l2) {
        final Graph graph = getGraph();
        final Node[] path;
        final Node nodeStart;
        final Node nodeStop;

        if (graph != null &&
            (nodeStart = graph.getNode(l1.getSceneX(), l1.getSceneY())) != null &&
            (nodeStop = graph.getNode(l2.getSceneX(), l2.getSceneY())) != null) {
            LocalPath.dijkstra(graph, nodeStart, nodeStop);
            path = LocalPath.follow(nodeStop);
        } else {
            path = new Node[0];
        }
        final int l = path.length;
        return l > 0 ? l : -1;
    }

    /**
     * Whether or not the location may be reached. This will return false if one of the locations
     * is not loaded, they're not on the same plane, or the distance between them is 0.
     * Note: this can be resource intensive, as it generates a path between these
     * two locations to determine if they're reachable. This should only be used if you need
     * to be certain that the point is reachable.
     * @return {@code true} if the player can reach the point, {@code false} otherwise.
     */
    public static boolean reachable(final LocalPoint p1) {
        return distance(Client.getLocalPlayer().getLocalLocation(), p1) > 0;
    }

    /**
     * Whether or not the location may be reached. This will return false if one of the locations
     * is not loaded, they're not on the same plane, or the distance between them is 0.
     * Note: this can be resource intensive, as it generates a path between these
     * two locations to determine if they're reachable. This should only be used if you need
     * to be certain that the point is reachable.
     * @return {@code true} if the two points can reach each other, {@code false} otherwise.
     */
    public static boolean reachable(final LocalPoint p1, final LocalPoint p2) {
        return distance(p1, p2) > 0;
    }

    static Node[] follow(Node target) {
        final List<Node> nodes = new LinkedList<>();
        if (Double.isInfinite(target.g)) {
            return new Node[0];
        }
        while (target != null) {
            nodes.add(target);
            target = target.parent;
        }

        Collections.reverse(nodes);
        final Node[] path = new Node[nodes.size()];
        return nodes.toArray(path);
    }

    public static Graph getGraph() {
        final int floor = Client.getPlane();
        final CollisionData[] maps = Client.getCollisionMaps();
        final CollisionData map;
        if (maps == null || floor < 0 || floor >= maps.length || (map = maps[floor]) == null) {
            return null;
        }
        final int[][] arr = map.getFlags();

        if (arr == null) return null;
        int offsetX = map.getXInset();
        int offsetY = map.getYInset();
        return new Graph(arr, offsetX, offsetY);
    }

    private static void dijkstra(final Graph graph, final Node source, final Node target) {
        source.g = 0d;
        source.f = 0d;

        final Queue<Node> queue = new PriorityQueue<>(8, Comparator.comparingDouble(o -> o.f));

        final double sqrt2 = Math.sqrt(2);

        queue.add(source);
        source.opened = true;
        while (!queue.isEmpty()) {
            final Node node = queue.poll();
            node.closed = true;
            if (node.equals(target)) {
                break;
            }
            for (final Node neighbor : graph.neighbors(node)) {
                if (neighbor.closed) {
                    continue;
                }
                final double ng = node.g + ((neighbor.x - node.x == 0 || neighbor.y - node.y == 0) ? 1d : sqrt2);

                if (!neighbor.opened || ng < neighbor.g) {
                    neighbor.g = ng;
                    neighbor.h = 0; //no heuristic
                    neighbor.f = neighbor.g + neighbor.h;
                    neighbor.parent = node;

                    if (!neighbor.opened) {
                        queue.offer(neighbor);
                        neighbor.opened = true;
                    }
                }
            }
        }
    }
}
