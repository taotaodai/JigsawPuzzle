package com.ttd.jigsawpuzzlev1.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DisplayUtil {
    public static float getScreenWidth(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static float getScreenHeight(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static void scaleDisplay(Context context,float scaleFactor) {
        // 获取资源对象
        Resources resources = context.getResources();
        // 获取当前显示设置的Metrics对象
        DisplayMetrics dm = resources.getDisplayMetrics();
        // 设置新的缩放比例
        dm.scaledDensity = scaleFactor * dm.scaledDensity;
        dm.density = scaleFactor * dm.density;
        // 通过Resources更新显示设置
        resources.updateConfiguration(resources.getConfiguration(), dm);
    }
}
