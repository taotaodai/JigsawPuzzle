package com.ttd.jigsawpuzzlev1.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;
import com.ttd.jigsawpuzzlev1.R;
import com.ttd.jigsawpuzzlev1.data.db.PuzzleItem;
import com.ttd.jigsawpuzzlev1.utils.PuzzleImageHelper;

import java.util.List;

public class PuzzleItemAdapter extends BaseQuickAdapter<PuzzleItem, QuickViewHolder> {
    private PuzzleImageHelper puzzleImageHelper;

    public PuzzleItemAdapter(@NonNull List<? extends PuzzleItem> items) {
        super(items);
    }

    @Override
    protected void onBindViewHolder(@NonNull QuickViewHolder quickViewHolder, int i, @Nullable PuzzleItem puzzleItem) {
        if (puzzleItem != null) {
            if (puzzleImageHelper == null) {
                puzzleImageHelper = new PuzzleImageHelper(getContext());
            }
            Bitmap bitmap = puzzleImageHelper.getBitmap(puzzleItem);
            if (bitmap != null) {
                quickViewHolder.setImageBitmap(R.id.iv_content, bitmap);
            }
        }
    }

    @NonNull
    @Override
    protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        return new QuickViewHolder(R.layout.adapter_main_contents, viewGroup);
    }

}
