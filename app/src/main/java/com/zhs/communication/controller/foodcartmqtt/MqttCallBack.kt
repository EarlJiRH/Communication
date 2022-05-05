package com.zhs.communication.controller.foodcartmqtt

import com.example.myapplication.control_my.common.JsonUtil
import com.zhs.communication.controller.time
import java.lang.Exception

open class MqttCallBack {

    var params = ParamsDefine()

    ////     pubtopicdef  = pubtopicdef
////     mqtt_time = 0
    var mqtt_time_localtime: Long = 0


////     usesendmqttinfo2real = False // 真实环境  把订单消息下发到 具体制作
//                                       False 模拟 收到订单后马上返回制作完成
//
//// 和 MQTT_SUBTOPIC （ 订阅主题数组）对应，
//     var mqtt_sub_callback_num= arrayOf(
//        this.mqtt_sub_callback_time,
//        this.mqtt_sub_default_callback,
//        this.mqtt_sub_callback_get,
//        this.mqtt_sub_callback_order,
//        this.mqtt_sub_callback_change_reply,
//        this.mqtt_sub_callback_sww,
//        this.mqtt_sub_default_callback,
//        this.mqtt_sub_default_callback,
//        this.mqtt_sub_callback_report,
//        )


    open fun showlogprint(m:String){

        println(m)
    }

    fun showlogl(msg: String) {
        showlogprint("MQTTCALLBACK->$msg")
    }
    fun showlogl(msgh:String,msgarr: IntArray) {
        var msg = "["
        for (i in msgarr){
            msg+= "$i,"
        }
        msg+="]"
        msg = msgh+msg
        showlogprint("MQTTCALLBACK->$msg")
    }
    fun showlogl(msgh:String,msgarr: LongArray) {
        var msg = "["
        for (i in msgarr){
            msg+= "$i,"
        }
        msg+="]"
        msg = msgh+msg

        showlogprint("MQTTCALLBACK->$msg")
    }
    fun showlogl(msgh:String,msgarr:MutableMap<String,Int>) {
        var msg = "["
        for (i in msgarr){
            msg+= "$i,"
        }
        msg+="]"
        msg = msgh+msg

        showlogprint("MQTTCALLBACK->$msg")
    }




    open fun pub_topic(topic: String,message: String): Boolean{
        return true
    }

    fun pubtopic(numtopic: Int, data: String) {
        ////发送数据
        //// print(numtopic,data)
        ////        if  pubtopicdef is not None :
        //
        ////         pubtopicdef( params.pubtopiclist[numtopic], data)
        ////         params.mqtt_message_id = int(time.time() * 1000)
//        showlogl("===============mqtt pub =================\r\ntopic=${params.pubtopiclist[numtopic]}\r\n" +
//                "data=${data}\r\n")
        val fok = pub_topic(params.pubtopiclist[numtopic], data)
        if (fok){
            showlogl("发送成功")
        }
        else{
            showlogl("发送失败")
        }
        params.mqtt_message_id = time.time()


    }
    fun mqtt_sub_default_callback(topic: String, payload: String) {
        /**
        # * 函数功能: 打印数据
        # * 输入参数: c  mqtt客户端结构     msg_data  消息
        # * 返 回 值: 无
        # * 说    明：无
        # */

        showlogl("mqtt_sub_default_callback\r\n")
        showlogl("topic=${topic}\r\n" )
        showlogl("payload=${payload}\r\n" )
    }

    fun mqtt_sub_callback_time(topic: String, payload: String): Boolean {
        // /**
        //   * 函数功能: 时间响应处理
        //   * 输入参数: *c  mqtt客户端结构     msg_data  消息
        //   * 返 回 值: 无
        //   * 说    明：将更新时间同步到 mqtt_time
        //   */
        try {

            if (JsonUtil.get_key_int(params.Dev_code, payload) == 200) {
                mqtt_time_localtime = JsonUtil.get_key_string(params.Dev_dt, payload).toLong()
                showlogl("同步时间 mqtt_time_localtime=${mqtt_time_localtime}")
            }


            return true
        } catch (e: Exception) {
            showlogl(e.toString())
        }
        return false
    }


