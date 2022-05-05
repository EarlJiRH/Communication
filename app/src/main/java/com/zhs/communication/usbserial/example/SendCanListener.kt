package com.zhs.communication.usbserial.example

/**
 * ================================================
 * 类名：com.zhs.communication.usbserial.example.SendCanListener
 * 时间：2022/4/26 11:35
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 * ================================================
 * @author Admin
 */
interface SendCanListener {
    /**
     * Called when new incoming data is 发送数据.
     */
    fun sendData2Serial(data: ByteArray?)

    /**
     * Called when [SerialInputOutputManager.run] aborts due to an error.
     */
//        fun onRunError(e: Exception?)
    /**发送CAN数据到主线程中*/
    fun sendData2SerialMainThread(data: ByteArray?)
}
