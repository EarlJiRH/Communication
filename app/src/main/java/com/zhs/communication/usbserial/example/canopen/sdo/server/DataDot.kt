package com.zhs.communication.usbserial.example.canopen.sdo.server

/**
 * ================================================
 * 类名：com.zhs.communication.usbserial.example.canopen.sdo.server
 * 时间：2022/5/5 16:58
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 * ================================================
 * @author Admin
 */
data class DataDot(
    var index: Int,
    var subIndex: Int,
    var dataSize: Int,
    var data: Int,
    var callback: SdoServerCallback? = SdoServerCallback()
)