    private fun addjsondataMyt(key: String, value: String,yinhao:Boolean=true,adddouhao:Boolean=true): String {

        var res = ""

        res = if (yinhao) {
            "\"$key\": \"$value\""
        } else{
            "\"$key\": $value"
        }
        if (adddouhao){
            res += ","
        }
        return res
    }

    private fun addjsondataMyt(key: String, value: Int,yinhao:Boolean=true,adddouhao:Boolean=true): String {
        var res = ""

        res = if (yinhao) {
            "\"$key\": \"$value\""
        } else{
            "\"$key\": $value"
        }
        if (adddouhao){
            res += ","
        }
        return res
    }
    private fun addjsondataMyt(key: String, value: Long,yinhao:Boolean=true,adddouhao:Boolean=true): String {
        var res = ""

        res = if (yinhao) {
            "\"$key\": \"$value\""
        } else{
            "\"$key\": $value"
        }
        if (adddouhao){
            res += ","
        }
        return res
    }

    private fun MQTT_CJSON_COMMOM(data_id: Long): String {
        var data1 = addjsondataMyt(params.Dev_flag, 2,yinhao = false)

        data1 += addjsondataMyt(params.Dev_id, data_id.toString())
        data1 += addjsondataMyt(params.Dev_onlyinfot, params.onlyinfotest)
        data1 += addjsondataMyt(params.Dev_version, params.MY_DEV_version)
        data1 += addjsondataMyt(params.Dev_business, params.MY_DEV_business)
        data1 += addjsondataMyt(params.Dev_cid, params.MY_DEV_cid)
        data1 += addjsondataMyt(params.Dev_type, params.MY_DEV_type)
        data1 += addjsondataMyt(params.Dev_wmode, 0,yinhao = false)

        return data1
    }

    private fun MQTT_CJSON_COMMOM_SUB(): String {
        val t = time.time() - mqtt_time_localtime

        return addjsondataMyt(params.Dev_dt, t.toString(),adddouhao = false)
    }

    fun MQTT_CJSON_COMMOM_SUB_POS(): String {
        /*
        """
        添加机器位置信息
        :param data:
        :return:
        """
        
         */
        var data1 = addjsondataMyt(params.Dev_lon, params.Dev_lon_value)
        data1 += addjsondataMyt(params.Dev_lat, params.Dev_lat_value)
        data1 += addjsondataMyt(params.Dev_alt, params.Dev_alt_value)

        return data1
    }
    private fun MQTT_CJSON_OREDROTHRER(varoidststr:String):String{
        var tt1 = ""
        tt1 += addjsondataMyt(params.Dev_order, varoidststr,yinhao = false)
        tt1 += addjsondataMyt(params.Dev_available, params.available_num,yinhao = false)


        val available_space = getkongweishuliang()
//        available_spacel = []
        tt1 += addjsondataMyt(params.Dev_available_space, "[${available_space}]",yinhao = false)
        tt1 += addjsondataMyt(params.Dev_leftover, "{ \"A0\":3, \"A1\":4, \"B0\":1 }",yinhao = false)

        return tt1
    }



    private fun MQTT_CJSON_ORDER(): String {
        ///**
        // * 函数功能: 订单信息
        // * 输入参数: *c  mqtt客户端结构
        // * 返 回 值: 无
        // * 说    明：向服务器发送订单的状态
        // */"order":	[{
        // 				"oid":	"82516278",
        // 				"os":	0
        // 			}, {
        // 				"oid":	"0",
        // 				"os":	0
        // 			}],

        var varoidststr = "["
        for (index in 0 until (params.order_array_id.size-1)) {
            varoidststr += "{"
            varoidststr += addjsondataMyt(params.Dev_oid, params.order_array_id[index])
            varoidststr += addjsondataMyt(params.Dev_work_state, params.order_array_id_state[index],yinhao = false,adddouhao = false)
            varoidststr += "},"

        }
        varoidststr += "{"
        varoidststr += addjsondataMyt(params.Dev_oid, params.order_array_id[params.order_array_id.size-1])
        varoidststr += addjsondataMyt(params.Dev_work_state, params.order_array_id_state[params.order_array_id.size-1],yinhao = false,adddouhao = false)
        varoidststr += "}"

        varoidststr += "]"


        return MQTT_CJSON_OREDROTHRER(varoidststr)

    }

