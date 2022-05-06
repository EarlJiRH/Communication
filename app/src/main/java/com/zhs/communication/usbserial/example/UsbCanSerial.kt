package com.zhs.communication.usbserial.example

import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.zhs.communication.eventbus.Event
import com.zhs.communication.eventbus.EventBusUtil
import com.zhs.communication.eventbus.EventCode
import com.zhs.communication.eventbus.LogMessage
import com.zhs.communication.utils.byte2unit
import com.zhs.communication.utils.printByteArray
import com.zhs.communication.utils.toHexStr

open class UsbCanSerial {

    companion object {
        const val TAG = "UsbCanSerial"
        const val MaxDataLen: Int = 32
    }


    var canDataNum: Int = 0

    private var dataBuff: ByteArray = ByteArray(0)
    private var dataBuffAll: ByteArray = ByteArray(0)

    //未知 7   失败5  成功  0  状态改变才会发给上位机   can通信时要
    private var usbCanSendDateState = 0
    // 注意每条指令都是21字节，不足补齐就行了

    //CAN数据发送情况的监听
    var mSendCanListener: SendCanListener? = null


    fun checkCommandData(data: ByteArray, indexHead: Int): Int {
        var sum: Int = 0
        for (i in (indexHead + 2)..(byte2unit(data[indexHead + 3]) + 2)) {
            sum += byte2unit(data[i])
        }
        return sum and 0xff
    }

    /**
     * @param canPort CAN口
     * @param canDataType CAN数据类型
     * @param canId CANId
     * @param canDataLength CAN数据长度
     * @param canData CAN数据*/
    open fun canData2handle(
        canPort: Int,
        canDataType: Int,
        canId: Int,
        canDataLength: Int,
        canData: ByteArray
    ) {
        //TODO 这里要子类去实现处理将底盘数据进行回传
    }


    fun sendCanData(
        canPort: Int,
        canDataType: Int,
        canId: Int,
        canDataLength: Int,
        canData: ByteArray
    ) {
        /*
        * 接收到数据 can
        *
        *
        * */
        canDataNum += 1

//        System.out.print("${rxcandatanum}  ${getCurrentDate()} 接收到数据 CAN${cankou} 类型 ${candatatype} id=${canid.toString(16)} len=${candatalen} data=[")
//        for(i in candata){
//            System.out.print("${byte2unit(i).toString(16)} ")
//        }
//        System.out.println("]")

        canData2handle(canPort, canDataType, canId, canDataLength, canData)
    }


