package com.zhs.communication.controller.meals

import com.blankj.utilcode.util.LogUtils
import com.zhs.communication.controller.time
import com.zhs.communication.usbserial.example.SdoServerCallbackImpl

open class process_control_oderman_steprun : process_control_md_constep_steprun2() {

    /*
     * 读取数组内容运行 控制
    
     */
    companion object {
        private const val TAG = "process_control_oderman"
    }

    fun stopRunThread() {
        ordermianrunf = false
    }


    fun startRunOderMain() {
        //单独的线程运行

        MB_printf("run odermain")
//        Thread.sleep(3000)
        val rest = sdoReadIndex(0x62, 0x2010, 1)
        LogUtils.e(TAG, "startRunOderMain: res=${rest}")

        while (ordermianrunf) {
            time.sleep(100)//100ms
            if (!ordermianrun_suspend_f) {
                order_main(1)
                order_main(1, run_th = 1)
                order_runmain(1)
            }
        }

        MB_printf("stop 润 ===========================================----")


    }

    fun checklocksdodata() {

        if (localsdolistalll.isNotEmpty()) {
            MB_printf("有要处理的信号")
            if ("key" in localsdolistalll) {
                var keydata = localsdolistalll.get("key")

                if ((keydata == 1)) {
                    if ((SdoServerCallbackImpl.canopentestmytt!!.myorder_step_saveth[0][0] == SdoServerCallbackImpl.canopentestmytt!!.WORK_STEP1) or
                        (SdoServerCallbackImpl.canopentestmytt!!.myorder_step_saveth[1][0] == SdoServerCallbackImpl.canopentestmytt!!.WORK_STEP1)
                    ) {

                        SdoServerCallbackImpl.canopentestmytt!!.MB_printf(
                            "不能控制  正在运行其他动作 请在当前控制完成后再控制 ${SdoServerCallbackImpl.canopentestmytt!!.myorder_step_saveth[0][0]} " +
                                    "${SdoServerCallbackImpl.canopentestmytt!!.myorder_step_saveth[1][0]} ${SdoServerCallbackImpl.canopentestmytt!!.WORK_STEP1}"
                        )
                        SdoServerCallbackImpl.canopentestmytt!!.playAudio("当前有工作在运行请稍后再试")
                    } else {
//                var fok = canopentestmytt!!.ifworkbaozijimotormyconset(1, 0)
//                if (fok){
                        SdoServerCallbackImpl.canopentestmytt!!.baozijimotormyconset(
                            1,
                            0,
                            1,
                            1,
                            5000
                        )
//                }
                    }
                }
                localsdolistalll.remove("key")
            }
        }
    }

    fun order_runmain(timenum: Int) {

        //chulixiamianshoudaodexinaho()
        checklocksdodata()
        baozizhunbeizhidai() // 提前准备包子
        xunzhaoyoubaozideceng()  // 找有包子的层
        baozishoumaijixuanpandongzuodef()  //包子售卖机寻盘动作
    }


    fun set_statemqttsend(state: Int) {
        /*
        """
        设置当前制作状态返回给mqtt
        :param state:
        :return:
        """
         */
        channel_state_tt[channel] = state
        MB_printf("设置mqtt 返回状态 通道${channel} 状态${channel_state_tt[channel]}")
    }

    fun printshowallrunlog(showstr: String) {
        if ((time.time() - oldprinttimeshow) > 5000) {
            oldprinttimeshow = time.time()
            MB_printf(showstr)
        }

    }