    private fun MQTT_CJSON_ORDER(index: Int): String {

        var varoidststr = "["

            varoidststr += "{"
            varoidststr += addjsondataMyt(params.Dev_oid, params.order_array_id[index])
            varoidststr += addjsondataMyt(params.Dev_work_state, params.order_array_id_state[index],yinhao = false,adddouhao = false)
            varoidststr += "}"

        varoidststr += "]"
        return MQTT_CJSON_OREDROTHRER(varoidststr)
    }
    private fun MQTT_CJSON_ORDER(indexlist: MutableList<Int>): String {
        var varoidststr = "["
        for (index in 0 until (indexlist.size-1)) {
            varoidststr += "{"
            varoidststr += addjsondataMyt(params.Dev_oid, params.order_array_id[indexlist[index]])
            varoidststr += addjsondataMyt(params.Dev_work_state, params.order_array_id_state[indexlist[index]],yinhao = false,adddouhao = false)
            varoidststr += "},"
        }

        varoidststr += "{"
        varoidststr += addjsondataMyt(params.Dev_oid, params.order_array_id[indexlist[indexlist.size-1]])
        varoidststr += addjsondataMyt(params.Dev_work_state, params.order_array_id_state[indexlist[indexlist.size-1]],yinhao = false,adddouhao = false)
        varoidststr += "}"

        varoidststr += "]"
        return MQTT_CJSON_OREDROTHRER(varoidststr)
    }


    fun pub_Create_data_getpost_JSON(numtopic: Int, id: Long) {
        //获取属性数据

        if ((numtopic == 1) or (numtopic == 2)) {
            var fangfastr = ""
            fangfastr = if (numtopic == 2) {
                addjsondataMyt(params.Dev_method, params.Dev_method_property_get,adddouhao = false)
            } else {
                addjsondataMyt(params.Dev_method, params.Dev_method_property_post,adddouhao = false)
            }


            val part =
                "{"+ MQTT_CJSON_ORDER() + MQTT_CJSON_COMMOM_SUB_POS() + MQTT_CJSON_COMMOM_SUB()  +"}"
            val data = "{" + MQTT_CJSON_COMMOM(id) + addjsondataMyt(params.Dev_params, part,yinhao = false) +fangfastr+ "}"

            pubtopic(numtopic, data)
        }

    }

    fun mqtt_sub_callback_get(topic: String, payload: String) {

        // /**
        // * 函数功能: get状态响应处理
        // * 输入参数: *c  mqtt客户端结构     msg_data  消息
        // * 返 回 值: 无
        // * 说    明：向服务器发送设备当前状态
        // */

        try {
            val id = JsonUtil.get_key_string(params.Dev_id, payload)
//            if params.Dev_id in payload:
//            id = payload[params.Dev_id]

            pub_Create_data_getpost_JSON(2, id.toLong())

        } catch (e: Exception) {


        }
    }


    fun mqtt_sub_callback_order(topic: String, payload: String) {
        // /**
        // * 函数功能: 订单响应处理
        // * 输入参数: *c  mqtt客户端结构     msg_data  消息
        // * 返 回 值: 无
        // * 说    明：接收到订单，解析数据并检查数据正确性
        // */

//        try {
            val id = JsonUtil.get_key_string(params.Dev_id, payload)
            if (id != params.orderolddevid) {
                params.orderolddevid = id
                order_callbackhandel_default_duogeoder(id, payload)
            } else {
                showlogl("这个订单id刚处理过了 ")
            }
//        } catch (e: Exception) {
//            showlogl("order err ${e}")
//        }
    }

    private fun getkongweishuliang(): Int {
        //    """
        //        获取空位数量
        //        :return:
        //        """
        var kongshu = 0
        for (element in params.order_array_id_state) {
            if (element == params.devworkstates.WORK_STATE_FREE) {
                kongshu += 1
            }
        }
        return kongshu
    }

