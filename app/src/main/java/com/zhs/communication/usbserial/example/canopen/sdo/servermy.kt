package com.zhs.communication.usbserial.example.canopen.sdo

import com.blankj.utilcode.util.LogUtils
import com.zhs.communication.usbserial.example.Network
import com.zhs.communication.utils.byte2unit
import com.zhs.communication.utils.int2byte
import com.zhs.communication.utils.toHexStr


open class callback_sdoserver {
    open fun run_setdata(datasdotd: servermy.datasdot) {
        println(
            "set data index=${datasdotd.index}" +
                    " subindex=${datasdotd.subindex}" +
                    "datasize=${datasdotd.datasize}" +
                    "data_int=${datasdotd.data_int}"
        )
    }
}

class servermy(tx_cobid: Int, rx_cobid: Int) {

    companion object{
        private const val TAG = "sdoServer"
    }


    var rx_cobidmy: Int = 0
    var tx_cobidmy: Int = 0
    var networkmy: Network? = null

    var _index = 0
    var _subindex = 0

    init {
        rx_cobidmy = rx_cobid
        tx_cobidmy = tx_cobid
    }

    var local_data: MutableList<datasdot> = mutableListOf()

    class datasdot(indexx: Int, subindexx: Int, datasizex: Int, datadata_int: Int) {
        var index: Int = 0
        var subindex: Int = 0

        var datasize: Int = 1 //1 byte 2 2byte  4 4byte 8 8byte
        var data_int: Int = 0

        var callback: callback_sdoserver? = callback_sdoserver()

        init {
            index = indexx
            subindex = subindexx
            datasize = datasizex
            data_int = datadata_int
        }


    }

    class msgSdosend() {
        var msgid: Int = 0

    }


    fun on_request(can_id: Int, data: ByteArray, timestamp: Float = 0.0F) {

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

        var command: Int = SdoStructUtils.unpack_from_cmd(data)
        var res_index = SdoStructUtils.unpack_from_index(data)
        var res_subindex = SdoStructUtils.unpack_from_subindex(data)


        _index = res_index
        _subindex = res_subindex


        var res_command = RESPONSE_DOWNLOAD
        var response = ByteArray(8)

        if ((command and EXPEDITED) > 0) {
//            logger.info("Expedited download for 0x%X:%d", index, subindex)
            var size = 0
            if ((command and SIZE_SPECIFIED) > 0) {
                size = 4 - ((command shr 2) and 0x3)
            } else {
                size = 4
            }

            var fok: Boolean = set_data(res_index, res_subindex, data.copyOfRange(4, 4 + size))
            if (!fok) {
                abort(0x08000024)
                return
            }
            SdoStructUtils.pack_into(response, 0, res_command, res_index, res_subindex)
            send_response(response)
            return

        }

        abort(0x08000024)
        return
    }


    private fun init_upload(data: ByteArray) {

//        var res_command = SdoStructUtils.unpack_from_cmd(data)
        val res_index = SdoStructUtils.unpack_from_index(data)
        val res_subindex = SdoStructUtils.unpack_from_subindex(data)


        _index = res_index
        _subindex = res_subindex
        var res_command = RESPONSE_UPLOAD or SIZE_SPECIFIED

        val response = ByteArray(8)

        val data_t = get_data(res_index, res_subindex)
        if (data_t != null) {
            val size = data_t.datasize
            if (size <= 4) {
//                logger.info("Expedited upload for 0x%X:%d", index, subindex)
                res_command = res_command or EXPEDITED
                res_command = res_command or ((4 - size) shl 2)
//                response[4:4 + size] = data

                when (data_t.datasize) {
                    1 -> {
                        response[4] = int2byte(data_t.data_int)
                    }
                    2 -> {
                        response[4] = int2byte(data_t.data_int and 0xff)
                        response[5] = int2byte((data_t.data_int and 0xff00) shr 8)
                    }
                    4 -> {
                        response[4] = int2byte(data_t.data_int and 0xff)
                        response[5] = int2byte((data_t.data_int and 0xff00) shr 8)
                        response[6] = int2byte((data_t.data_int and 0xff0000) shr 16)
                        response[7] =
                            int2byte((data_t.data_int and 0xff000000.toInt()) shr 24)
                    }
                    else -> {
                        abort(0x08000024)
                        return
                    }


                }


            }
//
//            else:
//            logger.info("Initiating segmented upload for 0x%X:%d", index, subindex)
//            struct.pack_into("<L", response, 4, size)
//            self._buffer = bytearray(data)
//            self._toggle = 0

            SdoStructUtils.pack_into(response, 0, res_command, res_index, res_subindex)
            send_response(response)
            return
        }

        abort(0x08000024)

    }

    private fun get_data(resIndex: Int, resSubindex: Int): datasdot? {
        for (ii in local_data) {
            if ((ii.index == resIndex) and (ii.subindex == resSubindex)) {
                return ii
            }
        }
        return null
    }

    private fun set_data(resIndex: Int, resSubindex: Int, bytes: ByteArray): Boolean {
        for (ii in local_data) {
            if ((ii.index == resIndex) and (ii.subindex == resSubindex)) {
                if (ii.datasize == bytes.size) {
                    when (ii.datasize) {
                        1 -> {
                            ii.data_int = byte2unit(bytes[0])
                        }
                        2 -> {
                            ii.data_int = byte2unit(bytes[0]) or (byte2unit(bytes[1]) shl 8)

                        }
                        4 -> {
                            ii.data_int = byte2unit(bytes[0]) or
                                    (byte2unit(bytes[1]) shl 8) or
                                    (byte2unit(bytes[2]) shl 16) or
                                    (byte2unit(bytes[3]) shl 24)

                        }
                        else -> {
                            return false
                        }


                    }

                    if (ii.callback != null) {
                        ii.callback!!.run_setdata(ii)
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


        send_response(data)
    }

    private fun send_response(data: ByteArray) {
        LogUtils.e(TAG, "sendMessage 发送数据 serial ${toHexStr(data)}")
        //这里会报错
        networkmy?.sendMessage(rx_cobidmy, data, usemanthread = true)
//        networkmy?.send_message11(rx_cobidmy, data)
    }

}