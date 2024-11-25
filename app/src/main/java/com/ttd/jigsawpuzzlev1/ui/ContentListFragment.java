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

import com.ttd.jigsawpuzzlev1.R;
import com.ttd.jigsawpuzzlev1.data.PuzzleContent;

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

        Bundle bundle = getArguments();
        if (bundle != null) {
            List<PuzzleContent> children = (List<PuzzleContent>) bundle.getSerializable(PuzzleContent[].class.getSimpleName());
            if (children != null) {
                PuzzleContentsAdapter adapter = new PuzzleContentsAdapter(children);
                setOnItemClickListener(adapter);
                rvContents.setAdapter(adapter);
                return;
            }
        }
        initDefaultContents();
    }

    private void setOnItemClickListener(PuzzleContentsAdapter adapter) {
        adapter.setOnItemClickListener((baseQuickAdapter, view, i) -> {
            PuzzleContent item = baseQuickAdapter.getItems().get(i);
            if (item.isClassification()) {
                if (contentListFragmentListener != null) {
                    contentListFragmentListener.onContentClick(item.getChildren());
                }
            } else {
                PuzzlePlayActivity.start(getContext(), item);
            }
        });
    }

    private void initDefaultContents() {
        AssetManager assetManager = context.getAssets();
        try {
            String rootDir = "gallery/";
            String[] contentDirs = assetManager.list(rootDir);
            Log.i(getClass().getSimpleName(), Arrays.toString(contentDirs));
            if (contentDirs != null) {
                List<PuzzleContent> puzzleContents = new ArrayList<>();
                for (String path : contentDirs) {
                    String dir = rootDir + path + File.separator;
                    String[] pathArray = assetManager.list(dir);
                    Log.i(getClass().getSimpleName(), Arrays.toString(pathArray));
                    if (pathArray != null && pathArray.length > 0) {
                        List<PuzzleContent> children = new ArrayList<>();
                        for (String fileName : pathArray) {
                            children.add(new PuzzleContent(dir + fileName, true));
                        }
                        //取第一张作为封面
                        PuzzleContent puzzleContent = new PuzzleContent(dir + pathArray[0], true, true, children);
                        puzzleContents.add(puzzleContent);
                    }
                }
                PuzzleContentsAdapter adapter = new PuzzleContentsAdapter(puzzleContents);
                setOnItemClickListener(adapter);

                rvContents.setAdapter(adapter);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
