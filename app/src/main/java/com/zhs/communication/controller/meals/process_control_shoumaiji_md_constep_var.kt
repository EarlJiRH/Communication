package com.zhs.communication.controller.meals

open class process_control_shoumaiji_md_constep_var : process_control_shoumaiji_md_var(){

    /*
     * 每个步骤对饮的id 初始化
     */

    val add_null = 200//空指令
    val set_FLB_dealy  =   34   //延时 参数1设置最大等待时间 单位S
    val if_FLB_dealy   =  39   //延时  参数1 等待时间

    val MQTTSTATESET = 115 //MQTT 制作状态   //0 空闲   1-进行中（制作中） 2-待取餐（制作完成） 3 取餐中 4 取餐完成  5 超时丢掉  99 - 异常
//    val step0panduan = 116 //判读参数是否是不是0 是0 跳转到下几步  不是零下一步
    val BZ_MOTORSET = 150  //包子机的电机控制
    val if_BZ_MOTOR_work = 151 // 包子机的电机控制 是否完成控制

    val BZ_DUOZUO_2_STOP = 154  // 包子机的多动作控制 停止
    val BZ_DUOZUO_2 = 155  // 包子机的多动作控制
    val if_BZ_DUOZUO_2_work = 156  // 包子机的多动作控制 是否完成控制


    val mmmaibaozi_work = 157   // 售卖包子
    val mmmaibaozi_work_init = 158   // 售卖包子 初始化

    val BZ_LC2JROKIF = 159 // 判断是否可以开始蒸包子
    val BZ_ZQFSQPWIF = 160 // 蒸汽发生器判断要不要排污
    val BZ_CGQIFSTEPADD = 161  // 包子机根据传感器判断 要跳转的步骤 第一个参数和第二个参数  一起 表示跳转
    //  0 5   1 6   是 0 跳转到 第五步 是1 跳转到 第六步  后面参数是传感器位置  0 板子 1 2

    val BZ_JILVPOSS = 162  // 记录包子位置
    val BZ_writePOSS = 163  // 写包子位置
    val if_BZ_JILVPOSS = 164  // 判断托盘上有没有包子 有包子怎么左没有包子怎么做

    val BZ_LC2JRFLAGSET = 165  // 冷藏到加热室完成的标志
    val BZ_JR2JRFLAGSET = 166  // 加热是否完成标志位 设置
    val BZ_JR2JRFLAGIF = 167  // 判断加热是否完成
    val BZ_SETZHUIBEIQI = 168  // 设置准备蒸汽时间 一个参数  时间秒

    val jilulcshibaozi = 169  // 记录冷藏室包子数量  够次数了就下一步不够上2步



    val Set_setp_run_num_th = 197 // 设置同步执行  参数1 那个同步执行 0-10   参数2 执行那个大的控制流程



    val Set_setp_run_num = 198 // 设置动作运行的次数


    val if_steprunnumisok_noadd = 196 //判读运行的次数是否到了运行够了就跳两步 不够就1步
    val if_steprunnumisok = 199 //判读运行的次数是否到了 到了就下一步  没到就 去设置的步骤 参数1 要去的大步骤 参数2 要去的小步骤


}