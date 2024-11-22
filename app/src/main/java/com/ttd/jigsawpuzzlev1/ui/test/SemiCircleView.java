package com.ttd.jigsawpuzzlev1.ui.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SemiCircleView extends View {

    private Paint paint;
    private Path path;

    public SemiCircleView(Context context) {
        super(context);
        init();
    }

    public SemiCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SemiCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xFFFF0000); // 红色
        paint.setStyle(Paint.Style.FILL);

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;

        path.reset();
        path.moveTo(width / 2, 0); // 移动到圆的起始点
        path.arcTo(new RectF(width / 2 - radius, 0, width / 2 + radius, 2 * radius), -90, 180, false); // 绘制半圆
        path.lineTo(width / 2, 0); // 闭合路径

        canvas.drawPath(path, paint);
    }
}