    private fun getkongweishuliangarr(): IntArray {
        //    """
        //        获取空位数量 列表  0 数量  后面依次是空位索引
        //        :return:
        //        """
        val kongshu = IntArray(params.order_max + 1)
        for (index in 0 until params.order_array_id_state.size) {
            if (params.order_array_id_state[index] == params.devworkstates.WORK_STATE_FREE) {
                kongshu[0] += 1
                kongshu[kongshu[0]] = index
            }
        }
        return kongshu
    }

    //
    fun getoidinodertopic(params_orderlist: List<String>): Array<LongArray> {
        /*
        """
            获取订单数量和类型
            :param params_orderlist:
            :return:
            """
         */
        val resarr = Array(3) { LongArray(params.order_max + 1) }
        for (indexx in params_orderlist.indices) {
            val oidt = params_orderlist[indexx]
            showlogl("oidt=${oidt}")
            val oid = JsonUtil.get_key_string(params.Dev_oid, oidt).toLong()
            var flparams = 1//默认一个包子

            val ttqq = JsonUtil.get_key_string(params.Dev_oid_quantity, oidt)
            if (ttqq != "") {
                flparams = ttqq.toInt()
                if (flparams > 2) {
                    showlogl("包子参数不对 $ttqq 用默认参数 1 个 ")
                    flparams = 1
                }
            }
            var fin = false
            for (ii in params.order_array_id) {
                if (ii == oid) {
                    fin = true
                    break
                }
            }
            if (fin) {
                resarr[0][0] += 1.toLong()
                resarr[0][indexx + 1] = oid
                showlogl("订单存在 包子机 可能是包子数量不对 返回异常\r\n ")
            } else {
                resarr[1][0] += 1.toLong()
                resarr[1][indexx + 1] = oid //订单id
                resarr[2][indexx + 1] = flparams.toLong() //订单参数
            }
        }
        return resarr

    }

