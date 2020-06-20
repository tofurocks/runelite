package com.runemax.bot.tasks.subtasks;

import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.coords.RectangularArea;
import com.runemax.bot.api.entities.tile.objects.TileObjects;
import com.runemax.bot.api.exception.BotException;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.movement.Movement;
import com.runemax.bot.api.wrappers.entity.tile.object.TileObject;
import com.runemax.bot.tasks.framework.Task;
import com.runemax.bot.tasks.framework.TaskSet;
import com.runemax.webwalker.WebWalker;
import com.runemax.webwalker.localpath.LocalPath;
import com.runemax.webwalker.spatial.TraversableWorldPoint;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.walkerdemo.WalkerDemoOverlay;

import java.util.Arrays;
import java.util.LinkedList;

@Slf4j
public class WalkTask extends Task {

    private static final String[] POSSIBLE_ACTIONS = new String[]{"open", "cross", "climb", "pass", "squeeze-through", "walk through", "walk-through", "squeeze-past"};
    private static final int MAX_DISTANCE = 14;
    private static final int TILE_SIZE = 128;
    private final int goalX;
    private final int goalY;
    private final int goalZ;
    private final WorldPoint goal;
    private LinkedList<TraversableWorldPoint> queue;
    private TraversableWorldPoint nextPoint;
    private int targetDistance = MAX_DISTANCE * TILE_SIZE;
    private int attempts = 0;

    /**
     * Walks to a random point in a given area
     *
     * @param area
     */
    public WalkTask(String taskDescription, RectangularArea area) {
        this(
            taskDescription,
            Rand.nextInt(area.getXMin(), area.getXMax()),
            Rand.nextInt(area.getYMin(), area.getYMax()),
            area.getPlane() != null ? area.getPlane() : 0
        );
    }

    /**
     * Walks to a given point
     *
     * @param point
     */
    public WalkTask(String taskDescription, WorldPoint point) {
        this(taskDescription, point.getX(), point.getY(), point.getPlane());
    }

    /**
     * Walks to a given point
     *
     * @param goalX
     * @param goalY
     * @param goalZ
     */
    public WalkTask(String taskDescription, int goalX, int goalY, int goalZ) {
        super(taskDescription);
        this.goalX = goalX;
        this.goalY = goalY;
        this.goalZ = goalZ;
        this.goal = new WorldPoint(goalX, goalY, goalZ);
    }

    @Override
    public Result execute(TaskSet parentQueue) {

        // If we haven't worked out the move points, call the web walker and populate the queue
        if (this.queue == null) {
            log.info("Starting up task");
            this.generatePath();
        }

        // If no target, or if we're near our goal then SUCCESS
        if (this.queue.isEmpty()) {
            log.info("No more targets, or near our destination");
            return Result.SUCCESS;
        }

        // Ascertain our current target
        this.nextPoint = this.getNextPoint();

        // Is our path unreachable?
        if (this.nextPoint == null || (!LocalPath.reachable(nextPoint.getLocalPoint()) && !nextPoint.isInteractive() && !nextPoint.isLadder_descending() && !nextPoint.isLadder_ascending())) {
            log.error("Current target is now unreachable, recalculating");
            this.generatePath();
            return Result.RUNNING;
        }

        // Is our next point an interactive one?
        if (this.nextPoint.isInteractive() || this.nextPoint.isLadder_ascending() || this.nextPoint.isLadder_descending()) {
            log.info("Next tile is interactive");
            TileObject obj = getInteractableAtTile(this.nextPoint);
            if (obj != null) {
                for (String action : obj.getActions()) {
                    for (String possibleAction : POSSIBLE_ACTIONS) {
                        if (action.toLowerCase().contains(possibleAction.toLowerCase())) {
                            //log.info("Interacting: Using action {} on {}", action, obj.getName());
                            obj.interact(action);
                            try {
                                // wait until we've interacted
                                Sleep.until(() -> getInteractableAtTile(this.nextPoint) == null, 15000);
                            } catch (Sleep.TimeoutException exception) {
                                // Unable to interact, regen path
                                this.generatePath();
                            }
                            return Result.RUNNING;
                        }
                    }
                }
                log.info("Object must have disappeared, continuing");
            } else {
                log.info("Skipping interactable object, not present");
            }
        }

        // Our next point must be a walkable one
        if (!this.isDestinationSameAsTarget() || !WebWalker.isMoving()) {
            boolean walkResult = Movement.walk(nextPoint); // step to the furthest point
            if (walkResult) {
                log.info(
                    "Walking: Destination set to X:{} Y:{} Z:{}",
                    nextPoint.getX(),
                    nextPoint.getY(),
                    nextPoint.getPlane()
                );
            } else {
                log.error(
                    "Was unable to walk to X:{} Y:{} Z:{}",
                    nextPoint.getX(),
                    nextPoint.getY(),
                    nextPoint.getPlane()
                );
                return Result.FAILED;
            }
        }

        return Result.RUNNING;
    }

