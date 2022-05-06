package com.zhs.communication.usbserial.example.canopen.sdo.server

import com.blankj.utilcode.util.LogUtils
import com.zhs.communication.usbserial.example.Network
import com.zhs.communication.usbserial.example.canopen.sdo.*
import com.zhs.communication.utils.byte2unit
import com.zhs.communication.utils.int2byte
import com.zhs.communication.utils.toHexStr

/**
 * @param receive 接收的参数(rx)
 * @param respond 响应的参数(tx)
 * */
class SdoServer(
    var respond: Int,
    var receive: Int,
    var network: Network? = null
) {

    companion object {
        private const val TAG = "sdoServer"
    }


//    var rx_cobidmy: Int = 0
//    var tx_cobidmy: Int = 0

//    var networkmy: Network? = null

    var _index = 0
    var _subindex = 0

//    init {
//        rx_cobidmy = receive
//        tx_cobidmy = respond
//    }

    var localData: MutableList<DataDot> = mutableListOf()

    fun onRequest(canId: Int, data: ByteArray, timestamp: Float = 0.0F) {

//        System.out.println("rx server on requrest ")
        try {
            var command = byte2unit(data[0])
            var ccs = command and 0xE0
//            System.out.println("rx server on requrest ccs=${ccs.toString(16)} ")
            when (ccs) {
                REQUEST_UPLOAD -> {
                    init_upload(data)
                }

                REQUEST_DOWNLOAD -> {
                    init_download(data)
                }

//                REQUEST_ABORTED->{
//                    request_aborted(data)
//                }


                else -> {
                    abort(0x05040001)
                }
            }

        } catch (e: Exception) {

            abort()

        }

    }

    private fun init_download(data: ByteArray) {
//        # TODO: Check if writable (now would fail on end of segmented downloads)

        var command: Int = SdoStructUtils.unpackFromCommand(data)
        var res_index = SdoStructUtils.unpackFromIndex(data)
        var res_subindex = SdoStructUtils.unpackFromSuIndex(data)


        _index = res_index
        _subindex = res_subindex


        var res_command = RESPONSE_DOWNLOAD
        var response = ByteArray(8)

        if ((command and EXPEDITED) > 0) {
//            logger.info("Expedited download for 0x%X:%d", index, subIndex)
            var size = 0
            if ((command and SIZE_SPECIFIED) > 0) {
                size = 4 - ((command shr 2) and 0x3)
            } else {
                size = 4
            }

            var fok: Boolean = setData(res_index, res_subindex, data.copyOfRange(4, 4 + size))
            if (!fok) {
                abort(0x08000024)
                return
            }
            SdoStructUtils.pack(response, 0, res_command, res_index, res_subindex)
            sendResponse(response)
            return

        }

        abort(0x08000024)
        return
    }


    private fun init_upload(data: ByteArray) {

//        var res_command = SdoStructUtils.unpack_from_cmd(data)
        val res_index = SdoStructUtils.unpackFromIndex(data)
        val res_subindex = SdoStructUtils.unpackFromSuIndex(data)


        _index = res_index
        _subindex = res_subindex
        var res_command = RESPONSE_UPLOAD or SIZE_SPECIFIED

        val response = ByteArray(8)

        val data_t = getData(res_index, res_subindex)
        if (data_t != null) {
            val size = data_t.dataSize
            if (size <= 4) {
//                logger.info("Expedited upload for 0x%X:%d", index, subIndex)
                res_command = res_command or EXPEDITED
                res_command = res_command or ((4 - size) shl 2)
//                response[4:4 + size] = data

                when (data_t.dataSize) {
                    1 -> {
                        response[4] = int2byte(data_t.data)
                    }
                    2 -> {
                        response[4] = int2byte(data_t.data and 0xff)
                        response[5] = int2byte((data_t.data and 0xff00) shr 8)
                    }
                    4 -> {
                        response[4] = int2byte(data_t.data and 0xff)
                        response[5] = int2byte((data_t.data and 0xff00) shr 8)
                        response[6] = int2byte((data_t.data and 0xff0000) shr 16)
                        response[7] =
                            int2byte((data_t.data and 0xff000000.toInt()) shr 24)
                    }
                    else -> {
                        abort(0x08000024)
                        return
                    }


                }


            }
            SdoStructUtils.pack(response, 0, res_command, res_index, res_subindex)
            sendResponse(response)
            return
        }

        abort(0x08000024)

    }

    private fun getData(resIndex: Int, resSubindex: Int): DataDot? {
        for (ii in localData) {
            if ((ii.index == resIndex) and (ii.subIndex == resSubindex)) {
                return ii
            }
        }
        return null
    }

    private fun setData(resIndex: Int, resSubindex: Int, bytes: ByteArray): Boolean {
        for (ii in localData) {
            if ((ii.index == resIndex) and (ii.subIndex == resSubindex)) {
                if (ii.dataSize == bytes.size) {
                    when (ii.dataSize) {
                        1 -> {
                            ii.data = byte2unit(bytes[0])
                        }
                        2 -> {
                            ii.data = byte2unit(bytes[0]) or (byte2unit(bytes[1]) shl 8)

                        }
                        4 -> {
                            ii.data = byte2unit(bytes[0]) or
                                    (byte2unit(bytes[1]) shl 8) or
                                    (byte2unit(bytes[2]) shl 16) or
                                    (byte2unit(bytes[3]) shl 24)

                        }
                        else -> {
                            return false
                        }


                    }

                    if (ii.callback != null) {
                        ii.callback!!.setData(ii)
                    }

                    return true
                }

            }
        }
        return false
    }


    fun abort(abort_code: Int = 0x08000000) {
//        """Abort current transfer."""
        val data = ByteArray(8)
//        _index, self._subindex, abort_code)

        data[7] = int2byte(((abort_code and 0xff000000.toInt()) shr 24))
        data[6] = int2byte(((abort_code and 0xff0000.toInt()) shr 16))
        data[5] = int2byte(((abort_code and 0xff00.toInt()) shr 8))
        data[4] = int2byte(((abort_code and 0xff.toInt())))

        data[3] = int2byte(_subindex)
        data[2] = int2byte(((_index and 0xff00.toInt()) shr 8))
        data[1] = int2byte(((_index and 0xff.toInt())))
        data[0] = int2byte(0x80)


        sendResponse(data)
    }

    private fun sendResponse(data: ByteArray) {
        LogUtils.e(TAG, "sendMessage 发送数据 serial ${toHexStr(data)}")
        //这里会报错
        network?.sendMessage(receive, data, userMainThread = true)
    }

}
