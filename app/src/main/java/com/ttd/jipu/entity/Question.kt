package com.ttd.jipu.entity

import java.io.Serializable

/**
 * Created by wt on 2018/4/23.
 */
class Question : Serializable {
    /**
     * 拼图资源id
     */
    var imgRes: Int = 0
    /**
     * 饱和度
     */
    var saturability: Float = 1f
    /**
     * 宫格行数，这里默认列数和行数一致
     */
//    var gridNum:Int = 3

    var row: Int = 3

    var col: Int = 3
    /**
     * 遮挡物数量
     */
    var shelterCount: Int = 0

    constructor(imgRes: Int, saturability: Float) {
        this.imgRes = imgRes
        this.saturability = saturability
    }

    constructor(imgRes: Int, gridNum: Int) {
        this.imgRes = imgRes
        row = gridNum
        col = gridNum
    }

    constructor(imgRes: Int, row: Int, col: Int) {
        this.imgRes = imgRes
        this.row = row
        this.col = col
    }


}