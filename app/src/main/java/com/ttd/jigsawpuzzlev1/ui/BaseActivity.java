package com.ttd.jigsawpuzzlev1.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ttd.jigsawpuzzlev1.utils.PermissionsUtil;

public class BaseActivity extends AppCompatActivity {
    protected PermissionsUtil permissionsUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUtils();
    }

    private void initUtils() {
        permissionsUtil = new PermissionsUtil(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsUtil.disposePermissionResult(requestCode, permissions, grantResults);
    }
}
