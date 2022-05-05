package com.zhs.communication.controller.meals

import com.zhs.communication.controller.common.FoodCart
import com.zhs.communication.controller.foodcartmqtt.FoodCarMqtt

open class process_control_shoumaiji_md_var : FoodCart() {

    /**
     * 包子售卖机的用到的 全局变量
     */
    var localsdolistalll: MutableMap<String, Int> = mutableMapOf() //本地sdo 收到的信号处理

    var BZJR_Value_tiaozhenzhizuo = arrayOf(7000, 5000, 10000, 7000, 7000, 180, 480, 900)
//    =[
//    ['加蒸汽到加热箱开关   关时间', 7000, '毫秒' ], //0
//    ['加蒸汽到加热箱开关   开时间', 5000, '毫秒' ], //1
//    ['加热箱排水开关       开时间', 10000, '毫秒'], //2
//    ['蒸汽发生器排污开关   关时间', 7000, '毫秒' ], //3
//    ['蒸汽发生器排污开关   开时间', 7000, '毫秒' ], //4
//    ['蒸汽发发生器排污时间',        180, '秒' ], //5
//    ['准备蒸汽时间',                480, '秒' ], //6
//    ['蒸包子时间  ',                900, '秒' ], //7
//
//
//
//    ]


    //    var oldprinttimeshow_if_lock = 0
//    var showprinttimes = 5 //5s 打印一次日志
//
//    var zhongduanbuzhoujilu_flag = false
//    var zhongduanbuzhoujilu_flag_2 = false
//    var zhongduanbuzhoujilu = arrayListOf<Int>(-1, -1,-1,-1,0 ) //通道 那个步骤的那一步 小步骤 最后位是打断后要运行的步骤
//
//    var rxcanopensdodata = 0 //收到下面板子sdo的数据  包子售卖机 按键信号数据
//
    // 板子的 id
    val banzishoumaisdoindex = arrayListOf(0x62, 0x63, 0x65)

    // 写板子控制电机命令的上一条指令的时间
    var banzishoumaiwritemotoroldtime = arrayListOf<Long>(0, 0, 0)
    var banzishoumaiwritemotoroldtime_cha = 300 //300 ms  写电机控制 差  300 毫秒  写卡快了  下面处理不对


    var channel_state_tt = arrayListOf<Int>(0, 0, 0, 0, 0)

    var baozishoumaijixuanpandongzuo = arrayListOf<Int>(0, 0, 0, 0, 0) //加热室和冷藏室寻找盘动作
    var baozishoumaijixuanpandongzuo_num = arrayListOf<Int>(0, 0, 0, 0, 0) //加热室和冷藏室寻找盘动作 执行次数
    var baozishoumaijikongtkaiison = false //记录包子售卖机空调状态 True 打开
    var baozishoumaijilengcengbaozishul = 0

    //var maibaozigeshu_quantity = 1 //买包子数量
    var baozilc2jrweizhipos = arrayListOf<Int>(0, 0, 0, 0, 0)
    var baozistatemail = Array(6) { IntArray(5) }//0 没有包子  1 有包子  2 包子买了
    //    baozistatemail[0][1]=0//[0,0,0,0,0], //包子的位置  每层5个 一共6层

    //    [0,0,0,0,0],
    //    [0,0,0,0,0],
    //    [0,0,0,0,0],
    //    [0,0,0,0,0],
    //    [0,0,0,0,0],
    //    ) // 0-5 包子是否被卖出
    var baozilc2jrokok = false //冷藏到加热是否完成
    var baozijr2jrokok = false //包子加热是否完成
    var baozidabaobujingposlist = arrayListOf<Int>(47, 5, 100, 195, 280, 345)


    var baozistatexiaobujindabaoguilingt = 0 //准备纸袋是否要归零小步进打包板
    var baozistatexiaobujindabaoguilingtguilintime: Long = 0 //归零time

    var baozistatemail_ceng = -1 //当前是在那一层
    var baozistatemail_ceng_fxiaiyiceng = 0 //要错开当前的层
    var baozistatemail_cengtuiganweizhi = 0 //当前的推杆位置
    var baozistatemail_tuiganhuiqutimetim: Long = 0 //推杆收回开始的时间
    var baozistatemail_maistepstep = 0 //买包子步骤
    var baozistatemail_maistepstep_ll = arrayListOf(0, 0, 0) //买包子步骤的准备纸袋步骤
    var baozistatemail_maistepstep_settimeout: Long = 0 //买包子步骤中添加延时
    var baozistatemail_maistepstep_settimeoutkaimentime: Long = 0 //加热室开门时间
    var baozistatemail_maistepstep_settimeoutkaimentime_guan: Long = 0 //加热室开门时间
    var baozistatemail_maistepstep_settimeoutkaimentimenum = 0 //加热室开门次数
    var baozistatemail_maistepstep_settimeoutkaimentimenum_guan = 0 //加热室开门次数
    var baozistatemail_maistepstep_dbshoumaidianjixiaobujinguiweipanduan: Long =
        0 //打包板小步进电机控制 归位判断  小步进电机控制位置运动错误的判断
    var baozistatemail_maistepstep_settimett: Long = 0 //买包子读取包子又没没有被取走
    var baozistatemail_num = 0 //接到包子数量

    var shoumaibaoziprintoldtime: Long = 0 //调试打印信息


    var mqttrunttapt: FoodCarMqtt? = null

    fun setmy_baozistatemail_allnum(value: Int) {

        for (i in baozistatemail.indices) {
            for (j in baozistatemail[i].indices) {
                baozistatemail[i][j] = value
            }

        }

    }

    fun adt_checkmytest(i: Int, arrayListOf: ArrayList<Int>) {
        mqttrunttapt!!.check_data_adt_adt_sendjson(
            i,
            mutableListOf(0, arrayListOf[0], arrayListOf[1], arrayListOf[2])
        )
    }

}