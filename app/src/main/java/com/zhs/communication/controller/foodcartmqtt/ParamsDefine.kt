package com.zhs.communication.controller.foodcartmqtt

import com.zhs.communication.utils.getCurrentMillis

class EnumDefine_1() {
    //companion object {
        var MQTT_REDER_STATE_ERORR = 100 // 订单状态错误
        var MQTT_REDER_ERORR = MQTT_REDER_STATE_ERORR + 1 // 订单错误

        var MQTT_REPLY_ABNORMAL0 = 198  // 有没有成功的oid
        var MQTT_REPLY_ERORR = 199 // 异常
        var MQTT_REPLY_OK = 200 // 正常
   // }
}

class EnumDefine_dev_work_state_mqtt() {
   //companion object {
        //0 空闲   1-进行中（制作中） 2-待取餐（制作完成） 3 取餐中 4 取餐完成  5 超时丢掉  99 - 异常
        var WORK_STATE_FREE = 0
        var WORK_STATE_IN_PRODUCTION = 1
        var WORK_STATE_PRODUCTION_COMPLETED = 2
        var WORK_STATE_WAITMEAL = 3
        var WORK_STATE_MEAL_TAKEOUT = 4
        var WORK_STATE_TIMEOUT_DISCARD = 5
        var WORK_STATE_ABNORMAL = 99
  //  }
}

class EnumDefine_dev_work_diceng_canopen_state() {
   // companion object {
        //0 空闲  1等待制作 2-进行中（制作中） 3 制作完成 4-待取餐（制作完成） 5 取餐中 6 取餐杯子转动完成 7 发送开门完成 8 等待开门完成 9 关门完成 98 超时丢掉  99 - 异常
        var DWORK_STATE_FREE = 0
        var DWORK_STATE_WAIT_PRODUCTION = 1
        var DWORK_STATE_IN_PRODUCTION = 2
        var DWORK_STATE_PRODUCTION_COMPLETED = 3
        var DWORK_STATE_WAITMEAL = 4
        var DWORK_STATE_WAIT_MEAL_TAKEOUT = 5
        var DWORK_STATE_MEAL_TAKEOUT_1 = 6
        var DWORK_STATE_MEAL_TAKEOUT_2 = 7
        var DWORK_STATE_MEAL_TAKEOUT_3 = 8
        var DWORK_STATE_MEAL_TAKEOUT_4 = 9
        var DWORK_STATE_TIMEOUT_DISCARD = 98
        var DWORK_STATE_ABNORMAL = 99
 //   }
}

class EnumDefine_dev_work_allstate() {
    //companion object {
        var Status_freeok = 0  // 空闲中正常
        var Status_workok = 1  // 工作中正常
        var Status_adterr = 2  //工作异常

  //  }
}

class EnumDefine_dev_type() {
   // companion object {

        var dev_type_fry_rice_cart = 1 //炒饭车
        var dev_type_noodle_cart = 2 //面条车
        var dev_type_soup_cart = 3 //汤饮车
        var dev_type_steamed_stuffed_bun_cart = 4 //包子售卖车
        var dev_type_steamed_stuffed_bun_cart_zhizuo = 5 //包子制作车
        var dev_type_zhufanji_cart = 6 //煮饭机

//        var dev_typeall = {
//
//            var dev_type_fry_rice_cart: "炒饭车",
//            var dev_type_noodle_cart: "面条车",
//            var dev_type_soup_cart: "汤饮车",
//            var dev_type_steamed_stuffed_bun_cart: "包子售卖车",
//            var dev_type_steamed_stuffed_bun_cart_zhizuo: "包子制作车",
//            var dev_type_zhufanji_cart: "煮饭机",
//
//        }
  //  }
}

class ParamsDefine {
//    def __init__(self,useenvtest=None,MY_DEV_cid="",devtype=0):
    // mqtt应答信息
//    companion object{
        var MY_DEV_cid = "8431202108260002"  // "包子售卖机"   //设备id
//    }

    var available_num = 100    //初始化默认可以做多少份  后面用主料计算这个还剩多少

    var devworkstates= EnumDefine_dev_work_state_mqtt()
    var devworkstates_diceng= EnumDefine_dev_work_diceng_canopen_state()
    var code_enum= EnumDefine_1()
    var devtypetypec= EnumDefine_dev_type()

    var devallstate = EnumDefine_dev_work_allstate() //机器的整体状态


    var testenv_MQTT_URI_ip = "www.jiutongtang.net"  // "tcp://www.jiutongtang.net:2883"    // 端口    //"tcp://test.mosquitto.org:1883"// //"tcp://mq.tongxinmao.com:18831"
    var testenv_MQTT_URI_port = 2883
    var testenv_auth_account1 = ""
    var testenv_auth_pwd1 = ""

