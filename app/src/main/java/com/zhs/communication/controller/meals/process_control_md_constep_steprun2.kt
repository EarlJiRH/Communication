package com.zhs.communication.controller.meals

import com.zhs.communication.controller.time

open class process_control_md_constep_steprun2 : process_control_md_constep_steprun() {


    //包子售卖机的一些组合动作控制

    fun shoumaibaozi(num: Int): Boolean {

        if ((time.time() - shoumaibaoziprintoldtime) > 5000) {
            shoumaibaoziprintoldtime = time.time()
            MB_printf("baozistatemail_maistepstep=${baozistatemail_maistepstep}")
            MB_printf("baozistatemail_maistepstep_ll[0]=${baozistatemail_maistepstep_ll[0]}")
            MB_printf("baozistatemail_maistepstep_ll[1]=${baozistatemail_maistepstep_ll[1]}")
        }
        if (baozistatemail_num <= num) {
            // 没有接够包子数量
            var fok = false

            if ((1 < baozistatemail_maistepstep) and (20 >= baozistatemail_maistepstep) and (baozistatemail_maistepstep_settimeoutkaimentimenum < 15)) {
                //开加热室落包子的门
                if ((time.time() - baozistatemail_maistepstep_settimeoutkaimentime) > 2100) {

                    fok = baozijimotormyconset(0, 6, 1, 2, 1000)
                    if (fok) {
                        baozistatemail_maistepstep_settimeoutkaimentime = time.time()
                        baozistatemail_maistepstep_settimeoutkaimentimenum += 1
                        MB_printf("加热室开门次数 $baozistatemail_maistepstep_settimeoutkaimentimenum")
                    }
                }
            }

            if ((29 < baozistatemail_maistepstep) and (baozistatemail_maistepstep < 48) and (baozistatemail_maistepstep_settimeoutkaimentimenum_guan < 15)) {
                if ((time.time() - baozistatemail_maistepstep_settimeoutkaimentime_guan) > 2100) {

                    fok = baozijimotormyconset(0, 6, 1, 1, 1000)
                    if (fok) {
                        baozistatemail_maistepstep_settimeoutkaimentime_guan = time.time()
                        baozistatemail_maistepstep_settimeoutkaimentimenum_guan += 1
                        MB_printf("加热室关门次数 $baozistatemail_maistepstep_settimeoutkaimentimenum_guan")
                    }
                }
            }
            if (0 == baozistatemail_maistepstep) {
                // 加热室寻找有托盘的架子 送餐

                // fok = baozizuhedongzuote(0,1)
                // if fok:
                if (!baozijr2jrokok) {
                    //包子加热是否完成
                    baozistatemail_maistepstep = 4222
                    MB_printf("包子没有加热不能卖")
                } else {

                    if (getbaozishuliang()) {
                        // 还有没有买的包子就要  准备纸袋

                        var num1 = ifanybaozistatemailvnum()
                        if (num1 >= num) {
                            if (baozistatemail_maistepstep_ll[1] == 0) {
                                baozistatemail_maistepstep_ll[1] = 4
                            }

                            shoumaibaozistepadd()
                        } else {
                            baozistatemail_maistepstep = 48
                            MB_printf("包子不够了  不能卖了")
                            MB_printf("还有 ${num1} 个包子没卖")
                        }
                    } else {
                        baozistatemail_maistepstep = 4222
                        MB_printf("没有包子了  不能卖了")
                    }

                }
            } else if (1 == baozistatemail_maistepstep) {// 加热板 开门送餐电机反转15秒开门
                fok = baozijimotormyconset(0, 6, 1, 2, 1000)
                if (fok) {
                    shoumaibaozistepadd()
                    baozistatemail_maistepstep_settimeoutkaimentime = time.time()
                }
            } else if (2 == baozistatemail_maistepstep) {  // 判断 纸袋是否在准备
                if (baozistatemail_maistepstep_ll[0] == 0) {
                    baozistatemail_maistepstep_ll[0] = 2
                }
                shoumaibaozistepadd()
            } else if (3 == baozistatemail_maistepstep) {  // 判断 纸袋是否准备好
                if (baozistatemail_maistepstep_ll[0] == 13) {
                    baozistatemail_maistepstep_ll[0] = 14
                    shoumaibaozistepadd()
                }

            } else if (4 == baozistatemail_maistepstep) {
                // 判断 纸袋是否准备好 in 16..100
                // if(baozistatemail_maistepstep_ll[0] in 16..100){}
                if ((baozistatemail_maistepstep_ll[0] >= 16) and (baozistatemail_maistepstep_ll[0] < 100)) {
                    //夹紧了
                    baozistatemail_maistepstep = 19
                }

                // shoumaibaozistepadd()
            } else if (19 == baozistatemail_maistepstep) {// 开取餐门
                // fok = baozijimotormyconset(2,5,2,1,1)
                // if fok:
                shoumaibaozistepadd()
            } else if (20 == baozistatemail_maistepstep) {// 加热室开门是否完成
                fok = ifworkbaozijimotormyconset(0, 6)
                if ((fok) and (baozistatemail_maistepstep_settimeoutkaimentimenum > 10)) {
                    shoumaibaozistepadd()
                    baozistatemail_maistepstep_settimeoutkaimentimenum = 0
                }
            } else if (21 == baozistatemail_maistepstep) {// 加热板 开门送餐电机反转2秒开门
                fok = baozijimotormyconset(0, 6, 1, 2, 2000)
                if (fok) {
                    shoumaibaozistepadd()
                }
            } else if (22 == baozistatemail_maistepstep) {// 加热室开门是否完成
                fok = ifworkbaozijimotormyconset(0, 6)
                if (fok) {
                    shoumaibaozistepadd()
                }
            } else if (23 == baozistatemail_maistepstep) {// // 加热室寻找有托盘的架子 是否完成
                // fok = ifwork_baozizuhedongzuote(0,1)
                //print("等待找包子", baozistatemail_maistepstep_ll)
                // if fok:
                if (baozistatemail_maistepstep_ll[1] == 10) { //判断是否找托盘完成
                    shoumaibaozistepadd()
                } else if (baozistatemail_maistepstep_ll[1] == 0) { // 没有包子了
                    baozistatemail_maistepstep = 4222
                }
            } else if (24 == baozistatemail_maistepstep) {//  打包板 大步进电机 第一个包子位置5 100 195 285  350
                fok = baozijimotormyconset(
                    2,
                    0,
                    2,
                    1,
                    baozidabaobujingposlist[baozistatemail_cengtuiganweizhi + 1]
                )
                MB_printf("打包板 大步进电机 到位置${baozistatemail_cengtuiganweizhi + 1}")


                if (fok) {
                    shoumaibaozistepadd()
                }
            } else if (25 == baozistatemail_maistepstep) {// 打包板 大步进电机 是否工作完成
                fok = ifworkbaozijimotormyconset(2, 0)
                if (fok) {
                    shoumaibaozistepadd()
                }
            } else if (26 == baozistatemail_maistepstep) {// 加热板 推包子电机正转转15秒推

                // 这里要判断是否真空泵没有吸到纸

                val value = readbaozijistatechanqi(2, 15, 0)
                if (value != null) {
                    if (value == 0) {  // 有 吸到
                        if ((baozistatemail_maistepstep_ll[0] >= 19) and (baozistatemail_maistepstep_ll[0] < 100)) {
                            MB_printf("推包子电机正转转15秒推 位置${baozistatemail_cengtuiganweizhi + 1}")
                            fok = baozijimotormyconset(
                                0,
                                baozistatemail_cengtuiganweizhi + 1,
                                1,
                                1,
                                15000
                            )
                            if (fok) {
                                shoumaibaozistepadd()
                            }
                        }
                    } else {
                        MB_printf("等待真空泵完成")
                        //等待真空泵完成
                    }
                }
            } else if (27 == baozistatemail_maistepstep) {// 加热板 推包子是否完成
                fok = ifworkbaozijimotormyconset(0, baozistatemail_cengtuiganweizhi + 1)
                if (fok) {
                    shoumaibaozistepadd()
                }
            } else if (28 == baozistatemail_maistepstep) {//  推包子电机正转转10秒归位
                MB_printf("推包子电机正转转10秒归位 位置${baozistatemail_cengtuiganweizhi + 1}")
                fok = baozijimotormyconset(0, baozistatemail_cengtuiganweizhi + 1, 1, 2, 15000)
                if (fok) {
                    baozistatemail_tuiganhuiqutimetim = time.time()
                    shoumaibaozistepadd()

                    baozistatemail_ceng_fxiaiyiceng = 0  // 要不要在结束时再错开下一层

                    baozistatemail[baozistatemail_ceng][baozistatemail_cengtuiganweizhi] = 2 //已卖出
                    baozistatemail_num += 1  //加一个包子的数量 纸袋中

                    if (baozizhecengisyoubaozi()) {
                        MB_printf("这层还有包子")
                    } else {
                        MB_printf("这层没包子了  要找下一层")
                        baozistatemail_maistepstep_ll[1] = 1
                    }
                    if (baozistatemail_num < num) {
                        baozistatemail_maistepstep = 23  // 推下一个包子
                    }
                }
            } else if (29 == baozistatemail_maistepstep) {//   加热板 开门送餐电机正转20秒关门
                fok = baozijimotormyconset(0, 6, 1, 1, 2300)
                if (fok) {
                    shoumaibaozistepadd()
                    baozistatemail_maistepstep_ll[0] = 0  // 清除一下
                }
            } else if (30 == baozistatemail_maistepstep) {//   打包板 大步进电机 出餐位置
                fok = baozijimotormyconset(2, 0, 2, 2, 47)
                if (fok) {
//                    baozistatemail_maistepstep = 30.123
                    shoumaibaozistepadd()
                }
            } else if (31 == baozistatemail_maistepstep) {// 开取餐门
                fok = baozijimotormyconset(2, 5, 2, 1, 1)
                if (fok) {
                    shoumaibaozistepadd()
//                    baozistatemail_maistepstep = 31
                    MB_printf("开取餐门")
                }
            } else if (32 == baozistatemail_maistepstep) {  // 打包板 关闭真空泵

                fok = baozijimotormyconset(2, 4, 2, 1, 0)
                if (fok) {
//                    baozistatemail_maistepstep = 33
                    shoumaibaozistepadd()
                }
            } else if (33 == baozistatemail_maistepstep) {//   打包板 小步进电机 是否完成
                fok = ifworkbaozijimotormyconset(2, 1)
                if (fok) {
//                    baozistatemail_maistepstep = 31.11
                    shoumaibaozistepadd()

                }
            } else if (34 == baozistatemail_maistepstep) {// 打包板 小步进电机 接纸袋
                fok = baozijimotormyconset(2, 1, 2, 2, 45)
                if (fok) {
//                    baozistatemail_maistepstep = 31.1
                    shoumaibaozistepadd()
                }
            } else if (35 == baozistatemail_maistepstep) {//   打包板 小步进电机 出餐位置 是否完成
                fok = ifworkbaozijimotormyconset(2, 1)
                if (fok) {
//                    baozistatemail_maistepstep = 31.2
                    shoumaibaozistepadd()
                }
            } else if (36 == baozistatemail_maistepstep) {//   打包板 大步进电机 出餐位置 是否完成
                fok = ifworkbaozijimotormyconset(2, 0)
                if (fok) {
                    shoumaibaozistepadd()
//                    baozistatemail_maistepstep = 31.3
                }
            } else if (37 == baozistatemail_maistepstep) { //取餐门开门是否完成
                //TODO 去餐门坏了
                //{
//                fok = ifworkbaozijimotormyconset(2, 4)
//                if (fok) {
                shoumaibaozistepadd()
//                    baozistatemail_maistepstep = 33

                //}
            } else if (38 == baozistatemail_maistepstep) {//   打包板 送餐电机正转转4秒送餐
                fok = baozijimotormyconset(2, 3, 1, 1, 4000)
                if (fok) {
                    shoumaibaozistepadd()
                }
            } else if (39 == baozistatemail_maistepstep) {//   打包板 送餐电机是否完成
                fok = ifworkbaozijimotormyconset(2, 3)
                if (fok) {
                    shoumaibaozistepadd()
                    MB_printf("请尽快取餐")
                    // if baozistatemail_ceng_fxiaiyiceng==1){
                    //     baozistatemail_maistepstep = 45   // 跳转到45
                    playAudio("请尽快取餐")
                    baozistatemail_maistepstep_settimeout = time.time()
                }
            } else if (40 == baozistatemail_maistepstep) {//   判断延时是否到了 延时10 s
                val cutt = time.time()
                if (cutt - baozistatemail_maistepstep_settimeout > 10000) {
                    // shoumaibaozistepadd()
                    playAudio("请尽快取餐")
                    MB_printf("请尽快取餐")
                }
                if (cutt - baozistatemail_maistepstep_settimett > 1000) {
                    baozistatemail_maistepstep_settimett = cutt
                    var value = readbaozijistatechanqi(2, 14, 0)
                    if (value != null) {
                        if (value == 1) {  //取走了
                            shoumaibaozistepadd()
                            playAudio("取餐成功")
                            MB_printf("取餐成功")
                        }
                    }
                }
                // baozistatemail_ceng_fxiaiyiceng = 0  // 要不要在结束时再错开下一层 结束

                // 这判断
            } else if (41 == baozistatemail_maistepstep) {//   打包板 送餐电机反转3秒归位
                fok = baozijimotormyconset(2, 3, 1, 2, 3000)
                if (fok) {
                    shoumaibaozistepadd()
//                    baozistatemail_maistepstep = 36.1
                }
            } else if (42 == baozistatemail_maistepstep) {//  //送餐电机是否完成
                fok = ifworkbaozijimotormyconset(2, 3)
                if (fok) {
//                    baozistatemail_maistepstep = 36.2
                    shoumaibaozistepadd()
                }
            } else if (43 == baozistatemail_maistepstep) {// 关取餐门
                fok = baozijimotormyconset(2, 5, 2, 1, 0)
                if (fok) {
//                    baozistatemail_maistepstep = 37
                    shoumaibaozistepadd()
                }
            } else if (44 == baozistatemail_maistepstep) {//   加热室关门是否完成
                fok = ifworkbaozijimotormyconset(0, 6)
                if (fok) {
                    shoumaibaozistepadd()
                    // 如果还有包子
                    if (getbaozishuliang()) { // 还有没有买的包子就要  准备纸袋
                        baozistatemail_maistepstep_ll[0] = 2
                        baozistatexiaobujindabaoguilingt = 0
                        MB_printf("还有没有买的包子就要  准备纸袋")
                    } else { // 没有了就不用准备了
                        baozistatemail_maistepstep_ll[0] = 0
                        MB_printf("没有包子了就不用准备了")
                        // 这里又开始准备纸袋
                    }
                }
            } else if (45 == baozistatemail_maistepstep) {//  加热板 开门送餐电机再反转2秒关门
                // fok = baozijimotormyconset(0,6,1,1,2000)
                // if fok){
                shoumaibaozistepadd()
            } else if (46 == baozistatemail_maistepstep) {//   加热室关门是否完成
                fok = ifworkbaozijimotormyconset(0, 6)
                if (fok) {
                    shoumaibaozistepadd()
                    baozistatemail_maistepstep_settimeoutkaimentimenum_guan = 0
                }
            } else if (47 == baozistatemail_maistepstep) {//   推包子是否完成 归位
                fok = ifworkbaozijimotormyconset(0, 5)
                if (fok) {
                    shoumaibaozistepadd()
                }
            } else if (48 == baozistatemail_maistepstep) {  // 完成
                MB_printf("买包子完成的==================================")
                MB_printf("baozistatemail 包子的位置=${baozistatemail}")
                MB_printf("baozistatemail_ceng 当前是在那一层=${baozistatemail_ceng}")
                MB_printf("baozistatemail_cengtuiganweizhi 当前的推杆位置=${baozistatemail_cengtuiganweizhi}")


                return true
            } else if (4222 == baozistatemail_maistepstep) {
                // 买包子 异常
                baozistatemail_maistepstep_ll[0] = 0
                baozistatemail_maistepstep_ll[1] = 0
                fok = baozijimotormyconset(2, 4, 2, 1, 0) // 关闭真空泵

                return true
            }
        }

        return false
    }

