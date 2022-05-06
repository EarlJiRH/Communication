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

/**获取当前日期格式*/
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

/**Byte转Int数据
 *@param byte Byte数值
 */
fun byte2unit(byte: Byte): Int {
    var a: Int = byte.toInt()
    if (a < 0) {
        a = 256 - abs(a)
    }
    return a
}

/**[0,256]的Int转换为Byte数据
 * @param num 0-256的整数值
 * */
fun int2byte(num: Int): Byte {
    if (num > 256) {
        throw  InvalidParameterException("Invalid int2byte int '$num'")
    }
    //0-127
    return if (num in 0..127) {
        num.toByte()
    } else {
        //128-256 翻转
        (num - 256).toByte()
    }
}

/**Byte数组打印成16进制数值的字符串格式
 * @param tag 打印的tag标签
 * @param data 需要打印的Byte数组
 */
fun printByteArray(tag: String?, data: ByteArray) {
    print("$tag [")
    data.forEach {
        print("${it.toString(16)} ")
    }
    println("]")
}

/**创建一维数组*/
fun zeros(arraySize: Int, dataType: String = "int16") = IntArray(arraySize)

/**创建二位数组*/
fun zeros(arraySize0: Int, arraySize1: Int, dataType: String = "int16"): Array<IntArray> {
    return Array(arraySize0) { IntArray(arraySize1) }

}

/**创建三维数组*/
fun zeros(
    arraySize0: Int,
    arraySize1: Int,
    arraySize2: Int,
    dataType: String = "int16"
): Array<Array<IntArray>> {
    return Array(arraySize0) { Array(arraySize1) { IntArray(arraySize2) } }
}


/**获取当前系统时间 毫秒数*/
fun getCurrentMillis() = System.currentTimeMillis()

/**使线程沉睡一定的毫秒数*/
fun threadSleep(sleepMillis: Long) = Thread.sleep(sleepMillis)
