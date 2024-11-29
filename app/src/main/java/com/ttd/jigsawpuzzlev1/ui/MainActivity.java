package com.ttd.jigsawpuzzlev1.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.ttd.jigsawpuzzlev1.MyApplication;
import com.ttd.jigsawpuzzlev1.R;
import com.ttd.jigsawpuzzlev1.data.db.DaoSession;
import com.ttd.jigsawpuzzlev1.data.db.PuzzleContent;
import com.ttd.jigsawpuzzlev1.data.db.PuzzleContentDao;
import com.ttd.jigsawpuzzlev1.data.db.PuzzleItem;
import com.ttd.jigsawpuzzlev1.data.db.PuzzleItemDao;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ContentListFragmentListener, View.OnClickListener {
    private SlidingTabLayout stlContents;
    private ViewPager vpContents;
    private ActivityResultLauncher<PickVisualMediaRequest> activityResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViews();
    }


    private void initViews() {
        stlContents = findViewById(R.id.stl_contents);
        vpContents = findViewById(R.id.vp_contents);
        View vAdd = findViewById(R.id.v_add);
        vAdd.setOnClickListener(this);
        initFragments();
        initMediaPicker();
    }

    private void initMediaPicker() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), o -> {
            if (o != null && isPuzzleListShowing() && puzzleListFragment != null) {
                DaoSession daoSession = (((MyApplication) getApplication()).getDaoSession());
                PuzzleItemDao puzzleItemDao = daoSession.getPuzzleItemDao();
                PuzzleContent puzzleContent = puzzleListFragment.getPuzzleContent();
                if (puzzleContent != null) {
                    PuzzleItem puzzleItem = new PuzzleItem(puzzleContent.getId(), false, o.toString());
                    puzzleItemDao.insert(puzzleItem);
                    refreshPuzzleList();
                }
            }
        });
    }

    private ArrayList<Fragment> fragments;
    private ContentListFragment topContentListFragment;
    private PuzzleListFragment puzzleListFragment;

    private void initFragments() {
        fragments = new ArrayList<>();
        topContentListFragment = new ContentListFragment();
        fragments.add(topContentListFragment);
        fragments.add(new HistoryListFragment());
        TabFragmentAdapter adapter = new TabFragmentAdapter(getSupportFragmentManager(), fragments, new String[]{"目录", "存档"});
        vpContents.setAdapter(adapter);
        stlContents.setViewPager(vpContents);

//        vpContents.getAdapter().no
    }

    private void refreshFragments() {
        TabFragmentAdapter adapter = (TabFragmentAdapter) vpContents.getAdapter();
        if (adapter != null) {
            adapter.setRefresh(true);
            adapter.notifyDataSetChanged();
        }
    }

    private void refreshContents() {
        if (topContentListFragment != null) {
            topContentListFragment.refresh();
        }
    }

    private void refreshPuzzleList() {
        if (puzzleListFragment != null) {
            puzzleListFragment.refresh();
        }
    }

    @Override
    public void onContentClick(PuzzleContent puzzleContent) {
        if (checkAndRequestPermissions()) {
            puzzleListFragment = new PuzzleListFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(PuzzleContent.class.getSimpleName(), puzzleContent);
            puzzleListFragment.setArguments(bundle);
            fragments.set(0, puzzleListFragment);
            refreshFragments();
        }
    }

    @Override
    public void requestPermission() {
        checkAndRequestPermissions();
    }

    private void openImagePicker() {
        PickVisualMediaRequest pickVisualMediaRequest = new PickVisualMediaRequest();
        activityResultLauncher.launch(pickVisualMediaRequest);
    }

    private void showDirCreator() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("创建拼图目录");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_dir_name, null);
        EditText etDir = view.findViewById(R.id.et_dir);
        builder.setPositiveButton("确定", (dialog, which) -> {
            DaoSession daoSession = (((MyApplication) getApplication()).getDaoSession());
            PuzzleContentDao puzzleContentDao = daoSession.getPuzzleContentDao();
            PuzzleContent puzzleContent = new PuzzleContent(null, false);
            puzzleContent.setName(etDir.getText().toString());
            puzzleContentDao.insert(puzzleContent);
            refreshContents();
        });
        builder.setNegativeButton("取消", null);
        builder.setView(view);
        builder.show();

    }

    private boolean isPuzzleListShowing() {
        boolean isFirst = vpContents.getCurrentItem() == 0;
        boolean isTop = fragments.indexOf(topContentListFragment) == 0;

        return isFirst && !isTop;
    }

    private static final int PERMISSION_REQUEST_CODE = 1;

    // 检查权限并请求
    private boolean checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
                return false;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

//    // 处理权限请求结果
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // 权限被用户同意，可以读取联系人
//            } else {
//                // 权限被用户拒绝，需要提示用户或者采取其他方式
//                Toast.makeText(this, "需要读取联系人的权限", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    @Override
    public void onBackPressed() {
        if (isPuzzleListShowing()) {
            fragments.set(0, topContentListFragment);
            refreshFragments();
        } else {
            super.onBackPressed();
        }

//        Log.i(getClass().getSimpleName(), String.valueOf(fragments.indexOf(topContentListFragment)));

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.v_add) {
            if (isPuzzleListShowing()) {
                openImagePicker();
            } else {
                showDirCreator();
            }
        }
    }
}
