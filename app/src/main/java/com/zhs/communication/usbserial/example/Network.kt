package com.zhs.communication.usbserial.example

import com.blankj.utilcode.util.LogUtils
import com.zhs.communication.usbserial.example.canopen.sdo.clientmy
import com.zhs.communication.usbserial.example.canopen.sdo.servermy
import com.zhs.communication.utils.toHexStr

class Network : UsbCanSerial() {

    private var subscribers = mutableMapOf<Int, clientmy>()
    private var subscribers_serv = mutableMapOf<Int, servermy>()


    fun addsubscriber(sdocli: clientmy) {
        subscribers[sdocli.tx_cobidmy] = sdocli
    }

    fun getsubscriberclicli(tx_cobidmy: Int): clientmy? {

        return subscribers[tx_cobidmy]!!
    }

    fun addsubscriberserver(sdoserv: servermy) {
        subscribers_serv[sdoserv.tx_cobidmy] = sdoserv
    }

    override fun canData2handler(
        cankou: Int,
        candatatype: Int,
        canid: Int,
        candatalen: Int,
        candata: ByteArray
    ) {
//        if (candatalen==1) {
//            System.out.print(
//                "Networkmy ${rxcandatanum}  ${getCurrentDate()} 接收到数据 CAN${cankou}" +
//                        " 类型 ${candatatype} id=${canid.toString(16)} len=${candatalen} data=["
//            )
//            for (i in candata) {
//                System.out.print("${bytemy2uint8(i).toString(16)} ")
//            }
//            System.out.println("]")
//        }

        if (subscribers.isNotEmpty()) {
            for (sub in subscribers) {
                if (canid == sub.value.tx_cobidmy) {
                    sub.value.on_response(canid, candata)
                }
            }
        }
        if (subscribers_serv.isNotEmpty()) {
            for (sub in subscribers_serv) {
                if (canid == sub.value.tx_cobidmy) {
                    sub.value.on_request(canid, candata)
                }
            }
        }

    }


    companion object {
        private const val TAG = "Network"
    }


    fun sendMessage(canid: Int, data: ByteArray, usemanthread: Boolean = false): Boolean {

//        System.out.print("这里要发送数据 ${canid.toString(16)}  data=")
//        printbaytearray(data)
        LogUtils.e(TAG, "sendMessage 发送数据 serial ${toHexStr(data)}")
        SendDate2Can(canId = canid, canData = data, userMainThread = usemanthread)
        return true
    }
}