package com.zhs.communication.usbserial.example.canopen.sdo

import com.zhs.communication.utils.byte2unit
import com.zhs.communication.utils.int2byte

object SdoStructUtils {

    fun pack_into(
        request: ByteArray,
        startindex: Int = 0,
        command: Int,
        index: Int,
        subindex: Int
    ): ByteArray {

        request[startindex] = int2byte(command)
        request[startindex + 1] = int2byte(index and 0xff)
        request[startindex + 2] = int2byte((index and 0xff00) shr 8)
        request[startindex + 3] = int2byte(subindex)

        return request
    }

    fun unpack_from_cmd(response: ByteArray): Int {
        return byte2unit(response[0])//.toInt()
    }

    fun unpack_from_index(response: ByteArray): Int {
        return byte2unit(response[1]) or (byte2unit(response[2]) shl 8)
    }

    fun unpack_from_subindex(response: ByteArray): Int {
        return byte2unit(response[3])//.toInt()
    }



    fun pack(command: Int, index: Int, subindex: Int): ByteArray {
        val request = ByteArray(4)
        request[0] = int2byte(command)
        request[1] = int2byte(index and 0xff)
        request[2] = int2byte((index and 0xff00) shr 8)//右移 缩小
        request[3] = int2byte(subindex)

        return request
    }

    fun getExceptCode(data: ByteArray): Int {
        //shl 左移xx位 扩大
        return ((byte2unit(data[7]) shl 24) or
                ((byte2unit(data[6]) shl 16))
                or ((byte2unit(data[5]) shl 8)) or
                ((byte2unit(data[4]))))
    }
}

