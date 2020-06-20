package com.runemax.webwalker.localpath;

public final class Node {
    public final int x, y;
    public final int flag;
    public boolean opened, closed;
    public Node parent;
    public double f, g, h;

    Node(final int x, final int y, final int flag) {
        this.x = x;
        this.y = y;
        this.flag = flag;
        reset();
    }

    public void reset() {
        opened = closed = false;
        parent = null;
        f = g = h = Double.POSITIVE_INFINITY;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Node)) {
            return false;
        }
        final Node n = (Node) o;
        return x == n.x && y == n.y;
    }
}