    fun sendcheckstatecanstatemingling() {
        val candata = ByteArray(21)
        candata[0] = 0x66
        candata[1] = 0xcc.toByte()
        candata[2] = 0x00
        candata[3] = 0x02
        candata[4] = 0x32
        candata[5] = 0x34
        mSendCanListener?.sendData2Serial(candata)
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
            sendCanData(cankou, candatatype, canid, candatalen, candata)

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
            usbCanSendDateState = staretcansendstae
        }

    }

    /**通过串口发送CAN数据 实际调用子类的实现*/
    fun sendData2Serial(canData: ByteArray, userMainThread: Boolean = false) {
//        System.out.print("这里要调用串口发送数据=[")
//        for(i in candata){
//            System.out.print("${byte2unit(i).toString(16)} ")
//        }
//        System.out.println("]")
        val sendDataArray: ByteArray
        when (canData.size) {
            in 0..21 -> {
                //数据长度不足21位 空位补齐
                sendDataArray = ByteArray(21)
                for (index in canData.indices) {
                    sendDataArray[index] = canData[index]
                }
                if (userMainThread) {
                    mSendCanListener?.sendData2SerialMainThread(sendDataArray)
                } else {
                    mSendCanListener?.sendData2Serial(sendDataArray)
                }
            }

            else -> {
                sendDataArray = canData
                if (userMainThread) {
                    mSendCanListener?.sendData2SerialMainThread(canData)
                } else {
                    mSendCanListener?.sendData2Serial(canData)
                }
            }
        }

        EventBusUtil.sendEvent(
            Event(
                EventCode.A,
                LogMessage(toHexStr(sendDataArray))
            )
        )
    }

    fun sendDate2Can(
        canPort: Int = 1,
        canDataType: Int = 3,
        canId: Int = 1271,
        canData: ByteArray,
        userMainThread: Boolean = false
    ): Boolean {
        var fokState: Boolean = false
        val sendDataLength: Int = canData.size
        val sendDataArray: ByteArray = ByteArray(sendDataLength + 13)
        return when (sendDataLength) {
            in 0..8 -> {//数据长度[0,8]
                //起始
                sendDataArray[0] = 0x66.toByte()
                sendDataArray[1] = 0xcc.toByte()

                //长度
                sendDataArray[2] = 0x00.toByte()
                sendDataArray[3] = (9 + sendDataLength).toByte()

                //命令 上位机下发指令
                sendDataArray[4] = 0x30.toByte()

                //CAN口
                sendDataArray[5] = canPort.toByte()

                //CAN 帧类型标识 0x03
                sendDataArray[6] = canDataType.toByte()

                //CAN帧Id
                sendDataArray[7] = ((canId and 0xff000000.toInt()) shr 24).toByte()
                sendDataArray[8] = ((canId and 0xff0000) shr 16).toByte()
                sendDataArray[9] = ((canId and 0xff00) shr 8).toByte()
                sendDataArray[10] = ((canId and 0xff)).toByte()

                //数据长度DLC 8
                sendDataArray[11] = (sendDataLength).toByte()

                //向下位机发送的CANOpen数据
                // sendDataArray[12] = Utils.int2byte(command)
                // sendDataArray[13] = Utils.int2byte(index and 0xff)
                // sendDataArray[14] = Utils.int2byte((index and 0xff00) shr 8)
                // sendDataArray[15] = Utils.int2byte(subIndex)
                if (sendDataLength > 0) {
                    for (index in 12 until 12 + sendDataLength) {
                        sendDataArray[index] = canData[index - 12]
                    }
                }
                //校验和 sendDataArray[20]
                sendDataArray[12 + sendDataLength] =
                    (checkCommandData(sendDataArray, 0)).toByte()

                LogUtils.e(TAG, "SendDate2can: ${toHexStr(sendDataArray)}")
                sendData2Serial(sendDataArray, userMainThread = userMainThread)
                when (usbCanSendDateState) {
                    0 -> {
                        println("发送数据成功-- $usbCanSendDateState")
                        printByteArray(
                            "发送数据到can总线--canId = [${canId.toString(16)}]=",
                            canData
                        )
                        fokState = true
                    }

                    5 -> {
                        println("发送数据失败-- $usbCanSendDateState")
                    }
                    else -> {
                        println("发送数据未知-- $usbCanSendDateState")
                    }
                }

                fokState
            }
            else -> {
                println("发送的数据长度不对 $sendDataLength")
                fokState
            }
        }
    }


    fun dataPackageToQueue(data: ByteArray) {

        val aLen = dataBuffAll.size
        val bLen = data.size
        val tembuf: ByteArray = ByteArray(aLen + bLen)


        System.arraycopy(dataBuffAll, 0, tembuf, 0, aLen)
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
                        dataBuffAll = ByteArray(0)
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
                                    dataBuffAll = ByteArray(0)
                                }

                            } else {
                                dataBuffAll = ByteArray(0)

                            }


                        } else {
                            println("数据没有接收完")
                            dataBuffAll = dataBuff
                        }


                    }
                } else {
                    dataBuffAll = ByteArray(0)
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
                dataBuffAll = ByteArray(0)

            }
        } else {
            dataBuffAll = ByteArray(0)
            println("数据长度不对 size=${dataBuff.size}")
        }
    }


}
