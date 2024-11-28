package com.ttd.jigsawpuzzlev1.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.ttd.jigsawpuzzlev1.R;
import com.ttd.jigsawpuzzlev1.data.PuzzleItem;

import java.io.IOException;
import java.io.InputStream;

public class PuzzleImageHelper {
//    private final Context context;
    private final AssetManager assetManager;
    private final ContentResolver contentResolver;

    public PuzzleImageHelper(Context context) {
//        this.context = context;
        assetManager = context.getAssets();
        contentResolver = context.getApplicationContext().getContentResolver();
    }

    public Bitmap getBitmap(PuzzleItem puzzleItem) {
        String filePath = puzzleItem.getFilePath();
        Bitmap bitmap = null;
        if (filePath != null) {
            try {
                if (puzzleItem.isComesWith()) {
                    InputStream is = assetManager.open(filePath);
                    bitmap = BitmapFactory.decodeStream(is);
                } else {
                    Uri uri = Uri.parse(puzzleItem.getFilePath());
                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    InputStream is = contentResolver.openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(is);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return bitmap;
    }
}