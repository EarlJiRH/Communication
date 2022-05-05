package com.zhs.communication.usbserial.example

import com.blankj.utilcode.util.LogUtils
import com.zhs.communication.eventbus.*
import com.zhs.communication.utils.byte2unit
import com.zhs.communication.utils.toHexStr
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

open class UsbCanSerial {

    companion object {
        const val TAG = "UsbCanSerial"
    }

    private var MaxDataLen: Int = 32

    var rxcandatanum: Int = 0
    private var dataBuff: ByteArray = ByteArray(0)
    private var databuffall: ByteArray = ByteArray(0)
    private var usbcansenddatesate = 0 //未知 7   失败5  成功  0  状态改变才会发给上位机   can通信时要
    // 注意每条指令都是21字节，不足补齐就行了

    var mListener: SendCanListener? = null



    fun checkCommandData(data: ByteArray, indexhead: Int): Int {

        var sum: Int = 0
        for (i in (indexhead + 2)..(byte2unit(data[indexhead + 3]) + 2)) {
            sum += byte2unit(data[i])
        }
        return sum and 0xff
    }

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        return sdf.format(Date())
    }

    open fun canData2handler(
        cankou: Int,
        candatatype: Int,
        canid: Int,
        candatalen: Int,
        candata: ByteArray
    ) {


    }


    fun rxcandata(cankou: Int, candatatype: Int, canid: Int, candatalen: Int, candata: ByteArray) {
        /*
        * 接收到数据 can
        *
        *
        * */
        rxcandatanum += 1

//        System.out.print("${rxcandatanum}  ${getCurrentDate()} 接收到数据 CAN${cankou} 类型 ${candatatype} id=${canid.toString(16)} len=${candatalen} data=[")
//        for(i in candata){
//            System.out.print("${byte2unit(i).toString(16)} ")
//        }
//        System.out.println("]")


        canData2handler(cankou, candatatype, canid, candatalen, candata)

    }


    fun sendcheckstatecanstatemingling() {
        val candata = ByteArray(21)
        candata[0] = 0x66
        candata[1] = 0xcc.toByte()
        candata[2] = 0x00
        candata[3] = 0x02
        candata[4] = 0x32
        candata[5] = 0x34
        mListener?.sendData2Serial(candata)
    }


    fun datacommytest(data: ByteArray) {

        val minglinwe = byte2unit(data[4])

        if (minglinwe == 0xb1) {
            //下位机向上位机发送实时CAN总线数据 0xB1
            var canid: Int = 0
            var candatatype: Int = 0
            var candatalen: Int = 0
            var candata: ByteArray = ByteArray(0)
            var cankou: Int = 0

            cankou = byte2unit(data[5])
            candatatype = byte2unit(data[6])
            candatalen = byte2unit(data[11])

            canid = (byte2unit(data[7]) shl 24) +
                    (byte2unit(data[8]) shl 16) +
                    (byte2unit(data[9]) shl 8) +
                    (byte2unit(data[10]))
            candata = data.copyOfRange(12, 12 + candatalen)
            rxcandata(cankou, candatatype, canid, candatalen, candata)

        } else if (minglinwe == 0xb2) {
            //5.3 上位机向下位机查询CAN报文发送状态 0x32 0xB2
            //当上位机不知道当前CAN报文发送状态时，需要向下位机发送请求CAN报文发送状态的消息，
            //上位机根据此状态来显示发送报文是否成功。
            //示例：下位机发送给上位机CAN报文发送状态
            var staretcansendstae = byte2unit(data[6])
            var cankou = byte2unit(data[5])

            when (staretcansendstae) {
                0 -> {
                    LogUtils.e(TAG, "CAN${cankou} 发送数据成功")

                }
                5 -> {
                    println("CAN${cankou} 发送数据失败-----")
                }
                else -> {
                    println("CAN${cankou} 发送数据不知道  ${staretcansendstae}-----")
                }

            }
            usbcansenddatesate = staretcansendstae
        }

    }


    fun sendData2Serial(canData: ByteArray, userMainThread: Boolean = false) {
//        System.out.print("这里要调用串口发送数据=[")
//        for(i in candata){
//            System.out.print("${byte2unit(i).toString(16)} ")
//        }
//        System.out.println("]")
        when (canData.size) {
            in 0..21 -> {
                EventBusUtil.sendEvent(
                    Event(
                        EventCode.A,
                        LogMessage("$TAG 发送数据 serial ${toHexStr(canData)}", MessageLevel.Info)
                    )
                )

                if (userMainThread) {
                    mListener?.sendData2SerialMainThread(canData)
                } else {
                    mListener?.sendData2Serial(canData)
                }
            }

            else -> {
                val sendatatem: ByteArray = ByteArray(21)
                for (i in canData.indices) {
                    sendatatem[i] = canData[i]
                }

                EventBusUtil.sendEvent(
                    Event(
                        EventCode.A,
                        LogMessage("$TAG 发送数据 serial ${toHexStr(sendatatem)}", MessageLevel.Info)
                    )
                )

                if (userMainThread) {
                    mListener?.sendData2SerialMainThread(sendatatem)
                } else {
                    mListener?.sendData2Serial(sendatatem)
                }
            }
        }
    }

    fun SendDate2Can(
        cankou: Int = 1,
        canDataType: Int = 3,
        canId: Int = 1271,
        canData: ByteArray,
        userMainThread: Boolean = false
    ): Boolean {
        var fokstate: Boolean = false
        var senddatalen: Int = canData.size
        var sendatatem: ByteArray = ByteArray(senddatalen + 13)

        if (senddatalen <= 8) {

//            var sendatatem:ByteArray =ByteArray(senddatalen+13)
            //
            sendatatem[0] = 0x66.toByte()
            sendatatem[1] = 0xcc.toByte()
            sendatatem[2] = 0x00.toByte()
            sendatatem[3] = (9 + senddatalen).toByte()
            sendatatem[4] = 0x30.toByte()
            sendatatem[5] = cankou.toByte()
            sendatatem[6] = canDataType.toByte()
            sendatatem[7] = ((canId and 0xff000000.toInt()) shr 24).toByte()
            sendatatem[8] = ((canId and 0xff0000.toInt()) shr 16).toByte()
            sendatatem[9] = ((canId and 0xff00.toInt()) shr 8).toByte()
            sendatatem[10] = ((canId and 0xff.toInt())).toByte()
            sendatatem[11] = (senddatalen).toByte()

            if (senddatalen > 0) {
                for (indexx in 12 until 12 + senddatalen) {
                    sendatatem[indexx] = canData[indexx - 12]
                }
            }

            sendatatem[12 + senddatalen] = 0x00
            sendatatem[12 + senddatalen] = (checkCommandData(sendatatem, 0)).toByte()

//            Log.e(TAG, "SendDate2can: ${GsonUtils.toJson(sendatatem)}")

            LogUtils.e(TAG, "SendDate2can 发送数据 serial ${toHexStr(sendatatem)}")
            sendData2Serial(sendatatem, userMainThread = userMainThread)
            when (usbcansenddatesate) {
                0 -> {
                    println("发送数据成功-- $usbcansenddatesate")
                    fokstate = true
                }

                5 -> {
                    println("发送数据失败-- $usbcansenddatesate")
                }

                else -> {
                    println("发送数据未知-- $usbcansenddatesate")
                }
            }
        } else {
            println("发送的数据长度不对 $senddatalen")
        }
        return fokstate
    }


    fun dataPackageToQueue(data: ByteArray) {

        val aLen = databuffall.size
        val bLen = data.size
        val tembuf: ByteArray = ByteArray(aLen + bLen)


        System.arraycopy(databuffall, 0, tembuf, 0, aLen)
        System.arraycopy(data, 0, tembuf, aLen, bLen)



        dataBuff = tembuf
//        var ff = false
//        System.out.println("收到数据处理")
        if (dataBuff.size > 5) {
            var indexhead: Int = dataBuff.indexOf(0x66)
//            System.out.println("indexhead=$indexhead")
            if (indexhead >= 0) {
//            if (ff){
                if ((byte2unit(dataBuff[indexhead]) == 0x66) && (byte2unit(dataBuff[indexhead + 1]) == 0xcc)) { //数据长度足够最小指令长度， 起始标志+包长度+命令+校验 = 6
                    var packageSize: Int = dataBuff[indexhead + 3].toInt()   //起始标志 = 2，起始标志+包长度 = 4
                    if (packageSize > MaxDataLen) {
                        println("丢弃数据")
                        databuffall = ByteArray(0)
                    } else {
                        if (dataBuff.size >= (4 + packageSize))//#数据已全部收全   起始标志+包长度 = 4
                        {
                            var commandData: Int = byte2unit(dataBuff[4 + packageSize - 1])
                            if (commandData == checkCommandData(dataBuff, indexhead)) {
//                            System.out.println("数据长度=$packageSize  commanddata=${commandData}")
                                //数据接收成功
                                datacommytest(dataBuff)

                            } else {

                                for (i in dataBuff) {
                                    print("${byte2unit(i).toString(16)} ")
                                }
                                println(
                                    "数据校验不对=${
                                        checkCommandData(
                                            dataBuff,
                                            indexhead
                                        )
                                    }  commanddata=${commandData}"
                                )
                            }

                            //还有检擦有没有粘包数据
                            if (dataBuff.size > (packageSize + 4)) {
                                dataBuff = dataBuff.copyOfRange(packageSize + 4, dataBuff.size)
                                indexhead = dataBuff.indexOf(0x66)

                                println("粘包数据 indexhead=$indexhead")
                                if (indexhead >= 0) {
                                    dataPackageToQueue(dataBuff)

                                } else {
                                    println("有其他数据但是不对丢弃")
                                    databuffall = ByteArray(0)
                                }

                            } else {
                                databuffall = ByteArray(0)

                            }


                        } else {
                            println("数据没有接收完")
                            databuffall = dataBuff
                        }


                    }
                } else {
                    databuffall = ByteArray(0)
                    println(
                        "不是 0x 66 0x cc 0-1:  0:${byte2unit(dataBuff[indexhead])}  1:${
                            byte2unit(
                                dataBuff[indexhead + 1]
                            )
                        }"
                    )

                }


            } else {


                println("数据中没有头数据 size=${dataBuff.size}")
                databuffall = ByteArray(0)

            }
        } else {
            databuffall = ByteArray(0)
            println("数据长度不对 size=${dataBuff.size}")
        }
    }


}
