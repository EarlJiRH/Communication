package com.zhs.communication.controller.foodcartmqtt

import com.zhs.communication.controller.meals.updatezhizupliucheng_u
import com.zhs.communication.controller.time
import java.lang.Exception

class FoodCarMqtt: MqttClient() {
    var runff = true
//    var runffr = true
    
    var canopentestmyttt : updatezhizupliucheng_u? = null



    override fun showlogprint(m:String){
        canopentestmyttt!!.MB_printf(m)
    }

    fun afterisrunok(uschannel:Int,kongzhifanshi_zuhe:Int,msginfo:String){
        if(canopentestmyttt!!.myorder_step_saveth[uschannel][0] != canopentestmyttt!!.WORK_STEP1){
            canopentestmyttt!!.MB_printf("mqtt 开始运行控制 ${kongzhifanshi_zuhe}")
            canopentestmyttt!!.defsetrunstepmytt(uschannel,kongzhifanshi_zuhe.toInt(),canopentestmyttt!!.WORK_STEP1,0,0)
        }
        else{
            canopentestmyttt!!.MB_printf("mqtt 不能控制  正在运行控制")
        }
    }

    private fun showstatemqtt(runtimes:Int){
        showlogl("${runtimes}  orderlist=",params.order_array_id)
        showlogl("orderstatelist=",params.order_array_id_state)
        showlogl("orderstatelist_diceng=",params.order_array_id_state_cansend_state)
        showlogl(
            "order_array_id_change_mqttsend_state=",
            params.order_array_id_change_mqttsend_state
        )
        showlogl(
            "order_array_id_change_mqttsend_id=",
            params.order_array_id_change_mqttsend_id
        )
        showlogl("order_array_id_orderyiqide=",params.order_array_id_orderyiqide)
        showlogl("adtlistalll=",params.adtlistalll)
        showlogl("order_fuliaoset=",params.order_fuliaoset)
    }
    
    private fun orderstateokoradt(){
        for (indexoid in 0 until params.order_array_id_state.size) {
            if ((params.order_array_id_state[indexoid] >= params.devworkstates.WORK_STATE_MEAL_TAKEOUT) and 
            (params.order_array_id_change_mqttsend_state [indexoid] == 0)){ //取餐完成或者异常
                // oid = params.order_array_id[indexoid]
                params.order_array_id_state[indexoid] =
                    params.devworkstates.WORK_STATE_FREE // 清除订单状态
                params.order_array_id_state_cansend_state[indexoid] =
                    params.devworkstates_diceng.DWORK_STATE_FREE // 清除订单状态
                params.order_array_id[indexoid] = params.defaultorderid // 清除订单
                Create_change_JSON(indexoid)
            }
        }
    }
    
