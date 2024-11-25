package com.ttd.jigsawpuzzlev1.ui;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;
import com.ttd.jigsawpuzzlev1.R;
import com.ttd.jigsawpuzzlev1.data.PuzzleContent;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PuzzleContentsAdapter extends BaseQuickAdapter<PuzzleContent, QuickViewHolder> {
    private AssetManager assetManager;

    public PuzzleContentsAdapter(@NonNull List<? extends PuzzleContent> items) {
        super(items);
    }

    @Override
    protected void onBindViewHolder(@NonNull QuickViewHolder quickViewHolder, int i, @Nullable PuzzleContent puzzleContent) {
        if (puzzleContent != null) {
            if(assetManager == null){
                assetManager = getContext().getAssets();
            }
            String filePath = puzzleContent.getFilePath();
            if (filePath != null) {
                try {
                    InputStream is = assetManager.open(filePath);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    quickViewHolder.setImageBitmap(R.id.iv_content, bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @NonNull
    @Override
    protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        return new QuickViewHolder(R.layout.adapter_main_contents, viewGroup);
    }

}
