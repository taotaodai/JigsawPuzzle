package com.ttd.jigsawpuzzlev1.ui;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ttd.jigsawpuzzlev1.MyApplication;
import com.ttd.jigsawpuzzlev1.R;
import com.ttd.jigsawpuzzlev1.data.DaoSession;
import com.ttd.jigsawpuzzlev1.data.PuzzleContent;
import com.ttd.jigsawpuzzlev1.data.PuzzleContentDao;
import com.ttd.jigsawpuzzlev1.data.PuzzleItem;
import com.ttd.jigsawpuzzlev1.data.PuzzleItemDao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContentListFragment extends Fragment implements TabFragmentAdapter.RefreshFragment {
    private RecyclerView rvContents;
    private Context context;
    private ContentListFragmentListener contentListFragmentListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        if (context instanceof ContentListFragmentListener) {
            contentListFragmentListener = (ContentListFragmentListener) context;
        }
        View view = inflater.inflate(R.layout.fragment_content_list, container, false);
        rvContents = view.findViewById(R.id.rv_contents);
        rvContents.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDefaultContents();
        refresh();
    }

    private void setOnItemClickListener(PuzzleContentsAdapter adapter) {
        adapter.setOnItemClickListener((baseQuickAdapter, view, i) -> {
            PuzzleContent item = baseQuickAdapter.getItems().get(i);
            if (contentListFragmentListener != null) {
                contentListFragmentListener.onContentClick(item);
            }
        });
    }

    private void initDefaultContents() {
        DaoSession daoSession = ((MyApplication) context.getApplicationContext()).getDaoSession();
        PuzzleContentDao puzzleContentDao = daoSession.getPuzzleContentDao();
        PuzzleItemDao puzzleItemDao = daoSession.getPuzzleItemDao();
        AssetManager assetManager = context.getAssets();
        try {
            String rootDir = "gallery/";
            String[] contentDirs = assetManager.list(rootDir);
            Log.i(getClass().getSimpleName(), Arrays.toString(contentDirs));
            if (contentDirs != null) {
                long dirId = 0;
                long puzzleId = 0;
                for (String path : contentDirs) {
                    dirId++;
                    String dir = rootDir + path + File.separator;
                    String[] pathArray = assetManager.list(dir);
                    Log.i(getClass().getSimpleName(), Arrays.toString(pathArray));
                    if (pathArray != null && pathArray.length > 0) {
                        for (String fileName : pathArray) {
                            puzzleId++;
                            PuzzleItem puzzleItem = new PuzzleItem(puzzleId, dirId, true, dir + fileName);
                            puzzleItemDao.insertOrReplace(puzzleItem);
                        }

                        //取第一张作为封面
                        PuzzleContent puzzleContent = new PuzzleContent(dirId, "", dir + pathArray[0], true);
                        puzzleContentDao.insertOrReplace(puzzleContent);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void refresh() {
        DaoSession daoSession = ((MyApplication) context.getApplicationContext()).getDaoSession();
        PuzzleContentDao puzzleContentDao = daoSession.getPuzzleContentDao();
        PuzzleItemDao puzzleItemDao = daoSession.getPuzzleItemDao();

        //先查出所有自带的拼图目录
        List<PuzzleContent> puzzleContentList = puzzleContentDao.queryBuilder().where(PuzzleContentDao.Properties.IsComesWith.eq(true)).list();
        for (PuzzleContent puzzleContent : puzzleContentList) {
            List<PuzzleItem> puzzleItems = puzzleItemDao.queryBuilder().where(PuzzleItemDao.Properties.ContentId.eq(puzzleContent.getId())).list();
            puzzleContent.setPuzzlePicList(puzzleItems);
        }

        List<PuzzleContent> puzzleContentListNew = puzzleContentDao.queryBuilder().where(PuzzleContentDao.Properties.IsComesWith.eq(false)).list();
        for (PuzzleContent puzzleContent : puzzleContentListNew) {
            List<PuzzleItem> puzzleItems = puzzleItemDao.queryBuilder().where(PuzzleItemDao.Properties.ContentId.eq(puzzleContent.getId())).list();
            puzzleContent.setPuzzlePicList(puzzleItems);
        }
        puzzleContentList.addAll(puzzleContentListNew);

        PuzzleContentsAdapter adapter = new PuzzleContentsAdapter(puzzleContentList);
        setOnItemClickListener(adapter);

        rvContents.setAdapter(adapter);
    }
}
