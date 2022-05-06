package com.zhs.communication.controller.meals

import com.zhs.communication.utils.getCurrentMillis

open class process_control_md_constep_steprun : process_control_shoumaiji_md_constep(){

    /**
     * 包子售卖机的单个步骤具体控制
     */

    var errmotorerrprinttime:Long = 0


    fun baozishoumaijicanreadtest(): Boolean {
        //测试can 通信
//        var banzisdoindex = arrayOf(0x62, 0x63, 0x65)

        val fok1 = sdowriteindex_uintone(banzishoumaisdoindex[0], 0x2010, 1, 0)
        val fok2 = sdowriteindex_uintone(banzishoumaisdoindex[1], 0x2010, 1, 0)
        val fok3 = sdowriteindex_uintone(banzishoumaisdoindex[2], 0x2010, 1, 0)

        MB_printf("baozi can test ${fok1}, ${fok2}, ${fok3}")
        return fok1 == true and fok2 == true and fok3 == true
    }
    
    fun set_setp_run_nusm_my(runnum: Int) {

        jilvsrununceshu = runnum
        jilvsrununceshu_cut = 0

        MB_printf("设置运行的次数 $jilvsrununceshu 次")
        myorder_step_add()
    }

    fun ifsetrunnumisok(runstep0:Int,runstep1:Int) {
        jilvsrununceshu_cut += 1
        MB_printf("当前运行了 ${jilvsrununceshu_cut}次")
        if (jilvsrununceshu_cut >= jilvsrununceshu){
      
            myorder_step_add()
            MB_printf("运行次数够了")
        }
        else {
            if (runstep0 > 0){ // 0 自己步骤内运行
                my_step_id_set(runstep0)  // 自己调试用
            }
            my_step1_set(runstep1)
            MB_printf("运行次数不够 跳转到 ${runstep0} 的 ${runstep1}")
        }
        //  Set_setp_run_num = 198  // 设置动作运行的次数
        //  if_steprunnumisok = 199  // 判读运行的次数是否到了 到了就下一步  没到就 去设置的步骤 参数1 要去的大步骤 参数2 要去的小步骤
    }
    fun ifsetrunnumisok_ADD2() {

         MB_printf("当前运行了 ${jilvsrununceshu_cut}次")
         if  (jilvsrununceshu_cut >=  jilvsrununceshu - 1){
             myorder_step_add()
             myorder_step_add()
             MB_printf("运行次数够了 11")
         }
         
        else{
             myorder_step_add()
             MB_printf("运行次数不够 跳转到11 ")
        }
         

    }


    open fun updateazhiuzocanshu(){
        MB_printf("这样要取更新参数")
    }

    fun setbaozizhunbeizhengqishijian(tims:Int) {
        /*
        """
            设置准备加热蒸汽时间
            :return:
        """
         */
        BZJR_Value_tiaozhenzhizuo[6] = tims

        updateazhiuzocanshu()

    }

    fun baozijizhengqifashengqipwuif() {
        /*
        """
        判断蒸汽发生气是否要排污
        :return:
        """

        //TODO
        return True
        
         */
    }

    fun baozijimotormyconset(banzi: Int, dianjihao: Int, konzhifanshi: Int, dirdd: Int, params: Int): Boolean {
        //"""
        //        包子电机控制
        //        :param banzi: 板子号  0 加热  1 冷藏 2 第三个板子
        //        :param dianjihao:  电机号
        //        :param konzhifanshi: 控制方式 01  时间控制    02 位置     05 加热板才有  控制mos管加热蒸汽
        //        :param dirdd:   方向                                         加热板才有  0 断电  1 关  2 开
        //        :param params:  参数   时间获取位置的参数                    加热板才有  0 一直维持这个状态  大于0  就是以 0.1 s 乘上这个数让后断电
        //        :return:
        //        """
        var cuttime = getCurrentMillis()
        if (cuttime-banzishoumaiwritemotoroldtime[banzi] >banzishoumaiwritemotoroldtime_cha ) {
            banzishoumaiwritemotoroldtime[banzi] = cuttime
            var canshu = arrayListOf<Int>(
                dianjihao,
                1,
                0,
                konzhifanshi,
                dirdd,
                ((params and 0xff00) shr 8),
                (params and 0xff)
            )
            var fok = sdowriteindex_uint_array(banzishoumaisdoindex[banzi], 0x2020, 1, canshu)


            MB_printf("包子机控制电机 $fok banzi=${banzishoumaisdoindex[banzi].toString(16)} ${dianjihao}-${1}-${0}-${konzhifanshi}-${dirdd}-${((params and 0xff00) shr 8)}-${(params and 0xff)}")

            return fok
        }
        return false
    }
    fun ifworkbaozijimotormyconset(banzihao:Int,dianjihao:Int):Boolean {
        /*

        """
            包子电机控制 是否空闲
            :param banzihao:
            :param dianjihao:
            :return:
            """

         */
//        var banzisdoindex = arrayListOf<Int>(0x62,0x63,0x65)
        var resr = sdoReadIndex(nodeidoftheserver = banzishoumaisdoindex[banzihao],
            index = 0x2010, subindex = dianjihao+1)
        if (resr!=null){
            if (resr == 0) {
                adt_checkmytest(4, arrayListOf<Int>(banzihao, dianjihao, 0))
                return true
            }
            else {


                if ((resr and 0xf0)>0) {
                    if (getCurrentMillis()-errmotorerrprinttime>2000) {
                        errmotorerrprinttime = getCurrentMillis()
                        MB_printf(
                            "$banzihao  电机运动错误 ${banzishoumaisdoindex[banzihao].toString(16)} " +
                                    "errcode=${resr.toString(16)}  " +
                                    "banzihao=${banzihao} dianjihao=${dianjihao}"
                        )
                    }

//                    if stateset_1steatdef is not None :
//                    stateset_1steatdef(
//                        "{} {} {}号电机运动错误 errcode={}".format(2, banzihao, dianjihao, hex(intdata))
//                    )

                    adt_checkmytest(4, arrayListOf<Int>(banzihao, dianjihao, resr))
                }
                else {
                    adt_checkmytest(4, arrayListOf<Int>(banzihao, dianjihao, 0))
                }
            }
        }
        return false
    }