    fun order_main(timenum: Int, run_th: Int = 0) {

        run_th_cut = run_th

        // myorder_step[:] =  myorder_step_saveth[(run_th * 2):((run_th+1) * 2)]
        myorder_stepget2myorder_step_saveth(run_th)
        for (i in 0..1) {
            channel = i
            //  /******工作流程*****/
            if (my_step0_get() == WORK_STEP1) {
                myorder_dealy[channel] += timenum // //可能存在时间不足   测试使用 后期优化
                val myorderdata3_0 = myorder_data3_get(0)

                //printshowallrunlog("当前控制步骤是第[{}] 步".format( my_step1_get()))
                printshowallrunlog("当前控制步骤是第[${my_step1_get()}] 步 [" + myorderstep_string[my_step_id_get()][my_step1_get()][0])

                when (myorderdata3_0) {
                    0 -> {
                        // case 0: //已无菜单信息，指向下一个菜单或结束
                        // /***********/
                        //print( myorder_data3_get(1),'*'*8)
                        if (myorder_data3_get(1) >= myorder_number1) { //超出菜单限制结束任务
                            stopchannlrun()
                        } else {
                            my_step_id_set(myorder_data3_get(1)) // //选择其他菜单
                            MB_printf("STOP 1")
                            my_step1_set(0)  //
                            my_step2_set(0)   //
                        }
                    }

                    add_null -> {//空指令 占位
                        myorder_step_add()
                    }

                    Set_setp_run_num_th -> {
                        defsetrunstepmytt(
                            channel,
                            stepid = myorder_data3_get(2),
                            step0 = WORK_STEP1,
                            stpe1 = 0,
                            step2 = 0,
                            run_th = myorder_data3_get(1)
                        )
                        MB_printf(
                            "通道${channel}  设置同步执行当前 th=${run_th_cut}  设置同步th=${
                                myorder_data3_get(
                                    1
                                )
                            }  执行动作${myorder_data3_get(2)}"
                        )
                        myorder_step_add()
                    }
                    Set_setp_run_num -> {//设置运次数
                        set_setp_run_nusm_my(myorder_data3_get(1))
                    }
                    if_steprunnumisok -> {//判断运行次数
                        ifsetrunnumisok(myorder_data3_get(1), myorder_data3_get(2))
                    }
                    if_steprunnumisok_noadd -> {//判读运行的次数是否到了运行够了就跳两步 不够就1步
                        ifsetrunnumisok_ADD2()
                    }

                    set_FLB_dealy -> {//设置控制延时
                        myorder_set_dealy[channel] = myorder_data3_get(1) * 10 //
                        myorder_dealy[channel] = 0 //
                        MB_printf("通道${channel}  设置延时  ${myorder_data3_get(1)}\r\n") //
                        myorder_step_add()
                    }

                    if_FLB_dealy -> {//判断延时
                        if ((myorder_dealy[channel] >= myorder_set_dealy[channel]) or
                            (myorder_dealy[channel] >= myorder_data3_get(1) * 10)
                        ) {
                            myorder_step_add()
                        }


                        if (time.time() - printftimeold > 2000) {
                            printftimeold = time.time()
                            MB_printf(
                                "通道${channel}   时间${myorder_dealy[channel]} 判读时间${
                                    myorder_data3_get(
                                        1
                                    )
                                }"
                            )
                        }

                    }

                    BZ_ZQFSQPWIF -> {  // 包子机的蒸汽发生器是否要排污

                        baozijizhengqifashengqipwuif()
                        myorder_step_add()
                    }

                    BZ_LC2JROKIF -> { //判断包子是否可以开始加热  冷藏到加热室运输完成
                        if (baozilc2jrokok == true) {
                            myorder_step_add()
                            MB_printf("包子运输完成可以加热")
                        } else {
                            if (time.time() - printftimeold1 > 3000) {
                                printftimeold1 = time.time()
                                MB_printf("包子还没有运输完成不能加热")
                            }
                        }

                    }

                    BZ_LC2JRFLAGSET -> {//设置冷藏到加热室运输包子标志
                        baozilc2jrokok = myorder_data3_get(1) == 1
                        myorder_step_add()
                    }
                    BZ_JR2JRFLAGSET -> {//设置加热完成标志
//                        mqttt.callback.params.available_num = int( getbaozishuliang())
                        //设置返回给服务器剩余包子数量
                        baozijr2jrokok = myorder_data3_get(1) == 1

                        myorder_step_add()
                    }
                    BZ_JR2JRFLAGIF -> {// 判断包子是否加热完成
                        if (baozijr2jrokok == true) {
                            myorder_step_add()
                            MB_printf("包子加热完成可以开始买包子")
                        } else {
                            MB_printf("包子没有加热不能买包子")
                        }
                    }
                    BZ_SETZHUIBEIQI -> {
                        //设置准备蒸汽时间 一个参数  时间秒
                        setbaozizhunbeizhengqishijian(myorder_data3_get(1))
                        myorder_step_add()
                    }

                    BZ_MOTORSET -> {//包子寿买机的电机控制
                        if (baozijimotormyconset(
                                (myorder_data3_get(1)), //板子号
                                (myorder_data3_get(2)), //电机号
                                (myorder_data3_get(3)), //控制方式
                                (myorder_data3_get(4)), //方向
                                (myorder_data3_get(5))
                            )// 参数
                        ) {
                            myorder_step_add()
                        }
                    }
                    if_BZ_MOTOR_work -> {//电机是否空虚
                        if (ifworkbaozijimotormyconset(
                                (myorder_data3_get(1)),   // 板子号
                                (myorder_data3_get(2)),   // 电机号
                            )
                        ) {   // 参数
                            myorder_step_add()
                        }
                    }
                    BZ_DUOZUO_2 -> {// 包子机的下面组合动作控制
                        if (baozizuhedongzuote_2(
                                (myorder_data3_get(1)),   // 板子号
                                (myorder_data3_get(2)),   // 组合动作编号
                                1
                            )
                        ) {   // 参数
                            myorder_step_add()
                        }
                    }
                    BZ_DUOZUO_2_STOP -> {// 包子机的下面组合动作控制
                        if (baozizuhedongzuote_2(
                                (myorder_data3_get(1)),   // 板子号
                                (myorder_data3_get(2)),   // 组合动作编号
                                0
                            )
                        ) {   // 参数
                            myorder_step_add()
                        }
                    }
                    if_BZ_DUOZUO_2_work -> {// 包子机的下面组合动作控制是否完成控制
                        if (ifwork_baozizuhedongzuote_2(
                                (myorder_data3_get(1)),   // 板子号
                                (myorder_data3_get(2)),   // 组合动作编号
                                (myorder_data3_get(3))
                            )
                        ) {   // 有错误后跳转到下几步
                            myorder_step_add()
                        }
                    }
                    jilulcshibaozi -> {
                        shoumaibaozi_jilulcshibaozi(myorder_data3_get(1), myorder_data3_get(2))
                    }
                    mmmaibaozi_work_init -> {
                        if (shoumaibaozi_init()) {
                            myorder_step_add()
                        }
                    }
                    mmmaibaozi_work -> {
                        if (shoumaibaozi(myorder_data3_get(1))) {
                            myorder_step_add()

                            if (ifbaozimaiwanl2lc())
                                MB_printf("start 2 lc")
                        }
                    }
                    BZ_CGQIFSTEPADD -> {// 根据传感器数据跳转运行
                        val value = readbaozijistatechanqi(
                            myorder_data3_get(5),
                            myorder_data3_get(6),
                            myorder_data3_get(7)
                        )
                        if (value != null) {
                            if (value == myorder_data3_get(1)) {
                                my_step1_set(myorder_data3_get(2))
                                MB_printf("跳转到当前步骤的  ${myorder_data3_get(2)} 步")
                            } else if (value == myorder_data3_get(3)) {
                                myorder_step_add()
                                my_step1_set(myorder_data3_get(4))
                                MB_printf("跳转到当前步骤的  ${myorder_data3_get(4)} 步")
                                //         // else:  // 下一步
                                //         //
                            }


                        }


                    }

                    if_BZ_JILVPOSS -> {  // 判断 冷藏室包子的位置是否是空
                        var b = true
                        for (ii in baozilc2jrweizhipos) {
                            if (ii > 0) {
                                myorder_step_add()
                                b = false
                                break
                            }
                        }
                        if (b) {
                            my_step1_set(myorder_data3_get(1))
                            MB_printf("跳转到当前步骤的  ${myorder_data3_get(1)} 步")
                        }
                    }

                    BZ_JILVPOSS -> {
                        if (bozijilvweizhishifouyoubaozi())
                            myorder_step_add()
                    }
                    BZ_writePOSS -> {
                        if (bozijilvweizhishifouyoubaozi_write())
                            myorder_step_add()
                    }
                    MQTTSTATESET -> {
                        set_statemqttsend(myorder_data3_get(1))
                        myorder_step_add()
                    }

                    else -> {
                        MB_printf("没有这个命令 myorderdata3_0=${myorderdata3_0}")   //
                    }
                }


            } else {// //工作结束
                my_step0_set(WORK_STEP0)
            }
        }

        myorder_step_savethget2myorder_step(run_th)
    }


}