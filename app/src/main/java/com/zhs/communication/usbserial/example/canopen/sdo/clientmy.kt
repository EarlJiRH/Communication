package com.zhs.communication.usbserial.example.canopen.sdo

import com.blankj.utilcode.util.LogUtils
import com.zhs.communication.usbserial.example.Network
import com.zhs.communication.usbserial.example.canopen.sdo.SdoStructUtils.getExceptCode
import com.zhs.communication.utils.byte2unit
import com.zhs.communication.utils.int2byte
import com.zhs.communication.utils.toHexStr


class clientmy(rx_cobid: Int, tx_cobid: Int) {

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


    var networkmy: Network? = null

    init {
        rx_cobidmy = rx_cobid
        tx_cobidmy = tx_cobid
    }


    fun on_response(can_id: Int, data: ByteArray, timestamp: Float = 0.0F) {
//        println("回调 canid = ${can_id.toString(16)}")
        responses.add(data)
    }

    companion object {
        private const val TAG = "SdoClient"
    }

    fun send_request(request: ByteArray) {


        var retries_left = MAX_RETRIES
        try {
//                if (PAUSE_BEFORE_SEND>0.0) {
//                    //time.sleep(self.PAUSE_BEFORE_SEND)
//
//                }
            LogUtils.e(TAG, "sendMessage 发送数据 serial ${toHexStr(request)}")
            networkmy?.sendMessage(rx_cobidmy, request, usemanthread = true)
//                except CanError as e :
//                // Could be a buffer overflow. Wait some time before trying again
//                retries_left -= 1
//                if not retries_left :
//                raise
//                logger.info(str(e))
//                time.sleep(0.1)
//                else:
//                break
        } catch (e: Exception) {
            println(e)
        }

    }

