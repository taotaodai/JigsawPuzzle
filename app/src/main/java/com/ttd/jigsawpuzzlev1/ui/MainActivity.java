package com.ttd.jigsawpuzzlev1.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.ttd.jigsawpuzzlev1.R;
import com.ttd.jigsawpuzzlev1.data.PuzzleContent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements ContentListFragmentListener {
    private SlidingTabLayout stlContents;
    private ViewPager vpContents;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        stlContents = findViewById(R.id.stl_contents);
        vpContents = findViewById(R.id.vp_contents);
        initFragments();
    }

    private ArrayList<Fragment> fragments;
    private ContentListFragment topContentListFragment;

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


    @Override
    public void onContentClick(List<PuzzleContent> children) {
        ContentListFragment inerContentListFragment = new ContentListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PuzzleContent[].class.getSimpleName(), (Serializable) children);
        inerContentListFragment.setArguments(bundle);
        fragments.set(0, inerContentListFragment);
        TabFragmentAdapter adapter = (TabFragmentAdapter) vpContents.getAdapter();
        if (adapter != null) {
            adapter.setRefresh(true);
            adapter.notifyDataSetChanged();
        }
    }
}
