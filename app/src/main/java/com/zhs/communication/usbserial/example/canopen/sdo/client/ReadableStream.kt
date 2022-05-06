package com.zhs.communication.usbserial.example.canopen.sdo.client

import com.blankj.utilcode.util.LogUtils
import com.zhs.communication.usbserial.example.canopen.sdo.*
import com.zhs.communication.utils.byte2unit
import com.zhs.communication.utils.int2byte
import com.zhs.communication.utils.toHexStr

/**
 * ================================================
 * 类名：com.zhs.communication.usbserial.example.canopen.sdo.client
 * 时间：2022/5/5 17:08
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 * ================================================
 * @author Admin
 */
class ReadableStream(var sdoClient: SdoClient, var index: Int, var subIndex: Int = 0) {

    companion object {
        private const val TAG = "ReadableStream"
    }

    //        """File like object for reading from a variable."""
    //         : Total size of data or ``None`` if not specified
    //        size = None
    var _done = false
    var _toggle = 0
    var pos = 0
    var expData = ByteArray(0)
    var fok_r = false  //有没有出错
    var size = -1

    init {
        fok_r = false
        _done = false
        _toggle = 0
        pos = 0

//                """
//        :param canopen.sdo.SdoClient sdo_client:
//            The SDO client to use for reading.
//        :param int index:
//            Object dictionary index to read from.
//        :param int subindex:
//            Object dictionary sub-index to read from.
//        """
//        logger.debug("Reading 0x%X:%d from node %d", index, subindex,
//        sdo_client.rx_cobid - 0x600)


        var request = ByteArray(8)
        request = SdoStructUtils.pack(request, 0, REQUEST_UPLOAD, index, subIndex)
        LogUtils.e(TAG, "$TAG init request=${toHexStr(request)}")
        val response = sdoClient.requestAndResponse(request)

        if (response.isEmpty()) {
            LogUtils.e(TAG, "init: -调试异常1 --")
        } else {
            val resCommand = SdoStructUtils.unpackFromCommand(response)
            val resIndex = SdoStructUtils.unpackFromIndex(response)
            val resSubIndex = SdoStructUtils.unpackFromSuIndex(response)
            //            var res_data = response[4:8]

            if ((resCommand and 0xE0) != RESPONSE_UPLOAD) {
                LogUtils.e(TAG, "init: Unexpected response $resCommand")
            } else {
                // Check that the message is for us
                if ((resIndex != index) or (resSubIndex != subIndex)) {
                    //                    raise SdoCommunicationError((
                    //            "Node returned a value for 0x{:X}:{:d} instead, "
                    //            "maybe there is another SDO client communicating "
                    //            "on the same SDO channel?").format(res_index, res_subindex))

                    LogUtils.e(
                        TAG,
                        "Node returned a value for 0x${resIndex.toString(16)}:${
                            resSubIndex.toString(
                                16
                            )
                        } instead," +
                                "maybe there is another SDO client communicating " +
                                "on the same SDO channel?"
                    )
                } else {

                    if ((resCommand and EXPEDITED) > 0) {
                        if ((resCommand and SIZE_SPECIFIED) > 0) {
                            size = 4 - ((resCommand shr 2) and 0x3)
                            expData = response.copyOfRange(4, 4 + size)
                        } else {
                            expData = response.copyOfRange(4, 8)
                        }
                        pos = expData.size
                        fok_r = true


                    } else if ((resCommand and SIZE_SPECIFIED) > 0) {
                        fok_r = true
                        size =
                            (byte2unit(response[4]) or ((byte2unit(response[5])) shl 8))//接收的数组大小

//                            System.out.println("sdo read size = ${size}")
                        //                        size, = struct.unpack("<L", res_data)
                        //                        logger.debug("Using segmented transfer of %d bytes", self.size)
                    } else {
                        //            logger.debug("Using segmented transfer")
                    }
                }
            }
        }
    }

    fun read(size: Int = -1): ByteArray {

//   """Read one segment which may be up to 7 bytes.
//      :param int size:
//      If size is -1, all data will be returned. Other values are ignored.
//      :returns: 1 - 7 bytes of data or no bytes if EOF.
//      :rtype: bytes
//   """

        fok_r = false

        if (_done) {
            return ByteArray(0)
        }

        if (expData.isNotEmpty()) {
            _done = true
            fok_r = true
            return expData
        }

        if (this.size < 0) {
            return ByteArray(0)
        }


        var command = REQUEST_SEGMENT_UPLOAD
        command = command or _toggle


        val request = ByteArray(8)
        request[0] = int2byte(command and 0xff)
        val response = sdoClient.requestAndResponse(request)

        if (response.isNotEmpty()) {
            val resCommand = byte2unit(response[0])
            if ((resCommand and 0xE0) != RESPONSE_SEGMENT_UPLOAD) {
//                    raise SdoCommunicationError ("Unexpected response 0x%02X" % res_command)
                println("Unexpected response 0x${resCommand.toString(16)}")
            } else {
                if ((resCommand and TOGGLE_BIT) != _toggle) {
//                        raise SdoCommunicationError ("Toggle bit mismatch")
                    println("Toggle bit mismatch")
                } else {
                    val length = 7 - ((resCommand shr 1) and 0x7)
                    if ((resCommand and NO_MORE_DATA) > 0) {
                        _done = true
                    }
                    _toggle = _toggle xor TOGGLE_BIT
                    pos += length

                    println("sdo read length = $length")
                    fok_r = true
                    return response.copyOfRange(1, length + 1)
                }
            }
        }

        return ByteArray(0)
    }

}