    fun getkeyizhizuodedingdangshuliang(kongwei: IntArray,orerlist: Array<LongArray>): Array<LongArray> {
        /*
    """
        获取可以制作的订单数量
        :param kongwei:  当前空位数量
        :param orerlist: 订单列表
        :return:
        可以制作的数量 可以制作的oid 不可以制作的oid列表
        """

     */

        val resarr = Array(4) { LongArray(params.order_max + 1) }


        if ((kongwei[0]) >= (orerlist[1][0])) {//空位数量大于要制作的订单数量
            if (params.available_num > orerlist[1][0]) {
                for (iii in 0 until orerlist[1][0]) {
                    val ii = iii.toInt()
                    resarr[1][ii + 1] = orerlist[1][ii + 1]// 订单id
                    resarr[2][ii + 1] = orerlist[2][ii + 1]//订单参数
                    resarr[1][0] += 1.toLong() //可以数量
                    resarr[3][ii + 1] = kongwei[ii + 1].toLong()//订单位置
                    params.available_num -= 1
                }

            }
        } else {
            showlogl("没有材料可以制作订单了1\r\n ")
            for (iii in 0 until orerlist[1][0]) {
                val ii = iii.toInt()
                resarr[0][ii + 1] = orerlist[1][ii + 1]
                resarr[0][0] += 1.toLong()
            }
        }

        for (iii in 0 until orerlist[0][0]) {
            val ii = iii.toInt()
            resarr[0][ii + 1 + orerlist[1][0].toInt()] = orerlist[0][ii + 1]
            resarr[0][0] += 1.toLong() //不可以的数量
        }

        return resarr
    }
    fun order_callbackhandel_default_duogeoder(idreturn: String, payload: String) {
        //    """
        //        默认订单处理
        //        :param idreturn:
        //        :param payload:
        //        :return:
        //        """
        //// TODO
        //// res = -1
        println("payload=${payload}")
        val t = JsonUtil.get_key_string(params.Dev_params, payload)
        val params_orderlist =
            JsonUtil.getList(params.Dev_order, t )

        val ordertypenum = getoidinodertopic(params_orderlist)
        val kongweishu = getkongweishuliangarr()
        val res = getkeyizhizuodedingdangshuliang(kongweishu, ordertypenum)

        val kezhizuonum = res[1][0].toInt()
        val kezhioderlist = res[1]
        val kezhioderlistpar = res[2]
        val kezhioderlist_posindex = res[3]
        val notkezhioderlist = res[0]

//        showlogl("* " * 30)
        showlogl("ordertypenum= ${ordertypenum.size} \r\n")
        showlogl("kongweishu= ${kongweishu[0]} \r\n")
        showlogl("kezhizuonum= ${kezhizuonum} \r\n")
        showlogl("kezhioderlist= ${kezhioderlist[1]} ${kezhioderlist[2]} ${kezhioderlist[3]} ${kezhioderlist[4]}.. \r\n")
        showlogl("kezhioderlistpar= ${kezhioderlistpar[1]} ${kezhioderlistpar[2]} ${kezhioderlistpar[3]} ${kezhioderlistpar[4]}.. \r\n")
        showlogl("kezhioderlist_posindex= ${kezhioderlist_posindex[1]} ${kezhioderlist_posindex[2]} ${kezhioderlist_posindex[3]} ${kezhioderlist_posindex[4]}.. \r\n")
        showlogl("notkezhioderlist= ${notkezhioderlist[0]} \r\n")
//        showlogl("* " * 30)
        if (kezhizuonum == 0) {
//
            Create_data_reply_ordertopic(
                3,
                idreturn,
                params.code_enum.MQTT_REPLY_ABNORMAL0,
                notkezhioderlist
            )
            showlogl("订单存在或者 没有空位 返回异常\r\n ")
            return
        }
        else {// kezhizuonum> 0 :
//            var  changelsitindex = mutableListOf<Int>()
            params.order_array_id_orderyiqide_num += 1
            for (iii in 0 until kezhioderlist[0]) {
                val ii = iii.toInt()
                showlogl(" params.order_array_id_orderyiqide_num=${params.order_array_id_orderyiqide_num} ")
                val iii_oid = kezhioderlist[ii + 1]
                val flcanshu = kezhioderlistpar[ii + 1].toInt()

                val order_null = kezhioderlist_posindex[ii + 1].toInt()
                params.order_array_id[order_null] = iii_oid
                params.order_array_id_state[
                        order_null] = params.devworkstates.WORK_STATE_IN_PRODUCTION
                params.order_array_id_state_cansend_state[
                        order_null] = params.devworkstates_diceng.DWORK_STATE_WAIT_PRODUCTION
                params.order_fuliaoset[order_null] = flcanshu
//                changelsitindex.add(order_null)
                showlogl("订单制作中 oid=${iii_oid},位置=${order_null}\r\n ")
                showlogl("这里要把订单下发到机器上制作 oid=${iii_oid},位置=${order_null}\r\n ")
            }
            var returncode = params.code_enum.MQTT_REPLY_ABNORMAL0
            if (notkezhioderlist[0] > 0) {
                returncode = params.code_enum.MQTT_REPLY_ABNORMAL0
                showlogl("没有空位 或者材料够做这些订单 返回异常\r\n ")
            }
            else {
                returncode = params.code_enum.MQTT_REPLY_OK

            }
            Create_data_reply_ordertopic(
                3, idreturn, returncode,
                notkezhioderlist
            )
//        return returncode
        }
    }

