package com.ttd.jipu.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import com.ttd.jipu.entity.Piece
import com.ttd.jipu.entity.Tile
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * Created by wt on 2018/3/27.
 */
class JipuView : View {
    /**
     * 拼图列数
     */
    var col: Int = 0
    /**
     * 拼图行数
     */
    var row: Int = 0
    /**
     * 用来进行拼图块位移，上下左右四个方向，移动一格
     */
    private val DIR = arrayOf(arrayOf(0, 1), arrayOf(1, 0), arrayOf(0, -1), arrayOf(-1, 0))
    /**
     * 每块拼图宽度
     */
    private var tileWidth: Int = 0
    /**
     * 每块拼图高度
     */
    private var tileHeight: Int = 0
    /**
     * 用来记录拼图位置的二维数组
     */
    lateinit var tileDatas: Array<Array<Tile>>
    /**
     * 记录图片资源是否已经计算缩放比
     */
    var isScale: Boolean = false
    /**
     * 记录基础数据是否初始化
     */
    var isInited: Boolean = false

    var isFirstLoad: Boolean = true

//    var misionComplete: Boolean = false
    /**
     * 拼图图片资源缩放比
     */
    var scale: Float = 1f
    /**
     * 拼图图片资源id
     */
    var imgRes: Int = 0
    /**
     * 拼图状态-预览
     */
    private val STATE_PREVIEW: Int = 0
    /**
     * 拼图状态-生成中
     */
    private val STATE_GENERATING: Int = 1
    /**
     * 拼图状态-拼图中
     */
    val STATE_PLAYING: Int = 2
    /**
     * 拼图状态
     */
    var state: Int = STATE_PREVIEW
    /**
     * 遮挡物数量
     */
    var shelterCount: Int = 0
    /**
     * 遮挡物图片资源
     */
    var shelterRes: Int = -1

    /**
     * 用来存储每块拼图的位图数组
     */
    lateinit var pieces: Array<Bitmap?>
    lateinit var shelterPieces: Array<Piece?>

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    /**
     * 初始化基础数据
     */
    private fun initBaseData() {
        isFirstLoad = true
        /**
         * 设置
         */
        val bm: Bitmap = getInitialImage()
        pieces = split(bm)
        shelterPieces = arrayOfNulls(col * row)
        state = STATE_PREVIEW

        /**
         * 初始化特效
         */
        openSaturability = false
        openShelter = false
    }

    /**
     * 初始化拼图
     */
    fun initPuzzle() {
        initBaseData()
        initTileData()
        initAnimationData()
    }

    /**
     * 获取最初始的未经过处理的图片素材（为了适配屏幕，这里只设置图片的缩放比例）
     *
     */
    private fun getInitialImage(): Bitmap {
        val matrix = Matrix()
//        matrix.postScale(scale, scale)
        matrix.setScale(scale, scale)
        val bm: Bitmap = (resources.getDrawable(imgRes) as? BitmapDrawable)!!.bitmap
        bm.height
        return Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
    }

    private fun getShelterImage(): Bitmap {
        val bm: Bitmap = (resources.getDrawable(shelterRes) as? BitmapDrawable)!!.bitmap
        val matrix = Matrix()
        matrix.postScale(tileWidth.toFloat() / bm.width, tileWidth.toFloat() / bm.width)

        return Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
    }

    /**
     * 默认饱和度
     */
    private var DEFAULT_SATURABILITY: Float = 1f
    /**
     * 默认透明度
     */
    var DEFAULT_ALPHA: Int = 255
    var alpha: Int = DEFAULT_ALPHA
    /**
     * 饱和度
     */
    var saturability: Float = DEFAULT_SATURABILITY
    /**
     * 是否开启饱和度
     */
    var openSaturability: Boolean = false
    /**
     * 是否开启遮挡物
     */
    var openShelter: Boolean = false

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (state == STATE_PREVIEW) {
            val bm: Bitmap = getInitialImage()
            canvas!!.drawBitmap(bm, 0f, 0f, Paint())
            return
        }
        if (rateTranslation == RATE_FINISH) {
            val emptyTile = getEmptyTile()
            val tile = getTouchedTile()

            if (tile != null) {
                /**
                 * 动画结束时，初始化两块拼图所在坐标的数据，并交换下标
                 */
                tile.ep.x = tile.sp.x
                tile.ep.y = tile.sp.y
                tile.epFinal.x = tile.epFinal.x
                tile.epFinal.y = tile.epFinal.y

                emptyTile!!.ep.x = emptyTile.sp.x
                emptyTile.ep.y = emptyTile.sp.y
                emptyTile.epFinal.x = emptyTile.epFinal.x
                emptyTile.epFinal.y = emptyTile.epFinal.y

                tile.isTouched = false

                val tempIndex = emptyTile.index
                emptyTile.index = tile.index
                tile.index = tempIndex

                isTransAniming = false
                rateTranslation = RATE_START
            }
        }

