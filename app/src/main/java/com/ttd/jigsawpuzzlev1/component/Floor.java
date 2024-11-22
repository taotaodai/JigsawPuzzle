package com.ttd.jigsawpuzzlev1.component;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.customview.widget.ViewDragHelper;

public class Floor extends PuzzleBoard {
    private ViewDragHelper viewDragHelper;

    public Floor(Context context) {
        super(context);
        init();
    }

    public Floor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, 2f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                return true;
            }

            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
//                if (child instanceof PuzzleBoard) {
//                    Log.i(this.getClass().getSimpleName(), left + "," + dx);
//                    lengthenBoard(child, left, 0);
//                }
                return left;
            }

            @Override
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
                return top;
            }
        });

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_UP) {
            viewDragHelper.cancel();
            return false;
        }
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

//    private int maxLeft;
//    private int minLeft;
//    private int maxTop;
//    private int minTop;
//    private final int minOffset = 50;
//
//    private void lengthenBoard(View child, int x, int y) {
//        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
//        if ((x > maxLeft && x > 0) || (x < minLeft && x < 0)) {
//            int offset = Math.abs(x - maxLeft);
//            if (offset > minOffset) {
//                layoutParams.width += offset;
//                maxLeft = x;
//                child.setLayoutParams(layoutParams);
//            }
//        }
//
//        if((y > maxTop && y > 0) || (y < minTop && y < 0)){
//            int offset = Math.abs(y - maxTop);
//            if(offset > minOffset){
//                layoutParams.height += offset;
//                maxTop = y;
//            }
//        }
//    }
}
