package com.ttd.jigsawpuzzlev1.ui.record;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.ttd.jigsawpuzzlev1.data.db.DaoSession;
import com.ttd.jigsawpuzzlev1.data.db.PuzzleItem;
import com.ttd.jigsawpuzzlev1.data.db.PuzzleItemDao;
import com.ttd.jigsawpuzzlev1.data.db.PuzzleRecord;
import com.ttd.jigsawpuzzlev1.data.db.PuzzleRecordDao;
import com.ttd.jigsawpuzzlev1.ui.BottomDialogManager;
import com.ttd.jigsawpuzzlev1.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

public class RecordListFragment extends Fragment {
    private RecyclerView rvRecord;
    private Context context;
    private RecordListFragmentListener recordListFragmentListener;
    private RecordListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        if (context instanceof RecordListFragmentListener) {
            recordListFragmentListener = (RecordListFragmentListener) context;
        }
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);
        rvRecord = view.findViewById(R.id.rv_history);
        rvRecord.setLayoutManager(new LinearLayoutManager(context));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (recordListFragmentListener != null) {
            recordListFragmentListener.requestPermissionInPageRecord();
        } else {
            showHistory();
        }
    }

    public void showHistory() {
        DaoSession daoSession = ((MyApplication) context.getApplicationContext()).getDaoSession();
        PuzzleRecordDao recordDao = daoSession.getPuzzleRecordDao();
        PuzzleItemDao itemDao = daoSession.getPuzzleItemDao();
        List<PuzzleRecord> recordList = recordDao.loadAll();
        List<PuzzleItem> itemList = new ArrayList<>();
        for (PuzzleRecord record : recordList) {
            PuzzleItem puzzleItem = itemDao.queryBuilder().where(PuzzleItemDao.Properties.Id.eq(record.getItemId())).unique();
            itemList.add(puzzleItem);
        }
        if (adapter == null) {
            adapter = new RecordListAdapter(itemList, recordList);
            setOnItemClickListener(adapter);
            adapter.addOnItemChildClickListener(R.id.v_operation, (baseQuickAdapter, view, i) -> showOperationDialog(recordList.get(i)));
            rvRecord.setAdapter(adapter);
        } else {
            adapter.setRecordList(recordList);
            adapter.submitList(itemList);
        }

    }

    private void setOnItemClickListener(RecordListAdapter adapter) {
        adapter.setOnItemClickListener((baseQuickAdapter, view, i) -> {
            PuzzleItem item = baseQuickAdapter.getItems().get(i);
            recordListFragmentListener.onRecordItemClick(item);
        });
    }

    private void showOperationDialog(PuzzleRecord record) {
        BottomDialogManager bottomDialogManager = new BottomDialogManager(context);
        bottomDialogManager.create(new String[]{"删除"}, new int[]{R.mipmap.ic_delete}, (baseQuickAdapter, view, i) -> {
            if (i == 0) {
                DaoSession daoSession = ((MyApplication) context.getApplicationContext()).getDaoSession();
                PuzzleRecordDao recordDao = daoSession.getPuzzleRecordDao();
                recordDao.delete(record);
                String previewImage = record.getPreviewPic();
                if (!TextUtils.isEmpty(previewImage)) {
                    FileUtil fileUtil = new FileUtil((Activity) context);
                    fileUtil.deleteImageFromSdcard(previewImage);
                }
                bottomDialogManager.dismiss();
                refresh();
            }
        });
        bottomDialogManager.show();
    }

    public void refresh() {
        showHistory();
    }

}
