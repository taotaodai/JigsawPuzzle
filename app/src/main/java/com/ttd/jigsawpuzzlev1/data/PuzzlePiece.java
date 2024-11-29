package com.ttd.jigsawpuzzlev1.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;
import java.util.List;

public class PuzzlePiece implements Serializable {
    public final Point realPoint;//真实坐标(相对于拼图板背景)
    private Point currentPoint;//当前坐标
    private Bitmap bitmap;
    private int rotation;
    private List<Point> autoPoints;

    public PuzzlePiece(Point point, Bitmap bitmap) {
        this.realPoint = point;
        this.bitmap = bitmap;
    }

    public Point getRealPoint() {
        return realPoint;
    }

    public Point getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(Point currentPoint) {
        this.currentPoint = currentPoint;
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
