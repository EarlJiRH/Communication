package com.zhs.communication.controller.meals

open class updatezhizupliucheng_u : process_control_oderman_steprun() {


    private fun getstepmyt(): Map<Int, Array<Pair<IntArray, String>>> {
        //包子售卖机冷藏室包子数量统计
        val init_step_baozishoumai_step_LC2LC: Array<Pair<IntArray, String>> = arrayOf(
            intArrayOf(BZ_MOTORSET, 1, 0, 2, 1, 0) to "冷藏板 步进电机 找零点",  // 冷藏板 步进电机 0
            intArrayOf(if_BZ_MOTOR_work, 1, 0) to "冷藏板 步进电机是否空闲",       // 冷藏板 步进电机是否空闲
            intArrayOf(BZ_MOTORSET, 1, 0, 2, 1, 1) to "冷藏板 步进电到检测位置",    // 冷藏板 步进电机 0
            intArrayOf(if_BZ_MOTOR_work, 1, 0) to "冷藏板 步进电机是否空闲",        // 冷藏板 步进电机是否空闲
            intArrayOf(jilulcshibaozi, 1, 4) to "记录包子 到了次数到下 一步 没到上4不",// 记录包子 到了次数到下 一步 没到上2不
            intArrayOf(BZ_DUOZUO_2_STOP, 1, 0) to "冷藏找有盘的架子停止",       // 冷藏找有盘的架子
            intArrayOf(0, myorder_number1) to "结束流程",           // 结束流程
        )

        //包子售卖机冷藏到加热
        val init_step_baozi_step_LC2JR: Array<Pair<IntArray, String>> = arrayOf(

            // 新的  两个门开个运
            intArrayOf(Set_setp_run_num, 6) to "设置 运行6次",  // 设置 运行5次
            intArrayOf(Set_setp_run_num, 6) to "设置 运行6次",  // 设置 运行5次
            // intArrayOf(Set_setp_run_num_th, 1, 37),  // 蒸包子
            // intArrayOf(add_null),

            intArrayOf(BZ_LC2JRFLAGSET, 0) to "设置包子没有到加热室没有准备好",//设置包子没有到加热室没有准备好

            intArrayOf(BZ_DUOZUO_2, 0, 0) to "加热室寻找没有托盘的架子",  // 加热室寻找没有托盘的架子


            intArrayOf(BZ_MOTORSET, 1, 2, 2, 1, 1) to "冷藏板 开冷藏室门",  // 冷藏板开冷藏室门
            intArrayOf(BZ_MOTORSET, 1, 3, 2, 1, 1) to " 冷藏板开加热室门",  // 冷藏板开加热室门

            intArrayOf(BZ_DUOZUO_2, 1, 0) to "冷藏找有盘的架子", // 冷藏找有盘的架子
            intArrayOf(if_BZ_MOTOR_work, 1, 2) to "冷藏板开冷藏室门是否完成",  // 冷藏板开冷藏室门是否完成
            intArrayOf(if_BZ_MOTOR_work, 1, 3) to "冷藏板开加热室门是否完成",  // 冷藏板开加热室门是否完成


            intArrayOf(if_BZ_DUOZUO_2_work, 1, 0, 42) to "冷藏找有盘的架子是否完成",  // 冷藏找有盘的架子是否完成

            intArrayOf(BZ_JILVPOSS) to "记录包子位置",// 记录包子位置
            intArrayOf(
                if_BZ_JILVPOSS,
                6
            ) to "没有包子就继续寻找 有就下一步 没有就 第二步继续寻找", // 没有包子就继续寻找 有就下一步 没有就 第二步继续寻找
            //这里要判读有没有包子

            intArrayOf(BZ_MOTORSET, 1, 0, 2, 1, 195) to "冷藏室 步进电机上195",    // 冷藏找 步进电机上195
            intArrayOf(if_BZ_MOTOR_work, 1, 0) to "冷藏找 步进电机 是否完成",  // 冷藏找 步进电机 是否完成
            intArrayOf(BZ_MOTORSET, 1, 0, 2, 1, 195) to "冷藏找 步进电机上195",  // 冷藏找 步进电机上195
            intArrayOf(if_BZ_MOTOR_work, 1, 0) to "冷藏找 步进电机 是否完成",  // 冷藏找 步进电机 是否完成


            intArrayOf(BZ_MOTORSET, 1, 1, 2, 1, 2) to "冷藏板控制转运电机到冷藏室",  // 冷藏板控制转运电机到冷藏室
            intArrayOf(if_BZ_MOTOR_work, 1, 1) to "冷藏板控制转运电机到冷藏室是否完成",  // 冷藏板控制转运电机到冷藏室是否完成

            intArrayOf(BZ_MOTORSET, 1, 0, 2, 1, 150) to "冷藏板 步进电机上150",  // 冷藏板 步进电机上150
            intArrayOf(if_BZ_MOTOR_work, 1, 0) to "冷藏板 步进电机是否空闲",  // 冷藏板 步进电机是否空闲

            intArrayOf(BZ_MOTORSET, 1, 1, 2, 1, 1) to "冷藏板控制转运电机到转运室内",  // 冷藏板控制转运电机到转运室内
            intArrayOf(if_BZ_MOTOR_work, 1, 1) to "冷藏板控制转运电机到转运室内是否完成",  // 冷藏板控制转运电机到转运室内是否完成

            intArrayOf(if_steprunnumisok_noadd) to "判断是否运行下一步", //判断是否运行下一步
            intArrayOf(BZ_DUOZUO_2, 1, 0) to "冷藏找有盘的架子",// 冷藏找有盘的架子


            intArrayOf(if_BZ_DUOZUO_2_work, 0, 0, 27) to "加热室寻找没有托盘的架子 是否完成",  // 加热室寻找没有托盘的架子 是否完成
            intArrayOf(BZ_MOTORSET, 0, 0, 2, 1, 160) to "加热板 步进电机上160",  // 加热板 步进电机上165
            intArrayOf(if_BZ_MOTOR_work, 0, 0) to "加热板步进电机是否空闲",  // 加热板步进电机是否空闲
            intArrayOf(if_BZ_MOTOR_work, 1, 3) to "冷藏板开加热室门是否完成",  // 冷藏板开加热室门是否完成

            intArrayOf(BZ_MOTORSET, 1, 1, 2, 1, 0) to "冷藏板控制转运电机到加热室",  // 冷藏板控制转运电机到加热室
            intArrayOf(if_BZ_MOTOR_work, 1, 1) to "冷藏板控制转运电机到加热室是否完成",  // 冷藏板控制转运电机到加热室是否完成

            intArrayOf(BZ_MOTORSET, 0, 0, 2, 1, 190) to "加热板 步进电机上190",  // 加热板 步进电机上195
            intArrayOf(if_BZ_MOTOR_work, 0, 0) to "加热板步进电机是否空闲",  // 加热板步进电机是否空闲

            intArrayOf(BZ_MOTORSET, 1, 1, 2, 1, 1) to "冷藏板控制转运电机到转运室内",  // 冷藏板控制转运电机到转运室内
            intArrayOf(if_BZ_MOTOR_work, 1, 1) to "冷藏板控制转运电机到转运室内是否完成",  // 冷藏板控制转运电机到转运室内是否完成

            intArrayOf(
                BZ_MOTORSET,
                1,
                1,
                1,
                1,
                300
            ) to "冷藏板控制转运电机到转运室内 再运动300 ms",// 冷藏板控制转运电机到转运室内 再运动200 ms
            intArrayOf(if_BZ_MOTOR_work, 1, 1) to "冷藏板控制转运电机到转运室内是否完成",  // 冷藏板控制转运电机到转运室内是否完成


            intArrayOf(BZ_MOTORSET, 0, 0, 2, 1, 10) to "加热板 步进电机上10",  // 加热板 步进电机上10
            intArrayOf(if_BZ_MOTOR_work, 0, 0) to " 加热板步进电机是否空闲",  // 加热板步进电机是否空闲


            intArrayOf(BZ_writePOSS) to "保存 记录包子位置",  // 保存 记录包子位置

            intArrayOf(if_steprunnumisok_noadd) to "判断是否运行下一步", //判断是否运行下一步
            intArrayOf(BZ_DUOZUO_2, 0, 0) to "加热室寻找没有托盘的架子",  // 加热室寻找没有托盘的架子

            intArrayOf(
                if_steprunnumisok,
                0,
                9
            ) to "这个是跳转步骤 到 0自己当前步骤的 大步跳转到 9 小步继续运行",  // 这个是跳转步骤 到 0自己当前步骤的 大步跳转到 9 小步继续运行


            intArrayOf(BZ_DUOZUO_2_STOP, 0, 0) to "加热室寻找没有托盘的架子",  // 加热室寻找没有托盘的架子
            intArrayOf(BZ_DUOZUO_2_STOP, 1, 0) to "冷藏找有盘的架子",  // 冷藏找有盘的架子
            intArrayOf(BZ_MOTORSET, 1, 2, 2, 1, 0) to "冷藏板关冷藏室门",  // 冷藏板关冷藏室门
            intArrayOf(BZ_MOTORSET, 1, 3, 2, 1, 0) to "冷藏板关加热室门",  // 冷藏板关加热室门
            intArrayOf(if_BZ_MOTOR_work, 1, 2) to "藏板关冷藏室门是否完成",  // 冷藏板关冷藏室门是否完成
            intArrayOf(if_BZ_MOTOR_work, 1, 3) to "冷藏板关加热室门是否完成",  // 冷藏板关加热室门是否完成

            intArrayOf(BZ_LC2JRFLAGSET, 1) to "设置包子没有到加热室准备好 了",  // 设置包子没有到加热室准备好 了
            // [0, 36),  // 结束流程
            intArrayOf(0, 33) to "统计冷藏室包子数量控制空调",  // 统计冷藏室包子数量控制空调

            intArrayOf(0, myorder_number1) to "结束流程",  // 结束流程

            intArrayOf(BZ_DUOZUO_2_STOP, 0, 0) to "加热室寻找没有托盘的架子",  // 加热室寻找没有托盘的架子
            intArrayOf(BZ_DUOZUO_2_STOP, 1, 0) to "冷藏找有盘的架子",  // 冷藏找有盘的架子
            intArrayOf(BZ_MOTORSET, 1, 2, 2, 1, 0) to "冷藏板关冷藏室门",  // 冷藏板关冷藏室门
            intArrayOf(BZ_MOTORSET, 1, 3, 2, 1, 0) to "冷藏板关加热室门",  // 冷藏板关加热室门
            intArrayOf(if_BZ_MOTOR_work, 1, 2) to "冷藏板关冷藏室门是否完成",  // 冷藏板关冷藏室门是否完成
            intArrayOf(if_BZ_MOTOR_work, 1, 3) to "冷藏板关加热室门是否完成",  // 冷藏板关加热室门是否完成
        )

        //包子售卖机加热到冷藏
        val init_step_baozi_step_JR2LC: Array<Pair<IntArray, String>> = arrayOf(

            intArrayOf(Set_setp_run_num, 6) to "设置 运行6次",  // 设置 运行6次
            intArrayOf(BZ_JR2JRFLAGSET, 0) to "设置包子没有加热",  // 设置包子没有加热


            intArrayOf(BZ_DUOZUO_2, 0, 1) to "加热室寻找有托盘的架子",  // 加热室寻找有托盘的架子
            intArrayOf(BZ_DUOZUO_2, 1, 1) to "冷藏送寻找没有托盘的架子",  // 冷藏送寻找没有托盘的架子

            intArrayOf(BZ_MOTORSET, 1, 2, 2, 1, 1) to "冷藏板开冷藏室门",  // 冷藏板开冷藏室门
            intArrayOf(BZ_MOTORSET, 1, 3, 2, 1, 1) to "冷藏板开加热室门",  // 冷藏板开加热室门

            intArrayOf(if_BZ_MOTOR_work, 1, 2) to "冷藏板开冷藏室门是否完成",  // 冷藏板开冷藏室门是否完成
            intArrayOf(if_BZ_MOTOR_work, 1, 3) to "冷藏板开加热室门是否完成",  // 冷藏板开加热室门是否完成


            intArrayOf(if_BZ_DUOZUO_2_work, 0, 1, 25) to "加热室寻找有托盘的架子 是否完成",  // 加热室寻找有托盘的架子 是否完成

            intArrayOf(BZ_MOTORSET, 0, 0, 2, 1, 190) to "加热板 步进电机上190",  // 加热板 步进电机上165
            intArrayOf(if_BZ_MOTOR_work, 0, 0) to "加热板步进电机是否空闲",  // 加热板步进电机是否空闲
            intArrayOf(if_BZ_MOTOR_work, 1, 3) to "冷藏板开加热室门是否完成",  // 冷藏板开加热室门是否完成

            intArrayOf(BZ_MOTORSET, 1, 1, 2, 1, 0) to "冷藏板控制转运电机到加热室",  // 冷藏板控制转运电机到加热室
            intArrayOf(if_BZ_MOTOR_work, 1, 1) to "冷藏板控制转运电机到加热室是否完成",  // 冷藏板控制转运电机到加热室是否完成

            intArrayOf(BZ_MOTORSET, 0, 0, 2, 1, 160) to "加热板 步进电机上160",  // 加热板 步进电机上195
            intArrayOf(if_BZ_MOTOR_work, 0, 0) to "加热板步进电机是否空闲",  // 加热板步进电机是否空闲

            intArrayOf(BZ_MOTORSET, 1, 1, 2, 1, 1) to "冷藏板控制转运电机到转运室内",  // 冷藏板控制转运电机到转运室内
            intArrayOf(if_BZ_MOTOR_work, 1, 1) to "冷藏板控制转运电机到转运室内是否完成",  // 冷藏板控制转运电机到转运室内是否完成

            intArrayOf(
                BZ_MOTORSET,
                1,
                1,
                1,
                1,
                300
            ) to "冷藏板控制转运电机到转运室内 再运动300 ms",  // 冷藏板控制转运电机到转运室内 再运动200 ms
            intArrayOf(if_BZ_MOTOR_work, 1, 1) to "冷藏板控制转运电机到转运室内是否完成",  // 冷藏板控制转运电机到转运室内是否完成


            // intArrayOf(BZ_MOTORSET, 0, 0, 2, 1, 10),  // 加热板 步进电机上10
            // intArrayOf(if_BZ_MOTOR_work, 0, 0),  // 加热板步进电机是否空闲
            intArrayOf(BZ_DUOZUO_2, 0, 1) to "加热室寻找有托盘的架子",  // 加热室寻找有托盘的架子


            intArrayOf(
                if_BZ_DUOZUO_2_work,
                1,
                1,
                12
            ) to "冷藏送寻找没有托盘的架子  是否完成",  // 冷藏送寻找没有托盘的架子  是否完成
            intArrayOf(BZ_MOTORSET, 1, 0, 2, 1, 165) to "冷藏板 步进电机上165",  // 冷藏板 步进电机上165

            intArrayOf(if_BZ_MOTOR_work, 1, 0) to "冷藏板 步进电机是否空闲",  // 冷藏板 步进电机是否空闲

            intArrayOf(BZ_MOTORSET, 1, 1, 2, 1, 2) to "冷藏板控制转运电机到冷藏室",  // 冷藏板控制转运电机到冷藏室
            intArrayOf(if_BZ_MOTOR_work, 1, 1) to "冷藏板控制转运电机到冷藏室是否完成",  // 冷藏板控制转运电机到冷藏室是否完成

            intArrayOf(BZ_MOTORSET, 1, 0, 2, 1, 195) to "冷藏板 步进电机上195",  // 冷藏板 步进电机上195
            intArrayOf(if_BZ_MOTOR_work, 1, 0) to "冷藏板 步进电机是否空闲",  // 冷藏板 步进电机是否空闲

            intArrayOf(BZ_MOTORSET, 1, 1, 2, 1, 1) to "冷藏板控制转运电机到转运室内",  // 冷藏板控制转运电机到转运室内
            intArrayOf(if_BZ_MOTOR_work, 1, 1) to "冷藏板控制转运电机到转运室内是否完成",  // 冷藏板控制转运电机到转运室内是否完成
            intArrayOf(BZ_DUOZUO_2, 1, 1) to "冷藏送寻找没有托盘的架子",  // 冷藏送寻找没有托盘的架子
            intArrayOf(
                BZ_CGQIFSTEPADD,
                0,
                21,
                1,
                0,
                1,
                15,
                0
            ) to "判断托盘是否还在不在的话就再跳转再运", // 判断托盘是否还在不在的话就再跳转再运

            intArrayOf(if_steprunnumisok, 0, 8) to "跳转到 8 步继续运行",  // 跳转到 1 步继续运行

            intArrayOf(BZ_DUOZUO_2_STOP, 0, 1) to " 加热室寻找没有托盘的架子",  // 加热室寻找没有托盘的架子
            intArrayOf(BZ_DUOZUO_2_STOP, 1, 1) to "冷藏找有盘的架子",  // 冷藏找有盘的架子
            intArrayOf(BZ_MOTORSET, 1, 2, 2, 1, 0) to "冷藏板关冷藏室门",  // 冷藏板关冷藏室门
            intArrayOf(BZ_MOTORSET, 1, 3, 2, 1, 0) to "冷藏板关加热室门",  // 冷藏板关加热室门
            intArrayOf(if_BZ_MOTOR_work, 1, 2) to "冷藏板关冷藏室门是否完成",  // 冷藏板关冷藏室门是否完成
            intArrayOf(if_BZ_MOTOR_work, 1, 3) to "冷藏板关加热室门是否完成",  // 冷藏板关加热室门是否完成


            // [0, 35),  // 结束流程
            intArrayOf(0, myorder_number1) to "结束流程",//结束流程

        )

        //买一个包子
        val maibaozitttt: Array<Pair<IntArray, String>> = arrayOf(

            intArrayOf(MQTTSTATESET, 3) to "取餐中",       // 取餐中
            intArrayOf(mmmaibaozi_work_init) to "售卖包子 初始化",  // 售卖包子 初始化
            intArrayOf(mmmaibaozi_work, 1) to "售卖包子",       // 冷藏板 步进电机是否空闲
            intArrayOf(MQTTSTATESET, 4) to "完成   ",       // 完成
            intArrayOf(0, myorder_number1) to "结束流程",           // 结束流程
        )

        //买2个包子
        val maibaozitttt2: Array<Pair<IntArray, String>> = arrayOf(

            intArrayOf(MQTTSTATESET, 3) to "取餐中",       // 取餐中
            intArrayOf(mmmaibaozi_work_init) to "售卖包子 初始化",  // 售卖包子 初始化
            intArrayOf(mmmaibaozi_work, 2) to "售卖包子",       // 冷藏板 步进电机是否空闲
            intArrayOf(MQTTSTATESET, 4) to "完成   ",       // 完成
            intArrayOf(0, myorder_number1) to "结束流程",           // 结束流程
        )

        //包子售卖机加热室加热
        val init_step_baozi_step_JRJR: Array<Pair<IntArray, String>> = arrayOf(

            // intArrayOf(BZ_MOTORSET, 0, 12, 5, 1, 70)               to "取餐中",  // 关闭排污阀   7s
            intArrayOf(BZ_JR2JRFLAGSET, 0) to "设置包子没有加热",//设置包子没有加热
            intArrayOf(
                BZ_MOTORSET,
                0,
                11,
                5,
                1,
                (BZJR_Value_tiaozhenzhizuo[0])
            ) to "加蒸汽到 加热箱开关关${BZJR_Value_tiaozhenzhizuo[0]}ms",  // 加蒸汽到 加热箱开关关 7s


            intArrayOf(BZ_MOTORSET, 0, 6, 1, 1, 20000) to "加热板 开门送餐电机转20秒关门",  // 加热板 开门送餐电机转20秒关门

            intArrayOf(set_FLB_dealy, 10) to "设置延时10 s",  // 设置延时7s
            intArrayOf(if_FLB_dealy, 10) to "判断时间",  // 判断时间

            intArrayOf(BZ_MOTORSET, 0, 10, 5, 2, 0) to "打开蒸汽开关准备蒸汽",  // 打开蒸汽开关准备蒸汽
            intArrayOf(
                BZ_MOTORSET,
                0,
                13,
                5,
                2,
                (BZJR_Value_tiaozhenzhizuo[2])
            ) to "加热箱排水开关打开 ${BZJR_Value_tiaozhenzhizuo[2]} ms",  // 加热箱排水开关打开10 s

            intArrayOf(BZ_ZQFSQPWIF) to "判断是否要排污", //判断是否要排污

            intArrayOf(
                set_FLB_dealy,
                (BZJR_Value_tiaozhenzhizuo[5])
            ) to "设置延时 蒸汽发发生器排污时间${BZJR_Value_tiaozhenzhizuo[5]} s",  // 设置延时3分钟
            intArrayOf(if_FLB_dealy, (BZJR_Value_tiaozhenzhizuo[5])) to "判断时间",  // 判断时间

            // intArrayOf(BZ_MOTORSET, 0, 12, 5, 1, 70)                            to "关闭排污阀",  // 关闭排污阀   7s
            intArrayOf(
                set_FLB_dealy,
                (BZJR_Value_tiaozhenzhizuo[6])
            ) to "设置延时 准备蒸汽 ${BZJR_Value_tiaozhenzhizuo[6]} s",  // 设置延时8分钟
            intArrayOf(if_FLB_dealy, (BZJR_Value_tiaozhenzhizuo[6])) to "判断时间",  // 判断时间


            intArrayOf(BZ_LC2JROKIF) to "判断 是否可以开始蒸包子", //判断 是否可以开始蒸包子
            intArrayOf(BZ_MOTORSET, 1, 3, 2, 1, 0) to "冷藏板关加热室门",  // 冷藏板关加热室门
            intArrayOf(if_BZ_MOTOR_work, 1, 3) to "冷藏板关加热室门是否完成",  // 冷藏板关加热室门是否完成

            intArrayOf(
                BZ_MOTORSET,
                0,
                11,
                5,
                2,
                (BZJR_Value_tiaozhenzhizuo[1])
            ) to "加蒸汽到 加热箱开关打开 ${BZJR_Value_tiaozhenzhizuo[1]} ms",  //加蒸汽到 加热箱开关打开5 s
            intArrayOf(set_FLB_dealy, 10) to "设置延时10s",  // 设置延时5s
            intArrayOf(if_FLB_dealy, 10) to "判断时间",  // 判断时间

            intArrayOf(
                set_FLB_dealy,
                (BZJR_Value_tiaozhenzhizuo[7])
            ) to "设置延时 分钟 蒸包子时间 ${BZJR_Value_tiaozhenzhizuo[7]} s",  // 设置延时15分钟
            intArrayOf(if_FLB_dealy, (BZJR_Value_tiaozhenzhizuo[7])) to "判断时间",  // 判断时间

            intArrayOf(
                BZ_MOTORSET,
                0,
                11,
                5,
                1,
                (BZJR_Value_tiaozhenzhizuo[0])
            ) to "加蒸汽到 加热箱开关关 ${BZJR_Value_tiaozhenzhizuo[0]} ms",  // 加蒸汽到 加热箱开关关 7s
            intArrayOf(set_FLB_dealy, 10) to "设置延时10s",  // 设置延时7s
            intArrayOf(if_FLB_dealy, 10) to "判断时间",  // 判断时间
            intArrayOf(BZ_SETZHUIBEIQI, 11) to "设置下一次的准备蒸汽时间10",  // 设置下一次的准备蒸汽时间10 S

            intArrayOf(BZ_JR2JRFLAGSET, 1) to "设置包子加热完成", //设置包子加热完成

        )


        val init_step_mapl = mapOf(

            (33 to init_step_baozishoumai_step_LC2LC),//包子售卖机冷藏室包子数量统计
            (8 to maibaozitttt),//买一个包子
            (9 to maibaozitttt2),//买2个包子
            (35 to init_step_baozi_step_LC2JR),//包子售卖机冷藏到加热
            (36 to init_step_baozi_step_JR2LC),//包子售卖机加热到冷藏
            (37 to init_step_baozi_step_JRJR),//包子售卖机加热室加热
        )
        return init_step_mapl
    }

    override fun updateazhiuzocanshu() {
        //
        MB_printf("更新参数")

        updatezhiuocanshutt()
    }

    fun updatezhiuocanshutt() {
        //更新制作参数 Map<Int, Array<Pair<IntArray, String>>>
        val init_step_mapl = getstepmyt()

//        for (ii in init_step_mapl) {
//            for (i in ii.value.indices) {
//                for (j in ii.value[i].first.indices) {
//                    myorder[ii.key][i][j] = ii.value[i].first[j]
//                }
//                myorderstep_string[ii.key][i][0] = ii.value[i].second
//            }
//        }

        init_step_mapl.forEach {
//            it.value.indices.forEach { indice->
//
//            }
            for (i in it.value.indices) {

                for (j in it.value[i].first.indices) {
                    myorder[it.key][i][j] = it.value[i].first[j]
                }

                myorderstep_string[it.key][i][0] = it.value[i].second
            }
        }
    }

    init {
        updatezhiuocanshutt()
    }

}