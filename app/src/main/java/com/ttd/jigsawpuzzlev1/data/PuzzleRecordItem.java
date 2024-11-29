package com.ttd.jigsawpuzzlev1.data;

import java.io.Serializable;

public class PuzzleRecordItem implements Serializable {
    private final int x;
    private final int y;

    public PuzzleRecordItem(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
