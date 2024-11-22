package com.ttd.jigsawpuzzlev1.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;
import java.util.List;

public class PuzzlePiece implements Serializable {
    private final int  x;//相对于拼图板背景的X坐标
    private final int y;
    private Bitmap bitmap;
    private int rotation;
    private List<Point> autoPoints;

    public PuzzlePiece(int x, int y, Bitmap bitmap) {
        this.x = x;
        this.y = y;
        this.bitmap = bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public void setAutoPoints(List<Point> autoPoints) {
        this.autoPoints = autoPoints;
    }

    public List<Point> getAutoPoints() {
        return autoPoints;
    }
}
