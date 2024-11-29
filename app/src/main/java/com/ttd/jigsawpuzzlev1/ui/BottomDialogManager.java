package com.ttd.jigsawpuzzlev1.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;
import com.ttd.jigsawpuzzlev1.R;

import java.util.Arrays;
import java.util.List;

public class BottomDialogManager {
    private final Context context;
    private AlertDialog dialog;

    public BottomDialogManager(Context context) {
        this.context = context;
    }

    public void create(String[] items, BaseQuickAdapter.OnItemClickListener<String> listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_Dialog_NoTitle);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_bottom, null);
        view.findViewById(R.id.v_cancel).setOnClickListener(v -> dialog.dismiss());
        RecyclerView rvOperation = view.findViewById(R.id.rv_operation);
        rvOperation.setLayoutManager(new LinearLayoutManager(context));
        BottomDialogAdapter adapter = new BottomDialogAdapter(Arrays.asList(items));
        adapter.setOnItemClickListener(listener);
        rvOperation.setAdapter(adapter);
        builder.setView(view);
        dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            //在style文件里设置窗口透明无效
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public static class BottomDialogAdapter extends BaseQuickAdapter<String, QuickViewHolder> {
        public BottomDialogAdapter(@NonNull List<? extends String> items) {
            super(items);
        }

        @Override
        protected void onBindViewHolder(@NonNull QuickViewHolder quickViewHolder, int i, @Nullable String s) {
            quickViewHolder.setText(R.id.tv_operation, s);
        }

        @NonNull
        @Override
        protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
            return new QuickViewHolder(R.layout.adapter_bottom_dialog_item, viewGroup);
        }
    }
}
