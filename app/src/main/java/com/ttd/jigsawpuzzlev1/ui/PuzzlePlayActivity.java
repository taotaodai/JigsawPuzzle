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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ttd.jigsawpuzzlev1.MyApplication;
import com.ttd.jigsawpuzzlev1.R;
import com.ttd.jigsawpuzzlev1.component.BlockView;
import com.ttd.jigsawpuzzlev1.component.Floor;
import com.ttd.jigsawpuzzlev1.component.PuzzleBoard;
import com.ttd.jigsawpuzzlev1.data.Point;
import com.ttd.jigsawpuzzlev1.data.PuzzlePiece;
import com.ttd.jigsawpuzzlev1.data.PuzzleRecordItem;
import com.ttd.jigsawpuzzlev1.data.db.DaoSession;
import com.ttd.jigsawpuzzlev1.data.db.PuzzleItem;
import com.ttd.jigsawpuzzlev1.data.db.PuzzleRecord;
import com.ttd.jigsawpuzzlev1.data.db.PuzzleRecordDao;
import com.ttd.jigsawpuzzlev1.utils.DisplayUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
    private PuzzleItem puzzleItem;
    private PuzzleRecord puzzleRecord;

    public static void start(Context context, PuzzleItem puzzleItem) {
        Intent intent = new Intent(context, PuzzlePlayActivity.class);
        intent.putExtra(PuzzleItem.class.getSimpleName(), puzzleItem);
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
        puzzleItem = (PuzzleItem) getIntent().getSerializableExtra(PuzzleItem.class.getSimpleName());
        if (puzzleItem == null) {
            finish();
        }
        DaoSession daoSession = ((MyApplication) getApplication()).getDaoSession();
        PuzzleRecordDao puzzleRecordDao = daoSession.getPuzzleRecordDao();
        List<PuzzleRecord> puzzleRecords = puzzleRecordDao.queryBuilder().where(PuzzleRecordDao.Properties.ItemId.eq(puzzleItem.getId())).list();
        //目前一个只做一个存档记录
        if (puzzleRecords != null && puzzleRecords.size() > 0) {
            puzzleRecord = puzzleRecords.get(0);
        }
    }

    private void initViews() {
        ViewGroup.LayoutParams layoutParams = floor.getLayoutParams();
        blockCropper = new BlockCropper(this, puzzleItem);

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
            scaleX = new BigDecimal(screenHeight).divide(new BigDecimal(boardHeight), 3, RoundingMode.HALF_UP).floatValue();
        }
        if (boardWidth > screenWidth) {
            scaleY = new BigDecimal(screenWidth).divide(new BigDecimal(boardWidth), 3, RoundingMode.HALF_UP).floatValue();
        }
        lessen(Math.min(scaleX, scaleY));
    }

    private List<PuzzlePiece> pieceList;

    private void initPuzzle() {
        vBoard.post(() -> {
            pieceList = blockCropper.cropping();

            List<Point> recordPoints = null;
            if (puzzleRecord != null) {
                recordPoints = new Gson().fromJson(puzzleRecord.getRecords(), new TypeToken<ArrayList<Point>>() {
                }.getType());
            }
            for (int i = 0; i < pieceList.size(); i++) {
                PuzzlePiece p = pieceList.get(i);
                BlockView blockView = new BlockView(getBaseContext());
                blockView.setBorder(0, 0, floorWidth, floorHeight);
                blockView.setElevation(2);
                Bitmap bitmap = p.getBitmap();
                blockView.setImageBitmap(bitmap);
                blockView.setPuzzlePiece(p);

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
                if (recordPoints != null && recordPoints.size() == pieceList.size()) {
                    Point point = recordPoints.get(i);
                    layoutParams.leftMargin = point.x;
                    layoutParams.topMargin = point.y;
                    p.setCurrentPoint(point);
                } else {
                    Point point = getRandomPoint();
                    layoutParams.leftMargin = point.x;
                    layoutParams.topMargin = point.y;
                    p.setCurrentPoint(point);
                }
                blockView.setLayoutParams(layoutParams);
                pbContainer.addView(blockView);
            }
        });
    }

    private void save() {
        List<PuzzleRecordItem> puzzleRecordItems = new ArrayList<>();
        for (PuzzlePiece puzzlePiece : pieceList) {
            Point currentPoint = puzzlePiece.getCurrentPoint();
            PuzzleRecordItem item = new PuzzleRecordItem(currentPoint.x, currentPoint.y);
            puzzleRecordItems.add(item);
        }
        String json = new Gson().toJson(puzzleRecordItems);
        PuzzleRecord puzzleRecordTo = new PuzzleRecord(puzzleItem.getId(), json);
        DaoSession daoSession = ((MyApplication) getApplication()).getDaoSession();
        PuzzleRecordDao puzzleRecordDao = daoSession.getPuzzleRecordDao();
        if (puzzleRecord != null) {
            puzzleRecordTo.setId(puzzleRecord.getId());
        }
        puzzleRecordDao.insertOrReplace(puzzleRecordTo);
    }

    private Point getRandomPoint() {
        int puzzleBoardLeft = vBoard.getLeft();
        int puzzleBoardTop = vBoard.getTop();
        int puzzleBoardRight = vBoard.getRight();
        int puzzleBoardBottom = vBoard.getBottom();
        int xArea = screenWidth / 2;
        Random random = new Random();
        //随机在拼图板两侧生成拼图块
        int x = random.nextBoolean() ? puzzleBoardLeft - random.nextInt(xArea) : puzzleBoardRight + random.nextInt(xArea);
        int y = puzzleBoardTop + random.nextInt(puzzleBoardBottom - puzzleBoardTop);
        return new Point(x, y);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        save();
    }
}
