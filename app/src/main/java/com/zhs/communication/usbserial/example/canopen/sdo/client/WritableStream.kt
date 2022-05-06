package com.zhs.communication.usbserial.example.canopen.sdo.client

import com.zhs.communication.usbserial.example.canopen.sdo.*
import com.zhs.communication.utils.byte2unit
import com.zhs.communication.utils.int2byte

/**
 * ================================================
 * 类名：com.zhs.communication.usbserial.example.canopen.sdo.client
 * 时间：2022/5/5 17:02
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 * ================================================
 * @author Admin
 */
class WritableStream (
    sdoClient: SdoClient,
    index: Int,
    subIndex: Int = 0,
    size: Int = -1,
    buffering: Int = 1024,
    forceSegment: Boolean = false
) {


    //        """File like object for writing to a variable."""
    var _done = false

    var fok_w = false  //有没有出错  出错就不在写

    var sdoClient: SdoClient? = null
    var toggle: Int = 0
    var pos = 0

    var exp_data = ByteArray(0)

    var expHeader = ByteArray(0)

    var size = -1

    init {
        fok_w = false
        this.sdoClient = sdoClient
        this.size = size
        pos = 0
        toggle = 0

        _done = false

        if ((this.size < 0) or (this.size > 4) or forceSegment) {
            // Initiate segmented download
            var request = ByteArray(8)
            var command = REQUEST_DOWNLOAD
            if (this.size > 0) {
                command = command or SIZE_SPECIFIED

                request[4] = int2byte(this.size and 0xff)
                request[5] = int2byte((this.size and 0xff00) shr 8)
                //                struct.pack_into("<L", request, 4, size)
            }

            request = SdoStructUtils.pack(request, 0, command, index, subIndex)
            val response = this.sdoClient?.requestAndResponse(request)
            if (response != null) {
                if (response.isNotEmpty()) {


                    val resCommand = response.let { SdoStructUtils.unpackFromCommand(it) }
                    if (resCommand != RESPONSE_DOWNLOAD) {
                        //                raise SdoCommunicationError (
                        //                        "Unexpected response 0x%02X" % res_command)
                        println("Unexpected response 0x${resCommand}")
                    } else {
                        fok_w = true
                    }
                }
            }
        } else {
            // Expedited download
            // Prepare header (first 4 bytes in CAN message)
            var command = REQUEST_DOWNLOAD or EXPEDITED or SIZE_SPECIFIED
            command = command or ((4 - this.size) shl 2)
            expHeader =
                SdoStructUtils.pack(command = command, index = index, subIndex = subIndex)
            fok_w = true

        }
    }


    fun write(b: ByteArray): Int {

        var bytes_sent: Int = 0
        fok_w = false

        if (_done) {
            //            raise RuntimeError("All expected data has already been transmitted")
        }

        if (!expHeader.isEmpty()) {
            // Expedited download
            if (b.size < size) {
                return 0
            }
            // Not enough data provided

            if (b.size > 4) {
                //                raise AssertionError ("More data received than expected")
            }
            //            data = b.tobytes() if isinstance(b, memoryview) else b
            var bsiz4 = ByteArray(4)
            if (b.size <= 4) {

                for (i in b.indices) {
                    bsiz4[i] = b[i]
                }
            }
            var request = expHeader + bsiz4
            var response = sdoClient?.requestAndResponse(request)
            if (response != null) {
                if (response.isNotEmpty()) {
                    var resCommand = response?.let { SdoStructUtils.unpackFromCommand(it) }
                    if (resCommand != null) {
                        if ((resCommand and 0xE0) != RESPONSE_DOWNLOAD) {
                            //                    raise SdoCommunicationError (
                            //                            "Unexpected response 0x%02X" % res_command)
                            println("Unexpected response 0x${resCommand}")
                        } else {
                            fok_w = true
                        }
                    }
                    bytes_sent = b.size

                }


                _done = true
            }

        } else {
            // Segmented download
            var request = ByteArray(8)
            var command = REQUEST_SEGMENT_DOWNLOAD
            // Add toggle bit
            command = command or toggle
            toggle = toggle xor TOGGLE_BIT
            //            // Can send up to 7 bytes at a time
            bytes_sent = minOf(b.size, 7)
            if ((size > 0) and ((pos + bytes_sent) >= size)) {


                //            // No more data after this message
                command = command or NO_MORE_DATA
                _done = true
            }
            //            // Specify number of bytes that do not contain segment data
            command = command or ((7 - bytes_sent) shl 1)
            request[0] = command.toByte()
            //            request[1:bytes_sent+1] = b[0:bytes_sent]
            for (ii in 0 until bytes_sent) {
                request[ii + 1] = b[ii]
            }


            var response = sdoClient?.requestAndResponse(request)
            if (response != null) {
                if (response.isNotEmpty()) {

                    var resCommand = byte2unit(response[0])
                    if ((resCommand and 0xE0) != RESPONSE_SEGMENT_DOWNLOAD) {
                        println("Unexpected response 0x${resCommand} (expected 0x$RESPONSE_SEGMENT_DOWNLOAD)")
                    } else {
                        fok_w = true
                    }
                }

            }

        }
        pos += bytes_sent


        return bytes_sent
    }

    fun close() {
        if ((!_done) and (expHeader.isNotEmpty())) {
            // Segmented download not finished
            var command = REQUEST_SEGMENT_DOWNLOAD or NO_MORE_DATA

            command = command or toggle
            // No data in this message
            command = command or (7 shl 1)
            val request = ByteArray(8)
            request[0] = int2byte(command)

            sdoClient?.requestAndResponse(request)
            _done = true
        }
    }

}