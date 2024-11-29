package com.ttd.jigsawpuzzlev1.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import com.ttd.jigsawpuzzlev1.data.Point;
import com.ttd.jigsawpuzzlev1.data.db.PuzzleItem;
import com.ttd.jigsawpuzzlev1.data.PuzzlePiece;
import com.ttd.jigsawpuzzlev1.utils.PuzzleImageHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 拼图碎片生成类
 * 一幅完整的拼图由14种碎片类型组成
 */
public class BlockCropper {
    private Bitmap originalBitmap;
    private final Context context;
    private final int slotRadius = 25;//插槽半径。
    private final int slotDiameter = slotRadius * 2;//圆弧直径
    private final int slotDepth = slotRadius * 2;//插槽和插头的深度和长度相同，均为半径的*2
    private final int blockMinSize = 128;//图块最小处尺寸，
    private final int dBorderToSlot = (blockMinSize - slotDepth) / 2;//图块边界到插槽的距离

    public BlockCropper(Context context, PuzzleItem puzzleItem) {
        this.context = context;
        PuzzleImageHelper puzzleImageHelper = new PuzzleImageHelper(context);
        Bitmap bitmap = puzzleImageHelper.getBitmap(puzzleItem);
        if (bitmap != null) {
            originalBitmap = bitmap;
        } else {
            ((Activity) context).finish();
        }
    }

    public Bitmap getOriginalBitmap() {
        return originalBitmap;
    }

    /**
     * @return 所有碎片
     */
    public List<PuzzlePiece> cropping() {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        //计算行数
        int rowCount = height / blockMinSize;
        if (rowCount % 2 != 0) {
            rowCount = rowCount - 1;
        }
        //计算列数(每行个数)
        int columnCount = width / blockMinSize;
        if (columnCount % 2 != 0) {
            columnCount = columnCount - 1;
        }

        List<PuzzlePiece> pieceList = new ArrayList<>(rowCount * columnCount);

        int y = 0;
        for (int i = 0; i < rowCount; i++) {
            int x = 0;
            for (int j = 0; j < columnCount; j++) {
                //第一行
                if (i == 0) {
                    if (j == 0) {
                        pieceList.add(createTopLeftPiece());
                        x += blockMinSize - slotDepth;
                    } else if (j == columnCount - 1) {
                        //行末尾
                        pieceList.add(createTopRightPiece(x, y));
                        y += blockMinSize;
                    } else {
                        //偶数块
                        if ((j - 1) % 2 == 0) {
                            pieceList.add(createTopMiddlePiece1(x, y));
                            x += slotDepth + blockMinSize;
                        } else {
                            pieceList.add(createTopMiddlePiece2(x, y));
                            x += blockMinSize - slotDepth;
                        }
                    }
                    //最后一行
                } else if (i == rowCount - 1) {
                    if (j == 0) {
                        pieceList.add(createLeftBottomPiece(x, y));
                        x += blockMinSize;
                    } else if (j == columnCount - 1) {
                        //行末尾
                        pieceList.add(createRightBottomPiece(x, y - slotDepth));
                    } else {
                        //偶数块
                        if ((j - 1) % 2 == 0) {
                            pieceList.add(createBottomMiddlePiece1(x, y - slotDepth));
                            x += blockMinSize - slotDepth;
                        } else {
                            pieceList.add(createBottomMiddlePiece2(x, y));
                            x += blockMinSize + slotDepth;
                        }
                    }
                } else {
                    //偶数行
                    if ((i - 1) % 2 == 0) {
                        if (j == 0) {
                            pieceList.add(createLeftMiddlePiece1(x, y));
                            x += blockMinSize;
                        } else if (j == columnCount - 1) {
                            pieceList.add(createRightMiddlePiece1(x, y - slotDepth));
                            y += blockMinSize;
                        } else {
                            //偶数块
                            if ((j - 1) % 2 == 0) {
                                pieceList.add(createMiddlePiece1(x, y - slotDepth));
                                x += blockMinSize - slotDepth;
                            } else {
                                pieceList.add(createMiddlePiece2(x, y));
                                x += blockMinSize + slotDepth;
                            }
                        }
                        //奇数行
                    } else {
                        if (j == 0) {
                            pieceList.add(createLeftMiddlePiece2(x, y - slotDepth));
                            x += blockMinSize - slotDepth;
                        } else if (j == columnCount - 1) {
                            pieceList.add(createRightMiddlePiece2(x, y));
                            y += blockMinSize;
                        } else {
                            //偶数块
                            if ((j - 1) % 2 == 0) {
                                pieceList.add(createMiddlePiece2(x, y));
                                x += blockMinSize + slotDepth;
                            } else {
                                pieceList.add(createMiddlePiece1(x, y - slotDepth));
                                x += blockMinSize - slotDepth;
                            }
                        }
                    }
                }
            }

            List<Point> autoPoints = new ArrayList<>();
            for (PuzzlePiece p : pieceList) {
                autoPoints.add(new Point(p.realPoint.x, p.realPoint.y));
            }
            for (PuzzlePiece p : pieceList) {
                p.setAutoPoints(autoPoints);
            }
        }

        return pieceList;
    }


