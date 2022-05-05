package com.zhs.communication.utils

import android.annotation.SuppressLint
import java.security.InvalidParameterException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * ================================================
 * 类名：com.zhs.communication.utils
 * 时间：2022/4/28 14:47
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 * ================================================
 * @author Admin
 */


fun getRandomNumber(): Int {
    return Random().nextInt()
}


@SuppressLint("SimpleDateFormat")
fun getCurrentDate(): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())


/**将Byte数组内的数据转换为十六进制的数据返回 展示*/
fun toHexStr(byteArray: ByteArray) =
    with(StringBuilder()) {
        byteArray.forEach {
            val hex = it.toInt() and (0xFF)
            val hexStr = Integer.toHexString(hex)
            if (hexStr.length == 1) append("0").append(hexStr)
            else append(hexStr)
        }
        toString()
    }


fun byte2unit(byte: Byte): Int {

    var a: Int = byte.toInt()
    if (a < 0) {
        a = 256 - abs(a)
    }
    return a
}

fun int2byte(num: Int): Byte {
    if (num > 256) {
        throw  InvalidParameterException("Invalid int2byte int '$num'")
    }

    return if (num in 0..128) {
        num.toByte()
    } else {
        (num - 256).toByte()
    }
}


fun zeros(shapesize: Int, dtype: String = "int16"): IntArray {
    return IntArray(shapesize)
}

fun zeros(
    shapesize0: Int,
    shapesize1: Int,
    shapesize2: Int,
    dtype: String = "int16"
): Array<Array<IntArray>> {
    return Array(shapesize0) { Array(shapesize1) { IntArray(shapesize2) } }
}

fun zeros(shapesize0: Int, shapesize1: Int, dtype: String = "int16"): Array<IntArray> {
    return Array(shapesize0) { IntArray(shapesize1) }
}


fun time(): Long {
    //返回当前毫秒
    return System.currentTimeMillis()
}

fun sleep(sleepms:Long){
    Thread.sleep(sleepms)
}