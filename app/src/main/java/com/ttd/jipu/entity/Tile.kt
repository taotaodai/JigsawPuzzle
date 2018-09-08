package com.ttd.jipu.entity

import android.graphics.Point
import android.graphics.PointF

/**
 * Created by wt on 2018/3/29.
 * 拼图块对象。这里
 */
class Tile {
    var index: Int = 0
    var toMove: Boolean = false
    /**
     * 拼图移动前的起始位置
     */
    var sp: Point = Point()
    /**
     * 画布每重新绘制一次，拼图将要移动的到位置
     */
    var ep: Point = Point()
    /**
     * 将要移动的目标坐标
     */
    var epFinal: Point = Point()
    var isTouched: Boolean = false
    var hasShelter :Boolean = false

    constructor(index: Int) {
        this.index = index
    }

}