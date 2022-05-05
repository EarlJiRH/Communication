package com.zhs.communication.usbserial.example.canopen.sdo

import com.blankj.utilcode.util.LogUtils
import com.zhs.communication.usbserial.example.Network
import com.zhs.communication.usbserial.example.canopen.sdo.SdoStructUtils.getExceptCode
import com.zhs.communication.utils.byte2unit
import com.zhs.communication.utils.int2byte
import com.zhs.communication.utils.toHexStr

/**
 * @param receive 接收的参数(rx)
 * @param respond 响应的参数(tx)
 * */
class SdoClient(receive: Int, respond: Int) {

    companion object {
        private const val TAG = "SdoClient"
    }

    //: Max time in seconds to wait for response from server
    val RESPONSE_TIMEOUT = 300 //ms

    //: Max number of request retries before raising error
    val MAX_RETRIES = 1

    //: Seconds to wait before sending a request, for rate limiting
    val PAUSE_BEFORE_SEND = 0.0

    //
//    """
//        :param int rx_cobid:
//            COB-ID that the server receives on (usually 0x600 + node ID)
//        :param int tx_cobid:
//            COB-ID that the server responds with (usually 0x580 + node ID)
//        :param canopen.ObjectDictionary od:
//            Object Dictionary to use for communication
//        """
    var rx_cobidmy: Int = 0
    var tx_cobidmy: Int = 0


    var responses: MutableList<ByteArray> = mutableListOf()

    var network: Network? = null

    init {
        rx_cobidmy = receive
        tx_cobidmy = respond
    }


    fun addResponse(can_id: Int, data: ByteArray, timestamp: Float = 0.0F) {
//        println("回调 canid = ${can_id.toString(16)}")
        responses.add(data)
    }


    fun sendRequest(request: ByteArray) {
        var retries_left = MAX_RETRIES
        LogUtils.e(TAG, "sendMessage 发送数据 serial ${toHexStr(request)}")
        network?.sendMessage(rx_cobidmy, request, userMainThread = true)
    }

    fun readResponse(): ByteArray {

        try {
            var fok = false
            for (ii in 1..RESPONSE_TIMEOUT) {
                Thread.sleep(1)
                if (responses.size > 0) {
//                    System.out.println("收到回复 在 ${ii} 毫秒")
                    fok = true
                    break
                }
            }



            if (fok) {
                val response = responses[0]
//            get(
//                block=True, timeout=self.RESPONSE_TIMEOUT)
//            except queue.Empty:
//            raise SdoCommunicationError("No SDO response received")
//            res_command, = struct.unpack_from("B", response)
                val resCommand = byte2unit(response[0])
                if (resCommand == RESPONSE_ABORTED) {
                    println("canopen fanshui 异常$response")

                    println(SdoAbortedError(getExceptCode(response)).getErrorCodeString())

                    return ByteArray(0)
                }
//            abort_code, = struct.unpack_from("<L", response, 4)
//            raise SdoAbortedError(abort_code)
//            return response
                return response
            } else {
//                raise SdoCommunicationError("No SDO response received")
                println("No SDO response received")
            }
        } catch (e: Exception) {
            println(e)
        }
        return ByteArray(0)
    }

    fun addrxdatatestdebugmy() {
        var data = ByteArray(8)

//        data[0]=0x80.toByte()
//
//        data[6]=0x04
//        data[7]=0x05

//        data[0]=0x4f
//        data[1]=0x10
//        data[2]=0x20
//        data[3]=0x01
//        data[4]=0x10
//        data[5]=0x00
//        data[6]=0x00

        data[0] = 0x51
        data[1] = 0x10
        data[2] = 0x20
        data[3] = 0x01
        data[4] = 0x10
        data[5] = 0x00
        data[6] = 0x01




        addResponse(0x00, data)
    }

    fun requestAndResponse(sdo_request: ByteArray): ByteArray {
        if (responses.isNotEmpty()) {
            responses.clear()
        }
        sendRequest(sdo_request)
        //调试
        return readResponse()
    }


    fun upload(index: Int, subindex: Int): ByteArray {
        //    """May be called to make a read operation without an Object Dictionary.
//
//        :param int index:
//            Index of object to read.
//        :param int subindex:
//            Sub-index of object to read.
//
//        :return: A data object.
//        :rtype: bytes
//
//        :raises canopen.SdoCommunicationError:
//            On unexpected response or timeout.
//        :raises canopen.SdoAbortedError:
//            When node responds with an error.
//        """
        val fp = openmy_r(index, subindex)
        var data = ByteArray(0)
        if (fp.fok_r) {
            while (true) {
                if (!fp.fok_r) {
                    break
                }
                data += fp.read()

                if (data.size >= fp.size) {
                    break
                }

            }

        }

        return data
    }