    private fun oderstatehandle(){
        var dengdaizhizuo = 0
        var dengdaizhizuolist = mutableListOf<Int>()
        var zhizuozhong = 0
        var zhizuozhonglist = mutableListOf<Int>()
        var dengdaiqucan = 0
        var dengdaiqucanlist = mutableListOf<Int>()
        var qucanzhong = 0
        var qucanzhonglist = mutableListOf<Int>()

        for (indexx2  in 0 until params.order_array_id_state_cansend_state.size) {

            var statest = params.order_array_id_state_cansend_state[indexx2]
            if (statest == params.devworkstates_diceng.DWORK_STATE_WAIT_PRODUCTION) {  // 等待制作
                dengdaizhizuo = dengdaizhizuo or (1 shl indexx2)
                dengdaizhizuolist.add(indexx2)
            }
            else if(statest == params.devworkstates_diceng.DWORK_STATE_IN_PRODUCTION ) {  // 进行中（制作中）
                zhizuozhong  =zhizuozhong or (1 shl indexx2)
                zhizuozhonglist.add(indexx2)
            }
            else if( statest == params.devworkstates_diceng.DWORK_STATE_WAITMEAL ) { // 4-待取餐（制作完成）
                dengdaiqucan =dengdaiqucan or (1 shl indexx2)
                dengdaiqucanlist.add(indexx2)
            }
            else if( statest == params . devworkstates_diceng . DWORK_STATE_WAIT_MEAL_TAKEOUT ) { // 5 取餐中
                qucanzhong = qucanzhong or (1 shl indexx2)
                qucanzhonglist.add(indexx2)
            }
        }

        // 有等待制作的 包子机
        if (dengdaizhizuo > 0) {
            for (order_null in dengdaizhizuolist) {

                if (canopentestmyttt!!.myorder_step[0][0]  == canopentestmyttt?.WORK_STEP1) {
                    showlogl("这个通道在工作中不能制作 包子机")
                }
                else {
                    if (params.posworkstate_diceng_state[0] == 0) {
                        params.posworkstate_diceng_state[0] = 1

                        params.order_array_id_state_cansend_state[
                                order_null] = params.devworkstates_diceng.DWORK_STATE_IN_PRODUCTION
                        // Create_change_JSON(0, indexlist=dengdaizhizuolist)
                        Create_change_JSON(order_null)
                        showlogl("${order_null}这个位置订单已发送成功到底层开始制作${dengdaizhizuo}")
                        showlogl("开始制作")
                        // self.play_audio("通道{}".format(order_null), "开始制作")
                        canopentestmyttt!!.playAudio("开始制作")
                        // self.name_type_3_starttime[order_null] = time.time()

                        // self.uiconcon.afterisrunok(order_null,
                        //                            self.uiconcon.canopenfoodcart.zhizuochaofan_index,
                        //                            "炒饭")  // 通道0 开始炒饭
                    }
                }
            }
        }

        // 制作中等待取餐 // 包子机
        if (zhizuozhong > 0) {  // 制作中
            for (order_null in zhizuozhonglist) {
                // if self.uiconcon.canopenfoodcart.channel_state_tt[
                //     order_null] == params.devworkstates.WORK_STATE_PRODUCTION_COMPLETED:
                // if time.time() - self.name_type_3_starttime[0] > 10:
                if (params.order_array_id_change_mqttsend_state[
                        order_null] == 0) {
                    params.order_array_id_state[
                            order_null] =
                        params.devworkstates.WORK_STATE_PRODUCTION_COMPLETED
                    params.order_array_id_state_cansend_state[
                            order_null] =
                        params.devworkstates_diceng.DWORK_STATE_PRODUCTION_COMPLETED
                    // oid = params.order_array_id[order_null]
                    Create_change_JSON(order_null)
                    showlogl("订单${order_null}制作完成等待取餐改变事件位置" )
                    params.posworkstate_diceng_state[0] = 0
                    // self.play_audio("通道{}".format(order_null), "制作完成")
                    canopentestmyttt!!.playAudio("制作完成")
                }
            }
        }


        // 有等等待取餐 // 包子机
        if (dengdaiqucan > 0) {

            for (order_null in dengdaiqucanlist) {
                // if self.uiconcon.canopenfoodcart.myorder_step[order_null][0] == self.uiconcon.canopenfoodcart.WORK_STEP1:
                //     showlogl("这个通道在工作中不能取餐")
                // else:
                if (qucanzhong == 0) {
                    showlogl("order_null={}".format(order_null))
                    params.order_array_id_state_cansend_state[
                            order_null] = params.devworkstates_diceng.DWORK_STATE_WAIT_MEAL_TAKEOUT
                    params.order_array_id_state[
                            order_null] = params.devworkstates.WORK_STATE_WAITMEAL
                    Create_change_JSON(order_null)
                    showlogl("${order_null}这个位置在取餐中 请尽快取餐")
                    // self.name_type_3_starttime[0] = time.time()
                    // self.play_audio("通道{}".format(order_null), "请尽快取餐")
                    //self.play_audio("请尽快取餐")


                    if (params.order_fuliaoset[order_null] == 2) {

                        afterisrunok(
                            0,
                            9,
                            "包子取餐"
                        )  //
                        showlogl("两个包子")
                    } else {
                        afterisrunok(
                            0,
                            8,
                            "包子取餐"
                        )  //
                        showlogl("1个包子")
                    }

                } else {
                    showlogl("现在有一个在取餐  不能现在取餐 要等上一份取餐完成")
                }
            }
        }
        // 取餐中  // 包子机
        if (qucanzhong > 0) {
            for( order_null in qucanzhonglist) {
                // if time.time() - self.name_type_3_starttime[0] > 10:
                if (canopentestmyttt!!.channel_state_tt[0] == params.devworkstates.WORK_STATE_MEAL_TAKEOUT) {
                    params.order_array_id_state_cansend_state[
                            order_null] = params.devworkstates_diceng.DWORK_STATE_MEAL_TAKEOUT_3

                    params.order_array_id_state[
                            order_null] = params.devworkstates.WORK_STATE_MEAL_TAKEOUT
                    // oid = params.order_array_id[order_null]
                    Create_change_JSON(order_null)
                    showlogl("订单取餐完成改变事件位置${order_null}")
                    // self.play_audio("通道{}".format(order_null), "取餐成功")
                    canopentestmyttt!!.playAudio("取餐成功")
                    canopentestmyttt!!.channel_state_tt[0] = 0
                }
            }

        }
    }
    
    fun runtest(){
        // 1 s 检测一次有没有新的订单 改变订单状态
        var runtimes = 0
        try {
            if (runff) {
                connect_server()
                time.sleep(1000)
                Create_time_JSON()
//            time.sleep(1000)
//            check_data_adt_adt_sendjson(1, mutableListOf(1, 2, 1, 4))

                while (runff) {
                    try {

                        runtimes += 1
                        time.sleep(1000)

                        oderstatehandle()//订单状态改变

                        if (runtimes % 30 == 0) {
                            showstatemqtt(runtimes)
                            orderstateokoradt()
                        }
                        if (runtimes % (60 * 1) == 0) {
                            //1 s 发送一次状态
                            pub_Create_data_getpost_JSON(1, params.mqtt_message_id)
                        }
                        if (runtimes % (24 * 60 * 60) == 0) {
                            Create_time_JSON() //同步时间  1天
                        }
                    } catch (e: Exception) {

                    }
                }
            }
        }catch (e:Exception) {

        }

        System.out.println("tingzhi yunxing ------------------------")



    }

}