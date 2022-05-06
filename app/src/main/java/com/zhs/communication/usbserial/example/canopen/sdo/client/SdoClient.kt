package com.zhs.communication.usbserial.example.canopen.sdo.client

import com.blankj.utilcode.util.LogUtils
import com.zhs.communication.usbserial.example.Network
import com.zhs.communication.usbserial.example.canopen.sdo.*
import com.zhs.communication.usbserial.example.canopen.sdo.SdoStructUtils.getExceptCode
import com.zhs.communication.utils.byte2unit
import com.zhs.communication.utils.toHexStr

/**
 * @param receive 接收的参数(rx)
 * @param respond 响应的参数(tx)
 * */
class SdoClient(
    var receive: Int,
    var respond: Int,
    var network: Network? = null
) {

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
//    var rx_cobidmy: Int = 0
//    var tx_cobidmy: Int = 0


    var responses: MutableList<ByteArray> = mutableListOf()

//    var network: Network? = null

//    init {
//        rx_cobidmy = receive
//        tx_cobidmy = respond
//    }


    fun addResponse(canId: Int, data: ByteArray, timestamp: Float = 0.0F) {
//        println("回调 canid = ${can_id.toString(16)}")
        responses.add(data)
    }


    fun sendRequest(request: ByteArray) {
        var retries_left = MAX_RETRIES
        LogUtils.e(TAG, "sendMessage 发送数据 serial ${toHexStr(request)}")
        network?.sendMessage(receive, request, userMainThread = true)
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

    fun requestAndResponse(sdoRequest: ByteArray): ByteArray {
        if (responses.isNotEmpty()) {
            responses.clear()
        }
        sendRequest(sdoRequest)
        //调试
        return readResponse()
    }


    fun upload(index: Int, subIndex: Int): ByteArray {
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
        val fp = getReadableStream(index, subIndex)
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
        subIndex: Int,
        data: ByteArray,
        forceSegment: Boolean = false
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
        val fp = getWritableStream(index, subIndex, data, 7)
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


    private fun getReadableStream(index: Int, subIndex: Int = 0): ReadableStream {
        return ReadableStream(this, index, subIndex)
    }

    private fun getWritableStream(
        index: Int,
        subIndex: Int = 0,
        data: ByteArray,
        buffering: Int = 7
    ): WritableStream = WritableStream(this, index, subIndex, data.size, buffering)

}



