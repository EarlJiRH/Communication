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

class MainActivity : BaseActivity(), View.OnClickListener {

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
     * ??????????????????
     *
     * @return ??????????????????
     */
    override fun getContentViewResId(): Int = R.layout.activity_main

    /**
     * ???????????????????????????
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

        // ???????????????
        //???????????????
        findViewById<Button>(R.id.saveDeviceIdBtn).setOnClickListener(this)
        //????????????
        findViewById<Button>(R.id.clearTestBtn).setOnClickListener(this)
        //?????????????????????
        findViewById<Button>(R.id.saveSuccessBtn).setOnClickListener(this)
        //??????
        findViewById<Button>(R.id.fillUpBtn).setOnClickListener(this)
        //??????
        findViewById<Button>(R.id.stateBtn).setOnClickListener(this)

        // ????????????
        //??????????????? ??????
        findViewById<ImageButton>(R.id.sendBtn).setOnClickListener(this)
        //?????????????????? ??????
        findViewById<ImageButton>(R.id.readBtn).setOnClickListener(this)

        //???????????????
        //????????????
        findViewById<Button>(R.id.startRunStepBtn).setOnClickListener(this)
        //????????????
        findViewById<Button>(R.id.stopRunStepBtn).setOnClickListener(this)
        //????????????
        findViewById<Button>(R.id.pauseStopRunBtn).setOnClickListener(this)
        //????????????
        findViewById<Button>(R.id.singleStepDebuggingBtn).setOnClickListener(this)

        XXPermissions.with(this)
            // ??????????????????
            .permission(Manifest.permission.READ_PHONE_STATE)
            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
            // ?????????????????????????????????????????????
            //.interceptor(new PermissionInterceptor())
            // ???????????????????????????????????????????????????
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
                mainStatus("??????Service")
                binService()
            }
            R.id.unbindService -> {
                mainStatus("??????Service")
                if (isBind) {
                    unbindService(conn)
                    isBind = false
                    mCanService = null
                }
            }
            R.id.con_can -> {
                mainStatus("??????can")
                mCanService?.connectCanSerial()
            }
            R.id.discon_can -> {
                mainStatus("????????????can")
                mCanService?.disconnect_can()
            }
            else -> {
                mainStatus("????????????,????????????")
                ToastUtils.showShort("????????????,????????????")
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

    private fun setMainStatusData(message: String, level: MessageLevel = MessageLevel.Debug) {
        setMainStatusData(LogMessage(message, level))
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
                    //???????????????
                }

                R.id.clearTestBtn -> {
                    //?????????????????????
                    mainStatus("?????????????????????")
                    mCanService?.apply {
                        canopenTest.setbaoziweizhi11(0)
                        canopenTest.baozijr2jrokok = false
                        mainStatus(canopenTest.getshowstrbaozisgate())
                    }
                }

                R.id.saveSuccessBtn -> {
                    //?????????????????????
                    mainStatus("????????????????????????")
                    mCanService?.apply {
                        canopenTest.baozilc2jrokok = true
                        mainStatus(canopenTest.getshowstrbaozisgate())
                    }
//
                }

                R.id.fillUpBtn -> {
                    //??????
                    mainStatus("??????????????????")
                    mCanService?.apply {
                        canopenTest.setbaoziweizhi11(1)
                        canopenTest.baozijr2jrokok = true
                        mainStatus(canopenTest.getshowstrbaozisgate())
                    }
                }

                R.id.stateBtn -> {
                    //??????
                    mCanService?.apply {
                        mainStatus(canopenTest.getshowstrbaozisgate())
                    }
                }

                R.id.sendBtn -> {
                    //??????????????? ??????
                    mainStatus("???????????????--------------")
                    mCanService?.apply {
                        val fok = canopenTest.downloadt(
                            spinnerServer.selectedItem as String,
                            spinnerIndex.selectedItem as String,
                            spinnerSubIndex.selectedItem as String,
                            spinnerByteArrayLength.selectedItem as String,
                            sendEdit.text.toString()
                        )
                        if (fok) {
                            mainStatus("??? ????????????")
                        } else {
                            mainStatus("??? ????????????")
                        }
                    }
                }

                R.id.readBtn -> {
                    //?????????????????? ??????
                    var fok = false
                    mCanService?.apply {
                        fok = canopenTest.uploadTest(
                            spinnerServer.selectedItem as String,
                            spinnerIndex.selectedItem as String,
                            spinnerSubIndex.selectedItem as String
                        )
                        if (fok) {
                            mainStatus("???????????????")
                        } else {
                            mainStatus("???????????????")
                        }
                    }
                }

                R.id.startRunStepBtn -> {
                    //????????????
                    val channel = spinnerChannel.selectedItem as String
                    mainStatus("??????=====${channel}")
                    val index = channel.substring(0, 1).toInt()
                    println("startRunStepBtn=====${index}")

                    mCanService?.apply {
                        if (canopenTest.myorder_step_saveth[index][0] == canopenTest.WORK_STEP1) {
                            mainStatus("????????????", resources.getColor(R.color.colorRedText))
                        } else {
                            val step = spinnerStep.selectedItem as String
                            //?????? 08
                            val intStep = step.substring(0, 2).toInt()
                            mainStatus("????????????=====${step}")
                            canopenTest.defsetrunstepmytt(
                                index,
                                intStep,
                                canopenTest.WORK_STEP1,
                                0,
                                0
                            )
                        }
                    }

                }

                R.id.stopRunStepBtn -> {
                    //????????????
                    val index =
                        (spinnerChannel.selectedItem as String).toString().substring(0, 1).toInt()
                    mCanService?.apply {
                        canopenTest.ordermianrun_suspend_f = true
                        canopenTest.stopchannelrun_mywaim(index)
                        canopenTest.stopchannelrun_mywaim(index, run_th = 1)
                        canopenTest.ordermianrun_suspend_f = false
                    }
                }

                R.id.pauseStopRunBtn -> {
                    //????????????
                    mCanService?.apply {
                        when (canopenTest.ordermianrun_suspend_f) {
                            true -> {
                                canopenTest.ordermianrun_suspend_f = false
                                mainStatus("????????????", resources.getColor(R.color.colorRedText))
                                (v as Button).text = "??????"
                            }

                            false -> {
                                canopenTest.ordermianrun_suspend_f = true
                                mainStatus("????????? ????????????", resources.getColor(R.color.colorRedText))
                                (v as Button).text = "??????"
                            }
                        }
                    }
                }

                R.id.singleStepDebuggingBtn -> {
                    //????????????
                    mCanService?.apply {
                        if (!canopenTest.zuhedongzuodanbutiaoshi) {
                            canopenTest.zuhedongzuodanbutiaoshi = true
                            mainStatus("?????????????????????????????????", resources.getColor(R.color.colorRedText))
                        } else {
                            canopenTest.zuhedongzuodanbutiaoshi = false
                            mainStatus("?????????????????????????????????", resources.getColor(R.color.colorRedText))
                        }
                    }

                }

                else -> {
                    mainStatus("???????????? ??????", resources.getColor(R.color.colorRedText))
                }
            }

        }
    }

}