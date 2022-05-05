package com.zhs.communication

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PhoneUtils
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.zhs.communication.base.BaseActivity
import com.zhs.communication.eventbus.Event
import com.zhs.communication.eventbus.EventCode
import com.zhs.communication.eventbus.LogMessage
import com.zhs.communication.eventbus.MessageLevel
import com.zhs.communication.service.CanService
import com.zhs.communication.utils.ToastUtils
import com.zhs.communication.utils.getRandomNumber

class MainActivity : BaseActivity(), View.OnClickListener  {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var receiveText: TextView

    private lateinit var spinnerServer: Spinner
    private lateinit var spinnerIndex: Spinner
    private lateinit var spinnerSubIndex: Spinner
    private lateinit var spinnerByteArrayLength: Spinner
    private lateinit var spinnerChannel: Spinner
    private lateinit var spinnerStep: Spinner

    private lateinit var deviceIdEdit: EditText
    private lateinit var sendEdit: EditText

    private var mCanService: CanService? = null
    private var isBind = false

    private var conn = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            isBind = true
            val myBinder = p1 as CanService.CanBinder
            mCanService = myBinder.service
            LogUtils.i(TAG, "$TAG - onServiceConnected")
            val num = getRandomNumber()
            LogUtils.i(TAG, "$TAG - getRandomNumber = $num")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBind = false
            LogUtils.i(TAG, "$TAG - onServiceDisconnected")
        }
    }

    /**
     * 获取资源文件
     *
     * @return 资源文件地址
     */
    override fun getContentViewResId(): Int = R.layout.activity_main

    /**
     * 初始化页面数据操作
     *
     * @param savedInstanceState Bundle
     */
    override fun init(savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        receiveText = findViewById(R.id.receiveText)
        receiveText.apply {
            setTextColor(resources.getColor(R.color.colorReceiveText))
            movementMethod = ScrollingMovementMethod.getInstance()
        }

        spinnerServer = findViewById(R.id.spinnerServer)
        spinnerIndex = findViewById(R.id.spinnerIndex)
        spinnerSubIndex = findViewById(R.id.spinnerSubIndex)
        spinnerByteArrayLength = findViewById(R.id.spinnerByteArrayLength)
        spinnerChannel = findViewById(R.id.spinnerChannel)
        spinnerStep = findViewById(R.id.spinnerStep)

        deviceIdEdit = findViewById(R.id.deviceIdEdit)


        sendEdit = findViewById(R.id.sendEdit)

        // 第一排按钮
        //保存设备号
        findViewById<Button>(R.id.saveDeviceIdBtn).setOnClickListener(this)
        //清空数据
        findViewById<Button>(R.id.clearTestBtn).setOnClickListener(this)
        //设置为成功状态
        findViewById<Button>(R.id.saveSuccessBtn).setOnClickListener(this)
        //加满
        findViewById<Button>(R.id.fillUpBtn).setOnClickListener(this)
        //状态
        findViewById<Button>(R.id.stateBtn).setOnClickListener(this)

        // 读写按钮
        //发送数据包 写入
        findViewById<ImageButton>(R.id.sendBtn).setOnClickListener(this)
        //读取底盘数据 读取
        findViewById<ImageButton>(R.id.readBtn).setOnClickListener(this)

        //最底部按钮
        //开始控制
        findViewById<Button>(R.id.startRunStepBtn).setOnClickListener(this)
        //停止控制
        findViewById<Button>(R.id.stopRunStepBtn).setOnClickListener(this)
        //暂停调试
        findViewById<Button>(R.id.pauseStopRunBtn).setOnClickListener(this)
        //单步调试
        findViewById<Button>(R.id.singleStepDebuggingBtn).setOnClickListener(this)

        XXPermissions.with(this)
            // 申请单个权限
            .permission(Manifest.permission.READ_PHONE_STATE)
            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
            // 设置权限请求拦截器（局部设置）
            //.interceptor(new PermissionInterceptor())
            // 设置不触发错误检测机制（局部设置）
            //.unchecked()
            .request { _, all ->
                if (all) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_PHONE_STATE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return@request
                    }
                    deviceIdEdit.setText(PhoneUtils.getDeviceId())
                }
            }
    }

    private fun binService() {
        val intent = Intent(this, CanService::class.java)
        bindService(intent, conn, Context.BIND_AUTO_CREATE)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cleartxt -> {
                receiveText.text = ""
            }
            R.id.bindService -> {
                mainStatus("绑定Service")
                binService()
            }
            R.id.unbindService -> {
                mainStatus("解绑Service")
                if (isBind) {
                    unbindService(conn)
                    isBind = false
                    mCanService = null
                }
            }
            R.id.con_can -> {
                mainStatus("连接can")
                mCanService?.connectCanSerial()
            }
            R.id.discon_can -> {
                mainStatus("断开连接can")
                mCanService?.disconnect_can()
            }
            else -> {
                mainStatus("无关功能,暂不开放")
                ToastUtils.showShort("无关功能,暂不开放")
            }
        }
        return true
    }

    override fun isRegisterEventBus() = true

    override fun receiveEvent(event: Event<Any>) {
        super.receiveEvent(event)
        when (event.code) {
            EventCode.A -> {
                val data: LogMessage = event.data as LogMessage
                setMainStatusData(data)
            }
        }
    }
    private fun setMainStatusData(message: String) {
        setMainStatusData(LogMessage(message))
    }

    private fun setMainStatusData(message: LogMessage) {
        message.apply {
            val colorRes = when (level) {
                MessageLevel.Verbose -> {
                    resources.getColor(R.color.colorVerbose)
                }
                MessageLevel.Debug -> {
                    resources.getColor(R.color.colorDebug)
                }
                MessageLevel.Info -> {
                    resources.getColor(R.color.colorInfo)
                }
                MessageLevel.Warn -> {
                    resources.getColor(R.color.colorWarn)
                }
                MessageLevel.Error -> {
                    resources.getColor(R.color.colorError)
                }

                MessageLevel.Assert -> {
                    resources.getColor(R.color.colorAssert)
                }
                else -> {
                    resources.getColor(R.color.colorDebug)
                }
            }
            message.message?.let { mainStatus(it, colorRes) }
        }

    }

    private fun mainStatus(
        str: String,
        intColor: Int = resources.getColor(R.color.colorStatusText)
    ) {
        val spn = SpannableStringBuilder("$str\n").apply {
            setSpan(
                ForegroundColorSpan(intColor),
                0,
                length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        receiveText.append(spn)
    }

    override fun onClick(v: View?) {
        if (v == null || mCanService == null || !isBind) {
            return
        }
        v.apply {
            when (v.id) {
                R.id.saveDeviceIdBtn -> {
                    //保存设备号
                }

                R.id.clearTestBtn -> {
                    //清空不可买数据
                    mainStatus("清空不可买数据")
                    mCanService?.apply {
                        canopenttt.setbaoziweizhi11(0)
                        canopenttt.baozijr2jrokok = false
                        mainStatus(canopenttt.getshowstrbaozisgate())
                    }
                }

                R.id.saveSuccessBtn -> {
                    //设置为成功状态
                    mainStatus("设置运输完可加热")
                    mCanService?.apply {
                        canopenttt.baozilc2jrokok = true
                        mainStatus(canopenttt.getshowstrbaozisgate())
                    }
//
                }

                R.id.fillUpBtn -> {
                    //加满
                    mainStatus("设置加满可买")
                    mCanService?.apply {
                        canopenttt.setbaoziweizhi11(1)
                        canopenttt.baozijr2jrokok = true
                        mainStatus(canopenttt.getshowstrbaozisgate())
                    }
                }

                R.id.stateBtn -> {
                    //状态
                    mCanService?.apply {
                        mainStatus(canopenttt.getshowstrbaozisgate())
                    }
                }

                R.id.sendBtn -> {
                    //发送数据包 写入
                    mainStatus("点击写命令--------------")
                    mCanService?.apply {
                        val fok = canopenttt.downloadt(
                            spinnerServer.selectedItem as String,
                            spinnerIndex.selectedItem as String,
                            spinnerSubIndex.selectedItem as String,
                            spinnerByteArrayLength.selectedItem as String,
                            sendEdit.text.toString()
                        )
                        if (fok) {
                            mainStatus("写 数据成功")
                        } else {
                            mainStatus("写 数据失败")
                        }
                    }
                }

                R.id.readBtn -> {
                    //读取底盘数据 读取
                    var fok = false
                    mCanService?.apply {
                        fok = canopenttt.uploadTest(
                            spinnerServer.selectedItem as String,
                            spinnerIndex.selectedItem as String,
                            spinnerSubIndex.selectedItem as String
                        )
                        if (fok) {
                            mainStatus("读数据成功")
                        } else {
                            mainStatus("读数据失败")
                        }
                    }
                }

                R.id.startRunStepBtn -> {
                    //开始控制
                    val channel = spinnerChannel.selectedItem as String
                    mainStatus("通道=====${channel}")
                    val index = channel.substring(0, 1).toInt()
                    println("startRunStepBtn=====${index}")

                    mCanService?.apply {
                        if (canopenttt.myorder_step_saveth[index][0] == canopenttt.WORK_STEP1) {
                            mainStatus("正在控制", resources.getColor(R.color.colorRedText))
                        } else {
                            val step = spinnerStep.selectedItem as String
                            //操作 08
                            val intStep = step.substring(0, 2).toInt()
                            mainStatus("操作流程=====${step}")
                            canopenttt.defsetrunstepmytt(
                                index,
                                intStep,
                                canopenttt.WORK_STEP1,
                                0,
                                0
                            )
                        }
                    }

                }

                R.id.stopRunStepBtn -> {
                    //停止控制
                    val index =
                        (spinnerChannel.selectedItem as String).toString().substring(0, 1).toInt()
                    mCanService?.apply {
                        canopenttt.ordermianrun_suspend_f = true
                        canopenttt.stopchannelrun_mywaim(index)
                        canopenttt.stopchannelrun_mywaim(index, run_th = 1)
                        canopenttt.ordermianrun_suspend_f = false
                    }
                }

                R.id.pauseStopRunBtn -> {
                    //暂停调试
                    mCanService?.apply {
                        when (canopenttt.ordermianrun_suspend_f) {
                            true -> {
                                canopenttt.ordermianrun_suspend_f = false
                                mainStatus("继续控制", resources.getColor(R.color.colorRedText))
                                (v as Button).text = "暂停"
                            }

                            false -> {
                                canopenttt.ordermianrun_suspend_f = true
                                mainStatus("已暂停 继续控制", resources.getColor(R.color.colorRedText))
                                (v as Button).text = "继续"
                            }
                        }
                    }
                }

                R.id.singleStepDebuggingBtn -> {
                    //单步调试
                    mCanService?.apply {
                        if (!canopenttt.zuhedongzuodanbutiaoshi) {
                            canopenttt.zuhedongzuodanbutiaoshi = true
                            mainStatus("组合动作单步调试已打开", resources.getColor(R.color.colorRedText))
                        } else {
                            canopenttt.zuhedongzuodanbutiaoshi = false
                            mainStatus("组合动作单步调试已关闭", resources.getColor(R.color.colorRedText))
                        }
                    }

                }

                else -> {
                    mainStatus("没有运行 控制", resources.getColor(R.color.colorRedText))
                }
            }

        }
    }

}