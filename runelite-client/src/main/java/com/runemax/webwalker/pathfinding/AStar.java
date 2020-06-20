package com.runemax.webwalker.pathfinding;

import com.runemax.webwalker.WebWalker;
import com.runemax.webwalker.quadtree.Quadtree;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.ArrayList;

@Slf4j
public class AStar {

    private ArrayList<Quadtree> quads;

    public void setQuads(ArrayList<Quadtree> quadsArrays) {
        this.quads = quadsArrays;
    }

    public Quadtree findQuad(int x, int y, int z) throws Exception {
        for (Quadtree quad : this.quads) {
            if (quadtreeContains(x, y, z, quad)) {
                return quad;
            }
        }
        throw new Exception(String.format("Unable to find quad at X:%s Y:%s", x, y));
    }

    private boolean quadtreeContains(int x, int y, int z, Quadtree quad) {
        int px = quad.x;
        int py = quad.y;
        int pz = quad.plane;
        int psize = quad.size;

        if (pz == z && x >= px && x <= px + psize && y >= py && y <= py + psize) return true;

        return false;
    }

    public void reset() {
        for (int height = 0; height < WebWalker.HEIGHT; height++) {
            for (Quadtree quad : quads) {
                quad.reset();
            }
        }

    }

    public double distanceTo(int x1, int y1, int x2, int y2) {

        int dx = x1 - x2;
        int dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Nullable
    public ArrayList<Quadtree> process(int x1, int y1, int z1, int x2, int y2, int z2, String heuristic, boolean approximate) throws Exception {

        this.reset();

        long startTime = System.currentTimeMillis();
        Quadtree startQuad = findQuad(x1, y1, z1);
        Quadtree endQuad = findQuad(x2, y2, z2);

        ArrayList<Quadtree> openList = new ArrayList<>();
        openList.add(startQuad);

        int traversedNodes = 1;
        startQuad.g = 0;
        startQuad.h = this.heuristic(startQuad, endQuad, heuristic);
        startQuad.ff = startQuad.g + startQuad.h;

        Quadtree closestNode = null;
        double closestDistance = Double.MAX_VALUE;

        while (openList.size() > 0) {
            int currentIndex = this.getNextIndex(openList);
            Quadtree currentNode = openList.get(currentIndex);

//            if (approximate) {
//                double distanceToEnd = this.distanceTo(currentNode.x, currentNode.y, endQuad.x, endQuad.y);
//                if (distanceToEnd < closestDistance) {
//                    closestNode = currentNode;
//                    closestDistance = distanceToEnd;
//                }
//            }

            if (currentNode == endQuad) {
                ArrayList<Quadtree> path = new ArrayList<>();
                Quadtree aNode = currentNode;
                while (aNode.cameFrom != null) {
                    path.add(aNode);
                    aNode.path = true;
                    aNode = aNode.cameFrom;
                }
                path.add(startQuad);
                startQuad.path = true;
                //                return {path: path,traversedNodes: traversedNodes};
                long endTime = System.currentTimeMillis();

                log.info("Processed path finding in {}ms", endTime - startTime);
                return path;
            }
            traversedNodes++;
            openList.remove(currentIndex);
            currentNode.open = false;
            currentNode.closed = true;
            ArrayList<Quadtree> neighbors = this.getNeighbors(currentNode);
            for (int i = 0; i < neighbors.size(); i++) {
                Quadtree neighbor = neighbors.get(i);
                if (neighbor.closed) {
                    continue;
                }
                double tentative_g = currentNode.g + this.distance(currentNode, neighbor, heuristic);
                boolean tentativeIsBetter;
                if (!neighbor.open) {
                    neighbor.open = true;
                    neighbor.h = this.heuristic(neighbor, endQuad, heuristic);
                    openList.add(neighbor);
                    tentativeIsBetter = true;
                } else if (tentative_g < neighbor.g) {
                    tentativeIsBetter = true;
                } else {
                    tentativeIsBetter = false;
                }
                if (tentativeIsBetter) {
                    neighbor.cameFrom = currentNode;
                    neighbor.g = tentative_g;
                    neighbor.ff = neighbor.g + neighbor.h;
                }
            }
        }

//        if(closestNode != null && approximate) {
//            return this.process(x1, y1, closestNode.x, closestNode.y, heuristic, true);
//        }

        long endTime = System.currentTimeMillis();
        log.info("Processed path finding in {}ms", endTime - startTime);

        return null;
    }

    public double heuristic(Quadtree nodeA, Quadtree nodeB, String heuristic) {
        double distance = distance(nodeA, nodeB, heuristic);
        return distance == Integer.MAX_VALUE ? 0d : distance;
    }

    public double distance(Quadtree nodeA, Quadtree nodeB, String heuristic) {
        int nodeAOffset = nodeA.size / 2;
        int nodeBOffset = nodeB.size / 2;
        int multiplier = 1;

        if(nodeB.door || nodeB.ladder_ascending || nodeB.ladder_descending) {
            multiplier = 2;
        }

        switch (heuristic) {
            case "manhatten":
                return Math.abs((nodeA.x + nodeAOffset) - (nodeB.x + nodeBOffset)) + Math.abs((nodeA.y + nodeAOffset) - (nodeB.y + nodeBOffset)) + (Math.abs(nodeA.plane - nodeB.plane)) * multiplier;
            case "euclidean":
                return Math.sqrt(Math.pow((nodeA.x + nodeAOffset) - (nodeB.x + nodeBOffset), 2) + Math.pow((nodeA.y + nodeAOffset) - (nodeB.y + nodeBOffset), 2) + (Math.pow((nodeA.plane - nodeB.plane), 2))) * multiplier;
        }
        return Integer.MAX_VALUE;
    }

    public int getNextIndex(ArrayList<Quadtree> openList) {
        int nextIndex = 0;
        for (int i = 0; i < openList.size(); i++) {
            if (openList.get(i).ff < openList.get(nextIndex).ff) {
                nextIndex = i;
            }
        }
        return nextIndex;
    }

    public ArrayList<Quadtree> getNeighbors(Quadtree quadtree) {
        ArrayList<Quadtree> neighbors = new ArrayList<>();
        for (int i : quadtree.neighbors) {
            neighbors.add(quads.get(i));
        }
        return neighbors;
    }

}
