package com.zhs.communication.controller.meals

import com.zhs.communication.utils.getCurrentMillis
import com.zhs.communication.utils.zeros

open class process_control_shoumaiji_md_constep : process_control_shoumaiji_md_constep_var() {


    /**
     * 控制数据 定义
     */

    val WORK_free = 0 //空闲
    val WORK_wait = 1 //准备工作
    val WORK_busy = 2    //工作中

    val WORK_end = WORK_free

    // 工作步骤
    //作为代替数值使用方便理解
    val WORK_STEP0 = 0
    val WORK_STEP1 = 1
    val WORK_STEP2 = 2
    val WORK_STEP3 = 3
    val WORK_STEP4 = 4
    val WORK_STEP5 = 5
    val WORK_STEP6 = 6
    val WORK_STEP7 = 7
    val WORK_STEP8 = 8
    val WORK_STEP9 = 9
    val WORK_STEP10 = 10
    val WORK_STEP11 = 11
    val WORK_STEP12 = 12
    val WORK_STEP_F0 = 0xf0
    val WORK_STEP_FF = 0xff

    //菜单动作内容
    val myorder_number1 = 40  //菜单模式种类   0-菜单   6等待取餐  7洗锅  8点动
    val myorder_number2 = 200  //菜单动作列表
    val myorder_number3 = 10  //菜单控制参数
    val myorder_number = 2 //最大菜单数量，同时制作的数量

    var myorder = zeros(myorder_number1, myorder_number2, myorder_number3, dataType = "int")
    var myorderstep_string: Array<Array<Array<String>>> =
        Array(myorder_number1) { Array(myorder_number2) { arrayOf("") } }
//    var myorder[0][0][1]= myorder_number1

    var myorder_id = 0  // 菜单id
    var myorder_step = zeros(2, 10, dataType = "int")  // 控制步骤
    var myorder_step_saveth = zeros(2 * 10, 10, dataType = "int")  // 控制步骤
    var run_th_cut = 0 // 当前是那个运行
    var my_data = zeros(10, dataType = "int")   // 单独变量组
    var myorder_dealy = zeros(myorder_number, dataType = "int")   // 延时计数
    var myorder_set_dealy = zeros(myorder_number, dataType = "int")   // 设置延时

    var startruntime11: LongArray = LongArray(2) //整体运行的开始时间
    var startrunold1: Long = 0 //单个步骤运行的开始时间
    var printftimeold: Long = 0 //上一次打印时间 判断延时
    var printftimeold1: Long = 0 //上一次打印时间 判断是否可以加热


    var jilvsrununceshu = 0  // 记录运行的次数
    var jilvsrununceshu_cut = 0  // 当前是第几次


    var channel = 0

    var zuhedongzuodanbutiaoshi = false //单步控制后暂停下一个步骤
    var ordermianrun_suspend_f = false //暂停标志 所有
    var ordermianrunf = true //线程运行


    var oldprinttimeshow: Long = 0

    init {
        myorder[0][0][1] = myorder_number1
    }


    fun my_step0_get(): Int {
        ////菜单运行步骤1，判断菜单是否启动
        return myorder_step[channel][0]
    }

    fun my_step1_get(): Int {
        ////菜单数组myorder执行步骤，指向菜单动作
        return myorder_step[channel][1]
    }

    fun my_step2_get(): Int {
        ////菜单运行动作控制，保证一套动作按顺序完成
        return myorder_step[channel][2]
    }

    fun my_step_id_get(): Int {
        return myorder_step[channel][9]  //菜单id
    }

    fun my_step_send_food_flg_get(): Int {
        return myorder_step[channel][8] ////送餐命令标记
    }

    fun my_step_food_flg_get(): Int {
        return myorder_step[channel][7]  // //送餐命令
    }

    fun my_step0_set(x: Int) {

        myorder_step[channel][0] = x////菜单运行步骤1，判断菜单是否启动
    }

    fun my_step1_set(x: Int, runtimerr: Long = 0) {
        ////菜单数组myorder执行步骤，指向菜单动作
        myorder_step[channel][1] = x

        if (zuhedongzuodanbutiaoshi) {
            ordermianrun_suspend_f = true

            if (x < myorder_number2) {
                MB_printf("下一个:[" + myorderstep_string[my_step_id_get()][x][0])
            }

            MB_printf("单步测试 当前步骤运行完成暂停 运行----")
            MB_printf("当前步骤 ${my_step1_get()}")
            MB_printf("myorder_step= ${myorder_step[channel][1]}")
            if (run_th_cut == 0) {
                hangshownum("${my_step1_get()},${runtimerr}")
            }
        }
    }

    fun my_step2_set(x: Int) {
        myorder_step[channel][2] = x////菜单运行动作控制，保证一套动作按顺序完成
    }

    fun my_step_id_set(x: Int) {
        myorder_step[channel][9] = x  //菜单id
    }

    fun my_step_send_food_flg_set(x: Int) {
        myorder_step[channel][8] = x ////送餐命令标记
    }

    fun my_step_food_flg_set(x: Int) {
        myorder_step[channel][7] = x  // //送餐命令
    }

    fun myorder_data3_get(x: Int): Int {
        // print( my_step_id_get(), my_step1_get())
        return myorder[my_step_id_get()][my_step1_get()][x]
    }

    fun myorder_data3_set(x: Int, y: Int) {
        myorder[my_step_id_get()][my_step1_get()][x] = y
    }