    var formalenv_MQTT_URI_ip = "zhskg.net"
    var formalenv_MQTT_URI_port = 11883
    var formalenv_auth_account1 = "smart_car"
    var formalenv_auth_pwd1 = "zhskg@2020"

    var confttxt_dev_id = "8411202107150001"
    var confttxt_cart_type = devtypetypec.dev_type_steamed_stuffed_bun_cart //默认包子售卖机
    var confttxt_use_env = "test_env" //默认测试环境

    var usetestenv = false

    // var formalenv_MQTT_URI_ip = "125.64.43.131"
    // var formalenv_MQTT_URI_port = 11883
    // var formalenv_auth_account1 = "smart_parking"
    // var formalenv_auth_pwd1 = "zhskg@2020"


//    if ( usetestenv )

//    var MQTT_URI_ip  =  testenv_MQTT_URI_ip
//    var MQTT_URI_port = testenv_MQTT_URI_port
//    var auth_account1 =  testenv_auth_account1
//    var auth_pwd1 =  testenv_auth_pwd1
//    print("mqtt 测试环境")

//    else:

//    private var baseUrl = "tcp://zhskg.net:11883"
//
//    private var userName = "smart_car"
//    private var password = "zhskg@2020"

    var MQTT_URI_baseUrl = "tcp://"+formalenv_MQTT_URI_ip+":${formalenv_MQTT_URI_port}"
//    var MQTT_URI_baseUrl = "tcp://"+testenv_MQTT_URI_ip+":${testenv_MQTT_URI_port}"
//    var MQTT_URI_port =  formalenv_MQTT_URI_port
    var auth_account1= formalenv_auth_account1
    var auth_pwd1= formalenv_auth_pwd1
//    print("mqtt 正式环境")




    var mqtt_qos = 1



        
        
    var devstatusallstatus = arrayOf<Int>(devallstate.Status_freeok)// 整个机器的工作状态  汤饮机有三个  其他机器只有一个

    var adtlistalll :MutableMap<String,Int> = mutableMapOf() //异常记录

    var devcartypetype = confttxt_cart_type//
    val cpuidinfostr = "81f2a28324d510dd"
    var onlyinfotest = "${devcartypetype}:android:${cpuidinfostr}:"
//    if devtype in var devtypetypec.dev_typeall:
//    var devcartypetype = devtype
//
//    print(var devtypetypec.dev_typeall[var devcartypetype])

    val order_max = 8  //订单最大容量

//    if var devcartypetype ==var devtypetypec.dev_type_soup_cart: //汤饮车有3个
//    var order_max = 24
//    var devstatusallstatus.append(var devallstate.Status_freeok)
//    var devstatusallstatus.append(var devallstate.Status_freeok)
//    elif var devcartypetype ==var devtypetypec.dev_type_steamed_stuffed_bun_cart: //包子车有8个
//    var order_max = 8
//
//    var order_send = var order_max  //发送量
//    var order_receive =1//接收量
//    var order_receive_check= var order_max//检测队列空间
//
    var order_array_id = LongArray(order_max)  // 订单列表
    var order_array_id_fenshu = IntArray(order_max)  // 订单列表 份数
    var order_array_id_state = IntArray(order_max)  // 订单状态列表 和mqtt通信
    var order_array_id_state_cansend_state = IntArray(order_max) // 订单状态列表 和底层驱动板通信
    var order_array_id_change_mqttsend_state =IntArray(order_max)  // mqtt 消息回复 订单状态列表
    var order_array_id_change_mqttsend_id =LongArray(order_max) // mqtt 消息回复 订单状态列表 id 消息id保存
    var order_array_id_change_mqttsend_id_timenum =IntArray(order_max)  // mqtt 消息回复 订单状态列表 id  检测到没有返回数据次数 次数 1s 检测1次
    var order_array_id_orderyiqide = IntArray(order_max)//订单是不是一起的一起的数是相同的
    var order_array_id_orderbeicanstatetf =IntArray(order_max)//订单的备餐状态
    var order_array_id_change_mqttsend_id_timenum_timeout = 30 //10 s 没有返回再发一次

//    var order_defaultparams_tangyinji = [[20,20],[15],[15]] // 汤引机  第一个是烫印机的酱油和醋的时间,第二第三个是 豆浆和绿豆 糖  单位 0.1s
    var order_fuliaoset = IntArray(order_max)  //每个订单辅料配置参数