    private fun shoumaibaozistepadd() {
        baozistatemail_maistepstep += 1
    }

    fun baozizhunbeizhidai() {
        /*
        """
        提前准备纸袋
        :return:
        """
        
         */
        var fok = false
        if (0 == baozistatemail_maistepstep_ll[0]) {
            return
        } else if (2 == baozistatemail_maistepstep_ll[0]) {  // 打包板 送餐电机反转两秒归位
            fok = baozijimotormyconset(2, 3, 1, 2, 2000)
            if (fok) {
                shoumaibaozistepadd_stepll(0)
            }
        } else if (3 == baozistatemail_maistepstep_ll[0]) {  // // 打包板 大步进电机 归零
            fok = baozijimotormyconset(2, 0, 4, 0, 0)
            if (fok) {
                shoumaibaozistepadd_stepll(0)
            }
        } else if (4 == baozistatemail_maistepstep_ll[0]) {  // 打包板 小步进电机 归零
            fok = baozijimotormyconset(2, 1, 4, 0, 0)
            if (fok) {
                shoumaibaozistepadd_stepll(0)
                baozistatexiaobujindabaoguilingtguilintime = time.time()
            }
        } else if (5 == baozistatemail_maistepstep_ll[0]) {  // 打包板 大步进电机 是否工作完成
            fok = ifworkbaozijimotormyconset(2, 0)
            if (fok) {
                shoumaibaozistepadd_stepll(0)
                baozistatexiaobujindabaoguilingtguilintime = time.time()
            } else {
                if (time.time() - baozistatexiaobujindabaoguilingtguilintime > 20 * 1000) {
                    baozistatemail_maistepstep_ll[0] = 4
                    MB_printf("小步进电机打包板  归零超时")
//                    play_audio("小步进归零超时1")
                }
            }
        } else if (6 == baozistatemail_maistepstep_ll[0]) {  // 打包板 小步进电机 是否工作完成
            fok = ifworkbaozijimotormyconset(2, 1)
            if (fok) {
                shoumaibaozistepadd_stepll(0)
            } else {
                if (time.time() - baozistatexiaobujindabaoguilingtguilintime > 20 * 1000) {
                    baozistatemail_maistepstep_ll[0] = 4
                    MB_printf("小步进电机打包板  归零超时")
                    //mqttt.play_audio('小步进归零超时1111')
                }
            }
        } else if (7 == baozistatemail_maistepstep_ll[0]) {  // // 打包板 大步进电机 接纸袋
            fok = baozijimotormyconset(2, 0, 2, 2, baozidabaobujingposlist[0])
            if (fok) {
                shoumaibaozistepadd_stepll(0)
            }
        } else if (8 == baozistatemail_maistepstep_ll[0]) {  // 打包板 小步进电机 接纸袋
            fok = baozijimotormyconset(2, 1, 2, 2, 32)
            if (fok) {
                shoumaibaozistepadd_stepll(0)
            }
        } else if (9 == baozistatemail_maistepstep_ll[0]) {  // 打包板 大步进电机 是否工作完成
            fok = ifworkbaozijimotormyconset(2, 0)
            if (fok) {
                shoumaibaozistepadd_stepll(0)
            }
        } else if (10 == baozistatemail_maistepstep_ll[0]) {  // 打包板 小步进电机 是否工作完成
            fok = ifworkbaozijimotormyconset(2, 1)
            if (fok) {
                shoumaibaozistepadd_stepll(0)
            }
        } else if (11 == baozistatemail_maistepstep_ll[0]) {  // 打包板 出纸
            fok = baozijimotormyconset(2, 2, 2, 1, 0)
            if (fok) {
                shoumaibaozistepadd_stepll(0)
            }
        } else if (12 == baozistatemail_maistepstep_ll[0]) {  // 打包板 出纸 是否工作完成
            fok = ifworkbaozijimotormyconset(2, 2)
            if (fok) {
                shoumaibaozistepadd_stepll(0)
            }
        } else if (13 == baozistatemail_maistepstep_ll[0]) {  //
            return
            // 这里到后面有可能要重复
        } else if (141 == baozistatemail_maistepstep_ll[0]) {  // 打包板 小步进电机 归零
            fok = baozijimotormyconset(2, 1, 4, 0, 0)
            if (fok) {
//                play_audio("小步进归零成功")
                time.sleep(1000)
                baozistatemail_maistepstep_ll[0] = 142
                baozistatexiaobujindabaoguilingtguilintime = time.time()
            }
        } else if (142 == baozistatemail_maistepstep_ll[0]) {  // 打包板 小步进电机 是否工作完成
            fok = ifworkbaozijimotormyconset(2, 1)
            if (fok) {
                baozistatemail_maistepstep_ll[0] = 14
            } else {
                if (time.time() - baozistatexiaobujindabaoguilingtguilintime > 20000) {
                    baozistatemail_maistepstep_ll[0] = 141
                    MB_printf("小步进电机打包板  归零超时 14.2")
                    //mqttt.play_audio('小步进归零超时2')

                }
            }
        } else if (14 == baozistatemail_maistepstep_ll[0]) {  // 打包板 小步进电机 夹紧
            fok = baozijimotormyconset(2, 1, 2, 1, 9)
            if (fok) {
                shoumaibaozistepadd_stepll(0)
                baozistatexiaobujindabaoguilingtguilintime = time.time()
                baozistatemail_maistepstep_dbshoumaidianjixiaobujinguiweipanduan = time.time()
            }
        } else if (15 == baozistatemail_maistepstep_ll[0]) {  // 打包板 小步进电机 是否工作完成
            fok = ifworkbaozijimotormyconset(2, 1)
            if (fok) {
                shoumaibaozistepadd_stepll(0)
            } else {
                if (time.time() - baozistatexiaobujindabaoguilingtguilintime > 20000) {
                    baozistatemail_maistepstep_ll[0] = 141
                    MB_printf("小步进电机打包板  归零超时 15")
                    //mqttt.play_audio('小步进归零超时2 15')
                }
            }
        } else if (16 == baozistatemail_maistepstep_ll[0]) {  // 打包板 打开真空泵
            fok = baozijimotormyconset(2, 4, 2, 1, 1)
            if (fok) {
                shoumaibaozistepadd_stepll(0)
            }
        } else if (17 == baozistatemail_maistepstep_ll[0]) {  // 打包板 小步进电机 打开
            fok = baozijimotormyconset(2, 1, 2, 2, 25)
            if (fok) {
                shoumaibaozistepadd_stepll(0)
                baozistatexiaobujindabaoguilingtguilintime = time.time()
            }
        } else if (18 == baozistatemail_maistepstep_ll[0]) {  // 打包板 小步进电机 是否工作完成
            fok = ifworkbaozijimotormyconset(2, 1)
            if (fok) {
                shoumaibaozistepadd_stepll(0)
            } else {
                if (time.time() - baozistatexiaobujindabaoguilingtguilintime > 20000) {
                    baozistatemail_maistepstep_ll[0] = 141
                    MB_printf("小步进电机打包板  归零超时 18")
                    //mqttt.play_audio('小步进归零超时2 18')
                }
            }
        } else if (19 == baozistatemail_maistepstep_ll[0]) {  //
            // 这里要判断是否真空泵没有吸到纸

            var value = readbaozijistatechanqi(2, 15, 0)
            if (value != null) {
                if (value == 1) {// 没有 吸到
                    // if baozistatemail_maistepstep<30){
                    baozistatemail_maistepstep_ll[0] = 14

                    baozistatexiaobujindabaoguilingt += 1
                    if (baozistatexiaobujindabaoguilingt > 2) {
                        baozistatemail_maistepstep_ll[0] = 141

                        MB_printf("打包板步进电机要归零  尝试次数{baozistatexiaobujindabaoguilingt}")
                        //mqttt.play_audio('小步进运动错误尝试归零')
                        baozistatexiaobujindabaoguilingt = 0
                        fok = baozijimotormyconset(2, 4, 2, 1, 0) // 关闭真空泵
                        if (time.time() - baozistatemail_maistepstep_dbshoumaidianjixiaobujinguiweipanduan < 3000) {
                            fok = baozijimotormyconset(2, 1, 4, 0, 0) //小步进电机归零
                        } else {
                            MB_printf("打包板小步进不需要归零")
                            // 重新吸
                        }
                    }
                }
            }
        }

    }