        val isSuccess = isSuccess()

        val paint = Paint()
        /**
         * 添加饱和度
         */
        val saturationmatrix = ColorMatrix()

        val tempSaturability = (saturability + (DEFAULT_SATURABILITY - saturability) * ((RATE_FINISH - rateSaturability).toFloat() / RATE_FINISH))
        if (tempSaturability == saturability) {
            isSatubiAniming = false
        }

        saturationmatrix.setSaturation(if (openSaturability) tempSaturability else DEFAULT_SATURABILITY)
        paint.colorFilter = ColorMatrixColorFilter(saturationmatrix)
        /**
         * 遍历绘制每块拼图
         */
        for (i in 0 until row) {
            for (j in 0 until col) {
                val tile = tileDatas[i][j]
                val index: Int = tile.index
                val x = j * tileWidth
                val y = i * tileHeight
                tile.sp.x = x
                tile.sp.y = y
                if (isFirstLoad) {
                    tile.ep.x = x
                    tile.ep.y = y
                }

                if (index == row * col - 1 && !isSuccess) {
                    continue
                }

                canvas?.drawBitmap(pieces[index]!!, tile.ep.x.toFloat(), tile.ep.y.toFloat(), paint)
                /**
                 * 添加遮挡物
                 */
                if (openShelter) {
                    val piece = shelterPieces[index]
                    if (piece?.bitmap != null) {
                        val paintA = Paint()
                        if (!piece.isShelterAnimed) {
                            paintA.alpha = DEFAULT_ALPHA - DEFAULT_ALPHA * (RATE_FINISH - rateShelter) / RATE_FINISH
                        }
//                        Log.i("aaaa", paintA.alpha.toString())
                        canvas?.drawBitmap(piece.bitmap!!, tile.ep.x.toFloat(), tile.ep.y.toFloat(), paintA)
                        if (rateShelter == RATE_FINISH) {
                            isShelterAniming = false
                            piece.isShelterAnimed = true
                        }
                    }
                }
//                Log.i("aaaaa", "第" + times + "次绘图(" + tile.index + "):" + tile.ep.x.toString() + "," + tile.ep.y)
            }
        }

        isFirstLoad = false