    fun baozizuhedongzuote_2(banzihao:Int,dongzuoindex:Int,writedata:Int = 1):Boolean {

        /*
    """
        包子售卖机
        :param banzihao: 0  0 寻找加热室无盘托架 |  0 1   寻找加热室有盘托架   |  1  0 寻找冷藏室有盘托架 |  1  1 寻找冷藏室无盘托架
        :param dongzuoindex:
        :param writedata: 1 执行 0 停止
        :return:
        """
        
     */
        var fok = true

        baozishoumaijixuanpandongzuo[banzihao * 2 + dongzuoindex] = writedata

        if (writedata == 0) {
            baozishoumaijixuanpandongzuo_num[banzihao * 2 + dongzuoindex] = 0
        }
        return fok
    }
    fun ifwork_baozizuhedongzuote_2(banzihao:Int,dongzuoindex:Int,nexstep:Int):Boolean {

        /*
        """
            组合动作控制 是否工作完成
            :param banzihao:
            :param dongzuoindex:
            :return:
            """
            
         */
        if (baozishoumaijixuanpandongzuo[banzihao * 2 + dongzuoindex] == 0) {
            return true
        } else if (baozishoumaijixuanpandongzuo[banzihao * 2 + dongzuoindex] < 0) {
            my_step1_set(my_step1_get() + nexstep)
            my_step2_set(0)
            MB_printf("错误后跳转到下 ${nexstep} 步")
        }
        return false
    }
    fun readbaozijistatechanqi(banzihao:Int,index:Int,subindex:Int):Int? {
        /*
        """
        包子电机控制 是否空闲
        :param banzihao:
        :param index: 2010 0-15  subindex 0-7  每一位表示一位
        :return:
        """
        
         */
//        var banzisdoindex = arrayListOf<Int>(0x62,0x63,0x65)
        
        var resv = sdoReadIndex(nodeidoftheserver = banzishoumaisdoindex[banzihao],
        index = 0x2010, subindex = index+1,
        )
        
        if (resv!=null) {
            var value = 0
            if (subindex < 8) {
                value = (((1 shl subindex) and resv) shr subindex)
            }
            else {
                value = resv
            }
            
            return value
        }

        return null
    }

    fun shoumaibaozi_jilulcshibaozi(nexstep:Int,f1:Int) {
        var value = readbaozijistatechanqi(1, 15, 8)
        if (value!=null){
            MB_printf("运动次数 冷藏室托盘 ${baozishoumaijixuanpandongzuo_num[4]} 包子状态${value.toString(2)}")
            
            if (baozishoumaijixuanpandongzuo_num[4] == 0)
                baozishoumaijilengcengbaozishul = 0
                MB_printf("清空冷藏室包子")
            baozishoumaijixuanpandongzuo_num[4] += 1
            if ((value and 0x02) > 0)
                baozishoumaijilengcengbaozishul += 1
            if ((value and 0x04) > 0)
                baozishoumaijilengcengbaozishul += 1
            if ((value and 0x08) > 0)
                baozishoumaijilengcengbaozishul += 1
            if ((value and 0x10) > 0)
                baozishoumaijilengcengbaozishul += 1
            if ((value and 0x20) > 0)
                baozishoumaijilengcengbaozishul += 1
            if (baozishoumaijixuanpandongzuo_num[4] >= 8) {
                my_step1_set(my_step1_get() + nexstep)


                if (baozishoumaijikongtkaiison) //空调已打开
                {
                    if (baozishoumaijilengcengbaozishul == 0) {
                        baozijimotormyconset(2, 6, 1, 1, 500)  // 空调开关
                        baozishoumaijikongtkaiison = false
                        playAudio("空调已关闭")

                        adt_checkmytest(1, arrayListOf(1, 0, 1)) //报警没有包子了
                    }
                    else {
                        adt_checkmytest(1,arrayListOf (1, 0, 0))  // 恢复报警还有包子
                    }
                }
                else {
                    //空调关闭
                    if (baozishoumaijilengcengbaozishul > 0){
                        
                    
                        baozijimotormyconset(2, 6, 1, 1, 500)  // 空调开关
                        baozishoumaijikongtkaiison = true
                        playAudio("空调已打开")
                        adt_checkmytest(1, arrayListOf(1, 0, 0)) //恢复报警还有包子
                    }
                    else {
                        adt_checkmytest(1, arrayListOf(1, 0, 1))  // 报警没有包子了
                    }
                    
                }
            }
            else {
                my_step1_set(my_step1_get() - f1)
                
            }
            my_step2_set(0)
        }
    }
    fun shoumaibaozi_init(): Boolean {
        baozistatemail_num = 0
        baozistatemail_maistepstep = 0
        return true
    }

