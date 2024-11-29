package com.ttd.jigsawpuzzlev1.component;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.widget.ImageViewCompat;

import com.ttd.jigsawpuzzlev1.R;
import com.ttd.jigsawpuzzlev1.data.Point;
import com.ttd.jigsawpuzzlev1.data.PuzzlePiece;

public class BlockView extends ImageView {
    private PuzzlePiece puzzlePiece;
    private int lastX;
    private int lastY;
    private int borderLeft;
    private int borderTop;
    private int borderRight;
    private int borderBottom;
    private boolean borderEnable = false;

    public BlockView(Context context/*,@DrawableRes int resId*/) {
        super(context);
    }

    public BlockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPuzzlePiece(PuzzlePiece puzzlePiece) {
        this.puzzlePiece = puzzlePiece;
    }

    public void setBorder(int left, int top, int right, int bottom) {
        this.borderEnable = true;
        this.borderLeft = left;
        this.borderTop = top;
        this.borderRight = right;
        this.borderBottom = bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveToTopside();
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = x - lastX;//计算滑动的距离
                int offsetY = y - lastY;

//                Log.i(getClass().getSimpleName(), "坐标：(" + getX() + "," + getY() + ")\n" +
//                        "宽高：(" + getWidth() + "," + getHeight() + ")" + "\n" +
//                        "相对坐标：(" + getLeft() + "," + getTop() + ")" + "\n" +
//                        "父布局坐标：(" + ((ViewGroup) getParent()).getX() + "," + ((ViewGroup) getParent()).getY() + ")");


                int restrictRight = borderRight - getWidth();
                int restrictBottom = borderBottom - getHeight();

                int newLeft = getLeft() + offsetX;
                int newTop = getTop() + offsetY;
                int newRight = getRight() + offsetX;
                int newBottom = getBottom() + offsetY;
                //控制不能划出边界
                if (borderEnable) {
                    if (newLeft < borderLeft) {
                        newRight += borderLeft - newLeft;
                        newLeft = borderLeft;
                    }
                    if (newTop < borderTop) {
                        newBottom += borderTop - newTop;
                        newTop = borderTop;
                    }
                    if (newRight > restrictRight) {
                        newLeft -= newRight - restrictRight;
                        newRight = restrictRight;
                    }
                    if (newBottom > restrictBottom) {
                        newTop -= newBottom - restrictBottom;
                        newBottom = restrictBottom;
                    }
                }

                //重新放置新的位置
                layout(newLeft, newTop, newRight, newBottom);

                break;
            case MotionEvent.ACTION_UP:
                Point point = autoHoming(getLeft(), getTop(), getRight(), getBottom());
                puzzlePiece.setCurrentPoint(point);
                break;
        }
        return true;
    }

    private void moveToTopside() {
        ViewGroup viewGroup = (ViewGroup) getParent();
        final int index = viewGroup.indexOfChild(this);
        if (index >= 0) {
            viewGroup.removeViewInLayout(this);
            if (viewGroup instanceof RelativeLayout) {
                //添加到末尾
                ((PuzzleBoard) viewGroup).addViewInLayout(this, -1, viewGroup.getLayoutParams());
            }
        }
    }

    private Point autoHoming(int left, int top, int right, int bottom) {
        Point finalPoint = new Point(left, top);
        ViewGroup viewGroup = (ViewGroup) getParent();
        View vBoard = viewGroup.findViewById(R.id.tv_board);
        boolean isInside = left >= vBoard.getLeft() && top >= vBoard.getTop() && right <= vBoard.getRight() && bottom <= vBoard.getBottom();
        double nearestDistance = 0;
        Point point = null;
        //每次抬起拼图碎片时计算与目标位置(目标位置指的是：碎片的正确拼接位置，除了自己的以外，其他的可算作干扰项)的最小距离
        for (Point p : puzzlePiece.getAutoPoints()) {
            double distance = calculateDistance(left, top, p.x + vBoard.getLeft(), p.y + vBoard.getTop());
            if (distance < nearestDistance || nearestDistance == 0) {
                point = p;
            }
            nearestDistance = nearestDistance == 0 ? distance : Math.min(nearestDistance, distance);

        }
        Log.i(getClass().getSimpleName(), "在区域内：" + isInside);
        if (isInside) {
            Log.i(getClass().getSimpleName(), "最小距离：" + nearestDistance);
            //抬起点与目标位置小于一定距离时，可进行自动拼接。这个主要是为了方便操作，手动去凑位置非常费力。
            if (nearestDistance <= 20 && point != null) {
                int newLeft = point.x + vBoard.getLeft();
                int newTop = point.y + vBoard.getTop();
                int relativeX = newLeft - left;
                int relativeY = newTop - top;
                layout(newLeft, newTop, right + relativeX, bottom + relativeY);
                Log.i(getClass().getSimpleName(), "自动匹配最近点位：(" + point.x + "," + point.y + ")");
                finalPoint = new Point(newLeft, newTop);
            }
        }
        return finalPoint;
    }


    private double calculateDistance(double x1, double y1, double x2, double y2) {
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

}
