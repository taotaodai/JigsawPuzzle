package com.ttd.jigsawpuzzlev1.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ttd.jigsawpuzzlev1.R;
import com.ttd.jigsawpuzzlev1.component.BlockView;
import com.ttd.jigsawpuzzlev1.component.Floor;
import com.ttd.jigsawpuzzlev1.component.PuzzleBoard;
import com.ttd.jigsawpuzzlev1.data.PuzzleContent;
import com.ttd.jigsawpuzzlev1.data.PuzzlePiece;
import com.ttd.jigsawpuzzlev1.utils.DisplayUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

public class PuzzlePlayActivity extends BaseActivity implements View.OnClickListener {
    private BlockCropper blockCropper;
    private Floor floor;
    private PuzzleBoard pbContainer;
    private LinearLayout llBlockList;
    private LinearLayout llOperation;
    private View vMagnify;
    private View vLessen;
    private TextView vBoard;
    private PuzzleContent puzzleContent;

    public static void start(Context context, PuzzleContent puzzleContent) {
        Intent intent = new Intent(context, PuzzlePlayActivity.class);
        intent.putExtra(PuzzleContent.class.getSimpleName(), puzzleContent);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_show);

        floor = findViewById(R.id.v_floor);
        pbContainer = findViewById(R.id.pb_block_container);
        llBlockList = findViewById(R.id.ll_block_list);
        llOperation = findViewById(R.id.ll_operation);
        vBoard = findViewById(R.id.tv_board);
        vMagnify = findViewById(R.id.v_magnify);
        vLessen = findViewById(R.id.v_lessen);
        vMagnify.setOnClickListener(this);
        vLessen.setOnClickListener(this);

        initData();
        initViews();
        initPuzzle();
    }

    private int screenWidth;
    private int screenHeight;
    private int floorWidth;
    private int floorHeight;

    private void initData() {
        screenWidth = (int) DisplayUtil.getScreenWidth(this);
        screenHeight = (int) DisplayUtil.getScreenHeight(this);
        floorWidth = screenWidth * 3;
        floorHeight = screenHeight * 3;
        puzzleContent = (PuzzleContent) getIntent().getSerializableExtra(PuzzleContent.class.getSimpleName());
        if (puzzleContent == null) {
            finish();
        }
    }

    private void initViews() {
        ViewGroup.LayoutParams layoutParams = floor.getLayoutParams();
//        blockCropper = new BlockCropper(this, R.mipmap.test_res1);
        blockCropper = new BlockCropper(this, puzzleContent);

        int maxWidth = floorWidth;
        int maxHeight = floorHeight;

        int offsetWidth = (maxWidth - screenWidth) / 2;
        int offsetHeight = (maxHeight - screenHeight) / 2;
        floor.setX((float) -offsetWidth);
        floor.setY((float) -offsetHeight);

        RelativeLayout.LayoutParams operationLayoutParams = (RelativeLayout.LayoutParams) llOperation.getLayoutParams();
        operationLayoutParams.setMargins(0, offsetHeight, offsetWidth, 0);
        //设置背景板大小
        layoutParams.width = maxWidth;
        layoutParams.height = maxHeight;
        //初始化背景板
        floor.setLayoutParams(layoutParams);

        int boardWidth = blockCropper.getOriginalBitmap().getWidth();
        int boardHeight = blockCropper.getOriginalBitmap().getHeight();
        //设置拼图板大小
        vBoard.setWidth(boardWidth);
        vBoard.setHeight(boardHeight);
        //初始化缩放比例
        float scaleX = 1f;
        float scaleY = 1f;
        if (boardHeight > screenHeight) {
            scaleX = new BigDecimal(screenHeight).divide(new BigDecimal(boardHeight), 3, BigDecimal.ROUND_HALF_UP).floatValue();
        }
        if (boardWidth > screenWidth) {
            scaleY = new BigDecimal(screenWidth).divide(new BigDecimal(boardWidth), 3, BigDecimal.ROUND_HALF_UP).floatValue();
        }
        lessen(Math.min(scaleX, scaleY));
    }

    private void initPuzzle() {
        vBoard.post(() -> {
            int screenWidth = (int) DisplayUtil.getScreenWidth(this);
//            int screenHeight = (int) DisplayUtil.getScreenHeight(this);
            int puzzleBoardLeft = vBoard.getLeft();
            int puzzleBoardTop = vBoard.getTop();
            int puzzleBoardRight = vBoard.getRight();
            int puzzleBoardBottom = vBoard.getBottom();
            List<PuzzlePiece> pieceList = blockCropper.cropping();

            int xArea = screenWidth / 2;
            Random random = new Random();

            for (PuzzlePiece p : pieceList) {
                BlockView blockView = new BlockView(getBaseContext());
                blockView.setBorder(0, 0, floorWidth, floorHeight);
                blockView.setElevation(2);
                Bitmap bitmap = p.getBitmap();
                blockView.setImageBitmap(bitmap);
                blockView.setPuzzlePiece(p);
                //随机在拼图板两侧生成拼图块
                int x = random.nextBoolean() ? puzzleBoardLeft - random.nextInt(xArea) : puzzleBoardRight + random.nextInt(xArea);
                int y = puzzleBoardTop + random.nextInt(puzzleBoardBottom - puzzleBoardTop);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
                layoutParams.leftMargin = x;
                layoutParams.topMargin = y;
                blockView.setLayoutParams(layoutParams);
                pbContainer.addView(blockView);
            }
        });

    }

    private void magnify() {
        float scaleX = pbContainer.getScaleX();
        magnify(scaleX + 0.2f);
    }

    private void magnify(float scale) {
        float scaleX = pbContainer.getScaleX();
        if (scaleX <= 2f) {
            pbContainer.setScaleX(scale);
            pbContainer.setScaleY(scale);
        }
    }

    private void lessen() {
        float scaleX = pbContainer.getScaleX();
        lessen(scaleX - 0.2f);
    }

    private void lessen(float scale) {
        float scaleX = pbContainer.getScaleX();
        if (scaleX >= 0.5f) {
            pbContainer.setScaleX(scale);
            pbContainer.setScaleY(scale);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.v_magnify) {
            magnify();
        } else if (v.getId() == R.id.v_lessen) {
            lessen();
        }
    }
}
