package com.ttd.jipu.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.ttd.jipu.R

/**
 * Created by wt on 2018/3/31.
 */
class LogoView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var tileWidth = 0
    private var tileHeight = 0
    private lateinit var bm: Bitmap
    private var paint: Paint

    override fun onDraw(canvas: Canvas?) {
        var index = 0
        for (i in 0 until ROW) {
            for (j in 0 until COL) {
                if (i == 0 && j == 0) {
                    canvas!!.drawBitmap(pieces[index++]!!, j * tileWidth.toFloat() + tileWidth * rate / FINISH_RATE, i * tileHeight.toFloat(), paint)
                }
                if (i == 1 && j == 0) {
                    canvas!!.drawBitmap(pieces[index++]!!, j * tileWidth.toFloat(), i * tileHeight.toFloat() - tileHeight * rate / FINISH_RATE, paint)
                }
                if (i == 0 && j == 1) {
                    canvas!!.drawBitmap(pieces[index++]!!, j * tileWidth.toFloat(), i * tileHeight.toFloat() + tileHeight * rate / FINISH_RATE, paint)
                }
                if (i == 1 && j == 1) {
                    canvas!!.drawBitmap(pieces[index++]!!, j * tileWidth.toFloat() - tileWidth * rate / FINISH_RATE, i * tileHeight.toFloat(), paint)
                }

            }
        }
    }

    private var pieces = arrayOfNulls<Bitmap>(COL * ROW)

    private fun split() {
        val bitmap = (ResourcesCompat.getDrawable(resources, R.mipmap.logo_jipu, null) as? BitmapDrawable)!!.bitmap
        val density = context.resources.displayMetrics.density
        bm = Bitmap.createScaledBitmap(bitmap, 30 * density.toInt(), 30 * density.toInt(), true)
        tileWidth = bm.width / COL
        tileHeight = bm.height / ROW
        val arr = arrayOfNulls<Bitmap>(COL * ROW)

        var index = 0
        for (i in 0 until ROW) {
            for (j in 0 until COL) {
                arr[index++] = Bitmap.createBitmap(bm, j * tileWidth, i * tileHeight, tileWidth, tileHeight)
            }
        }

        pieces = arr
    }

    private var rate = 0
    fun startAnimation() {
        val anim: ValueAnimator = ValueAnimator.ofInt(0, FINISH_RATE).setDuration(2000)
        anim.addUpdateListener { listener: ValueAnimator ->
            rate = listener.animatedValue as Int
            invalidate()
        }
        anim.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(bm.width, bm.height)
    }

    init {
        split()
        paint = Paint()
    }

    companion object {
        private const val ROW: Int = 2
        private const val COL: Int = 2
        private const val FINISH_RATE = 100
    }

}