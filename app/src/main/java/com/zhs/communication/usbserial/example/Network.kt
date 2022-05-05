package com.zhs.communication.usbserial.example

import com.blankj.utilcode.util.LogUtils
import com.zhs.communication.usbserial.example.canopen.sdo.SdoClient
import com.zhs.communication.usbserial.example.canopen.sdo.SdoServer
import com.zhs.communication.utils.toHexStr

class Network : UsbCanSerial() {

    companion object {
        private const val TAG = "Network"
    }

    private var subscribers = mutableMapOf<Int, SdoClient>()
    private var subscribers_serv = mutableMapOf<Int, SdoServer>()


    fun addsubscriber(sdocli: SdoClient) {
        subscribers[sdocli.tx_cobidmy] = sdocli
    }

    fun getsubscriberclicli(tx_cobidmy: Int): SdoClient? {

        return subscribers[tx_cobidmy]!!
    }

    fun addsubscriberserver(sdoserv: SdoServer) {
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
                    sub.value.addResponse(canid, candata)
                }
            }
        }

        if (subscribers_serv.isNotEmpty()) {
            for (sub in subscribers_serv) {
                if (canid == sub.value.tx_cobidmy) {
                    sub.value.onRequest(canid, candata)
                }
            }
        }

    }

    fun sendMessage(canId: Int, data: ByteArray, userMainThread: Boolean = false){
        LogUtils.e(TAG, "sendMessage 发送数据 serial ${toHexStr(data)}")
        SendDate2Can(canId = canId, canData = data, userMainThread = userMainThread)
    }


}