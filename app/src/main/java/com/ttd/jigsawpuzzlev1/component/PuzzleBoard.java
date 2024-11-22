package com.ttd.jigsawpuzzlev1.component;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class PuzzleBoard extends RelativeLayout {

    public PuzzleBoard(Context context) {
        super(context);
    }

    public PuzzleBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PuzzleBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PuzzleBoard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params) {
        return super.addViewInLayout(child, index, params);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(this.getClass().getSimpleName(),getX()+","+getY());
        return super.onTouchEvent(event);
    }
}
