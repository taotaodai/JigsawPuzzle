package com.example.jipu

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import java.util.*

/**
 * Created by wt on 2018/3/27.
 */
class JipuView : View {
    /**
     * 拼图列数
     */
    val COL: Int = 3
    /**
     * 拼图行数
     */
    val ROW: Int = 3
    /**
     * 用来进行拼图块位移，上下左右四个方向，移动一格
     */
    val DIR = arrayOf(arrayOf(0, 1), arrayOf(1, 0), arrayOf(0, -1), arrayOf(-1, 0))
    /**
     * 每块拼图宽度
     */
    var tileWidth: Int = 0
    /**
     * 每块拼图高度
     */
    var tileHeight: Int = 0
    /**
     * 用来记录拼图位置的二维数组
     */
    var tileDatas = Array(COL) { IntArray(ROW) }
    /**
     * 记录图片资源是否已经计算缩放比
     */
    var isScale: Boolean = false
    /**
     * 记录基础数据是否初始化
     */
    var isInited: Boolean = false
    /**
     * 拼图图片资源缩放比
     */
    var scale: Float = 1f
    /**
     * 拼图图片资源id
     */
    var imgRes: Int = 0
    /**
     * 用来存储每块拼图的位图数组
     */
    lateinit var pieces: Array<Bitmap?>

    constructor(context: Context) : super(context) {
        initBaseData()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initBaseData()
    }

    /**
     * 初始化基础数据
     */
    fun initBaseData() {
        imgRes = R.drawable.a_2
    }

    /**
     * 初始化拼图
     */
    fun initPuzzle() {
        /**
         * 为了适配屏幕，对图片各种尺寸的图片进行缩放
         */
        val matrix: Matrix = Matrix()
        matrix.postScale(scale, scale)
        val bm: Bitmap = (resources.getDrawable(imgRes) as? BitmapDrawable)!!.bitmap
        pieces = split(Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true))

        initTileData()
        createRandomBoard()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (!isInited) {
            initPuzzle()
            isInited = true
        }

        val isSuccess = isSuccess()
        for (i in 0..ROW - 1) {
            for (j in 0..COL - 1) {
                var index: Int = tileDatas[i][j]
                if (index == ROW * COL - 1 && !isSuccess) {
                    continue
                }
                canvas!!.drawBitmap(pieces[index++], j * tileWidth.toFloat(), i * tileHeight.toFloat(), Paint())
            }
        }
        if (isSuccess) {
            Toast.makeText(context, "ok的", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 把图片资源按行列分割成若干块
     */
    fun split(bitmap: Bitmap): Array<Bitmap?> {
        tileWidth = bitmap.width / COL
        tileHeight = bitmap.height / ROW
        val arr = arrayOfNulls<Bitmap>(COL * ROW)

        var index = 0
        for (i in 0..ROW - 1) {
            for (j in 0..COL - 1) {
                arr[index++] = Bitmap.createBitmap(bitmap, j * tileWidth, i * tileHeight, tileWidth, tileHeight)
            }
        }

        return arr
    }

    /**
     * 初始化顺序正常的位置数据
     */
    fun initTileData() {
        var index: Int = 0
        for (i in 0..ROW - 1) {
            for (j in 0..COL - 1) {
                tileDatas[i][j] = index++
            }
        }
    }

    /**
     * 打乱拼图位置数据。
     * 为了避免无解的情况出现，这里模拟拼图还原时的规则，
     * 对拼图位置数据打乱。
     */
    fun createRandomBoard() {
        /**
         * 拼图中的空白块，起始默认是最后一块
         */
        var count: Int = 0
        var tempPoint: Point? = Point(COL - 1, ROW - 1)
        val rd: Random = Random()
        /**
         * 打乱的次数
         */
        val num: Int = rd.nextInt(100) + 20
        while (count < num) {
            tempPoint = getNextPoint(tempPoint)
            count++
        }
    }

    fun getNextPoint(src: Point?): (Point) {
        val rd: Random = Random()
        val index: Int = rd.nextInt(4)
        val xOffset: Int = DIR[index][0]
        val yOffset: Int = DIR[index][1]

        val newPoint = move(src!!.x, src.y, xOffset, yOffset)

        if (newPoint.x != -1 && newPoint.y != -1) {
            return newPoint
        }
        return getNextPoint(src)
    }

    /**
     * 拼图按指定方向、位移移动。
     */
    fun move(srcX: Int, srcY: Int, xOffset: Int, yOffset: Int): (Point) {
        val x: Int = srcX + xOffset
        val y: Int = srcY + yOffset
        if (x < 0 || y < 0 || x >= COL || y >= ROW) {
            return Point(-1, -1)
        }
        val temp: Int = tileDatas[y][x]
        tileDatas[y][x] = tileDatas[srcY][srcX]
        tileDatas[srcY][srcX] = temp

        return Point(x, y)
    }

    fun xyToIndex(x: Int, y: Int): (Point) {
        val extraX: Int = if (x % tileWidth > 0) 1 else 0
        val extraY: Int = if (y % tileHeight > 0) 1 else 0

        val col = x / tileWidth + extraX
        val row = y / tileHeight + extraY

        return Point(col - 1, row - 1)
    }

    /**
     * 验证手指触摸的点是否超出拼图的有效区域
     */
    fun isBeyondTheBoundary(event: MotionEvent?): (Boolean) {
        if (event!!.x > COL * tileWidth) {
            return true
        }
        if (event!!.y > ROW * tileHeight) {
            return true
        }
        return false
    }

    /**
     * 验证拼图结果
     */
    fun isSuccess(): (Boolean) {
        var temp: Int = 0
        for (i: Int in 0..ROW - 1) {
            for (j: Int in 0..COL - 1) {
                if (temp > tileDatas[i][j]) {
                    return false
                }
                temp = tileDatas[i][j]
            }
        }
        return true
    }

    /**
     * 已选中拼图块
     */
    var sp: Point? = null
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isBeyondTheBoundary(event)) {
            return true
        }
        val point = xyToIndex(event!!.x.toInt(), event.y.toInt())
        if (event.action == MotionEvent.ACTION_MOVE) {
            /**
             * 触摸区域是空白拼图块时，移动已选中拼图
             */
            if (sp != null && (tileDatas[point.y][point.x] == COL * ROW - 1)) {
                val temp = tileDatas[point.y][point.x]
                tileDatas[point.y][point.x] = tileDatas[sp!!.y][sp!!.x]
                tileDatas[sp!!.y][sp!!.x] = temp
                invalidate()
            } else {
                /**
                 * 触摸区域是非空白块时记录当前拼图为选中拼图
                 */
                for (i: Int in 0..DIR.size - 1) {
                    val newX: Int = point.x + DIR[i][0]
                    val newY: Int = point.y + DIR[i][1]
                    if (newX >= 0 && newX < COL && newY >= 0 && newY < ROW) {
                        if (tileDatas[newY][newX] == COL * ROW - 1) {
                            sp = point
                        }
                    }
                }
            }

        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (!isScale) {
            /**
             * 以控件的宽度/图片宽度，计算图片缩放比
             */
            val scaleTemp = measuredWidth.toFloat() / (resources.getDrawable(imgRes) as? BitmapDrawable)!!.bitmap.width.toFloat()
//            if (scaleTemp > 1) {
                scale = scaleTemp
//            }
            isScale = true
        }
        super.onLayout(changed, left, top, right, bottom)
    }
}