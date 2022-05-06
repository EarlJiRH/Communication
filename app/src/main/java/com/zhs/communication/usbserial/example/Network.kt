package com.zhs.communication.usbserial.example

import com.blankj.utilcode.util.LogUtils
import com.zhs.communication.usbserial.example.canopen.sdo.client.SdoClient
import com.zhs.communication.usbserial.example.canopen.sdo.server.SdoServer
import com.zhs.communication.utils.toHexStr

class Network : UsbCanSerial() {

    companion object {
        private const val TAG = "Network"
    }

    private var subscriberClients = mutableMapOf<Int, SdoClient>()
    private var subscriberServers = mutableMapOf<Int, SdoServer>()


    fun addSubscriberClient(sdoClient: SdoClient) {
        subscriberClients[sdoClient.respond] = sdoClient
    }

    fun getSubscriberClient(respond: Int): SdoClient? {
        return subscriberClients[respond]!!
    }

    fun addSubscriberServer(sdoServer: SdoServer) {
        subscriberServers[sdoServer.respond] = sdoServer
    }

    override fun canData2handle(
        canPort: Int,
        canDataType: Int,
        canId: Int,
        canDataLength: Int,
        canData: ByteArray
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
        subscriberClients.forEach { clientMap ->
            if (canId ==clientMap.value.respond){
                clientMap.value.addResponse(canId, canData)
            }
        }

//        if (subscriberClients.isNotEmpty()) {
//            for (sub in subscriberClients) {
//                if (canid == sub.value.respond) {
//                    sub.value.addResponse(canid, candata)
//                }
//            }
//        }
//
//        if (subscriberServers.isNotEmpty()) {
//            for (sub in subscriberServers) {
//                if (canid == sub.value.respond) {
//                    sub.value.onRequest(canid, candata)
//                }
//            }
//        }
        subscriberServers.forEach { serverMap ->
            if (canId ==serverMap.value.respond){
                serverMap.value.onRequest(canId, canData)
            }
        }
    }

    fun sendMessage(canId: Int, data: ByteArray, userMainThread: Boolean = false) {
        LogUtils.e(TAG, "sendMessage 发送数据 serial ${toHexStr(data)}")
        sendDate2Can(canId = canId, canData = data, userMainThread = userMainThread)
    }


}