    public PuzzlePiece createTopLeftPiece() {
        Bitmap clippedBitmap = Bitmap.createBitmap(blockMinSize, blockMinSize + slotDepth, Bitmap.Config.ARGB_8888);
        Bitmap srcBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, clippedBitmap.getWidth(), clippedBitmap.getHeight());

        Paint paint = getPaint();

        Canvas canvas = getCanvas(clippedBitmap);

        Path path1 = new Path();
        //往右移至最右侧
        path1.lineTo(blockMinSize, 0);
        //向下至插槽口
        path1.lineTo(blockMinSize, dBorderToSlot);
        // TODO 添加曲线.这里曲线的弧度不好掌握，暂时放一下
//        path1.quadTo(120,64,103,36);
        //向左至半圆定点处
        path1.lineTo(blockMinSize - slotDepth, dBorderToSlot);
        //逆时针添加圆弧
        path1.arcTo(new RectF(blockMinSize - slotDepth, dBorderToSlot, blockMinSize, blockMinSize - dBorderToSlot), -90, -180);
        //向右至插槽口
        path1.lineTo(blockMinSize, blockMinSize - dBorderToSlot);
        //向下至右下角
        path1.lineTo(blockMinSize, blockMinSize);
        //向左至插头底部
        path1.lineTo(blockMinSize - dBorderToSlot, blockMinSize);
        //向下至圆弧起始点
        path1.lineTo(blockMinSize - dBorderToSlot, blockMinSize + slotRadius);
        //顺时针添加圆弧
        path1.arcTo(new RectF(dBorderToSlot, blockMinSize, blockMinSize - dBorderToSlot, blockMinSize + slotDepth), 0, 180);
        //向上至插头底部
        path1.lineTo(dBorderToSlot, blockMinSize);
        //向左至左下角
        path1.lineTo(0, blockMinSize);
        //回到原点
        path1.lineTo(0, 0);

        canvas.drawPath(path1, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        return new PuzzlePiece(new Point(0, 0), clippedBitmap);
    }