    fun mqtt_sub_callback_sww( topic: String, payload: String){
         /**
         * 函数功能: 控制响应处理
         * 输入参数: *c  mqtt客户端结构     msg_data  消息
         * 返 回 值: 无
         * 说    明：用于服务器发送控制指令，设备执行命令   ；   主要用于取餐
         */
        try {

            val idreturn = JsonUtil.get_key_string(params.Dev_id,payload)
            val param = JsonUtil.get_key_string(params.Dev_params,payload)
            val orderlist =JsonUtil.getList(params.Dev_order,param)

            if (orderlist.size>0) {

                //这个要多个订单取餐
                val oidll = mutableListOf<Long>()
                for (oidff in orderlist) {
                    oidll.add(JsonUtil.get_key_string(params.Dev_oid,oidff).toLong())
                    showlogl("swwoid list=${oidll[0]} ")
                }

                var okoidnum = 0
                var indexoid = 0
                for (oid in oidll) {
                    if (oid in params.order_array_id) {//订单存在
                        indexoid = params.order_array_id.indexOf(oid)
                        var stateoid = params.order_array_id_state[indexoid]

                        if (stateoid == params.devworkstates.WORK_STATE_PRODUCTION_COMPLETED) // 等待取餐
                            okoidnum += 1
                        else{
                            showlogl("订单不可取oid=${oid}")
                        }
                    }
                    else{
                        showlogl("订单不存在oid=${oid}")
                    }
                }
                if (okoidnum == oidll.size) {
                    Create_data_reply(5, idreturn, params.code_enum.MQTT_REPLY_OK)
                    showlogl("订单全是可取状态 ")

                    for (oid in oidll) {
                        indexoid = params.order_array_id.indexOf(oid)
                        if (params.order_array_id_change_mqttsend_state[indexoid] == 0) {
                            params.order_array_id_state_cansend_state[indexoid] =
                                params.devworkstates_diceng.DWORK_STATE_WAITMEAL
                            // DWORK_STATE_WAIT_MEAL_TAKEOUT
                            //  params.order_array_id_state[indexoid] =  params.devworkstates.WORK_STATE_WAITMEAL
                            //  Create_change_JSON(indexoid) //取餐中
                            showlogl("状态改变 等待取餐中oid=${oid} 位置=${indexoid}\r\n")
                        }
                        else {
                            showlogl("没有收到位置${indexoid}发送的制作完成改变事件响应 ")
                        }
                    }
                }


                else {
                    showlogl("订单不存在或者订单没完成 ")
                    Create_data_reply(5, idreturn, params.code_enum.MQTT_REDER_ERORR)
                }
            }
        }catch (e:Exception){
            showlogl("mqtt_sub_callback_sww ${e}")

        }
    }


    fun Create_data_reply( numtopic:Int,id:String,code:Int) {
        /**
         * 函数功能: 应答
         * 输入参数: pub_id 应答端口 data_id 应答id   data 应答数据（code）
         * 返 回 值: 无
         * 说    明：无需特殊处理：响应服务器的应答结构
         */
        var data = ""

        data += addjsondataMyt(params.Dev_flag, params.Dev_flag_value_2,yinhao = false)
        data += addjsondataMyt(params.Dev_id, id)
        data += addjsondataMyt(params.Dev_code, code,yinhao = false)
        data += addjsondataMyt(params.Dev_data, "{}",yinhao = false,adddouhao = false)

        data = "{" + data + "}"

        pubtopic(numtopic, data)
    }

    fun Create_data_reply_ordertopic(
            numtopic: Int,
            id: String,
            code: Int,
            nobukeyizhiuolist: LongArray
        ) {
            ///**
            // * 函数功能: 应答
            // * 输入参数: pub_id 应答端口 data_id 应答id   data 应答数据（code）
            // * 返 回 值: 无
            // * 说    明：无需特殊处理：响应服务器的应答结构
            // */
            var data = ""

            data += addjsondataMyt(params.Dev_flag, params.Dev_flag_value_2)
            data += addjsondataMyt(params.Dev_id, id.toString())
            data += addjsondataMyt(params.Dev_code, code,yinhao = false)
            var ttoid = ""
            for (ii in 0 until (nobukeyizhiuolist[0]-1)) {
                ttoid += nobukeyizhiuolist[ii.toInt() + 1].toString()+","


            }
            if(nobukeyizhiuolist[0]>1) {
                ttoid += nobukeyizhiuolist[(nobukeyizhiuolist[0].toInt() - 1) + 1].toString()
            }
            data += addjsondataMyt(params.Dev_fail_oid, "[${ttoid}]",yinhao = false)
            data += addjsondataMyt(params.Dev_data, "{}",false,false)

            data = "{" + data + "}"

            pubtopic(numtopic, data)
        }

