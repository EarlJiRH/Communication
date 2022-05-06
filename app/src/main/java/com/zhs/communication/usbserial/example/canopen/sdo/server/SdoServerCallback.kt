package com.zhs.communication.usbserial.example.canopen.sdo.server

import com.blankj.utilcode.util.LogUtils

/**
 * ================================================
 * 类名：com.zhs.communication.usbserial.example.canopen.sdo.server
 * 时间：2022/5/5 16:59
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 * ================================================
 * @author Admin
 */
open class SdoServerCallback {
    companion object {
        private const val TAG = "SdoServerCallback"
    }

    open fun setData(dataDot: DataDot) {
        LogUtils.i(
            TAG, """setData: index=${dataDot.index}
                             subIndex=${dataDot.subIndex}
                             dataSize=${dataDot.dataSize}
                             data=${dataDot.data}"""
        )
    }
}