    var posworkstate_diceng_state=IntArray(order_max)

//    var posworkstate_diceng_state = [0,0,0]  //机器上不同工位的工作清空  0 空闲 1 工作
//    var posworkstate_diceng_state_qingxizhizuo = [0,0,0]  //机器上不同工位的工作清空  0 空闲 1 工作
//
//    var dev_run_abnormal_otype=[0,0,0]  //单个种类异常
//    var dev_run_abnormal_tangyinji_ddgyb=[0,0]  //单独记录汤饮机 公用板和打鸡蛋板异常
//    var dev_run_abnormal_public = 0     //公共异常
//
    var cutdevidt = mutableListOf<String>() //之前处理过的消息id
//
    var order_array_id_orderyiqide_num=order_max
    var defaultorderid:Long = 0
//
//    var tangyinjizicaiyaobeicandeshuliao = 0  // 当前有多少个订单需要备餐
//    var tangyinjizicaiyaobeicandeshuliao_id = 0  // 当前订单需要备餐_id
//    var tangyinjizicaiyaobeicandeshuliao_id_num = 0  // 当前订单需要备餐_id 相同个数
//
//    for i in range(var order_max):
//    var order_array_id_state.append(var devworkstates.WORK_STATE_FREE)
//    var order_array_id.append(var defaultorderid)
//    var order_array_id_state_cansend_state.append(var devworkstates_diceng.DWORK_STATE_FREE)
//    var order_array_id_change_mqttsend_state.append(var defaultorderid)
//    var order_array_id_change_mqttsend_id.append(var defaultorderid)
//    var order_array_id_change_mqttsend_id_timenum.append(var defaultorderid)
//    var order_array_id_fenshu.append(1)
//    var order_array_id_orderyiqide.append(-i)
//    if var order_max==24:
//    var order_fuliaoset.append(var order_defaultparams_tangyinji[int(i/8)])
//    else:
//    var order_fuliaoset.append([1])
//    print(var order_fuliaoset)
//



    var mqtt_message_id = (getCurrentMillis())


    val MY_DEV_version="v0.1"   //版本号
    val MY_DEV_business="smart_cart"   //业务名称
    val MY_DEV_type ="food_cart"   //设备ID

    val Dev_lon_value = "104.00573136"
    val Dev_lat_value = "30.46798166"
    val Dev_alt_value = "457.06"

    // 关键字对应表
    val Dev_code="code" //信息状态
    val Dev_data="data" //


    val Dev_dt="dt"  //时间

    val Dev_id="id"  //信息id
    var orderolddevid="null"  //order 信息id
    val Dev_version="version"
    val Dev_business="business"
    val Dev_cid="cid"  //设备id
    val Dev_type="type"
    val Dev_onlyinfot = "onlyinfot"
    val Dev_params= "params"  //数据容器

    val Dev_lon = "lon"
    val Dev_lat = "lat"
    val Dev_alt = "alt"

    val Dev_method="method"  //主题

    val Dev_flag= "flag"  //标志区分： 底盘1 设备2

    val Dev_flag_value_0 = 0//所有
    val Dev_flag_value_1 = 1// 底盘
    val Dev_flag_value_2 = 2//炒饭机


    val Dev_leftover = "leftover" // 余量
    val Dev_available_space = "available_space" // 剩余空位数

    val Dev_wmode = "wmode"  //设备工作模式
    // // // // // // // // // /
    // 时间同步
    val Dev_method_time="thing.service.time"
    // 属性数据上报
    val Dev_method_property_post = "thing.event.property.post"

    // 状态应答
    val Dev_method_property_get =  "thing.event.property.get"

    // 事件上报
    val Dev_method_change_post= "thing.event.change.post"

    // 设备通用信息
    // 异常信息上报
    val Dev_method_ad_post = "thing.event.adt.post"
    // 恢复数据上报
    val Dev_method_rad_post = "thing.event.radt.post"

    // / *
    // 订单信息
    val Dev_method_set_order = "thing.service.order"

    // 服务器设置下发
    val Dev_method_service_ssw="thing.service.ssw"
    // * /

    // // // // // // // // // /

    val Dev_order     =      "order"  //订单容器
    val Dev_fail_oid     =      "fail_oid"  //下订单后失败制作的订单号列表

    val Dev_available ="available"   //剩余余量可以制作份数

    val Dev_oid       =     "oid"  //订单号
    val Dev_oid_quantity       =     "quantity"  //包子份数
    val Dev_con1 = "con" //订单的配料设置参数  比如酱油 醋 糖 等


    // 酱油：A01
    // 糖：A02
    // 醋：A03

    val Dev_conA01 = "A01" //配料1  酱油
    val Dev_conA02 = "A03" //配料2  醋
    val Dev_conA03 = "A02" //配料3  糖
    val Dev_conA04 = "A04" //配料4
    val Dev_conB01 = "B01" //配料B1
    val Dev_conB02 = "B02" //配料B1

    val Dev_oid_type       =     "o_type"  //订单号 类型
    val Dev_work_state  =     "os" //"dev_work_state" //设备订单状态