    fun xunzhaoyoubaozideceng() {  // 找有包子的层
        if (0 == baozistatemail_maistepstep_ll[1]) {
            return
        } else if (1 == baozistatemail_maistepstep_ll[1]) { // 加热板 推包子是否完成

            //MB_printf("判断 这个推杆是否收回成功 ${baozistatemail_cengtuiganweizhi + 1}")
            if ((time.time() - baozistatemail_tuiganhuiqutimetim) > 10000) {
                var fok = ifworkbaozijimotormyconset(
                    0,
                    baozistatemail_cengtuiganweizhi + 1
                )  // 这个是第五个 最后一个
                if (fok) {
                    shoumaibaozistepadd_stepll(1)
                }
            }
//            else {
//                MB_printf("没有到保护时间")
//            }
        } else if (2 == baozistatemail_maistepstep_ll[1]) { // 先错开当前层 步进向上运动
            var fok = baozijimotormyconset(0, 0, 2, 1, 5)
            if (fok) {
                shoumaibaozistepadd_stepll(1)
            }
        } else if (3 == baozistatemail_maistepstep_ll[1]) {   // 加热板 步进电机 是否完成
            var fok = ifworkbaozijimotormyconset(0, 0)
            if (fok) {
                shoumaibaozistepadd_stepll(1)
            }
        } else if (4 == baozistatemail_maistepstep_ll[1]) {  // 加热室寻找有托盘的架子 送餐
            var fok = baozijimotormyconset(0, 0, 2, 1, 0) ////步进托盘检测原点
            // fok = baozizuhedongzuote(0, 1)
            if (fok) {
                shoumaibaozistepadd_stepll(1)
                // baozistatemail_ceng += 1
                // MB_printf('当前是 {} 层'.format(baozistatemail_ceng))
                // if baozistatemail_ceng>=len(baozistatemail)){
                //     MB_printf('没有包子了')
                //     baozistatemail_maistepstep_ll[1] = 0
            }
        } else if (5 == baozistatemail_maistepstep_ll[1]) { // // 加热室寻找有托盘的架子 是否完成
            // fok = ifwork_baozizuhedongzuote(0, 1)
            var fok = ifworkbaozijimotormyconset(0, 0)
            if (fok) {
                shoumaibaozistepadd_stepll(1)
            }
        } else if (6 == baozistatemail_maistepstep_ll[1]) {  // 加热室寻找有托盘的架子 送餐
            var fok = baozijimotormyconset(0, 0, 2, 1, 1)  // //步进托盘检测原点 托盘检测原点

            if (fok) {
                shoumaibaozistepadd_stepll(1)
            }
        } else if (7 == baozistatemail_maistepstep_ll[1]) {  // // 加热室寻找有托盘的架子 是否完成

            var fok = ifworkbaozijimotormyconset(0, 0)
            if (fok) {
                shoumaibaozistepadd_stepll(1)

                baozistatemail_ceng += 1
                MB_printf("当前是 ${baozistatemail_ceng} 层")
                if (baozistatemail_ceng >= (baozistatemail.size)) {
                    MB_printf("没有包子了 1")

                    baozistatemail_ceng = baozistatemail_ceng % ((baozistatemail.size))

                    if (getbaozishuliang() == true) {

                        baozistatemail_maistepstep_ll[1] = 0
                    } else {
                        MB_printf("还有包子  继续寻找")
                    }
                }
                // 判断一下这层是否有包子
                if (getbaozishuliang()) {
                    if (baozizhecengisyoubaozi() == false)
                        baozistatemail_maistepstep_ll[1] = 4 // 再去找有包子的层
                }
            }
        } else if (8 == baozistatemail_maistepstep_ll[1]) {  // 加热板 步进电机下60
            var fok = baozijimotormyconset(0, 0, 2, 2, 70)
            if (fok) {
                shoumaibaozistepadd_stepll(1)
            }
        } else if (9 == baozistatemail_maistepstep_ll[1]) {  // 加热板 步进电机下 是否完成
            var fok = ifworkbaozijimotormyconset(0, 0)
            if (fok) {
                shoumaibaozistepadd_stepll(1)
            }
        } else if (10 == baozistatemail_maistepstep_ll[1])
            return


    }