    fun ifanybaozistatemailv(value:Int):Boolean{
        for (i in 0 until baozistatemail.size){
            for (j in 0 until baozistatemail[i].size){
                if (baozistatemail[i][j]== value){
                    return true
                }
            }

        }
        return false
    }

    fun ifanybaozistatemailvnum(value:Int=1):Int{
        var num = 0
        for (i in 0 until baozistatemail.size){
            for (j in 0 until baozistatemail[i].size){
                if (baozistatemail[i][j]== value){
                    num+=1
                }
            }

        }
        return num
    }

    fun ifbaozimaiwanl2lc():Boolean{
        
    /*
    """
        只能再order_main 里调用
        :return:
        """
        
     */
//        tt = np.array(baozistatemail).reshape(-1)

        if (ifanybaozistatemailv(1))//any(tt == 1):  // 还有没有买的包子就要  准备纸袋
        {
            return false
        }
        else{


            MB_printf("没有包子了  要转运托盘到冷藏室")
            baozistatemail_maistepstep_ll[0] = 0
            baozistatemail_maistepstep_ll[1] = 0
            // defsetrunstepmytt(1, 36, WORK_STEP1, 0, 0, 0) //这里调用这个没用
            myorder_step[1][0] = WORK_STEP1  ////菜单运行步骤1，判断菜单是否启动
            myorder_step[1][1] = 0  ////菜单数组myorder执行步骤，指向菜单动作
            myorder_step[1][2] = 0  ////菜单运行动作控制，保证一套动作按顺序完成
            myorder_step[1][9] = 36  // 菜单id

            baozistatemail_ceng = -1
            // print("---11-",myorder_step_saveth)
            return true
        }
    }




    fun bozijilvweizhishifouyoubaozi():Boolean {
        /*"""
        记录冷藏室中包子的位置
        :return:
        """

     */
        var  value = readbaozijistatechanqi(1, 15, 8)
        if (value!=null) {
            for (indexx in 0 until (baozilc2jrweizhipos.size))
            {
                baozilc2jrweizhipos[indexx] = (value and (1 shl (indexx+1))) shr (indexx+1)
                MB_printf("冷藏一个托盘包子位置 ${indexx} ${baozilc2jrweizhipos[indexx]}")
            }
            return true
        }
        return false
    }
    fun bozijilvweizhishifouyoubaozi_write():Boolean {
        /*"""
        加热室保存包子位置
        :return:
        """

     */
        MB_printf("包子层数 ${jilvsrununceshu_cut}")
        if (jilvsrununceshu_cut < (baozistatemail.size)) {
            for (ii in 0 until ((baozilc2jrweizhipos.size))) {
                baozistatemail[jilvsrununceshu_cut][ii] = baozilc2jrweizhipos[ii]
//                MB_printf("包子位置 ${baozistatemail}")
            }
            return true

        }
        return false
    }



    fun getshowstrbaozisgate(): String {
        //获取包子售卖机 的状态转字符串
        var showstr = ""
        if (baozijr2jrokok) {
            showstr += "加热完成\n"
        }
        else{
            showstr += "没有加热\n"
        }

        if(baozilc2jrokok){
            showstr += "运输完成可以加热\n"
        }
        else{
            showstr += "运输没有完成不可以加热\n"
        }


        var shengyushul = ifanybaozistatemailvnum()

        showstr = showstr + "\n剩余包子数   ${shengyushul}\n" +
                "当前在第${baozistatemail_ceng + 1}层\n" +
                "当前的推杆位置${baozistatemail_cengtuiganweizhi + 1}\n" +
                "冷藏室包子数量${baozishoumaijilengcengbaozishul}\n" +
                (baozijr2jrokok.toString())

        for (i in baozistatemail) {
            showstr = showstr + "\n"
            for (j in i) {
                showstr = showstr + (j.toString()) + " "
            }
        }
        MB_printf(showstr)
        return showstr
    }

    fun setbaoziweizhi11_test(){
        //测试用的
        baozistatemail[0][2]=1
        baozistatemail[1][3]=1
        baozijr2jrokok = true
    }

    fun setbaoziweizhi11(value:Int = 0) {
        // 设置包子售卖的状态
        for (i in 0 until baozistatemail.size){
            for (j in 0 until baozistatemail[i].size){
                baozistatemail[i][j]=value
            }
        }
    }


}