    private void generatePath() {
        log.info("Regenerating path");
        this.queue = new LinkedList<>();
        TraversableWorldPoint[] movePoints = this.getMovePoints();
        WalkerDemoOverlay.points = movePoints;
        queue.addAll(Arrays.asList(movePoints));
        attempts++;
        if (attempts > 5) {
            throw new BotException("Unable to reach destination");
        }
    }

    private TraversableWorldPoint getNextPoint() {

        // Calc the distance between us and our current target
        float distance = this.getDistanceFromTarget();
        log.info("Distance: {}", distance);

        // We already have a target that we're planning on walking to
        if (this.nextPoint != null && distance >= targetDistance) {
            return this.nextPoint;
        }

        // Randomise the distance that we'll next click a tile
        targetDistance = Rand.nextInt(5 * TILE_SIZE, MAX_DISTANCE * TILE_SIZE);

        // Get the furthest walkable tile within a distance
        TraversableWorldPoint target = null;
        for (int offset = 0; offset < Math.min(MAX_DISTANCE, queue.size()); offset++) {
            TraversableWorldPoint tmp = queue.get(offset);
            if (movePointViable(tmp) || tmp.isInteractive() || tmp.isLadder_ascending() || tmp.isLadder_descending()) {
                attempts = 0;
                target = queue.get(offset);
                if ((target.isInteractive() || tmp.isLadder_ascending() || tmp.isLadder_descending()) && this.getInteractableAtTile(target) != null) {
                    // tile is interactable, so we have to deal with this first
                    break;
                }
            }
        }

        if (target == null) {
            return null;
        }

        this.removeQueueUntil(target);
        return target;
    }

    private void removeQueueUntil(TraversableWorldPoint object) {
        while (!queue.isEmpty()) {
            TraversableWorldPoint point = this.queue.pop();
            if (object.equals(point)) {
                break;
            }
        }
    }

    private float getDistanceFromTarget() {
        if (nextPoint == null) return 0;

        LocalPoint localPoint = LocalPoint.fromWorld(Client.getInstance(), nextPoint);
        if (localPoint == null) return 0;

        LocalPoint currentDestination = Client.getLocalDestinationLocation();
        if (currentDestination == null) return 0;

        return Client.getLocalPlayer().getLocalLocation().distanceTo(localPoint);
    }

    private boolean movePointViable(TraversableWorldPoint traversableWorldPoint) {
        if (traversableWorldPoint == null) return false;
        if (!traversableWorldPoint.isInScene(Client.getInstance())) return false;
        if (traversableWorldPoint.distanceTo(Client.getLocalPlayer().getWorldArea()) > MAX_DISTANCE) return false;
        if (traversableWorldPoint.getLocalPoint() == null) return false;
        if (Perspective.localToMinimap(Client.getInstance(), traversableWorldPoint.getLocalPoint(), MAX_DISTANCE * 2 * TILE_SIZE) == null)
            return false;
        if (!LocalPath.reachable(traversableWorldPoint.getLocalPoint())) return false;
        return true;
    }

    private boolean isDestinationSameAsTarget() {
        if (Client.getLocalDestinationLocation() == null) return false;

        WorldPoint destination = WorldPoint.fromLocal(Client.getInstance(), Client.getLocalDestinationLocation());
        if (destination.getX() != this.nextPoint.getX() || destination.getY() != this.nextPoint.getY())
            return false;

        return true;
    }

    private TileObject getInteractableAtTile(WorldPoint point) {

        if(!point.isInScene(Client.getInstance())) return null;

        for (TileObject obj : TileObjects.getAt(point)) {
            for (String action : obj.getActions()) {
                if (action == null) continue;
                for (String possibleAction : POSSIBLE_ACTIONS) {
                    if (action.toLowerCase().contains(possibleAction.toLowerCase())) {
                        return obj;
                    }
                }
            }
        }
        return null;
    }

    private TraversableWorldPoint[] getMovePoints() {
        WorldPoint playerPoint = Client.getLocalPlayer().getWorldLocation();

        if (playerPoint == null) {
            throw new BotException("Unable to find player world location");
        }

        // Attempt to get path from com.runemax.webwalker API
        TraversableWorldPoint[] movePoints = WebWalker.getPath(
            Client.getInstance(),
            playerPoint.getX()+1,
            playerPoint.getY()+1,
            playerPoint.getPlane(),
            this.goalX,
            this.goalY,
            this.goalZ,
            false
        );

        // Were we unable to get a path?
        if (movePoints == null) {
            throw new BotException("Unable to get path");
        }

        return movePoints;
    }
}