    private fun baozizhecengisyoubaozi(): Boolean {
        /*
        """
        这层是否有包子   有包子返回 True 并且设置推杆位置
        :return:
        """
        
         */
        var fok = false

        for (index in 0 until baozistatemail[baozistatemail_ceng].size) {
            if (baozistatemail[baozistatemail_ceng][index] == 1)//有包子
            {
                baozistatemail_cengtuiganweizhi = index
                fok = true
                break
            }
        }


        return fok

    }

    fun getbaozishuliang(startid: Int = 1): Boolean {
        // 还有没有包子

        return ifanybaozistatemailv(startid)
    }

    private fun shoumaibaozistepadd_stepll(i: Int) {
        baozistatemail_maistepstep_ll[i] += 1
    }

    fun baozishoumaijixuanpandongzuodef() {  //包子售卖机寻盘动作
        baozishoumaijixuanpandongzuo[0] =
            baozishoumaijixuanpandeftt(0, baozishoumaijixuanpandongzuo[0], 1, 0, 0)
        baozishoumaijixuanpandongzuo[1] =
            baozishoumaijixuanpandeftt(1, baozishoumaijixuanpandongzuo[1], 0, 0, 0)
        baozishoumaijixuanpandongzuo[2] =
            baozishoumaijixuanpandeftt(2, baozishoumaijixuanpandongzuo[2], 0, 1, 6)
        baozishoumaijixuanpandongzuo[3] =
            baozishoumaijixuanpandeftt(3, baozishoumaijixuanpandongzuo[3], 1, 1, 6)


    }