    private fun hangshownum(s: String) {
        //这里要在界面上显示一些信息
        println("hangshownum-$s")
    }

    open fun MB_printf(s: String) {
        println("日志:${s}")

    }

    fun stopchannlrun(run_th: Int = 0) {
        my_step0_set(WORK_STEP5)
        my_step1_set(0)   //
        my_step2_set(0)   //
        MB_printf("控制完成 $run_th_cut")

        if (run_th_cut == 0) {
            val allruntttime = getCurrentMillis() - startruntime11[channel]
            MB_printf("通道${channel} 控制完成---------------一共用时${allruntttime / 1000.0}秒  换算${allruntttime / 1000.0 / 60}分钟")
            // startruntime11 = getCurrentMillis()
            // startrunold1 = getCurrentMillis()
            //            if stateset_1steatdef is not None:
            //            stateset_1steatdef("%d通道%d控制完成"%(channel,channel+1))
            playAudio("控制完成")
        }

    }

    fun defsetrunstepmytt(
        channel: Int,
        stepid: Int,
        step0: Int,
        stpe1: Int,
        step2: Int,
        run_th: Int = 0
    ) {

        /*
    """
        :param channel:  通道
        :param stepid:  那个运行的步骤
        :param step0:   是否工作
        :param stpe1:   小步骤
        :param step2:   小步骤中的小步骤
        :return:
        """
     */

        //print("----",channel,stepid,step0,stpe1,step2,run_th)
        myorder_step_saveth[run_th * 2 + channel][0] = step0
        myorder_step_saveth[run_th * 2 + channel][1] = stpe1
        myorder_step_saveth[run_th * 2 + channel][2] = step2
        myorder_step_saveth[run_th * 2 + channel][9] = stepid
        // myorder_step[channel][0] = step0  ////菜单运行步骤1，判断菜单是否启动
        // myorder_step[channel][1] = stpe1  ////菜单数组myorder执行步骤，指向菜单动作
        // myorder_step[channel][2] = step2  ////菜单运行动作控制，保证一套动作按顺序完成
        // myorder_step[channel][9] = stepid  // 菜单id
        //print('------运行控制',myorder_step_saveth)
        startruntime11[channel] = getCurrentMillis()
        MB_printf("开始控制 $channel  stepid=${stepid}")
        //    if stateset_1steatdef is not None:
        //    stateset_1steatdef("%d通道%d 开始控制"%(channel,channel+1))
        //    if run_th==0:
        playAudio("开始控制")
    }



    fun stopchannelrun_mywaim(channel: Int, run_th: Int = 0) {
        // myorder_step[channel][0] = WORK_STEP5  ////菜单运行步骤1，判断菜单是否启动
        // myorder_step[channel][1] = 0  ////菜单数组myorder执行步骤，指向菜单动作
        // myorder_step[channel][2] = 0  ////菜单运行动作控制，保证一套动作按顺序完成

        myorder_step_saveth[run_th * 2 + channel][0] = WORK_STEP5
        myorder_step_saveth[run_th * 2 + channel][1] = 0
        myorder_step_saveth[run_th * 2 + channel][2] = 0

        //包子售卖机的
        baozishoumaijixuanpandongzuo[0] = 0  // 加热室和冷藏室寻找盘动作
        baozishoumaijixuanpandongzuo[1] = 0
        baozishoumaijixuanpandongzuo[2] = 0
        baozishoumaijixuanpandongzuo[3] = 0

        baozishoumaijixuanpandongzuo_num[0] = 0  // 加热室和冷藏室寻找盘动作
        baozishoumaijixuanpandongzuo_num[1] = 0
        baozishoumaijixuanpandongzuo_num[2] = 0
        baozishoumaijixuanpandongzuo_num[3] = 0
        baozishoumaijixuanpandongzuo_num[4] = 0

        MB_printf("控制完成 $channel")
        //        if stateset_1steatdef is not None :
        //        stateset_1steatdef("%d通道%d控制完成" % (channel, channel + 1))
        playAudio("控制完成")

    }

    fun myorder_step_add() {


        // if my_step1_get()==0:
        //     startruntime11 = getCurrentMillis()
        //     startrunold1 = getCurrentMillis()
        if (run_th_cut == 0) {
            val runtime11 = getCurrentMillis() - startrunold1
            startrunold1 = getCurrentMillis()
            MB_printf("--------步骤${my_step1_get()} 运行时间${runtime11 / 1000.0}")
            my_step1_set(my_step1_get() + 1, runtime11)
        } else {
            my_step1_set(my_step1_get() + 1)
            my_step2_set(0)
            // my_step1() += 1
            // my_step2() = 0
        }
    }

    // myorder_step[:] =  myorder_step_saveth[(run_th * 2):((run_th+1) * 2)]

    fun myorder_stepget2myorder_step_saveth(run_th: Int) {
        for (i in myorder_step.indices) {
            for (j in myorder_step[i].indices) {
                myorder_step[i][j] = myorder_step_saveth[run_th * 2 + i][j]
            }
        }
    }

    fun myorder_step_savethget2myorder_step(run_th: Int) {
        for (i in myorder_step.indices) {
            for (j in myorder_step[i].indices) {
                myorder_step_saveth[run_th * 2 + i][j] = myorder_step[i][j]
            }
        }
    }


}