    fun download(
        index: Int,
        subindex: Int,
        data: ByteArray,
        force_segment: Boolean = false
    ): Boolean {
//        """May be called to make a write operation without an Object Dictionary.
//
//        :param int index:
//            Index of object to write.
//        :param int subindex:
//            Sub-index of object to write.
//        :param bytes data:
//            Data to be written.
//        :param bool force_segment:
//            Force use of segmented transfer regardless of data size.
//
//        :raises canopen.SdoCommunicationError:
//            On unexpected response or timeout.
//        :raises canopen.SdoAbortedError:
//            When node responds with an error.
//        """
//
//        print("-----------write----",data,len(data))
        var fok = false
        val fp = openmy_w(index, subindex, data, 7)
        if (fp.fok_w) {
            while (true) {
                if (!fp.fok_w) {
                    break
                }

                fp.write(data.copyOfRange(fp.pos, data.size))
                fp.close()
                //        System.out.println("fp.pos = ${fp.pos}")
                if (fp.pos >= data.size) {
                    fok = true
                    break
                }
            }
        }
        return fok
    }


    fun openmy_r(index: Int, subindex: Int = 0): ReadableStream {
        return ReadableStream(this, index, subindex)
    }

    fun openmy_w(
        index: Int,
        subindex: Int = 0,
        data: ByteArray,
        buffering: Int = 7
    ): WritableStream {
        return WritableStream(this, index, subindex, data.size, buffering)
    }


    class ReadableStream(sdoClient: SdoClient, index: Int, subIndex: Int = 0) {

        companion object {
            private const val TAG = "ReadableStream"
        }
//        """File like object for reading from a variable."""

        //: Total size of data or ``None`` if not specified
//        size = None
        var _done = false
        var sdo_client: SdoClient? = null
        var _toggle = 0
        var pos = 0
        var exp_data = ByteArray(0)
        var fok_r = false  //有没有出错
        var size = -1

        init {
            fok_r = false
            _done = false
            sdo_client = sdoClient
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
            request = SdoStructUtils.pack_into(request, 0, REQUEST_UPLOAD, index, subIndex)

            var response = sdo_client!!.requestAndResponse(request)

            if (response.isNotEmpty()) {
                var res_command = SdoStructUtils.unpack_from_cmd(response)
                var res_index = SdoStructUtils.unpack_from_index(response)
                var res_subindex = SdoStructUtils.unpack_from_subindex(response)
                //            var res_data = response[4:8]
                //

                if ((res_command and 0xE0) != RESPONSE_UPLOAD) {
                    //                raise SdoCommunicationError("Unexpected response 0x%02X" % res_command)
                    println("Unexpected response $res_command")
                } else {
                    // Check that the message is for us
                    if ((res_index != index) or (res_subindex != subIndex)) {
                        //                    raise SdoCommunicationError((
                        //            "Node returned a value for 0x{:X}:{:d} instead, "
                        //            "maybe there is another SDO client communicating "
                        //            "on the same SDO channel?").format(res_index, res_subindex))

                        println(
                            "Node returned a value for 0x${res_index.toString(16)}:${
                                res_subindex.toString(
                                    16
                                )
                            } instead," +
                                    "maybe there is another SDO client communicating " +
                                    "on the same SDO channel?"
                        )
                    } else {

                        if ((res_command and EXPEDITED) > 0) {
                            if ((res_command and SIZE_SPECIFIED) > 0) {
                                size = 4 - ((res_command shr 2) and 0x3)
                                exp_data = response.copyOfRange(4, 4 + size)
                            } else {

                                exp_data = response.copyOfRange(4, 8)
                            }
                            pos = exp_data.size

                            fok_r = true


                        }
                        //            // Expedited upload
                        //
                        else if ((res_command and SIZE_SPECIFIED) > 0) {
                            fok_r = true
                            size =
                                (byte2unit(response[4]) or ((byte2unit(response[5])) shl 8))//接收的数组大小

//                            System.out.println("sdo read size = ${size}")
                            //                        size, = struct.unpack("<L", res_data)
                            //                        logger.debug("Using segmented transfer of %d bytes", self.size)
                        }
                        //
                        //
                        else {
                            //            logger.debug("Using segmented transfer")
                        }
                    }
                    //
                    //
                    //
                }
            } else {
                println("-调试异常1 --")
            }
//
//
//
        }