    fun mqtt_sub_callback_report( topic: String, payload: String) {
        /**
         * 函数功能: 阀值设定
         * 输入参数: *c  mqtt客户端结构     msg_data  消息
         * 返 回 值: 无
         * 说    明：服务器阀值设定，设备向服务器响应
         */

        try {


            var idreturn = JsonUtil.get_key_string(params.Dev_id,payload)

            Create_data_reply(8, idreturn, params.code_enum.MQTT_REPLY_OK)
        }catch (e:Exception) {

        }
    }
    fun Create_time_JSON() {
        /**
         *函数功能: 时间更新
         *输入参数: *c
        mqtt客户端结构
         *返
        回
        值: 无
         *说
        明：通过向服务器发送时间主题，从另一个入口获得时间数据
         */
        var fangfastr = addjsondataMyt(params.Dev_method, params.Dev_method_time,adddouhao = false)

        var part = MQTT_CJSON_COMMOM_SUB_POS() + MQTT_CJSON_COMMOM_SUB()
        var data = "{" + MQTT_CJSON_COMMOM(params.mqtt_message_id) + addjsondataMyt(params.Dev_params, "{"+part+"}",yinhao = false) +fangfastr+ "}"


        pubtopic(0, data)

        showlogl("请求时间\r\n ")
    }
    fun mqtt_sub_callback_change_reply( topic: String, payload: String) {

        /*
        """
            改变事件 响应
            :param topic:
            :param payload:
            :return:
            """

         */
        try {
            var ido = JsonUtil.get_key_string(params.Dev_id,payload)
            var code = JsonUtil.get_key_int(params.Dev_code,payload)

            if (code == params.code_enum.MQTT_REPLY_OK) {


                var idi = ido.toLong()

                if (idi > params.defaultorderid) {
                    // while True:
                    while (idi in params.order_array_id_change_mqttsend_id) {
                        var index = params.order_array_id_change_mqttsend_id.indexOf(idi)

                        params.order_array_id_change_mqttsend_id[index] = params.defaultorderid
                        params.order_array_id_change_mqttsend_state[index] = 0
                        params.order_array_id_change_mqttsend_id_timenum[index] = 0
                        showlogl(
                            "位置${index}发送的改变事件有响应  [${params.order_array_id_change_mqttsend_state[index]}] "
                        )

                        if (params.order_array_id_state[index] == params.devworkstates.WORK_STATE_FREE) {
                            //  params.order_array_id_change_mqttsend_state[index] =  params.defaultorderid
                            showlogl("位置${index}完成一个订单 ")
                        }
                    }
                }
            }
            else {
                if (ido in params.cutdevidt) {
                    var ii = params.cutdevidt.indexOf(ido)
                    params.cutdevidt.removeAt(ii)
                }
                showlogl("改变事件返回的code 状态失败 code=${code}\r\n ")
            }
        }catch (e:Exception) {

        }
    }

    fun Create_change_JSON( index:Int,msgid:Long = 0,indexlist:MutableList<Int> = mutableListOf()) {
        /**
         * 函数功能: 事件更新
         * 输入参数: 无
         * 返 回 值: 无
         * 说    明：封装 订单状态改变时数据
         */
        var sendmsid = params.mqtt_message_id
        if (msgid == 0.toLong()) {
            sendmsid = params.mqtt_message_id
        }
        else {
            sendmsid = msgid
        }



        var orders = ""


        if (indexlist.size==0) {
            orders = MQTT_CJSON_ORDER(index)

            params.order_array_id_change_mqttsend_id[index] = sendmsid
            params.order_array_id_change_mqttsend_state[index] = params.order_array_id_state[index]
        }
        else {
            orders = MQTT_CJSON_ORDER(indexlist)
            for (i in indexlist) {

                params.order_array_id_change_mqttsend_id[i] = sendmsid
                params.order_array_id_change_mqttsend_state[i] = params.order_array_id_state[i]
            }

        }
        var part =
            orders + MQTT_CJSON_COMMOM_SUB_POS() + MQTT_CJSON_COMMOM_SUB()

        var data = "{" + MQTT_CJSON_COMMOM(sendmsid) +
                addjsondataMyt(params.Dev_params, "{"+part+"}",yinhao = false) +
                addjsondataMyt(params.Dev_method, params.Dev_method_change_post,adddouhao = false)+ "}"



        pubtopic(4, data)
    }

