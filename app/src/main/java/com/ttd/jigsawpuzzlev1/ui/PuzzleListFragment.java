package com.ttd.jigsawpuzzlev1.ui;

import android.content.Context;
import android.os.Bundle;
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
import com.ttd.jigsawpuzzlev1.data.PuzzleItem;
import com.ttd.jigsawpuzzlev1.data.PuzzleItemDao;

import java.util.List;

public class PuzzleListFragment extends Fragment implements TabFragmentAdapter.RefreshFragment {
    private RecyclerView rvContents;
    private Context context;
    private ContentListFragmentListener contentListFragmentListener;
    private PuzzleContent puzzleContent;
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
            puzzleContent = (PuzzleContent) bundle.getSerializable(PuzzleContent.class.getSimpleName());
            refresh();
        }
    }

    private void setOnItemClickListener(PuzzleItemAdapter adapter) {
        adapter.setOnItemClickListener((baseQuickAdapter, view, i) -> {
            PuzzleItem item = baseQuickAdapter.getItems().get(i);
            PuzzlePlayActivity.start(getContext(), item);
        });
    }

    public void refresh(){
        DaoSession daoSession = ((MyApplication)context.getApplicationContext()).getDaoSession();
        PuzzleItemDao puzzleItemDao = daoSession.getPuzzleItemDao();
        List<PuzzleItem> puzzleItems = puzzleItemDao.queryBuilder().where(PuzzleItemDao.Properties.ContentId.eq(puzzleContent.getId())).list();
        PuzzleItemAdapter adapter = new PuzzleItemAdapter(puzzleItems);
        setOnItemClickListener(adapter);
        rvContents.setAdapter(adapter);
    }

    public PuzzleContent getPuzzleContent() {
        return puzzleContent;
    }
}