        fun read(size1: Int = -1): ByteArray {


//                """Read one segment which may be up to 7 bytes.
//
//        :param int size:
//            If size is -1, all data will be returned. Other values are ignored.
//
//        :returns: 1 - 7 bytes of data or no bytes if EOF.
//        :rtype: bytes
//        """

            fok_r = false

            if (_done) {
                return ByteArray(0)
            }

            if (!exp_data.isEmpty()) {
                _done = true
                fok_r = true
                return exp_data
            }

            if (size < 0) {
                return ByteArray(0)
            }


            var command = REQUEST_SEGMENT_UPLOAD
            command = command or _toggle


            var request = ByteArray(8)
            request[0] = int2byte(command and 0xff)
            var response = sdo_client?.requestAndResponse(request)

            if (response != null) {
                if (response.isNotEmpty()) {
                    val res_command = byte2unit(response[0])
//                    if (res_command != null) {
                    if ((res_command and 0xE0) != RESPONSE_SEGMENT_UPLOAD) {
//                    raise SdoCommunicationError ("Unexpected response 0x%02X" % res_command)
                        println("Unexpected response 0x${res_command.toString(16)}")
                    } else {
                        if ((res_command and TOGGLE_BIT) != _toggle) {
//                        raise SdoCommunicationError ("Toggle bit mismatch")
                            println("Toggle bit mismatch")
                        } else {
                            var length = 7 - ((res_command shr 1) and 0x7)
                            if ((res_command and NO_MORE_DATA) > 0) {
                                _done = true
                            }


                            _toggle = _toggle xor TOGGLE_BIT
                            pos += length

                            println("sdo read length = $length")
                            fok_r = true
                            return response.copyOfRange(1, length + 1)

                        }
                    }
//                    }
                }
            }



            return ByteArray(0)
        }

//        fun readinto(b):
//                """
//        Read bytes into a pre-allocated, writable bytes-like object b,
//        and return the number of bytes read.
//        """
//        data = self.read(7)
//        b[:len(data )] = data
//        return len(data )
//
//        fun readable(self):
//                return True
//
//        fun tell(self):
//                return self.pos
    }

    //
//
    class WritableStream(
        sdo_client1: SdoClient,
        index: Int,
        subindex: Int = 0,
        size1: Int = -1,
        buffering: Int = 1024,
        force_segment: Boolean = false
    ) {


        //        """File like object for writing to a variable."""
        var _done = false

        var fok_w = false  //有没有出错  出错就不在写

        var sdo_client: SdoClient? = null
        var _toggle: Int = 0
        var pos = 0
        var exp_data = ByteArray(0)

        var _exp_header = ByteArray(0)
        var size = -1

        init {
            fok_w = false
            sdo_client = sdo_client1
            size = size1
            pos = 0
            _toggle = 0

            _done = false

            if ((size < 0) or (size > 4) or force_segment) {
                // Initiate segmented download
                var request = ByteArray(8)
                var command = REQUEST_DOWNLOAD
                if (size > 0) {
                    command = command or SIZE_SPECIFIED

                    request[4] = int2byte(size and 0xff)
                    request[5] = int2byte((size and 0xff00) shr 8)
                    //                struct.pack_into("<L", request, 4, size)
                    //TODO
                }

                request = SdoStructUtils.pack_into(request, 0, command, index, subindex)
                val response = sdo_client?.requestAndResponse(request)
                if (response != null) {
                    if (response.isNotEmpty()) {


                        val resCommand = response.let { SdoStructUtils.unpack_from_cmd(it) }
                        if (resCommand != RESPONSE_DOWNLOAD) {
                            //                raise SdoCommunicationError (
                            //                        "Unexpected response 0x%02X" % res_command)
                            println("Unexpected response 0x${resCommand}")
                        } else {
                            fok_w = true
                        }
                        //else TODO
                    } //else TODO
                }
                //else TODO
            } else {
                // Expedited download
                // Prepare header (first 4 bytes in CAN message)
                var command = REQUEST_DOWNLOAD or EXPEDITED or SIZE_SPECIFIED
                command = command or ((4 - size) shl 2)
                _exp_header = SdoStructUtils.pack(command, index, subindex)
                fok_w = true

                //            if self._exp_header is not None :
                //            ll = []
                //            for i in self._exp_header:
                //            ll.append(hex(i))
                //            print('size=', size, 'head====', ll)
            }
        }


        fun write(b: ByteArray): Int {

            var bytes_sent: Int = 0
            fok_w = false

            if (_done) {
                //            raise RuntimeError("All expected data has already been transmitted")
            }

            if (!_exp_header.isEmpty()) {
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
                var request = _exp_header + bsiz4
                var response = sdo_client?.requestAndResponse(request)
                if (response != null) {
                    if (response.isNotEmpty()) {
                        var res_command = response?.let { SdoStructUtils.unpack_from_cmd(it) }
                        if (res_command != null) {
                            if ((res_command and 0xE0) != RESPONSE_DOWNLOAD) {
                                //                    raise SdoCommunicationError (
                                //                            "Unexpected response 0x%02X" % res_command)
                                println("Unexpected response 0x${res_command}")
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
                command = command or _toggle
                _toggle = _toggle xor TOGGLE_BIT
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


                var response = sdo_client?.requestAndResponse(request)
                if (response != null) {
                    if (response.isNotEmpty()) {

                        var resCommand = byte2unit(response[0])
                        if ((resCommand and 0xE0) != RESPONSE_SEGMENT_DOWNLOAD) {
                            println("Unexpected response 0x${resCommand} (expected 0x${RESPONSE_SEGMENT_DOWNLOAD})")
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
            if ((!_done) and (_exp_header.isNotEmpty())) {
                // Segmented download not finished
                var command = REQUEST_SEGMENT_DOWNLOAD or NO_MORE_DATA

                command = command or _toggle
                // No data in this message
                command = command or (7 shl 1)
                val request = ByteArray(8)
                request[0] = int2byte(command)

                sdo_client?.requestAndResponse(request)
                _done = true
            }
        }

    }

}