    fun baozishoumaijixuanpandeftt(
        indexx: Int,
        valuestep: Int,
        okvalue: Int,
        banzihao: Int,
        chuangnaqiweizhi: Int
    ): Int {
        /*
        寻盘动作
         */
        var valuestepres = valuestep
        if (valuestep > 0) //
        {
            if (valuestep == 1) {
                if (ifworkbaozijimotormyconset(banzihao, 0)) {
                    valuestepres += 1
                }
            } else if (valuestep == 2) {
                var fok = baozijimotormyconset(banzihao, 0, 2, 1, 0) //
                if (fok)
                    valuestepres += 1
            } else if (valuestep == 3) {
                var fok = ifworkbaozijimotormyconset(banzihao, 0)
                if (fok)
                    valuestepres += 1
            } else if (valuestep == 4) {

                var fok = baozijimotormyconset(banzihao, 0, 2, 1, 1) //
                if (fok)
                    valuestepres += 1
            } else if (valuestep == 5) {
                var fok = ifworkbaozijimotormyconset(banzihao, 0)
                if (fok)
                    valuestepres += 1
            } else if (valuestep == 6) {//判断是否有盘
                var fv = readbaozijistatechanqi(banzihao, 15, chuangnaqiweizhi)
                if (fv != null) {

                    if (fv == okvalue) //没有盘子
                        valuestepres = 0
                    else {
                        valuestepres = 1 //再次寻找
                    }
                    baozishoumaijixuanpandongzuo_num[indexx] += 1

                    // if baozishoumaijixuanpandongzuo_num[2] == 1:
                    //     baozishoumaijilengcengbaozishul = 0
                    //     MB_printf('清空冷藏室包子')

                    MB_printf(
                        "动作 ${indexx}  运行了 ${baozishoumaijixuanpandongzuo_num[indexx]} 次 "
                    )
                    if (banzihao == 0)//加热室
                    {
                        if (baozishoumaijixuanpandongzuo_num[indexx] > 12) {
                            //mqttt.play_audio('包子制作机加热室寻盘错误')
                            valuestepres = -1 //报错
                        }
                    } else {
                        if (baozishoumaijixuanpandongzuo_num[indexx] > 16) {
                            valuestepres = -1 //报错
                            //mqttt.play_audio('包子制作机冷藏室寻盘错误')
                        }
                    }
                    if (valuestepres == -1) {
                        baozishoumaijixuanpandongzuo_num[indexx] = 0
                        MB_printf("动作 执行次数超过正常值 报错")
                        playAudio("异常报警")
                        adt_checkmytest(1, arrayListOf(banzihao, 1, 2))  // 报警托盘运动错误
                    } else {
                        adt_checkmytest(1, arrayListOf(banzihao, 1, 0))  // 报警托盘运动错误恢复
                    }
                }
            }
        }
        return valuestepres
    }

}