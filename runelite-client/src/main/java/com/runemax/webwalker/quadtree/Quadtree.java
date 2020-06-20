package com.runemax.webwalker.quadtree;

import java.io.Serializable;

public class Quadtree implements Serializable {

    private static final long serialVersionUID = 3729250898855804771L;

    public transient Quadtree cameFrom = null;
    public transient boolean open = false;
    public transient boolean closed = false;
    public transient boolean path = false;
    public transient double g = 0;
    public transient double h = 0;
    public transient double ff = 0;

    public short x;
    public short y;
    public short plane;
    public short size;
    public int[] neighbors;
    public boolean door = false;
    public boolean ladder_ascending = false;
    public boolean ladder_descending = false;

    public void reset() {
        this.open = false;
        this.closed = false;
        this.path = false;
        this.cameFrom = null;
        this.g = 0;
        this.ff = 0;
        this.h = 0;
    }
}
