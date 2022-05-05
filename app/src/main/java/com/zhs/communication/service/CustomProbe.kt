package com.zhs.communication.service

import com.hoho.android.usbserial.driver.CdcAcmSerialDriver
import com.hoho.android.usbserial.driver.ProbeTable
import com.hoho.android.usbserial.driver.UsbSerialProber

/**
 * ================================================
 * 类名：com.zhs.communication.service
 * 时间：2022/4/28 16:41
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 * ================================================
 * @author Admin
 */
object CustomProbe {

    val customProbe: UsbSerialProber
        get() {
            val customTable = ProbeTable()
            customTable.addProduct(
                0x16d0,
                0x087e,
                CdcAcmSerialDriver::class.java
            )
            return UsbSerialProber(customTable)
        }
}