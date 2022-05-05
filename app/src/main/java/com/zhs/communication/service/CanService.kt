package com.zhs.communication.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.*
import android.widget.Toast
import com.blankj.utilcode.util.LogUtils
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import com.zhs.communication.BuildConfig
import com.zhs.communication.controller.foodcartmqtt.FoodCarMqtt
import com.zhs.communication.eventbus.*
import com.zhs.communication.lib.MqttManager
import com.zhs.communication.usbserial.example.CANOpenTest
import com.zhs.communication.usbserial.example.SendCanListener
import com.zhs.communication.utils.toHexStr
import java.io.IOException

/**
 * ================================================
 * 类名：com.zhs.communication.service
 * 时间：2022/5/5 13:37
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 * ================================================
 * @author Admin
 */
class CanService : Service(), SerialInputOutputManager.Listener, SendCanListener {
    companion object {
        private const val TAG = "CanService"
    }

    //通过内部类的方式获取当前Service对象
    inner class CanBinder : Binder() {
        val service: CanService
            get() = this@CanService
    }

    //通过binder实现调用者client与Service之间的通信
    private val binder = CanBinder()

    override fun onBind(intent: Intent?): IBinder {
        LogUtils.i(TAG, "$TAG - onBind - Thread = " + Thread.currentThread().name)
        return binder
    }

    private val INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB"
    private val WRITE_WAIT_MILLIS = 2000
    private val READ_WAIT_MILLIS = 2000

    private var deviceId = 0
    private var portNum: Int = 0
    private var baudRate: Int = 0
    private var withIoManager = false

    private var broadcastReceiver: BroadcastReceiver? = null
    private var mainLooper: Handler? = null
//    private var receiveText: TextView? = null
//    private var controlLines: MyService.ControlLines? = null

    private var usbIoManager: SerialInputOutputManager? = null
    private var usbSerialPort: UsbSerialPort? = null
    private var usbPermission = UsbPermission.Unknown
    private var connected = false

    private var receiveallnum: Int = 0
    private var receiveallnumallbytes: Long = 0

    var canopenTest = CANOpenTest()

    var usbCanSerial = canopenTest.getNetWork()

    var mqtt1m = FoodCarMqtt()

    private fun status(str: String, level: MessageLevel = MessageLevel.Debug) {
//        LogUtils.e(TAG,"status=${str}  color = ${intcolor.toString(16)}")
        EventBusUtil.sendEvent(
            Event(
                EventCode.A,
                LogMessage(str, level)
            )
        )
    }

    private fun initCanMqtt() {
        LogUtils.e(TAG, "create  mm ")
//        controlLines = ControlLines()

//        usbCanSerial.mListener = this


//        canopenTest.mqttrunttapt = mqtt1m
//
//        SdoServerCallbackImpl.canopentestmytt = canopenTest
//
//        mqtt1m.canOpenTest = canopenTest

        //mqtt1m.initMqtt(this)

        Thread {
            kotlin.run {
                canopenTest.startRunOderMain()
            }

        }.start()

//        Thread {
//            kotlin.run {
//                mqtt1m.runTest()
//            }
//        }.start()

    }


