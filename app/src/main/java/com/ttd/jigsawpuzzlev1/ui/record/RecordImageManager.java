package com.ttd.jigsawpuzzlev1.ui.record;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;

import com.ttd.jigsawpuzzlev1.MyApplication;
import com.ttd.jigsawpuzzlev1.data.db.DaoSession;
import com.ttd.jigsawpuzzlev1.data.db.PuzzleRecord;
import com.ttd.jigsawpuzzlev1.data.db.PuzzleRecordDao;
import com.ttd.jigsawpuzzlev1.utils.FileUtil;
import com.ttd.jigsawpuzzlev1.utils.SystemUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class RecordImageManager {
    private final Activity context;
    private final FileUtil fileUtil;

    public RecordImageManager(Activity context) {
        this.context = context;
        fileUtil = new FileUtil(context);
    }

    /**
     * 从 View 中获取 Bitmap
     *
     * @param view 将要被截图的View
     */
    public void saveBitmapFromView(View view, PuzzleRecord record) {
        Bitmap b;
        //请求转换
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //获取layout的位置
            final int[] location = new int[2];
            view.getLocationInWindow(location);
            //准备一个bitmap对象，用来将copy出来的区域绘制到此对象中
            b = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888, true);
            PixelCopy.request(context.getWindow(),
                    new Rect(location[0], location[1], location[0] + view.getWidth(), location[1] + view.getHeight()),
                    b, copyResult -> {
                        //如果成功
                        if (copyResult == PixelCopy.SUCCESS) {
                            compressAndSave(b, record);
                        }
                    }, new Handler(Looper.getMainLooper()));
        } else {
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY));
            view.layout((int) view.getX(), (int) view.getY(),
                    (int) view.getX() + view.getMeasuredWidth(), (int) view.getY() + view.getMeasuredWidth());
            b = Bitmap.createBitmap(view.getDrawingCache(),
                    0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.setDrawingCacheEnabled(false);
            view.destroyDrawingCache();
            compressAndSave(b, record);
        }
    }

    private void compressAndSave(Bitmap bitmap, PuzzleRecord record) {
        String oldPath = record.getPreviewPic();
        if (!TextUtils.isEmpty(oldPath)) {//如果存在截图则先删除
            boolean deleteResult = fileUtil.deleteImageFromSdcard(oldPath);
            Log.i(getClass().getSimpleName(), "删除截图：" + oldPath + "\n删除结果：" + deleteResult);
        }
        Bitmap bitmapCompressed = compressImage(bitmap);
        String path = fileUtil.saveImage2Sdcard(bitmapCompressed, String.valueOf(System.currentTimeMillis()));
        DaoSession daoSession = ((MyApplication) context.getApplication()).getDaoSession();
        PuzzleRecordDao puzzleRecordDao = daoSession.getPuzzleRecordDao();
        record.setPreviewPic(path);
        puzzleRecordDao.update(record);
        if (SystemUtil.isApkInDebug(context)) {
            Log.i(getClass().getSimpleName(), "保存截图结果id=：" + record.getId() + "\n截图uri：" + path);
        }
    }

    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 5, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(isBm, null, null);
    }

}