    val Dev_adtStatusdev = "status_dev" //设备状态  0 正常没有工作 1 正常工作中 2 异常
    val Dev_adt_id = "adt_id"           // 异常上报id
    val Dev_adt_type = "adt_type"           // 异常上报id 类型
    val Dev_adt_msginfo = "msginfo"     //异常上报时信息

    val Dev_work_out   =    "tm_alarm"//"dev_work_out"
    val MY_MQTT_START = "/zhskg/smart/cart/"  //通用起始主题
    var pubtopiclist = arrayOf(
     MY_MQTT_START + MY_DEV_cid + "/thing/service/time",  // 时间请求
     MY_MQTT_START + MY_DEV_cid + "/thing/event/property/post",  // 属性上报
     MY_MQTT_START + MY_DEV_cid + "/thing/event/property/get_reply",  // 返回状态
     MY_MQTT_START + MY_DEV_cid + "/thing/service/order_reply",  // 订单响应
     MY_MQTT_START + MY_DEV_cid + "/thing/event/change/post",  // 改变事件发送
     MY_MQTT_START + MY_DEV_cid + "/thing/service/ssw_reply",  // 服务器设置下发响应
     MY_MQTT_START + MY_DEV_cid + "/thing/event/adt/post",  // 异常上报
     MY_MQTT_START + MY_DEV_cid + "/thing/event/radt/post",  // 异常恢复
     MY_MQTT_START + MY_DEV_cid + "/thing/service/report_reply",  // 阀值设定
    )

    var subtopiclist =  arrayOf(
     MY_MQTT_START + MY_DEV_cid + "/thing/service/time_reply",  // 时间返回
     MY_MQTT_START + MY_DEV_cid + "/thing/event/property/post_reply",  // 属性上报返回
     MY_MQTT_START + MY_DEV_cid + "/thing/event/property/get",  // 服务器请求状态
     MY_MQTT_START + MY_DEV_cid + "/thing/service/order",  // 订单下发
     MY_MQTT_START + MY_DEV_cid + "/thing/event/change/post_reply",  // 改变事件返回
     MY_MQTT_START + MY_DEV_cid + "/thing/service/ssw",  // 服务器设置下发
     MY_MQTT_START + MY_DEV_cid + "/thing/event/adt/post_reply",  // 异常上报
     MY_MQTT_START + MY_DEV_cid + "/thing/event/radt/post_reply",  // 异常恢复
     MY_MQTT_START + MY_DEV_cid + "/thing/service/report",  // 阀值设定
    // "/zhskg///"
    )


    fun uptoiclist(){
        pubtopiclist = arrayOf(
            MY_MQTT_START + MY_DEV_cid + "/thing/service/time",  // 时间请求
            MY_MQTT_START + MY_DEV_cid + "/thing/event/property/post",  // 属性上报
            MY_MQTT_START + MY_DEV_cid + "/thing/event/property/get_reply",  // 返回状态
            MY_MQTT_START + MY_DEV_cid + "/thing/service/order_reply",  // 订单响应
            MY_MQTT_START + MY_DEV_cid + "/thing/event/change/post",  // 改变事件发送
            MY_MQTT_START + MY_DEV_cid + "/thing/service/ssw_reply",  // 服务器设置下发响应
            MY_MQTT_START + MY_DEV_cid + "/thing/event/adt/post",  // 异常上报
            MY_MQTT_START + MY_DEV_cid + "/thing/event/radt/post",  // 异常恢复
            MY_MQTT_START + MY_DEV_cid + "/thing/service/report_reply",  // 阀值设定
        )

        subtopiclist =  arrayOf(
            MY_MQTT_START + MY_DEV_cid + "/thing/service/time_reply",  // 时间返回
            MY_MQTT_START + MY_DEV_cid + "/thing/event/property/post_reply",  // 属性上报返回
            MY_MQTT_START + MY_DEV_cid + "/thing/event/property/get",  // 服务器请求状态
            MY_MQTT_START + MY_DEV_cid + "/thing/service/order",  // 订单下发
            MY_MQTT_START + MY_DEV_cid + "/thing/event/change/post_reply",  // 改变事件返回
            MY_MQTT_START + MY_DEV_cid + "/thing/service/ssw",  // 服务器设置下发
            MY_MQTT_START + MY_DEV_cid + "/thing/event/adt/post_reply",  // 异常上报
            MY_MQTT_START + MY_DEV_cid + "/thing/event/radt/post_reply",  // 异常恢复
            MY_MQTT_START + MY_DEV_cid + "/thing/service/report",  // 阀值设定
            // "/zhskg///"
        )
    }

    fun get_adt_id(adttype:Int=1,parmaslist:MutableList<Int>): String {
        /*
    """
        获取异常表
        :param adttype:
        :param parmaslist:
        :return:
        """

     */
        var res = adttype.toString()

        for (i in 0 until (parmaslist.size-1)){
            res += "_${parmaslist[i]}"
        }


        return res
    }
}