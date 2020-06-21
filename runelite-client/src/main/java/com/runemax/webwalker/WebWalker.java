package com.runemax.webwalker;

import com.runemax.webwalker.pathfinding.AStar;
import com.runemax.webwalker.quadtree.Quadtree;
import com.runemax.webwalker.spatial.TraversableWorldPoint;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static net.runelite.api.AnimationID.IDLE;

@Slf4j
public final class WebWalker {

    @Getter
    private static AStar aStar = new AStar();
    private static Random random = new Random();
    private static final String FILENAME = "web.bin";
    private static WorldPoint lastPosition = null;
    private static Instant lastMoving = Instant.now();
    private static final int INFILL_SPACING = 3;
    private static ArrayList<Quadtree> quads = new ArrayList<>();
    public static final int HEIGHT = 4;

    @Getter
    private static boolean isMoving = false;

    public static void tick(Client client) {
        isMoving = checkMovement(Duration.ofMillis(1000), client.getLocalPlayer());
    }

    public static ArrayList<Quadtree> getQuads() {
        return quads;
    }

    private static boolean checkMovement(Duration waitDuration, Player local) {
        if (lastPosition == null) {
            lastPosition = local.getWorldLocation();
            return false;
        }

        WorldPoint position = local.getWorldLocation();
        if (lastPosition.equals(position)) {
            if (local.getAnimation() == IDLE && Instant.now().compareTo(lastMoving.plus(waitDuration)) >= 0) {
                return false;
            }
        } else {
            lastPosition = position;
            lastMoving = Instant.now();
        }

        return true;
    }

    /**
     * This needs to be run once to initialise the web
     */
    public static void initialise() {
        URL webwalkerPath = WebWalker.class.getResource(FILENAME);
        if (webwalkerPath == null) {
            log.error("Webwalker data file {} cannot be found", FILENAME);
            System.exit(1);
        }

        try {
            InputStream is = WebWalker.class.getResourceAsStream(FILENAME);
            long startTime = System.currentTimeMillis();
            ObjectInputStream oi = new ObjectInputStream(is);
            quads = (ArrayList<Quadtree>) oi.readObject();
            oi.close();
            is.close();
            long endTime = System.currentTimeMillis();
            log.info("Loaded {} in {}ms", FILENAME, endTime - startTime);
        } catch (ClassNotFoundException | IOException e) {
            log.error("Error processing {}", FILENAME);
            e.printStackTrace();
            System.exit(1);
        }

        aStar.setQuads(quads);
    }

    /**
     * Identifies the nearest bank
     */
    public static TraversableWorldPoint[] getClosestBank(Client client) {
        return getClosestPoint(client, Locations.BANKS);
    }

    /**
     * Identifies the closest deposit box
     */
    public static TraversableWorldPoint[] getClosestDepositBox(Client client) {
        return getClosestPoint(client, Locations.DEPOSIT_BOXES);
    }

    /**
     * Returns the closest point from a list of points
     */
    public static TraversableWorldPoint[] getClosestPoint(Client client, WorldPoint... point) {
        final Player player = client.getLocalPlayer();
        final WorldPoint playerLocation = player.getWorldLocation();

        // Sorts the points in order of closest first from the player
        WorldPoint[] sortedPoints = Arrays.asList(point).stream().sorted(
            Comparator.comparingInt(o -> o.distanceTo(playerLocation))
        ).collect(Collectors.toList()).toArray(new WorldPoint[0]);

        // foreach the points and return the closest point we can reach
        for (WorldPoint p : sortedPoints) {
            if (p.getPlane() != playerLocation.getPlane()) continue;
            TraversableWorldPoint[] closestPoint = getPath(client, p.getX(), p.getY(), p.getPlane(), true);
            if (closestPoint != null) return closestPoint;
        }

        // No path found
        return null;
    }

    /**
     * Generates a path between the player and x/y.
     *
     * @return A traversable array of worldpoints
     */
    public static TraversableWorldPoint[] getPath(Client client, int x, int y, int z, boolean approximate) {
        WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
        return getPath(client, playerPoint.getX(), playerPoint.getY(), playerPoint.getPlane(), x, y, z, approximate);
    }

    /**
     * Generates a path between x1/y1 and x2/y2.
     *
     * @return A traversable array of worldpoints
     */
    public static TraversableWorldPoint[] getPath(Client client, int x1, int y1, int z1, int x2, int y2, int z2, boolean approximate) {
        try {
            ArrayList<Quadtree> quadtrees = aStar.process(x1, y1, z1, x2, y2, z2, "euclidean", approximate);
            ArrayList<TraversableWorldPoint> points = new ArrayList<>();
            TraversableWorldPoint prev = null;

            if (quadtrees == null) return null;

            for (Quadtree quadtree : quadtrees) {

                int x = quadtree.x;
                int y = quadtree.y;
                int z = quadtree.plane;

                // if quadtree larger than 1, pick a random tile in it's area
                if (quadtree.size > 1) {
                    x = random.nextInt((quadtree.x + quadtree.size) - quadtree.x) + quadtree.x;
                    y = random.nextInt((quadtree.y + quadtree.size) - quadtree.y) + quadtree.y;
                }

                TraversableWorldPoint point = new TraversableWorldPoint(x, y, z, quadtree.door, quadtree.ladder_ascending, quadtree.ladder_descending);

                // Check to see if we should fill in the tilepath between points
                if (prev != null) {
                    int prevDistance = prev.distanceTo(point);
                    if (prevDistance > 1 && prev.getPlane() == point.getPlane()) {
                        points.addAll(infillPoints(client, prevDistance - 1, prev, point));
                    }
                }

                points.add(point);
                prev = point;
            }

            Collections.reverse(points);
            return points.toArray(new TraversableWorldPoint[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds extra tiles to fill in gaps on the tilepath
     */
    static ArrayList<TraversableWorldPoint> infillPoints(Client client, int quantity, WorldPoint p1, WorldPoint p2) {
        ArrayList<TraversableWorldPoint> points = new ArrayList<>();
        int ydiff = p2.getY() - p1.getY(), xdiff = p2.getX() - p1.getX();
        double slope = (double) (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
        double x, y;

        --quantity;

        for (double i = 0; i < quantity; i++) {
            y = slope == 0 ? 0 : ydiff * (i / quantity);
            x = slope == 0 ? xdiff * (i / quantity) : y / slope;
            points.add(
                new TraversableWorldPoint(
                    (int) Math.round(x) + p1.getX(),
                    (int) Math.round(y) + p1.getY(),
                    client.getLocalPlayer().getWorldLocation().getPlane(),
                    false,
                    false,
                    false
                )
            );
        }

        return points;
    }
}