    fun getdevadtstatesall():String {
        var res = ""
        for (i in params.devstatusallstatus) {
            res += i.toString()
        }
        return res
    }

    fun check_data_adt_adt_sendjson( adttpye:Int,param:MutableList<Int>) {
        // 异常上报
        // 异常恢复
        var adt_id = params.get_adt_id(adttpye, param)
        var adt_value = param.get(param.size-1)
        if (adt_value == 0) {
            if (adt_id in params.adtlistalll.keys) {
                if (params.adtlistalll.size==1) { //判读是否所有异常都消除了
//                    if (params.devstatusallstatus) == 1:  // 包子机 炒饭机
                    params.devstatusallstatus[0] = params.devallstate.Status_workok


                }

                Create_data_adt_JSON(
                    7,
                    adttpye,
                    adt_id + "." + (params.adtlistalll[adt_id].toString()),
                    "id ${adt_id}异常恢复"
                )
                params.adtlistalll.remove(adt_id)
                showlogl("异常恢复 ${adt_id}  当前所有异常${params.adtlistalll} ")
            }
        }
        else {

//            if (params.devstatusallstatus.size) == 1://包子机 炒饭机
            params.devstatusallstatus[0] = params.devallstate.Status_adterr


            if (adt_id in params.adtlistalll.keys) {

            }else {
                var adtinfostrtt = arrayListOf("异常报警0", "缺料异常 ", "异常报警2", "废水箱满 要清理", "4电机运动错误")
                var adtinfostr=""
                if (adttpye == 4) {
                    adtinfostr = adtinfostrtt[adttpye] + "${param[0]}号板${param[1]}号电机运动错误 errcode=${param[2]}  "
                }
                else if (adttpye ==1) {
                    adtinfostr = adtinfostrtt[adttpye] + "_异常值 ${adttpye} ${param[0]} ${param[1]} ${param[2]}"
                }
                else{
                    adtinfostr = adtinfostrtt[adttpye] + "_异常值 ${adttpye} ${param[0]} ${param[1]} ${param[2]}"
                }
                Create_data_adt_JSON(6, adttpye, adt_id+ "." + adt_value.toString(), adtinfostr)
                params.adtlistalll.put(adt_id,adt_value)//  [adt_id] = [1, adt_value]
                showlogl("异常发送 ${adt_id}  当前所有异常${params.adtlistalll} ")
            }
        }
    }

    fun Create_data_adt_JSON( numtopic:Int,rad_type:Int,rad_id:String,adtinfostr:String) {
        /**
         * 函数功能: 异常上报
         * 输入参数: pub_id  状态分类处理  6 异常上报 7异常恢复
        rad_id  异常的id号对应异常属性表
        type    类型/调试参数
         * 返 回 值: 无
         * 说    明：发送设备异常状态的触发和恢复
         */

        if( (numtopic == 6 ) or (numtopic == 7)) {//编号受订阅主题的影响

            var metht = ""

            if (numtopic == 6) {
                metht = addjsondataMyt(params.Dev_method, params.Dev_method_ad_post,adddouhao = false)
            }
            else if (numtopic ==7){
                metht = addjsondataMyt(params.Dev_method, params.Dev_method_rad_post,adddouhao = false)
            }


            var adtinfo = ""

            adtinfo+= addjsondataMyt(params.Dev_adtStatusdev, getdevadtstatesall())
            adtinfo+= addjsondataMyt(params.Dev_adt_id, rad_id)
            adtinfo+= addjsondataMyt(params.Dev_adt_type, rad_type.toString())
            adtinfo+= addjsondataMyt(params.Dev_adt_msginfo, adtinfostr)

            var part =
                 MQTT_CJSON_COMMOM_SUB_POS() + MQTT_CJSON_COMMOM_SUB()

            var data = "{" +adtinfo + MQTT_CJSON_COMMOM(params.mqtt_message_id) + addjsondataMyt(params.Dev_params,"{"+ part+"}",yinhao = false) +metht+ "}"




            pubtopic(numtopic, data)
        }
        else {
            showlogl("Create_data_adt_JSON numtopic err $numtopic")
        }
    }
    }



