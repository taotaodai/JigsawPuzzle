package com.ttd.jipu.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import com.ttd.jipu.R

/**
 * Created by wt on 2018/3/31.
 */
class LogoView : View {
    var tileWidth = 0
    var tileHeight = 0
    lateinit var bm: Bitmap

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        split()
    }

    override fun onDraw(canvas: Canvas?) {
        var index = 0
        for (i in 0..ROW - 1) {
            for (j in 0..COL - 1) {
                if (i == 0 && j == 0) {
                    canvas!!.drawBitmap(pieces[index++]!!, j * tileWidth.toFloat() + tileWidth * rate / FINISH_RATE, i * tileHeight.toFloat(), Paint())
                }
                if (i == 1 && j == 0) {
                    canvas!!.drawBitmap(pieces[index++]!!, j * tileWidth.toFloat(), i * tileHeight.toFloat() - tileHeight * rate / FINISH_RATE, Paint())
                }
                if (i == 0 && j == 1) {
                    canvas!!.drawBitmap(pieces[index++]!!, j * tileWidth.toFloat(), i * tileHeight.toFloat() + tileHeight * rate / FINISH_RATE, Paint())
                }
                if (i == 1 && j == 1) {
                    canvas!!.drawBitmap(pieces[index++]!!, j * tileWidth.toFloat() - tileWidth * rate / FINISH_RATE, i * tileHeight.toFloat(), Paint())
                }

            }
        }
    }

    val ROW: Int = 2
    val COL: Int = 2

    var pieces = arrayOfNulls<Bitmap>(COL * ROW)
    fun split() {
        val bitmap = (resources.getDrawable(R.mipmap.logo_jipu) as? BitmapDrawable)!!.bitmap
        val density = context.resources.displayMetrics.density
        bm = Bitmap.createScaledBitmap(bitmap,30*density.toInt(),30*density.toInt(),true)
        tileWidth = bm.width / COL
        tileHeight = bm.height / ROW
        val arr = arrayOfNulls<Bitmap>(COL * ROW)

        var index = 0
        for (i in 0..ROW - 1) {
            for (j in 0..COL - 1) {
                arr[index++] = Bitmap.createBitmap(bm, j * tileWidth, i * tileHeight, tileWidth, tileHeight)
            }
        }

        pieces = arr
    }

    var rate = 0
    var FINISH_RATE = 100
    fun startAnimation() {
        var anim: ValueAnimator = ValueAnimator.ofInt(0, FINISH_RATE).setDuration(3000)
        anim.addUpdateListener { listener: ValueAnimator ->
            rate = listener.animatedValue as Int
            invalidate()
        }
        anim.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(bm.width, bm.height)
    }

}