        if (isSuccess && state == STATE_PLAYING) {
            /**
             * 拼图完成，提示用户
             * TODO
             */
            Toast.makeText(context, "ok的", Toast.LENGTH_SHORT).show()
            /**
             * 还原拼图为预览状态
             */
            initBaseData()
            invalidate()
        }
    }

    /**
     * 记录一次动画需要执行的次数，主要用来调试
     */
    var times: Int = 0
    /**
     * 动画是否正在进行
     */

    private var isTransAniming: Boolean = false
    private var isSatubiAniming: Boolean = false
    private var isShelterAniming: Boolean = false
    /**
     * 拼块移动动画完成率
     */
    private var rateTranslation: Int = 0
    /**
     * 饱和度动画完成率
     */
    private var rateSaturability: Int = 0
    /**
     * 遮挡物显示动画完成率
     */
    private var rateShelter: Int = 0
    val ANIM_TRANSLATION: Int = 0
    val ANIM_SATURABILITY: Int = 1
    val ANIM_SHELTER: Int = 2

    /**
     * 动画起始完成率
     */
    val RATE_START: Int = 0
    /**
     * 动画最大完成率
     */
    val RATE_FINISH: Int = 100
    /**
     * 用户移动拼块时动画间隔
     */
    val ANIM_DURATION_PLAY: Long = 200

    /**
     * 打乱时拼块时动画间隔
     */
    val ANIM_DURATION_AUTO: Long = 10

    private fun initAnimationData() {
        isTransAniming = false
        isSatubiAniming = false
        isShelterAniming = false
        rateTranslation = RATE_START
        rateSaturability = RATE_START
        rateShelter = RATE_START

        times = 0
    }

    /**
     * 开启拼块移动动画
     */
    private fun startTileAnim(tile: Tile) {
        isTransAniming = true
        times = 0
        val sp = tile.sp
        val ep = tile.ep
        val epFinal = tile.epFinal
        val anim: ValueAnimator = ValueAnimator.ofInt(0, RATE_FINISH).setDuration(if (state == STATE_GENERATING) ANIM_DURATION_AUTO else ANIM_DURATION_PLAY)
        anim.addUpdateListener { listener: ValueAnimator ->
            val p = listener.getAnimatedValue() as Int
            rateTranslation = p
            if (p > 0) {
                ep.x = epFinal.x - ((epFinal.x - sp.x) * (RATE_FINISH - p) / RATE_FINISH)
                ep.y = epFinal.y - ((epFinal.y - sp.y) * (RATE_FINISH - p) / RATE_FINISH)

                invalidate()

//                Log.i("aaaa", p.toString())
//                Log.i("aaaa", "第" + ++times + "次(" + tile.index + "):" + ep.x.toString() + "," + ep.y)
            }
        }
        anim.addPauseListener(object : Animator.AnimatorPauseListener {
            override fun onAnimationPause(animation: Animator?) {
            }

            override fun onAnimationResume(animation: Animator?) {
            }
        })
        anim.interpolator = LinearInterpolator()
        anim.start()
    }

    /**
     * 开启动画
     */
    private fun startAnim(duration: Long, animType: Int) {
        val anim: ValueAnimator = ValueAnimator.ofInt(RATE_START, RATE_FINISH).setDuration(duration)
        anim.addUpdateListener { listener: ValueAnimator ->
            val p = listener.animatedValue as Int
            when (animType) {
                ANIM_TRANSLATION -> {
                    rateTranslation = p
                }
                ANIM_SATURABILITY -> {
                    rateSaturability = p
                }
                ANIM_SHELTER -> {
                    Log.i("aaa", p.toString())
                    rateShelter = p
                }
            }

            invalidate()
        }
        anim.interpolator = LinearInterpolator()
        anim.start()

        when (animType) {
            ANIM_TRANSLATION -> {
                isTransAniming = true
            }
            ANIM_SATURABILITY -> {
                isSatubiAniming = true
            }
            ANIM_SHELTER -> {
                isShelterAniming = true
            }
        }
    }

    /**
     * 把图片资源按行列分割成若干块
     */
    private fun split(bitmap: Bitmap): Array<Bitmap?> {
        tileWidth = bitmap.width / col
        tileHeight = bitmap.height / row
        val arr = arrayOfNulls<Bitmap>(col * row)

        var index = 0
        for (i in 0 until row) {
            for (j in 0 until col) {
                arr[index++] = Bitmap.createBitmap(bitmap, j * tileWidth, i * tileHeight, tileWidth, tileHeight)
            }
        }
        return arr
    }

    private fun addShelter() {
        val bitmap = getShelterImage()
//        val arr = arrayOfNulls<Bitmap>(col * row)

        val rd = Random()
        val index: Int = rd.nextInt(shelterPieces.size)
        if (shelterPieces[index] == null) {
            val piece = Piece(bitmap)
            shelterPieces[index] = piece
        } else {
            addShelter()
        }

//        var index = 0
//        for (i in 0..row - 1) {
//            for (j in 0..col - 1) {
//                arr[index++] = Bitmap.createBitmap(bitmap, j * tileWidth, i * tileHeight, tileWidth, tileHeight)
//            }
//        }

    }


    /**
     * 初始化顺序正常的位置数据
     */
    private fun initTileData() {
        tileDatas = Array(row) { Array(col) { Tile(0) } }
        var index = 0
        for (i in 0 until row) {
            for (j in 0 until col) {
                tileDatas[i][j].index = index++
            }
        }
    }

    /**
     * 打乱拼图位置数据。
     * 为了避免无解的情况出现，这里模拟拼图还原时的规则，
     * 对拼图位置数据打乱。
     */
    val RANDOM_TIMES: Int = 50

    private fun createRandomBoard(times: Int, blankPoint: Point?) {
        /**
         * ①打乱拼图
         */
        if (times < RANDOM_TIMES) {
//            var p = getNextPoint(blankPoint)
//            createRandomBoard(times + 1, p)

            Observable.create(Observable.OnSubscribe<Point> { t ->
                val p = getNextPoint(blankPoint)
                tileDatas[p.y][p.x].isTouched = true
                t?.onNext(p)

            }).observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { t ->
                        convertToNext(t!!)
                    }
                    .observeOn(Schedulers.io())
                    .doOnNext {
                        var flag = true
                        /**
                         * 等待动画结束
                         */
                        while (flag) {
                            if (!isTransAniming) {
                                flag = false
                            }
                        }
                    }.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<Point>() {
                        override fun onNext(t: Point?) {
                            /**
                             * 继续下一块拼块的移动
                             */
                            createRandomBoard(times + 1, t)
                        }

                        override fun onCompleted() {
                        }

                        override fun onError(e: Throwable?) {
                        }
                    })
        } else {
            /**
             * 这里需要判断一下是否已经被打乱
             */
            if (isSuccess()) {
                createRandomBoard()
            }
            /**
             * ②开启饱和度
             */
            if (saturability < DEFAULT_SATURABILITY) {
                openSaturability = true
                startAnim(1000, ANIM_SATURABILITY)
            }

            /**
             * ③开启遮挡物
             */
            Observable.create(Observable.OnSubscribe<String> {
                it.onNext("")
            })
                    .observeOn(Schedulers.io())
                    .doOnNext {
                        var flag = true
                        /**
                         * 等待动画结束
                         */
                        while (flag) {
                            if (!isShelterAniming) {
                                flag = false
                            }
                        }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        openShelter()
                    }
            state = STATE_PLAYING
        }
    }

    /**
     * 开启遮挡物
     */
    private fun openShelter(count: Int) {
        Observable.create(Observable.OnSubscribe<String> {
            it.onNext("")
        })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    if (shelterCount > 0) {
                        addShelter()
                        Log.i("aaa", "第$count$isShelterAniming")
                        startAnim(2000, ANIM_SHELTER)
                    }
                }
                .observeOn(Schedulers.io())
                .doOnNext {
                    var flag = true
                    /**
                     * 等待动画结束
                     */
                    while (flag) {
                        if (!isShelterAniming) {
                            flag = false
                        }
                    }
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (count < shelterCount) {
                        openShelter(count + 1)
                    }
                }
    }

    private fun openShelter() {
        openShelter = true
        openShelter(1)
    }


    /**
     * 打乱拼图
     */
    fun createRandomBoard() {
        state = STATE_GENERATING
        /**
         * 拼图中的空白块，起始默认是最后一块
         */
        val blankPoint: Point? = Point(col - 1, row - 1)
//        tileDatas[col - 1][row - 1].isTouched = true
        createRandomBoard(0, blankPoint!!)
    }

    /**
     * 拼图随机向一个方向移动一格
     */
    private fun getNextPoint(src: Point?): (Point) {
        val rd = Random()
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
    private fun move(srcX: Int, srcY: Int, xOffset: Int, yOffset: Int): (Point) {
        val x: Int = srcX + xOffset
        val y: Int = srcY + yOffset
        if (x < 0 || y < 0 || x >= col || y >= row) {
            return Point(-1, -1)
        }
        val temp: Int = tileDatas[y][x].index
        tileDatas[y][x].index = tileDatas[srcY][srcX].index
        tileDatas[srcY][srcX].index = temp

        return Point(x, y)
    }

    /**
     * 把触摸点转换为拼块的原点
     */
    private fun xyToIndex(x: Int, y: Int): (Point) {
        val extraX: Int = if (x % tileWidth > 0) 1 else 0
        val extraY: Int = if (y % tileHeight > 0) 1 else 0

        val col = x / tileWidth + extraX
        val row = y / tileHeight + extraY

        return Point(col - 1, row - 1)
    }

    /**
     * 验证手指触摸的点是否超出拼图的有效区域
     */
    private fun isBeyondTheBoundary(event: MotionEvent?): (Boolean) {
        if (event!!.x > col * tileWidth) {
            return true
        }
        if (event.y > row * tileHeight) {
            return true
        }
        return false
    }

    /**
     * 验证拼图结果
     */
    private fun isSuccess(): (Boolean) {
        var temp = 0
        for (i: Int in 0 until row) {
            for (j: Int in 0 until col) {
                if (temp > tileDatas[i][j].index) {
                    return false
                }
                temp = tileDatas[i][j].index
            }
        }
        return true
    }

    /**
     * 获取空白拼图块
     */
    private fun getEmptyTile(): (Tile?) {
        for (i: Int in 0 until row) {
            for (j: Int in 0 until col) {
                val tile = tileDatas[i][j]
                if (tile.index == row * col - 1) {
                    return tile
                }
            }
        }
        return null
    }

    /**
     * 获取当前选中的拼图块
     */
    private fun getTouchedTile(): (Tile?) {
        for (i: Int in 0 until row) {
            for (j: Int in 0 until col) {
                val tile = tileDatas[i][j]
                if (tile.isTouched) {
                    return tile
                }
            }
        }
        return null
    }

    private fun convertToNext(point: Point) {
        if (point.x < 0 || point.y < 0) {
            return
        }
        /**
         * 当前触摸的拼图块
         */
        val tileTouch = tileDatas[point.y][point.x]
        /**
         * 触摸区域是空白拼图块时，移动已选中拼图
         */
        val tileTo = getTouchedTile()
        if (tileTo != null && (tileTouch.index == col * row - 1)) {
            /**
             * 选中的拼图
             */

            val tempSp = Point(tileTouch.sp.x, tileTouch.sp.y)
            val tempEp = Point(tileTouch.ep.x, tileTouch.ep.y)
            /**
             * 交换空白块和选中拼图的目标坐标
             */
            tileTouch.epFinal.x = tileTo.sp.x
            tileTouch.epFinal.y = tileTo.sp.y
            /**
             * 因为空白块不需要给它动画，这里直接设置它的结束位置
             */
            tileTouch.ep.x = tileTo.sp.x
            tileTouch.ep.y = tileTo.sp.y
            tileTo.epFinal = tempSp

            startTileAnim(tileTo)
        } else {
            /**
             * 检查触摸拼块的四个相邻拼块是否是空白拼块；
             * 因为只有当相邻拼块中有空白拼块时才能移动。
             */
            var isValid = false
            for (i: Int in 0 until DIR.size) {
                val newX: Int = point.x + DIR[i][0]
                val newY: Int = point.y + DIR[i][1]
                if (newX in 0..(col - 1) && newY >= 0 && newY < row) {
                    if (tileDatas[newY][newX].index == col * row - 1) {
                        /**
                         * 当检查到有相邻有空白拼块时，设置其为被触摸状态
                         */
                        tileTouch.isTouched = true
                        isValid = true
                    }
                }
            }
            if (isValid) {
                /**
                 * 当有有效拼块被触摸时，取消其他拼块触摸状态
                 */
                for (i in tileDatas.indices) {
                    for (j in tileDatas[i].indices) {
                        val temp = tileDatas[i][j]
                        if (tileTouch.index != temp.index) {
                            temp.isTouched = false
                        }
                    }
                }
            }
        }
    }

    private fun isAniming(): Boolean {
        return isTransAniming && isSatubiAniming && isShelterAniming
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isBeyondTheBoundary(event) || isAniming() || (rateTranslation in 1..(RATE_FINISH - 1))) {
            return true
        }
        val point = xyToIndex(event!!.x.toInt(), event.y.toInt())
        if (event.action == MotionEvent.ACTION_MOVE) {
            convertToNext(point)
        }
        return true
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