package com.ttd.jigsawpuzzlev1.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

public class FileUtil {
    private final Activity context;

    public FileUtil(Activity context) {
        this.context = context;
    }

    public String saveImage2Sdcard(Bitmap bitmap, String name) {
        String image = MediaStore.Images.Media.insertImage(
                context.getContentResolver(),
                bitmap, name, "");

        //保存图片后发送广播通知更新数据库
//        context.sendBroadcast(new Intent(
//                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                Uri.parse(image)));

        return image;
    }

    public boolean deleteImageFromSdcard(String uriString) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse(uriString);
        int result = resolver.delete(uri, null, null);
        return result > 0;
    }
}
