package com.ttd.jigsawpuzzlev1.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;
import com.ttd.jigsawpuzzlev1.R;
import com.ttd.jigsawpuzzlev1.data.PuzzleItem;
import com.ttd.jigsawpuzzlev1.utils.PuzzleImageHelper;

import java.io.IOException;
import java.io.InputStream;
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
