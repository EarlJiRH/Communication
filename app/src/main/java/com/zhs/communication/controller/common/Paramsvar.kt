package com.zhs.communication.controller.common

open class Paramsvar {


    /*
     * can open 参数定义初始化

     */
    companion object {
        val canopennodeid: Int = 0x10 //节点id

        val to_bytes_big_little: String = "little" //数字转can发送字节 对齐方式   big 大段  little 小端

//        val STATE_FREE:Int = 0   // 空闲状态


        // LOCAL SDO
        val LOCALSDO1_COB_ID_Client_TRANSMIT: Int = 0x666
        val LOCALSDO1_COB_ID_Client_RECEIVE: Int = 0x667
        val LOCALSDO1_NODE_ID_OF_THE_SERVER: Int = 0x99  // 本地sdo

        // SDO
        val SDO1_COB_ID_Client_TRANSMIT: Int = 0x601
        val SDO1_COB_ID_Client_RECEIVE: Int = 0x602
        val SDO1_NODE_ID_OF_THE_SERVER: Int = 0x10           // 炒饭机 炒饭层

        val SDO2_COB_ID_Client_TRANSMIT: Int = 0x603
        val SDO2_COB_ID_Client_RECEIVE: Int = 0x604
        val SDO2_NODE_ID_OF_THE_SERVER: Int = 0x11          // 炒饭机 炒饭层     汤饮机  豆浆

        val SDO3_COB_ID_Client_TRANSMIT: Int = 0x611
        val SDO3_COB_ID_Client_RECEIVE: Int = 0x612
        val SDO3_NODE_ID_OF_THE_SERVER: Int = 0x20         // 炒饭机 推饭层    汤饮机   绿豆

        val SDO4_COB_ID_Client_TRANSMIT: Int = 0x613
        val SDO4_COB_ID_Client_RECEIVE: Int = 0x614
        val SDO4_NODE_ID_OF_THE_SERVER: Int = 0x21         // 炒饭机 推饭层   汤饮机    公用

        val SDO5_COB_ID_Client_TRANSMIT: Int = 0x651
        val SDO5_COB_ID_Client_RECEIVE: Int = 0x652
        val SDO5_NODE_ID_OF_THE_SERVER: Int = 0x65     // 炒饭车的底层控制     汤饮机    打鸡蛋 // 包子 打包售卖

        val SDO6_COB_ID_Client_TRANSMIT: Int = 0x621
        val SDO6_COB_ID_Client_RECEIVE: Int = 0x622
        val SDO6_NODE_ID_OF_THE_SERVER: Int = 0x62    //  炒饭车的辅料层控制  // 包子车的加热控制

        val SDO7_COB_ID_Client_TRANSMIT: Int = 0x623
        val SDO7_COB_ID_Client_RECEIVE: Int = 0x624
        val SDO7_NODE_ID_OF_THE_SERVER: Int = 0x63      //炒饭车的打鸡蛋层控制    // 包子车的冷藏控制 汤饮机 紫菜

        val SDO8_COB_ID_Client_TRANSMIT: Int = 0x625
        val SDO8_COB_ID_Client_RECEIVE: Int = 0x626
        val SDO8_NODE_ID_OF_THE_SERVER: Int = 0x64  // 炒饭车的右门控制

        val SDO_LIST_CLIENT_MAP = mapOf<Int, Pair<Int, Int>>(
            SDO1_NODE_ID_OF_THE_SERVER to Pair(
                SDO1_COB_ID_Client_TRANSMIT,
                SDO1_COB_ID_Client_RECEIVE
            ),
            SDO2_NODE_ID_OF_THE_SERVER to Pair(
                SDO2_COB_ID_Client_TRANSMIT,
                SDO2_COB_ID_Client_RECEIVE
            ),
            SDO3_NODE_ID_OF_THE_SERVER to Pair(
                SDO3_COB_ID_Client_TRANSMIT,
                SDO3_COB_ID_Client_RECEIVE
            ),
            SDO4_NODE_ID_OF_THE_SERVER to Pair(
                SDO4_COB_ID_Client_TRANSMIT,
                SDO4_COB_ID_Client_RECEIVE
            ),
            SDO5_NODE_ID_OF_THE_SERVER to Pair(
                SDO5_COB_ID_Client_TRANSMIT,
                SDO5_COB_ID_Client_RECEIVE
            ),
            SDO6_NODE_ID_OF_THE_SERVER to Pair(
                SDO6_COB_ID_Client_TRANSMIT,
                SDO6_COB_ID_Client_RECEIVE
            ),
            SDO7_NODE_ID_OF_THE_SERVER to Pair(
                SDO7_COB_ID_Client_TRANSMIT,
                SDO7_COB_ID_Client_RECEIVE
            ),
            SDO8_NODE_ID_OF_THE_SERVER to Pair(
                SDO8_COB_ID_Client_TRANSMIT,
                SDO8_COB_ID_Client_RECEIVE
            ),
        )

        val SDO_LIST_SERVER_MAP = mapOf<Int, Pair<Int, Int>>(
            LOCALSDO1_NODE_ID_OF_THE_SERVER to Pair(
                LOCALSDO1_COB_ID_Client_TRANSMIT,
                LOCALSDO1_COB_ID_Client_RECEIVE
            ),
        )


        val SDO_NODE_ID_OF_THE_SERVER_LIST = SDO_LIST_CLIENT_MAP.keys
    }

    init {
        println("初始化  ${1}.")
    }
}