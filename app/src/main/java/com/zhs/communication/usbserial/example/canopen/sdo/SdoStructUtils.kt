package com.zhs.communication.usbserial.example.canopen.sdo

import com.zhs.communication.utils.byte2unit
import com.zhs.communication.utils.int2byte

object SdoStructUtils {

    fun pack(
        request: ByteArray = ByteArray(4),
        startIndex: Int = 0,
        command: Int,
        index: Int,
        subIndex: Int
    ): ByteArray {
        request[startIndex] = int2byte(command)
        request[startIndex + 1] = int2byte(index and 0xff)
        request[startIndex + 2] = int2byte((index and 0xff00) shr 8)//右移8位 缩小
        request[startIndex + 3] = int2byte(subIndex)
        return request
    }

    fun unpackFromCommand(response: ByteArray): Int {
        return byte2unit(response[0])//.toInt()
    }

    fun unpackFromIndex(response: ByteArray): Int {
        return byte2unit(response[1]) or (byte2unit(response[2]) shl 8)
    }

    fun unpackFromSuIndex(response: ByteArray): Int {
        return byte2unit(response[3])//.toInt()
    }

    fun getExceptCode(data: ByteArray): Int {
        //shl 左移xx位 扩大
        return ((byte2unit(data[7]) shl 24) or
                ((byte2unit(data[6]) shl 16))
                or ((byte2unit(data[5]) shl 8)) or
                ((byte2unit(data[4]))))
    }
}