    public PuzzlePiece createTopMiddlePiece1(int x, int y) {
        Bitmap clippedBitmap = Bitmap.createBitmap(blockMinSize + (slotDepth * 2), blockMinSize, Bitmap.Config.ARGB_8888);
        Bitmap srcBitmap = Bitmap.createBitmap(originalBitmap, x, y, clippedBitmap.getWidth(), clippedBitmap.getHeight());

        Paint paint = getPaint();

        Canvas canvas = getCanvas(clippedBitmap);

        Path path1 = new Path();
        //移至左上角
        path1.moveTo(slotDepth, 0);
        //向右至右上角
        path1.lineTo(slotDepth + blockMinSize, 0);
        //向下至插头底部
        path1.lineTo(slotDepth + blockMinSize, dBorderToSlot);
        //向右至圆弧起始点
        path1.lineTo(slotDepth + blockMinSize + slotRadius, dBorderToSlot);
        //逆时针添加圆弧
        path1.arcTo(new RectF(slotDepth + blockMinSize, dBorderToSlot, blockMinSize + (slotDepth * 2), blockMinSize - dBorderToSlot), 270, 180);
        //向左至插头底部
        path1.lineTo(blockMinSize + slotDepth, blockMinSize - dBorderToSlot);
        //向下至右下角
        path1.lineTo(blockMinSize + slotDepth, blockMinSize);
        //向左至插槽口
        path1.lineTo(blockMinSize + slotDepth - dBorderToSlot, blockMinSize);
        //逆时针添加圆弧
        path1.arcTo(new RectF(slotDepth + dBorderToSlot, blockMinSize - slotDepth, slotDepth + blockMinSize - dBorderToSlot, blockMinSize), 0, -180);
        //向下至插槽口
        path1.lineTo(slotDepth + dBorderToSlot, blockMinSize);
        //向左至左下角
        path1.lineTo(slotDepth, blockMinSize);
        //向上至插头底部
        path1.lineTo(slotDepth, blockMinSize - dBorderToSlot);
        //向左至圆弧起始点
        path1.lineTo(slotRadius, blockMinSize - dBorderToSlot);
        //顺时针添加圆弧
        path1.arcTo(new RectF(0, dBorderToSlot, slotDepth, dBorderToSlot + slotDepth), 90, 180);
        //向右至插头底部
        path1.lineTo(slotDepth, dBorderToSlot);
        //回到原点
        path1.lineTo(slotDepth, 0);

        canvas.drawPath(path1, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        return new PuzzlePiece(new Point(x, y), clippedBitmap);
    }

    public PuzzlePiece createTopMiddlePiece2(int x, int y) {
        Bitmap clippedBitmap = Bitmap.createBitmap(blockMinSize, blockMinSize + slotDepth, Bitmap.Config.ARGB_8888);
        Bitmap srcBitmap = Bitmap.createBitmap(originalBitmap, x, y, clippedBitmap.getWidth(), clippedBitmap.getHeight());

        Paint paint = getPaint();

        Canvas canvas = getCanvas(clippedBitmap);

        Path path1 = new Path();
        //向右至右上角
        path1.lineTo(blockMinSize, 0);
        //向下至插槽口
        path1.lineTo(blockMinSize, dBorderToSlot);
        //向左至圆弧起始点
        path1.lineTo(blockMinSize - slotRadius, dBorderToSlot);
        //逆时针添加圆弧
        path1.arcTo(new RectF(blockMinSize - slotDepth, dBorderToSlot, blockMinSize, blockMinSize - dBorderToSlot), 270, -180);
        //向右至插槽底部
        path1.lineTo(blockMinSize, blockMinSize - dBorderToSlot);
        //向下至右下角
        path1.lineTo(blockMinSize, blockMinSize);
        //向左至插头底部
        path1.lineTo(blockMinSize - dBorderToSlot, blockMinSize);
        //向下至圆弧起始点
        path1.lineTo(blockMinSize - dBorderToSlot, blockMinSize + slotRadius);
        //顺时针添加圆弧
        path1.arcTo(new RectF(dBorderToSlot, blockMinSize, dBorderToSlot + slotDepth, blockMinSize + slotDepth), 0, 180);
        //向上至插头底部
        path1.lineTo(dBorderToSlot, blockMinSize);
        //向左至左下角
        path1.lineTo(0, blockMinSize);
        //向上至插槽口
        path1.lineTo(0, blockMinSize - dBorderToSlot);
        //向右至圆弧起始点
        path1.lineTo(slotRadius, blockMinSize - dBorderToSlot);
        //逆时针添加圆弧
        path1.arcTo(new RectF(0, dBorderToSlot, slotDepth, dBorderToSlot + slotDepth), 90, -180);
        //向左至插槽底部
        path1.lineTo(0, dBorderToSlot);
        //回到原点
        path1.lineTo(0, 0);

        canvas.drawPath(path1, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        return new PuzzlePiece(new Point(x, y), clippedBitmap);
    }

    public PuzzlePiece createTopRightPiece(int x, int y) {
        Bitmap clippedBitmap = Bitmap.createBitmap(blockMinSize + slotDepth, blockMinSize, Bitmap.Config.ARGB_8888);
        Bitmap srcBitmap = Bitmap.createBitmap(originalBitmap, x, y, clippedBitmap.getWidth(), clippedBitmap.getHeight());

        Paint paint = getPaint();

        Canvas canvas = getCanvas(clippedBitmap);

        Path path1 = new Path();
        //定位至左上角
        path1.moveTo(slotDepth, 0);
        //往右至最右侧
        path1.lineTo(blockMinSize + slotDepth, 0);
        //向下至右下角
        path1.lineTo(blockMinSize + slotDepth, blockMinSize);
        //向左至插槽底部
        path1.lineTo(blockMinSize + slotDepth - dBorderToSlot, blockMinSize);
        //向上至圆弧起始点
        path1.lineTo(blockMinSize + slotDepth - dBorderToSlot, blockMinSize - slotRadius);
        //逆时针添加圆弧
        path1.arcTo(new RectF(slotDepth + dBorderToSlot, blockMinSize - slotDepth, blockMinSize + slotDepth - dBorderToSlot, blockMinSize), 0, -180);
        //向下至插槽底部
        path1.lineTo(slotDepth + dBorderToSlot, blockMinSize);
        //向左至左下角
        path1.lineTo(slotDepth, blockMinSize);
        //向上至插头底部
        path1.lineTo(slotDepth, blockMinSize - dBorderToSlot);
        //左至圆弧起始点
        path1.lineTo(slotRadius, blockMinSize - dBorderToSlot);
        //顺时针添加圆弧
        path1.arcTo(new RectF(0, dBorderToSlot, slotDepth, dBorderToSlot + slotDepth), 90, 180);
        //向右至插头底部
        path1.lineTo(slotDepth, dBorderToSlot);
        //回到初始点
        path1.lineTo(slotDepth, 0);

        canvas.drawPath(path1, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        return new PuzzlePiece(new Point(x, y), clippedBitmap);
    }

    public PuzzlePiece createLeftMiddlePiece1(int x, int y) {
        Bitmap clippedBitmap = Bitmap.createBitmap(blockMinSize + slotDepth, blockMinSize, Bitmap.Config.ARGB_8888);
        Bitmap srcBitmap = Bitmap.createBitmap(originalBitmap, x, y, clippedBitmap.getWidth(), clippedBitmap.getHeight());

        Paint paint = getPaint();

        Canvas canvas = getCanvas(clippedBitmap);

        Path path1 = new Path();
        //向右至插槽口
        path1.lineTo(dBorderToSlot, 0);
        //向下至圆弧起始点
        path1.lineTo(dBorderToSlot, slotRadius);
        //逆时针添加圆弧
        path1.arcTo(new RectF(dBorderToSlot, 0, dBorderToSlot + slotDiameter, slotDiameter), 180, -180);
        //向上至插槽底部
        path1.lineTo(blockMinSize - dBorderToSlot, 0);
        //向右至右上角
        path1.lineTo(blockMinSize, 0);
        //向下至插头底部
        path1.lineTo(blockMinSize, dBorderToSlot);
        //向右至圆弧起始点
        path1.lineTo(blockMinSize + slotRadius, dBorderToSlot);
        //顺时针添加圆弧
        path1.arcTo(new RectF(blockMinSize, dBorderToSlot, blockMinSize + slotDiameter, dBorderToSlot + slotDiameter), 270, 180);
        //向左至插头底部
        path1.lineTo(blockMinSize, blockMinSize - dBorderToSlot);
        //向下至右下角
        path1.lineTo(blockMinSize, blockMinSize);
        //向左至插槽底部
        path1.lineTo(blockMinSize - dBorderToSlot, blockMinSize);
        //向上至圆弧起始点
        path1.lineTo(blockMinSize - dBorderToSlot, blockMinSize - slotRadius);
        //逆时针添加圆弧
        path1.arcTo(new RectF(dBorderToSlot, blockMinSize - slotDiameter, dBorderToSlot + slotDiameter, blockMinSize), 0, -180);
        //向下至插槽底部
        path1.lineTo(dBorderToSlot, blockMinSize);
        //向左至左下角
        path1.lineTo(0, blockMinSize);

        //回到初始点
        path1.lineTo(0, 0);

        canvas.drawPath(path1, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        return new PuzzlePiece(new Point(x, y), clippedBitmap);
    }

    public PuzzlePiece createLeftMiddlePiece2(int x, int y) {
        Bitmap clippedBitmap = Bitmap.createBitmap(blockMinSize, blockMinSize + (slotDepth * 2), Bitmap.Config.ARGB_8888);
        Bitmap srcBitmap = Bitmap.createBitmap(originalBitmap, x, y, clippedBitmap.getWidth(), clippedBitmap.getHeight());

        Paint paint = getPaint();

        Canvas canvas = getCanvas(clippedBitmap);

        Path path1 = new Path();
        //定初始位置
        path1.moveTo(0, slotDepth);
        //向右至插头底部
        path1.lineTo(dBorderToSlot, slotDepth);
        //向上至圆弧起始点
        path1.lineTo(dBorderToSlot, slotDepth - slotRadius);
        //顺时针添加圆弧
        path1.arcTo(new RectF(dBorderToSlot, 0, dBorderToSlot + slotDiameter, slotDiameter), 180, 180);
        //向下至插头底部
        path1.lineTo(blockMinSize - dBorderToSlot, slotDepth);
        //向右至右上角
        path1.lineTo(blockMinSize, slotDepth);
        //向下至插槽口
        path1.lineTo(blockMinSize, slotDepth + dBorderToSlot);
        //向左至圆弧起始点
        path1.lineTo(blockMinSize - slotRadius, slotDepth + dBorderToSlot);
        //逆时针添加圆弧
        path1.arcTo(new RectF(blockMinSize - slotDepth, slotDepth + dBorderToSlot, blockMinSize - slotDepth + slotDiameter, slotDepth + dBorderToSlot + slotDiameter), 270, -180);
        //向右至插槽口
        path1.lineTo(blockMinSize, slotDepth + blockMinSize - dBorderToSlot);
        //向下至右下角
        path1.lineTo(blockMinSize, slotDepth + blockMinSize);
        //向左至插头底部
        path1.lineTo(blockMinSize - dBorderToSlot, slotDepth + blockMinSize);
        //向下至圆弧起始点
        path1.lineTo(blockMinSize - dBorderToSlot, slotDepth + blockMinSize + slotRadius);
        //顺时针添加圆弧
        path1.arcTo(new RectF(dBorderToSlot, blockMinSize + (slotDepth * 2) - slotDiameter, dBorderToSlot + slotDiameter, blockMinSize + (slotDepth * 2)), 0, 180);
        //向上至插头底部
        path1.lineTo(dBorderToSlot, slotDepth + blockMinSize);
        //向左至左下角
        path1.lineTo(0, slotDepth + blockMinSize);

        //回到初始点
        path1.lineTo(0, slotDepth);

        canvas.drawPath(path1, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        return new PuzzlePiece(new Point(x, y), clippedBitmap);
    }

    public PuzzlePiece createLeftBottomPiece(int x, int y) {
        Bitmap clippedBitmap = Bitmap.createBitmap(blockMinSize + slotDepth, blockMinSize, Bitmap.Config.ARGB_8888);
        Bitmap srcBitmap = Bitmap.createBitmap(originalBitmap, x, y, clippedBitmap.getWidth(), clippedBitmap.getHeight());

        Paint paint = getPaint();

        Canvas canvas = getCanvas(clippedBitmap);

        Path path1 = new Path();
        //向右至插槽口
        path1.lineTo(dBorderToSlot, 0);
        //向下至圆弧起始点
        path1.lineTo(dBorderToSlot, slotRadius);
        //逆时针添加圆弧
        path1.arcTo(new RectF(dBorderToSlot, 0, dBorderToSlot + slotDiameter, slotDiameter), 180, -180);
        //向上至插槽底部
        path1.lineTo(blockMinSize - dBorderToSlot, 0);
        //向右至右上角
        path1.lineTo(blockMinSize, 0);
        //向下至插头底部
        path1.lineTo(blockMinSize, dBorderToSlot);
        //向右至圆弧起始点
        path1.lineTo(blockMinSize + slotRadius, dBorderToSlot);
        //顺时针添加圆弧
        path1.arcTo(new RectF(blockMinSize, dBorderToSlot, blockMinSize + slotDiameter, dBorderToSlot + slotDiameter), 270, 180);
        //向左至插头底部
        path1.lineTo(blockMinSize, blockMinSize - dBorderToSlot);
        //向下至右下角
        path1.lineTo(blockMinSize, blockMinSize);
        //向左至左下角
        path1.lineTo(0, blockMinSize);

        //回到初始点
        path1.lineTo(0, 0);

        canvas.drawPath(path1, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        return new PuzzlePiece(new Point(x, y), clippedBitmap);
    }

    private Paint getPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
//        paint.setShadowLayer(2,2,2, Color.BLACK);
        return paint;
    }

    private Canvas getCanvas(Bitmap clippedBitmap) {
        Canvas canvas = new Canvas(clippedBitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        return canvas;
    }

    public PuzzlePiece createBottomMiddlePiece1(int x, int y) {
        Bitmap clippedBitmap = Bitmap.createBitmap(blockMinSize, blockMinSize + slotDepth, Bitmap.Config.ARGB_8888);
        Bitmap srcBitmap = Bitmap.createBitmap(originalBitmap, x, y, clippedBitmap.getWidth(), clippedBitmap.getHeight());

        Paint paint = getPaint();
        Canvas canvas = getCanvas(clippedBitmap);

        Path path1 = new Path();
        //定初始位置
        path1.moveTo(0, slotDepth);
        //向右至插头底部
        path1.lineTo(dBorderToSlot, slotDepth);
        //向上至圆弧起始点
        path1.lineTo(dBorderToSlot, slotDepth - slotRadius);
        //顺时针添加圆弧
        path1.arcTo(new RectF(dBorderToSlot, 0, dBorderToSlot + slotDiameter, slotDiameter), 180, 180);
        //向下至插头底部
        path1.lineTo(blockMinSize - dBorderToSlot, slotDepth);
        //向右至右上角
        path1.lineTo(blockMinSize, slotDepth);
        //向下至插槽口
        path1.lineTo(blockMinSize, slotDepth + dBorderToSlot);
        //向左至圆弧起始点
        path1.lineTo(blockMinSize - slotRadius, slotDepth + dBorderToSlot);
        //逆时针添加圆弧
        path1.arcTo(new RectF(blockMinSize - slotDepth, slotDepth + dBorderToSlot, blockMinSize - slotDepth + slotDiameter, slotDepth + dBorderToSlot + slotDiameter), 270, -180);
        //向右至插槽口
        path1.lineTo(blockMinSize, slotDepth + blockMinSize - dBorderToSlot);
        //向下至右下角
        path1.lineTo(blockMinSize, slotDepth + blockMinSize);
        //向左至左下角
        path1.lineTo(0, slotDepth + blockMinSize);
        //向上至插槽底部
        path1.lineTo(0, slotDepth + blockMinSize - dBorderToSlot);
        //向右至圆弧起始点
        path1.lineTo(slotRadius, slotDepth + blockMinSize - dBorderToSlot);
        //逆时针添加圆弧
        path1.arcTo(new RectF(0, slotDepth + dBorderToSlot, slotDiameter, slotDepth + dBorderToSlot + slotDiameter), 90, -180);
        //向左至插槽底部
        path1.lineTo(0, slotDepth + dBorderToSlot);

        //回到初始点
        path1.lineTo(0, slotDepth);

        canvas.drawPath(path1, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        return new PuzzlePiece(new Point(x, y), clippedBitmap);
    }

    public PuzzlePiece createBottomMiddlePiece2(int x, int y) {
        Bitmap clippedBitmap = Bitmap.createBitmap(blockMinSize + (slotDepth * 2), blockMinSize, Bitmap.Config.ARGB_8888);
        Bitmap srcBitmap = Bitmap.createBitmap(originalBitmap, x, y, clippedBitmap.getWidth(), clippedBitmap.getHeight());

        Paint paint = getPaint();
        Canvas canvas = getCanvas(clippedBitmap);

        Path path1 = new Path();
        //移至左上角
        path1.moveTo(slotDepth, 0);
        //向右至插槽口
        path1.lineTo(slotDepth + dBorderToSlot, 0);
        //向下至圆弧起始点
        path1.lineTo(slotDepth + dBorderToSlot, slotRadius);
        //逆时针添加圆弧
        path1.arcTo(new RectF(slotDepth + dBorderToSlot, 0, slotDepth + dBorderToSlot + slotDiameter, slotDiameter), 180, -180);
        //向上至插槽口
        path1.lineTo(slotDepth + blockMinSize - dBorderToSlot, 0);
        //向右至右上角
        path1.lineTo(slotDepth + blockMinSize, 0);
        //向下至插头底部
        path1.lineTo(slotDepth + blockMinSize, dBorderToSlot);
        //向右至圆弧起始点
        path1.lineTo(slotDepth + blockMinSize + slotRadius, dBorderToSlot);
        //逆时针添加圆弧
        path1.arcTo(new RectF(slotDepth + blockMinSize, dBorderToSlot, blockMinSize + (slotDepth * 2), blockMinSize - dBorderToSlot), 270, 180);
        //向左至插头底部
        path1.lineTo(blockMinSize + slotDepth, blockMinSize - dBorderToSlot);
        //向下至右下角
        path1.lineTo(blockMinSize + slotDepth, blockMinSize);
        //向左至左下角
        path1.lineTo(slotDepth, blockMinSize);
        //向上至插头底部
        path1.lineTo(slotDepth, blockMinSize - dBorderToSlot);
        //向左至圆弧起始点
        path1.lineTo(slotRadius, blockMinSize - dBorderToSlot);
        //顺时针添加圆弧
        path1.arcTo(new RectF(0, dBorderToSlot, slotDepth, dBorderToSlot + slotDepth), 90, 180);
        //向右至插头底部
        path1.lineTo(slotDepth, dBorderToSlot);
        //回到原点
        path1.lineTo(slotDepth, 0);

        canvas.drawPath(path1, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        return new PuzzlePiece(new Point(x, y), clippedBitmap);
    }

    public PuzzlePiece createMiddlePiece1(int x, int y) {
        Bitmap clippedBitmap = Bitmap.createBitmap(blockMinSize, blockMinSize + (slotDepth * 2), Bitmap.Config.ARGB_8888);
        Bitmap srcBitmap = Bitmap.createBitmap(originalBitmap, x, y, clippedBitmap.getWidth(), clippedBitmap.getHeight());

        Paint paint = getPaint();
        Canvas canvas = getCanvas(clippedBitmap);

        Path path1 = new Path();
        //定初始位置
        path1.moveTo(0, slotDepth);
        //向右至插头底部
        path1.lineTo(dBorderToSlot, slotDepth);
        //向上至圆弧起始点
        path1.lineTo(dBorderToSlot, slotDepth - slotRadius);
        //顺时针添加圆弧
        path1.arcTo(new RectF(dBorderToSlot, 0, dBorderToSlot + slotDiameter, slotDiameter), 180, 180);
        //向下至插头底部
        path1.lineTo(blockMinSize - dBorderToSlot, slotDepth);
        //向右至右上角
        path1.lineTo(blockMinSize, slotDepth);
        //向下至插槽口
        path1.lineTo(blockMinSize, slotDepth + dBorderToSlot);
        //向左至圆弧起始点
        path1.lineTo(blockMinSize - slotRadius, slotDepth + dBorderToSlot);
        //逆时针添加圆弧
        path1.arcTo(new RectF(blockMinSize - slotDepth, slotDepth + dBorderToSlot, blockMinSize - slotDepth + slotDiameter, slotDepth + dBorderToSlot + slotDiameter), 270, -180);
        //向右至插槽口
        path1.lineTo(blockMinSize, slotDepth + blockMinSize - dBorderToSlot);
        //向下至右下角
        path1.lineTo(blockMinSize, slotDepth + blockMinSize);
        //向左至插头底部
        path1.lineTo(blockMinSize - dBorderToSlot, slotDepth + blockMinSize);
        //向下至圆弧起始点
        path1.lineTo(blockMinSize - dBorderToSlot, slotDepth + blockMinSize + slotRadius);
        //顺时针添加圆弧
        path1.arcTo(new RectF(dBorderToSlot, blockMinSize + (slotDepth * 2) - slotDiameter, dBorderToSlot + slotDiameter, blockMinSize + (slotDepth * 2)), 0, 180);
        //向上至插头底部
        path1.lineTo(dBorderToSlot, slotDepth + blockMinSize);
        //向左至左下角
        path1.lineTo(0, slotDepth + blockMinSize);
        //向上至插槽口
        path1.lineTo(0, slotDepth + blockMinSize - dBorderToSlot);
        //向右至圆弧起始点
        path1.lineTo(slotRadius, slotDepth + blockMinSize - dBorderToSlot);
        //逆时针添加圆弧
        path1.arcTo(new RectF(0, slotDepth + dBorderToSlot, slotDiameter, slotDepth + dBorderToSlot + slotDiameter), 90, -180);
        //向左至插槽口
        path1.lineTo(0, slotDepth + dBorderToSlot);

        //回到初始点
        path1.lineTo(0, slotDepth);

        canvas.drawPath(path1, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        return new PuzzlePiece(new Point(x, y), clippedBitmap);
    }

    public PuzzlePiece createMiddlePiece2(int x, int y) {
        Bitmap clippedBitmap = Bitmap.createBitmap(blockMinSize + (slotDepth * 2), blockMinSize, Bitmap.Config.ARGB_8888);
        Bitmap srcBitmap = Bitmap.createBitmap(originalBitmap, x, y, clippedBitmap.getWidth(), clippedBitmap.getHeight());

        Paint paint = getPaint();

        Canvas canvas = getCanvas(clippedBitmap);

        Path path1 = new Path();
        //移至左上角
        path1.moveTo(slotDepth, 0);
        //向右至插槽口
        path1.lineTo(slotDepth + dBorderToSlot, 0);
        //向下至圆弧起始点
        path1.lineTo(slotDepth + dBorderToSlot, slotRadius);
        //逆时针添加圆弧
        path1.arcTo(new RectF(slotDepth + dBorderToSlot, 0, slotDepth + dBorderToSlot + slotDiameter, slotDiameter), 180, -180);
        //向上至插槽口
        path1.lineTo(slotDepth + blockMinSize - dBorderToSlot, 0);
        //向右至右上角
        path1.lineTo(slotDepth + blockMinSize, 0);
        //向下至插头底部
        path1.lineTo(slotDepth + blockMinSize, dBorderToSlot);
        //向右至圆弧起始点
        path1.lineTo(slotDepth + blockMinSize + slotRadius, dBorderToSlot);
        //逆时针添加圆弧
        path1.arcTo(new RectF(slotDepth + blockMinSize, dBorderToSlot, blockMinSize + (slotDepth * 2), blockMinSize - dBorderToSlot), 270, 180);
        //向左至插头底部
        path1.lineTo(blockMinSize + slotDepth, blockMinSize - dBorderToSlot);
        //向下至右下角
        path1.lineTo(blockMinSize + slotDepth, blockMinSize);
        //向左至插槽口
        path1.lineTo(blockMinSize + slotDepth - dBorderToSlot, blockMinSize);
        //逆时针添加圆弧
        path1.arcTo(new RectF(slotDepth + dBorderToSlot, blockMinSize - slotDepth, slotDepth + blockMinSize - dBorderToSlot, blockMinSize), 0, -180);
        //向下至插槽口
        path1.lineTo(slotDepth + dBorderToSlot, blockMinSize);
        //向左至左下角
        path1.lineTo(slotDepth, blockMinSize);
        //向上至插头底部
        path1.lineTo(slotDepth, blockMinSize - dBorderToSlot);
        //向左至圆弧起始点
        path1.lineTo(slotRadius, blockMinSize - dBorderToSlot);
        //顺时针添加圆弧
        path1.arcTo(new RectF(0, dBorderToSlot, slotDepth, dBorderToSlot + slotDepth), 90, 180);
        //向右至插头底部
        path1.lineTo(slotDepth, dBorderToSlot);
        //回到原点
        path1.lineTo(slotDepth, 0);

        canvas.drawPath(path1, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        return new PuzzlePiece(new Point(x, y), clippedBitmap);
    }

    public PuzzlePiece createRightBottomPiece(int x, int y) {
        Bitmap clippedBitmap = Bitmap.createBitmap(blockMinSize, blockMinSize + slotDepth, Bitmap.Config.ARGB_8888);
        Bitmap srcBitmap = Bitmap.createBitmap(originalBitmap, x, y, clippedBitmap.getWidth(), clippedBitmap.getHeight());

        Paint paint = getPaint();

        Canvas canvas = getCanvas(clippedBitmap);

        Path path1 = new Path();
        //定初始位置
        path1.moveTo(0, slotDepth);
        //向右至插头底部
        path1.lineTo(dBorderToSlot, slotDepth);
        //向上至圆弧起始点
        path1.lineTo(dBorderToSlot, slotDepth - slotRadius);
        //顺时针添加圆弧
        path1.arcTo(new RectF(dBorderToSlot, 0, dBorderToSlot + slotDiameter, slotDiameter), 180, 180);
        //向下至插头底部
        path1.lineTo(blockMinSize - dBorderToSlot, slotDepth);
        //向右至右上角
        path1.lineTo(blockMinSize, slotDepth);
        //向下至右下角
        path1.lineTo(blockMinSize, slotDepth + blockMinSize);
        //向左至左下角
        path1.lineTo(0, slotDepth + blockMinSize);
        //向上至插槽底部
        path1.lineTo(0, slotDepth + blockMinSize - dBorderToSlot);
        //向右至圆弧起始点
        path1.lineTo(slotRadius, slotDepth + blockMinSize - dBorderToSlot);
        //逆时针添加圆弧
        path1.arcTo(new RectF(0, slotDepth + dBorderToSlot, slotDiameter, slotDepth + dBorderToSlot + slotDiameter), 90, -180);
        //向左至插槽底部
        path1.lineTo(0, slotDepth + dBorderToSlot);

        //回到初始点
        path1.lineTo(0, slotDepth);

        canvas.drawPath(path1, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        return new PuzzlePiece(new Point(x, y), clippedBitmap);
    }

    public PuzzlePiece createRightMiddlePiece1(int x, int y) {
        Bitmap clippedBitmap = Bitmap.createBitmap(blockMinSize, blockMinSize + (slotDepth * 2), Bitmap.Config.ARGB_8888);
        Bitmap srcBitmap = Bitmap.createBitmap(originalBitmap, x, y, clippedBitmap.getWidth(), clippedBitmap.getHeight());

        Paint paint = getPaint();

        Canvas canvas = getCanvas(clippedBitmap);

        Path path1 = new Path();
        //定初始位置
        path1.moveTo(0, slotDepth);
        //向右至插头底部
        path1.lineTo(dBorderToSlot, slotDepth);
        //向上至圆弧起始点
        path1.lineTo(dBorderToSlot, slotDepth - slotRadius);
        //顺时针添加圆弧
        path1.arcTo(new RectF(dBorderToSlot, 0, dBorderToSlot + slotDiameter, slotDiameter), 180, 180);
        //向下至插头底部
        path1.lineTo(blockMinSize - dBorderToSlot, slotDepth);
        //向右至右上角
        path1.lineTo(blockMinSize, slotDepth);
        //向下至右下角
        path1.lineTo(blockMinSize, slotDepth + blockMinSize);
        //向左至插头底部
        path1.lineTo(blockMinSize - dBorderToSlot, slotDepth + blockMinSize);
        //向下至圆弧起始点
        path1.lineTo(blockMinSize - dBorderToSlot, slotDepth + blockMinSize + slotRadius);
        //顺时针添加圆弧
        path1.arcTo(new RectF(dBorderToSlot, blockMinSize + (slotDepth * 2) - slotDiameter, dBorderToSlot + slotDiameter, blockMinSize + (slotDepth * 2)), 0, 180);
        //向上至插头底部
        path1.lineTo(dBorderToSlot, slotDepth + blockMinSize);
        //向左至左下角
        path1.lineTo(0, slotDepth + blockMinSize);
        //向上至插槽口
        path1.lineTo(0, slotDepth + blockMinSize - dBorderToSlot);
        //向右至圆弧起始点
        path1.lineTo(slotRadius, slotDepth + blockMinSize - dBorderToSlot);
        //逆时针添加圆弧
        path1.arcTo(new RectF(0, slotDepth + dBorderToSlot, slotDiameter, slotDepth + dBorderToSlot + slotDiameter), 90, -180);
        //向左至插槽口
        path1.lineTo(0, slotDepth + dBorderToSlot);

        //回到初始点
        path1.lineTo(0, slotDepth);

        canvas.drawPath(path1, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        return new PuzzlePiece(new Point(x, y), clippedBitmap);
    }

    public PuzzlePiece createRightMiddlePiece2(int x, int y) {
        Bitmap clippedBitmap = Bitmap.createBitmap(blockMinSize + slotDepth, blockMinSize, Bitmap.Config.ARGB_8888);
        Bitmap srcBitmap = Bitmap.createBitmap(originalBitmap, x, y, clippedBitmap.getWidth(), clippedBitmap.getHeight());

        Paint paint = getPaint();

        Canvas canvas = getCanvas(clippedBitmap);

        Path path1 = new Path();
        //移至左上角
        path1.moveTo(slotDepth, 0);
        //向右至插槽口
        path1.lineTo(slotDepth + dBorderToSlot, 0);
        //向下至圆弧起始点
        path1.lineTo(slotDepth + dBorderToSlot, slotRadius);
        //逆时针添加圆弧
        path1.arcTo(new RectF(slotDepth + dBorderToSlot, 0, slotDepth + dBorderToSlot + slotDiameter, slotDiameter), 180, -180);
        //向上至插槽口
        path1.lineTo(slotDepth + blockMinSize - dBorderToSlot, 0);
        //向右至右上角
        path1.lineTo(slotDepth + blockMinSize, 0);
        //向下至右下角
        path1.lineTo(blockMinSize + slotDepth, blockMinSize);
        //向左至插槽口
        path1.lineTo(blockMinSize + slotDepth - dBorderToSlot, blockMinSize);
        //逆时针添加圆弧
        path1.arcTo(new RectF(slotDepth + dBorderToSlot, blockMinSize - slotDepth, slotDepth + blockMinSize - dBorderToSlot, blockMinSize), 0, -180);
        //向下至插槽口
        path1.lineTo(slotDepth + dBorderToSlot, blockMinSize);
        //向左至左下角
        path1.lineTo(slotDepth, blockMinSize);
        //向上至插头底部
        path1.lineTo(slotDepth, blockMinSize - dBorderToSlot);
        //向左至圆弧起始点
        path1.lineTo(slotRadius, blockMinSize - dBorderToSlot);
        //顺时针添加圆弧
        path1.arcTo(new RectF(0, dBorderToSlot, slotDepth, dBorderToSlot + slotDepth), 90, 180);
        //向右至插头底部
        path1.lineTo(slotDepth, dBorderToSlot);
        //回到原点
        path1.lineTo(slotDepth, 0);

        canvas.drawPath(path1, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        return new PuzzlePiece(new Point(x, y), clippedBitmap);
    }

}
