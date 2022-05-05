package com.zhs.communication.controller.foodcartmqtt

import android.content.Context
import com.zhs.communication.lib.MqttConfig
import com.zhs.communication.lib.MqttManager

open class MqttClient: MqttCallBack() {

    var client = null
//    var mqttbaseurl = params.MQTT_URI_baseUrl
//    var mqttusername = params.auth_account1
//    var mqttuserpassword = params.auth_pwd1
    var qos_response = 0
    var ondisconnectf =false

    
    var newconnectf = false
    var rxisrun = false
    var flag_connect = false


    private var mMqttConfig: MqttConfig? = null
    /**
     * 默认ClientID
     */
    private var defaultClientId = "12345"

    /**
     * 测试消息数量（以收到消息次数为准）
     */
    private val MAX = 1

    /**
     * 当前的发送消息次数
     */
    private var sendMsgNum = 0

    /**
     * 当前的收到消息次数
     */
    private var receiverMsgNum = 0

    /**
     * 记录收发数据时间戳
     */
    private var startTime = 0L
    private var endTime = 0L

    /**
     * 记录建立联接时间戳
     */
    private var connectStartTime = 0L
    private var connectEndTime = 0L

    /**
     * 记录当次测试周期中的最大最小值
     */
    private var maxTime = Long.MIN_VALUE
    private var minTime = Long.MAX_VALUE

    /**
     * 累计时间差总和（ms）
     */
    private var totalAverTime = 0L


    open fun handleArrivedMessage(topic:String,message: String){
//        var mqtt_sub_callback_num= arrayOf(
//        0    this.mqtt_sub_callback_time,
//        1    this.mqtt_sub_default_callback,
//        2    this.mqtt_sub_callback_get,
//        3    this.mqtt_sub_callback_order,
//        4    this.mqtt_sub_callback_change_reply,
//        5    this.mqtt_sub_callback_sww,
//        6    this.mqtt_sub_default_callback,
//        7    this.mqtt_sub_default_callback,
//        8    this.mqtt_sub_callback_report,
//        )
        showlogl("接收到mqtt消息topic=${topic}  message = ${message}")
        try {
            when (topic) {
                params.subtopiclist[0] -> {
                    mqtt_sub_callback_time(topic, message)
                }
                params.subtopiclist[1] -> {
                    mqtt_sub_default_callback(topic, message)
                }
                params.subtopiclist[2] -> {
                    mqtt_sub_callback_get(topic, message)
                }
                params.subtopiclist[3] -> {
                    mqtt_sub_callback_order(topic, message)
                }
                params.subtopiclist[4] -> {
                    mqtt_sub_callback_change_reply(topic, message)
                }
                params.subtopiclist[5] -> {
                    mqtt_sub_callback_sww(topic, message)
                }
                params.subtopiclist[6] -> {
                    mqtt_sub_default_callback(topic, message)
                }
                params.subtopiclist[7] -> {
                    mqtt_sub_default_callback(topic, message)
                }
                params.subtopiclist[8] -> {
                    mqtt_sub_callback_report(topic, message)
                }

                else -> {
                    showlogl("NO SUB TOPIC${topic}")
                }
            }
        }catch (e:Exception){

        }
    }

    fun connect_server(){
        connectStartTime = System.currentTimeMillis()
        showlogl("正在连接中...")
        MqttManager.getInstance().connect {
            onConnectSuccess {
                connectEndTime = System.currentTimeMillis()
                var text = "建立联连耗时（ms）" + (connectEndTime - connectStartTime)
                showlogl(text)
                showlogl("服务器连接成功")
//                showlogl("服务器连接成功")
                for(topicsub in params.subtopiclist){
                    sub_topic(topicsub)
                }


            }
            onConnectFailed {
                showlogl("服务器连接失败：${it?.message}")
//                toast("服务器连接失败：${it?.message}")
            }
        }
    }
    fun sub_topic( topic:String, qos:Int=0)
    {

        showlogl("正在订阅中...")
        MqttManager.getInstance().subscribe(topic) {

            onSubscriberSuccess {
                showlogl("订阅成功")
            }

            onSubscriberFailed {
                showlogl("订阅失败：${it?.message}")
            }

            onMessageArrived { topic, message, qos ->


            }

            onDeliveryComplete {
                showlogl("消息推送完毕：$it")
            }

            onConnectionLost {
                showlogl("连接已断开$it")
            }
        }
    }
    private fun resetmqttvar() {
        maxTime = Long.MIN_VALUE
        minTime = Long.MAX_VALUE
        receiverMsgNum = 0
        sendMsgNum = 0
        totalAverTime = 0
    }
    override fun pub_topic(topic: String,message: String):Boolean{
        //这里还有一个qos 参数没有用到
//        super.pub_topic(topic, message)
//        mMqttConfig?.let {
        var fok = false
        if (mMqttConfig!=null) {
            resetmqttvar()
            sendMsgNum++
            //p2p相互发送消息
//            val messagess = "消息来自：" + it.getClientId() + it.getJsonData(sendMsgNum)
//            showlogl("pub topic =${topic} mes=${message}")
//            System.out.println(message.length)
            fok= MqttManager.getInstance()
                .publishMessage(topic, message)
            showlogl(topic+message)
//                .publishMessage(MqttManager.getInstance().getTopic()!!, message)
//        }
        }

        if (fok==false){
            connect_server()
        }

        return fok
    }

    fun disconnectmqtt(){
        showlogl("正在断开中...")
        MqttManager.getInstance().disconnect() {
            onConnectFailed {
                it?.message?.let { it1 -> showlogl(it1) }
//                toast(it?.message)
            }
        }
    }

    fun initMqtt(context: Context) {
        // 初始化
        mMqttConfig = MqttConfig().create()
        mMqttConfig!!.setBaseUrl(params.MQTT_URI_baseUrl)
        mMqttConfig!!.setUserName(params.auth_account1)
        mMqttConfig!!.setPassword(params.auth_pwd1)
        MqttManager.getInstance().init(context, mMqttConfig!!) {
            //收到消息
            onMessageArrived { topic, message, qos ->
                handleArrivedMessage(topic, message.toString())
            }
            //连接失败
            onConnectionLost {
//                disconnectmqtt()
                showlogl("连接已断开11${it?.message.toString()}")
//                connect_server()
//                showlogl("连接已断开${it?.message.toString()}")
            }
//            onDeliveryComplete {
//                var text = "已发送的消息2：$it"
//                showlogl(text)
//            }

            onConnectFailed {
                showlogl("onConnectFailed${it?.message.toString()}")
            }
        }
        showlogl("服务器地址：${MqttManager.getInstance().getServerUrl()}")
//        tvClientId.text = "当前Client id: " + MqttManager.getInstance().getClientId()
    }
}