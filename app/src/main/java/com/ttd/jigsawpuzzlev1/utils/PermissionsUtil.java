package com.ttd.jigsawpuzzlev1.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class PermissionsUtil {
    private final Activity context;
    private PermissionDisposeResultCallBack resultCallBack;

    public PermissionsUtil(Activity context) {
        this.context = context;
    }

    public void disposePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 权限被用户同意
            if (resultCallBack != null) {
                resultCallBack.onAuthorize(true);
            }
        } else {
            // 权限被用户拒绝，需要提示用户或者采取其他方式
            Toast.makeText(context, "请求权限被拒绝", Toast.LENGTH_SHORT).show();
            if (resultCallBack != null) {
                resultCallBack.onAuthorize(false);
            }
        }
    }

    private static final int PERMISSION_REQUEST_CODE = 1;

    public void setResultCallBack(PermissionDisposeResultCallBack resultCallBack) {
        this.resultCallBack = resultCallBack;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private static final String[] permissions33 = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
    private static final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public boolean checkReadImagePermissions() {
        boolean imagePermissionGranted;
        boolean writeExternalStorageGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            imagePermissionGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
            if (!imagePermissionGranted) {
                ActivityCompat.requestPermissions(context, permissions33, PERMISSION_REQUEST_CODE);
                return false;
            }
        } else {
            imagePermissionGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            if (!imagePermissionGranted || !writeExternalStorageGranted) {
                ActivityCompat.requestPermissions(context, permissions, PERMISSION_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    public interface PermissionDisposeResultCallBack {
        void onAuthorize(boolean agree);
    }
}
