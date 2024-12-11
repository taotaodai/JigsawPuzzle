package com.ttd.jigsawpuzzlev1.ui.record;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;
import com.ttd.jigsawpuzzlev1.R;
import com.ttd.jigsawpuzzlev1.data.db.PuzzleItem;
import com.ttd.jigsawpuzzlev1.data.db.PuzzleRecord;
import com.ttd.jigsawpuzzlev1.utils.PuzzleImageHelper;

import java.util.List;

public class RecordListAdapter extends BaseQuickAdapter<PuzzleItem, QuickViewHolder> {
    private PuzzleImageHelper puzzleImageHelper;
    private List<PuzzleRecord> recordList;

    public RecordListAdapter(@NonNull List<? extends PuzzleItem> items, List<PuzzleRecord> recordList) {
        super(items);
        this.recordList = recordList;
    }

    public void setRecordList(List<PuzzleRecord> recordList) {
        this.recordList = recordList;
    }

    @Override
    protected void onBindViewHolder(@NonNull QuickViewHolder quickViewHolder, int i, @Nullable PuzzleItem puzzleItem) {
        if (puzzleItem != null) {
            if (puzzleImageHelper == null) {
                puzzleImageHelper = new PuzzleImageHelper(getContext());
            }
            Bitmap bitmap = puzzleImageHelper.getBitmap(puzzleItem);
            if (bitmap != null) {
                ImageView iv = quickViewHolder.getView(R.id.iv_content);
                Glide.with(getContext()).load(bitmap).into(iv);
            }
            ImageView ivPreview = quickViewHolder.getView(R.id.iv_preview);
            if (recordList != null && recordList.size() == getItemCount()) {
                PuzzleRecord record = recordList.get(i);
                String previewPic = record.getPreviewPic();
                ivPreview.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(previewPic)) {
                    Glide.with(getContext()).load(Uri.parse(previewPic)).into(ivPreview);
                }
            } else {
                ivPreview.setVisibility(View.INVISIBLE);
            }
        }
    }

    @NonNull
    @Override
    protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        return new QuickViewHolder(R.layout.adapter_main_contents, viewGroup);
    }
}