    init {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                LogUtils.e(
                    TAG,
                    "init mm INTENT_ACTION_GRANT_USB = $INTENT_ACTION_GRANT_USB intent.action=${intent.action}"
                )
                if (INTENT_ACTION_GRANT_USB == intent.action) {
                    usbPermission = if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED,
                            false
                        )
                    ) UsbPermission.Granted else UsbPermission.Denied
                    LogUtils.e(TAG, "init mm ")
                }
            }
        }
        mainLooper = Handler(Looper.getMainLooper())
    }


    /***/
    private fun getUsbSerial() {
        val usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        val usbDefaultProbe = UsbSerialProber.getDefaultProber()
        val usbCustomProbe: UsbSerialProber = CustomProbe.customProbe
        LogUtils.e(TAG, "start ======================================")
        usbManager.deviceList.values.forEach { device ->
            var driver = usbDefaultProbe.probeDevice(device)
            if (driver == null) {
                driver = usbCustomProbe.probeDevice(device)

            }
            driver?.apply {
                this.ports.indices.forEach { port ->
                    LogUtils.e(
                        TAG,
                        "------${
                            driver.javaClass.simpleName.replace(
                                "SerialDriver",
                                ""
                            )
                        }"
                    )
                    LogUtils.e(
                        TAG,
                        "------deviceId=${device.deviceId} prot=${port} baudrate=${1500000} ,withiomanager=${true}"
                    )
                    deviceId = device.deviceId
                    portNum = port
                    baudRate = 1500000
                    withIoManager = true
                }
            }
        }
        LogUtils.e(TAG, "======================================")
    }


    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            //获得刚才发送的Message对象，然后在这里进行UI操作
            val t = msg.data["datainfo"] as ByteArray
            LogUtils.e(TAG, "mHandler 发送数据 serial ${toHexStr(t)}")
            sendByteArray(t)
        }
    }

    /*
     * Serial
     */
    private fun connect_can() {
        status("connect ---")
        canopenTest.MB_printf("正在连接 usb serial")
        var device: UsbDevice? = null
        val usbManager = applicationContext.getSystemService(Context.USB_SERVICE) as UsbManager
        for (v in usbManager.deviceList.values) {
            if (v.deviceId == deviceId) device = v

//            LogUtils.e(TAG,"v=$v ,")
        }
        if (device == null) {
            status("connection failed: device not found")
            canopenTest.MB_printf("usb serial connection failed: device not found")
            return
        }
        var driver = UsbSerialProber.getDefaultProber().probeDevice(device)
        if (driver == null) {
            driver = CustomProbe.customProbe.probeDevice(device)
        }
        if (driver == null) {
            status("connection failed: no driver for device")
            canopenTest.MB_printf("usb serial connection failed: no driver for device")
            return
        }
        if (driver.ports.size < portNum) {
            status("connection failed: not enough ports at device")
            canopenTest.MB_printf("usb serial connection failed: not enough ports at device")
            return
        }
        usbSerialPort = driver.ports[portNum]
        val usbConnection = usbManager.openDevice(driver.device)
        if (usbConnection == null && usbPermission == UsbPermission.Unknown && !usbManager.hasPermission(
                driver.device
            )
        ) {
            usbPermission = UsbPermission.Requested
            val usbPermissionIntent = PendingIntent.getBroadcast(
                applicationContext,
                0,
                Intent(INTENT_ACTION_GRANT_USB),
                0
            )
            usbManager.requestPermission(driver.device, usbPermissionIntent)
            return
        }
        if (usbConnection == null) {
            if (!usbManager.hasPermission(driver.device)) status("connection failed: permission denied") else status(
                "connection failed: open failed"
            )
            return
        }
        try {
            usbSerialPort?.open(usbConnection)
            usbSerialPort?.setParameters(baudRate, 8, 1, UsbSerialPort.PARITY_NONE)
            if (withIoManager) {
                usbIoManager = SerialInputOutputManager(usbSerialPort, this)
                usbIoManager!!.start()
            }
            status("connected.")
            canopenTest.MB_printf("usb serial connection USB CAN 连接成功")
            connected = true
            //controlLines!!.start()
        } catch (e: Exception) {
            status("connection failed: " + e.message)
            disconnect_can()
            canopenTest.MB_printf("usb serial connection USB CAN 连接失败")
        }
    }


    fun disconnect_can() {
        connected = false
        //controlLines!!.stop()
        if (usbIoManager != null) {
            usbIoManager!!.listener = null
            usbIoManager!!.stop()
        }
        usbIoManager = null
        try {
            usbSerialPort!!.close()
        } catch (ignored: IOException) {

        }
        usbSerialPort = null
        receiveallnum = 0
        receiveallnumallbytes = 0
    }


    private fun isMainThread(): Boolean {
        return Looper.getMainLooper() == Looper.myLooper()
    }


    fun sendByteArray(data: ByteArray) {
//        LogUtils.e(TAG, "发送数据byte数据 ${GsonUtils.toJson(data)}")
        //LogUtils.e(TAG, "发送数据 serial ${toHexStr(data)}")

//        EventBusUtil.sendEvent(
//            Event(
//                EventCode.A,
//                LogMessage("发送数据 serial ${toHexStr(data)}", MessageLevel.Info)
//            )
//        )
        if (!connected) {
            Toast.makeText(applicationContext, "can通信未连接", Toast.LENGTH_SHORT).show()
            return
        }

        usbSerialPort!!.write(data, WRITE_WAIT_MILLIS)
    }


    /**连接CAN通信串口*/
    fun connectCanSerial() {
        getUsbSerial()
        if (!connected) {
            connect_can()
        }
    }

    override fun onCreate() {
        super.onCreate()
        LogUtils.e(TAG, "$TAG - onCreate - Thread = " + Thread.currentThread().name)
        applicationContext.registerReceiver(
            broadcastReceiver,
            IntentFilter(INTENT_ACTION_GRANT_USB)
        )
        connectCanSerial()
        initCanMqtt()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtils.i(
            TAG,
            "$TAG - onStartCommand - startId = $startId, Thread = " + Thread.currentThread().name
        )
        return START_NOT_STICKY
    }


    override fun onUnbind(intent: Intent): Boolean {
        LogUtils.i(TAG, "$TAG - onUnbind - from = " + intent.getStringExtra("from"))
        return false
    }

    override fun onDestroy() {
        LogUtils.i(TAG, "$TAG - onDestroy - Thread = " + Thread.currentThread().name)
        canopenTest.stopRunThread()
//        mqtt1m.runff = false
//        mqtt1m.disconnectmqtt()
        MqttManager.getInstance().close()

        if (connected) {
            status("disconnected")
            disconnect_can()
        }
        applicationContext.unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    /**
     * Called when new incoming data is available.
     */
    override fun onNewData(data: ByteArray?) {
        data?.let { usbCanSerial.dataPackageToQueue(it) }
    }

    /**
     * Called when [SerialInputOutputManager.run] aborts due to an error.
     */
    override fun onRunError(e: Exception) {
        mainLooper?.post {
            status("connection lost: " + e.message)
            disconnect_can()
        }
    }

    /**
     * Called when new incoming data is 发送数据.
     */
    override fun sendData2Serial(data: ByteArray?) {
        if (data == null) {
            return
        }
        LogUtils.e(TAG, "$TAG sendData2Serial 发送数据 serial ${toHexStr(data)}")
        sendByteArray(data)
    }

    /**发送CAN数据到主线程中*/
    override fun sendData2SerialMainThread(data: ByteArray?) {
        if (data == null) {
            return
        }
        if (!isMainThread()) {
//            LogUtils.e(TAG, "不是主线程  ")
            val msg = Message()
            val bundle = Bundle()
            bundle.putByteArray("datainfo", data)
            msg.data = bundle
            LogUtils.e(TAG, "$TAG sendData2SerialMainThread 发送数据到 mHandler ${toHexStr(data)}")
            mHandler.sendMessage(msg)
        } else {
            LogUtils.e(TAG, "$TAG sendData2SerialMainThread 发送数据 serial ${toHexStr(data)}")
            sendByteArray(data)
        }
    }
}