    fun read_response(): ByteArray {
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
                var response = responses[0]
//            get(
//                block=True, timeout=self.RESPONSE_TIMEOUT)
//            except queue.Empty:
//            raise SdoCommunicationError("No SDO response received")
//            res_command, = struct.unpack_from("B", response)
                var res_command = byte2unit(response[0])
                if (res_command == RESPONSE_ABORTED) {
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




        on_response(0x00, data)
    }

    fun request_response(sdo_request: ByteArray): ByteArray {

        var res: ByteArray = ByteArray(0)
        var retries_left = MAX_RETRIES
//        if not self . responses . empty ():
        if (responses.isNotEmpty()) {
            responses.clear()
        }

        send_request(sdo_request)

        // Wait for node to respond
        try {
            //TODO 调试用的
//            addrxdatatestdebugmy()

            res = read_response()
        } catch (e: Exception) {

        }
//            return self.read_response()
//            except SdoCommunicationError as e :
//            retries_left -= 1
//            if not retries_left :
//            self.abort(0x5040000)
//            raise
//            logger.warning(str(e))

        //调试


        return res
    }

    fun abort(abort_code: Int = 0x08000000) {
//        """Abort current transfer."""
//        request = bytearray(8)
//        request[0] = REQUEST_ABORTED
//        // TODO: Is it necessary to include index and subindex?
//        struct.pack_into("<L", request, 4, abort_code)
//        self.send_request(request)
//        logger.error("Transfer aborted by client with code 0x{:08X}".format(abort_code))

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


//        if (size >0) {
//        // Node did not specify how many bytes to use
//        // Try to find out using Object Dictionary
//        var = self.od.get_variable(index, subindex)
//        if var is not None:
//        // Found a matching variable in OD
//        // If this is a data type (string, domain etc) the size is
//        // unknown anyway so keep the data as is
//        if var.data_type not in objectdictionary.DATA_TYPES:
//        // Get the size in bytes for this variable
//        size = len(var) // 8
//        // Truncate the data to specified size
//        data = data[0:size]
//        }
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


    class ReadableStream(sdo_client1: clientmy, index: Int, subindex: Int = 0) {

        companion object {
            private const val TAG = "ReadableStream"
        }
//        """File like object for reading from a variable."""

        //: Total size of data or ``None`` if not specified
//        size = None
        var _done = false
        var sdo_client: clientmy? = null
        var _toggle = 0
        var pos = 0
        var exp_data = ByteArray(0)
        var fok_r = false  //有没有出错
        var size = -1

        init {
            fok_r = false
            _done = false
            sdo_client = sdo_client1
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
            request = SdoStructUtils.pack_into(request, 0, REQUEST_UPLOAD, index, subindex)

            var response = sdo_client!!.request_response(request)

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
                    if ((res_index != index) or (res_subindex != subindex)) {
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
            var response = sdo_client?.request_response(request)

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
        sdo_client1: clientmy,
        index: Int,
        subindex: Int = 0,
        size1: Int = -1,
        buffering: Int = 1024,
        force_segment: Boolean = false
    ) {


        //        """File like object for writing to a variable."""
        var _done = false

        var fok_w = false  //有没有出错  出错就不在写

        var sdo_client: clientmy? = null
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
                val response = sdo_client?.request_response(request)
                if (response != null) {
                    if (response.isNotEmpty()) {


                        var res_command = response.let { SdoStructUtils.unpack_from_cmd(it) }
                        if (res_command != RESPONSE_DOWNLOAD) {
                            //                raise SdoCommunicationError (
                            //                        "Unexpected response 0x%02X" % res_command)
                            println("Unexpected response 0x${res_command}")
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
                var response = sdo_client?.request_response(request)
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
                for (ii in 0..(bytes_sent - 1)) {
                    request[ii + 1] = b[ii]
                }


                var response = sdo_client?.request_response(request)
                if (response != null) {
                    if (response.isNotEmpty()) {

                        var res_command = byte2unit(response[0])
                        if ((res_command and 0xE0) != RESPONSE_SEGMENT_DOWNLOAD) {
                            //                        raise SdoCommunicationError (
                            //                    "Unexpected response 0x%02X (expected 0x%02X)" %
                            //                            (res_command, RESPONSE_SEGMENT_DOWNLOAD))
                            println("Unexpected response 0x${res_command} (expected 0x${RESPONSE_SEGMENT_DOWNLOAD})")
                        } else {
                            fok_w = true
                        }
                    }

                }
                //
                //            // Advance position

            }
            pos += bytes_sent


            return bytes_sent
        }

        //
        fun close() {
            if ((!_done) and (_exp_header.isNotEmpty())) {
                // Segmented download not finished
                var command = REQUEST_SEGMENT_DOWNLOAD or NO_MORE_DATA

                command = command or _toggle
                // No data in this message
                command = command or (7 shl 1)
                var request = ByteArray(8)
                request[0] = int2byte(command)

                sdo_client?.request_response(request)
                _done = true
            }
        }
//            fun writable(self):
//            return True
//
//            fun tell(self):
//            return self.pos

    }


   /* class BlockUploadStream(io.RawIOBase):
        """File like object for reading from a variable using block upload."""

    //: Total size of data or ``None`` if not specified
    size = None

    blksize = 127

    crc_supported = False

    fun __init__(  sdo_client, index, subindex=0, request_crc_support=True):
    """
        :param canopen.sdo.SdoClient sdo_client:
            The SDO client to use for reading.
        :param int index:
            Object dictionary index to read from.
        :param int subindex:
            Object dictionary sub-index to read from.
        :param bool request_crc_support:
            If crc calculation should be requested when using block transfer
        """
    self._done = False
    self.sdo_client = sdo_client
    self.pos = 0
    self._crc = sdo_client.crc_cls()
    self._server_crc = None
    self._ackseq = 0

    logger.debug("Reading 0x%X:%d from node %d", index, subindex,
    sdo_client.rx_cobid - 0x600)
    // Initiate Block Upload
    request = bytearray(8)
    command = REQUEST_BLOCK_UPLOAD | INITIATE_BLOCK_TRANSFER
    if request_crc_support:
    command |= CRC_SUPPORTED
    struct.pack_into("<BHBBB", request, 0,
    command, index, subindex, self.blksize, 0)
    response = sdo_client.request_response(request)
    res_command, res_index, res_subindex = SdoStructUtils.unpack_from(response)
    if res_command & 0xE0 != RESPONSE_BLOCK_UPLOAD:
    raise SdoCommunicationError("Unexpected response 0x%02X" % res_command)
    // Check that the message is for us
    if res_index != index or res_subindex != subindex:
    raise SdoCommunicationError((
    "Node returned a value for 0x{:X}:{:d} instead, "
    "maybe there is another SDO client communicating "
    "on the same SDO channel?").format(res_index, res_subindex))
    if res_command & BLOCK_SIZE_SPECIFIED:
    self.size, = struct.unpack_from("<L", response, 4)
    logger.debug("Size is %d bytes", self.size)
    self.crc_supported = bool(res_command & CRC_SUPPORTED)
    // Start upload
    request = bytearray(8)
    request[0] = REQUEST_BLOCK_UPLOAD | START_BLOCK_UPLOAD
    sdo_client.send_request(request)

    fun read(  size=-1):
    """Read one segment which may be up to 7 bytes.

        :param int size:
            If size is -1, all data will be returned. Other values are ignored.

        :returns: 1 - 7 bytes of data or no bytes if EOF.
        :rtype: bytes
        """
    if self._done:
    return b""
    if size is None or size < 0:
    return self.readall()

    try:
    response = self.sdo_client.read_response()
    except SdoCommunicationError:
    response = self._retransmit()
    res_command, = struct.unpack_from("B", response)
    seqno = res_command & 0x7F
    if seqno == self._ackseq + 1:
    self._ackseq = seqno
    else:
    // Wrong sequence number
    response = self._retransmit()
    res_command, = struct.unpack_from("B", response)
    if self._ackseq >= self.blksize or res_command & NO_MORE_BLOCKS:
    self._ack_block()
    if res_command & NO_MORE_BLOCKS:
    n = self._end_upload()
    data = response[1:8 - n]
    self._done = True
    else:
    data = response[1:8]
    if self.crc_supported:
    self._crc.process(data)
    if self._done:
    if self._server_crc != self._crc.final():
    self.sdo_client.abort(0x05040004)
    raise SdoCommunicationError("CRC is not OK")
    logger.info("CRC is OK")
    self.pos += len(data)
    return data

    fun _retransmit(self):
    logger.info("Only %d sequences were received. Requesting retransmission",
    self._ackseq)
    end_time = time.time() + self.sdo_client.RESPONSE_TIMEOUT
    self._ack_block()
    while time.time() < end_time:
    response = self.sdo_client.read_response()
    res_command, = struct.unpack_from("B", response)
    seqno = res_command & 0x7F
    if seqno == self._ackseq + 1:
    // We should be back in sync
    self._ackseq = seqno
    return response
    raise SdoCommunicationError("Some data were lost and could not be retransmitted")

    fun _ack_block(self):
    request = bytearray(8)
    request[0] = REQUEST_BLOCK_UPLOAD | BLOCK_TRANSFER_RESPONSE
    request[1] = self._ackseq
    request[2] = self.blksize
    self.sdo_client.send_request(request)
    if self._ackseq == self.blksize:
    self._ackseq = 0

    fun _end_upload(self):
    response = self.sdo_client.read_response()
    res_command, self._server_crc = struct.unpack_from("<BH", response)
    if res_command & 0xE0 != RESPONSE_BLOCK_UPLOAD:
    self.sdo_client.abort(0x05040001)
    raise SdoCommunicationError("Unexpected response 0x%02X" % res_command)
    if res_command & 0x3 != END_BLOCK_TRANSFER:
    self.sdo_client.abort(0x05040001)
    raise SdoCommunicationError("Server did not end transfer as expected")
    // Return number of bytes not used in last message
    return (res_command >> 2) & 0x7

    fun close(self):
    if self.closed:
    return
    super(BlockUploadStream, self).close()
    if self._done:
    request = bytearray(8)
    request[0] = REQUEST_BLOCK_UPLOAD | END_BLOCK_TRANSFER
    self.sdo_client.send_request(request)

    fun tell(self):
    return self.pos

    fun readinto(  b):
    """
        Read bytes into a pre-allocated, writable bytes-like object b,
        and return the number of bytes read.
        """
    data = self.read(7)
    b[:len(data)] = data
    return len(data)

    fun readable(self):
    return True


    class BlockDownloadStream(io.RawIOBase):
        """File like object for block download."""

    fun __init__(  sdo_client, index, subindex=0, size=None, request_crc_support=True):
    """
        :param canopen.sdo.SdoClient sdo_client:
            The SDO client to use for communication.
        :param int index:
            Object dictionary index to read from.
        :param int subindex:
            Object dictionary sub-index to read from.
        :param int size:
            Size of data in number of bytes if known in advance.
        :param bool request_crc_support:
            If crc calculation should be requested when using block transfer
        """
    self.sdo_client = sdo_client
    self.size = size
    self.pos = 0
    self._done = False
    self._seqno = 0
    self._crc = sdo_client.crc_cls()
    self._last_bytes_sent = 0
    command = REQUEST_BLOCK_DOWNLOAD | INITIATE_BLOCK_TRANSFER
    if request_crc_support:
    command |= CRC_SUPPORTED
    request = bytearray(8)
    logger.info("Initiating block download for 0x%X:%d", index, subindex)
    if size is not None:
    logger.debug("Expected size of data is %d bytes", size)
    command |= BLOCK_SIZE_SPECIFIED
    struct.pack_into("<L", request, 4, size)
    else:
    logger.warning("Data size has not been specified")
    SdoStructUtils.pack_into(request, 0, command, index, subindex)
    response = sdo_client.request_response(request)
    res_command, res_index, res_subindex = SdoStructUtils.unpack_from(response)
    if res_command & 0xE0 != RESPONSE_BLOCK_DOWNLOAD:
    self.sdo_client.abort(0x05040001)
    raise SdoCommunicationError(
    "Unexpected response 0x%02X" % res_command)
    // Check that the message is for us
    if res_index != index or res_subindex != subindex:
    self.sdo_client.abort()
    raise SdoCommunicationError((
    "Node returned a value for 0x{:X}:{:d} instead, "
    "maybe there is another SDO client communicating "
    "on the same SDO channel?").format(res_index, res_subindex))
    self._blksize, = struct.unpack_from("B", response, 4)
    logger.debug("Server requested a block size of %d", self._blksize)
    self.crc_supported = bool(res_command & CRC_SUPPORTED)

    fun write(  b):
    """
        Write the given bytes-like object, b, to the SDO server, and return the
        number of bytes written. This will be at most 7 bytes.

        :param bytes b:
            Data to be transmitted.

        :returns:
            Number of bytes successfully sent or ``None`` if length of data is
            less than 7 bytes and the total size has not been reached yet.
        """
    if self._done:
    raise RuntimeError("All expected data has already been transmitted")
    // Can send up to 7 bytes at a time
    data = b[0:7]
    if self.size is not None and self.pos + len(data) >= self.size:
    // This is the last data to be transmitted based on expected size
    self.send(data, end=True)
    elif len(data) < 7:
    // We can't send less than 7 bytes in the middle of a transmission
    return None
    else:
    self.send(data)
    return len(data)

    fun send(  b, end=False):
    """Send up to 7 bytes of data.

        :param bytes b:
            0 - 7 bytes of data to transmit.
        :param bool end:
            If this is the last data.
        """
    assert len(b) <= 7, "Max 7 bytes can be sent"
    if not end:
    assert len(b) == 7, "Less than 7 bytes only allowed if last data"
    self._seqno += 1
    command = self._seqno
    if end:
    command |= NO_MORE_BLOCKS
    self._done = True
    // Change expected ACK:ed sequence
    self._blksize = self._seqno
    // Save how many bytes this message contains since this is the last
    self._last_bytes_sent = len(b)
    request = bytearray(8)
    request[0] = command
    request[1:len(b) + 1] = b
    self.sdo_client.send_request(request)
    self.pos += len(b)
    if self.crc_supported:
    // Calculate CRC
    self._crc.process(b)
    if self._seqno >= self._blksize:
    // End of this block, wait for ACK
    self._block_ack()

    fun tell(self):
    return self.pos

    fun _block_ack(self):
    logger.debug("Waiting for acknowledgement of last block...")
    response = self.sdo_client.read_response()
    res_command, ackseq, blksize = struct.unpack_from("BBB", response)
    if res_command & 0xE0 != RESPONSE_BLOCK_DOWNLOAD:
    self.sdo_client.abort(0x05040001)
    raise SdoCommunicationError(
    "Unexpected response 0x%02X" % res_command)
    if res_command & 0x3 != BLOCK_TRANSFER_RESPONSE:
    self.sdo_client.abort(0x05040001)
    raise SdoCommunicationError("Server did not respond with a "
    "block download response")
    if ackseq != self._blksize:
    self.sdo_client.abort(0x05040003)
    raise SdoCommunicationError(
    ("%d of %d sequences were received. "
    "Retransmission is not supported yet.") % (ackseq, self._blksize))
    logger.debug("All %d sequences were received successfully", ackseq)
    logger.debug("Server requested a block size of %d", blksize)
    self._blksize = blksize
    self._seqno = 0

    fun close(self):
    """Closes the stream."""
    if self.closed:
    return
    super(BlockDownloadStream, self).close()
    if not self._done:
    logger.error("Block transfer was not finished")
    command = REQUEST_BLOCK_DOWNLOAD | END_BLOCK_TRANSFER
    // Specify number of bytes in last message that did not contain data
    command |= (7 - self._last_bytes_sent) << 2
    request = bytearray(8)
    request[0] = command
    if self.crc_supported:
    // Add CRC
    struct.pack_into("<H", request, 1, self._crc.final())
    logger.debug("Ending block transfer...")
    response = self.sdo_client.request_response(request)
    res_command, = struct.unpack_from("B", response)
    if not res_command & END_BLOCK_TRANSFER:
    raise SdoCommunicationError("Block download unsuccessful")
    logger.info("Block download successful")

    fun writable(self):
    return True*/
}


