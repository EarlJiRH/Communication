package com.zhs.communication.controller.common

import android.os.Environment
import com.zhs.communication.usbserial.example.Network
import com.zhs.communication.usbserial.example.SdoServerCallbackImpl
import com.zhs.communication.usbserial.example.canopen.sdo.DataDot
import com.zhs.communication.usbserial.example.canopen.sdo.SdoClient
import com.zhs.communication.usbserial.example.canopen.sdo.SdoServer
import com.zhs.communication.utils.byte2unit
import com.zhs.communication.utils.int2byte
import java.io.File

open class FoodCart : Paramsvar() {

    /*
     * can open 读写操作

     */

    companion object {
        var network = Network()

        val rootDir: String = Environment.getExternalStorageDirectory().absolutePath + "/kotlinmy"
        val audioPathDir: String = "$rootDir/audiofile"
        val htmlPathDir: String = "$rootDir/htmls"
        val logPath: String = "$rootDir/logs"
        val mqttConfigPath: String = "$rootDir/mqttconfig.json"
        val logFilePath: String = "$logPath/log.txt"

        val logFile: File = File(logFilePath)

        fun getLogFileText(): String {
            return if (logFile.exists()) {
                logFile.readText()
            } else {
                "null"
            }
        }
    }


    fun sdowriteindex(
        nodeidoftheserver: Int,
        index: Int = 0x2020,
        subindex: Int = 1,
        data: ByteArray
    ): Boolean {


        /*
        写 sdo   主要发送控制命令
        :param nodeidoftheserver: 找对应的sdo server
        :param index:
        :param subindex:
        :param data:
        :return:
         */
        for (cli in SDO_NODE_ID_OF_THE_SERVER_LIST) {
            if (cli == nodeidoftheserver) {
                val fok = SDO_LIST_CLIENT_MAP[cli]?.second?.let {
                    network.getsubscriberclicli(it)
                        ?.download(index, subindex, data)
                }
                return fok == true
            }
        }
        println("没有这个sdo server 不能写入 {}".format((nodeidoftheserver.toString(16))))
        return false
    }


    /**CANopen 通信*/
    fun sdoReadIndex(nodeidoftheserver: Int, index: Int = 0x2020, subindex: Int = 1): Int? {


        /*"""
            写 sdo   主要发送读取数据命令
            :param nodeidoftheserver: 找对应的sdo server
            :param index:
            :param subindex:
            :return: True成功, False 失败
            """

         */

//        SDO_NODE_ID_OF_THE_SERVER_list.forEach {
//            if (it==nodeidoftheserver){
//
//            }
//        }
        for (cli in SDO_NODE_ID_OF_THE_SERVER_LIST) {
            if (cli == nodeidoftheserver) {
//                System.out.println("sdoreadindex=${cli.toString(16)}")

                val datab = SDO_LIST_CLIENT_MAP[cli]?.second?.let {
                    network.getsubscriberclicli(it)
                        ?.upload(index, subindex)
                }
                if (datab != null) {
                    if (datab.isNotEmpty()) {
                        when (datab.size) {
                            1 -> {
                                return byte2unit(datab[0])
                            }
                            2 -> {
                                return ((byte2unit(datab[1]) shl 8) or (byte2unit(
                                    datab[0]
                                )))
                            }

                            4 -> {
                                return ((byte2unit(datab[3]) shl 24) or
                                        (byte2unit(datab[2]) shl 16) or
                                        (byte2unit(datab[1]) shl 8) or
                                        (byte2unit(datab[0])))
                            }
                            else -> {
                                println("这个数据为太多了不处理")
                                return null
                            }

                        }
                    }
                }

            }
        }
        println(
            "没有这个sdo server 读 ${nodeidoftheserver.toString(16)}  index=${index.toString(16)} subindex=${subindex}"
        )
        return null
    }

    fun sdowriteindex_uintone(
        nodeidoftheserver: Int,
        index: Int = 0x2020,
        subindex: Int = 1,
        uintone: Int = 0,
        byteslen: Int = 1
    ): Boolean {


        val data = ByteArray(byteslen)
        when (byteslen) {
            1 -> {
                data[0] = int2byte(uintone and 0xff)
            }
            2 -> {
                data[1] = int2byte((uintone and 0xff00) shr 8)
                data[0] = int2byte(uintone and 0xff)
            }

            4 -> {
                data[3] = int2byte((uintone and 0xff000000.toInt()) shr 14)
                data[2] = int2byte((uintone and 0xff0000) shr 16)
                data[1] = int2byte((uintone and 0xff00) shr 8)
                data[0] = int2byte(uintone and 0xff)
            }
            else -> {
                println("这个数据为太多了不处理 11")
                return false
            }

        }



        return sdowriteindex(
            nodeidoftheserver = nodeidoftheserver,
            index = index, subindex = subindex, data = data
        )
    }

    fun sdowriteindex_uint_array(
        nodeidoftheserver: Int,
        index: Int = 0x2020,
        subindex: Int = 1,
        array: ArrayList<Int>,
        byteslen: Int = 1
    ): Boolean {

        if (array.size < 1) {
            return false
        }

        val datas = ByteArray(array.size)



        for (ii in 0 until array.size) {
            datas[ii] = int2byte(array[ii] and 0xff)
        }

        return sdowriteindex(
            nodeidoftheserver = nodeidoftheserver,
            index = index, subindex = subindex, data = datas
        )
    }


    open fun playAudio(name1: String, name2: String? = null) {

    }

    private fun addloclvarsdoserver_1(t: SdoServer) {
        //测试添加变量
        val ttc = SdoServerCallbackImpl()
        val td = DataDot(index = 0x2010, subIndex = 1, dataSize = 1, data = 1, callback = ttc)
        t.local_data.add(td)
    }


    fun logfileappendtxt(s: String) {

        if (logFile.exists()) {
            logFile.appendText(s)
//            logfilemy.
        }
    }

    fun createLogPath() {

        println("createmylogpath---")

        val rootDir: File = File(rootDir)
        val logdirmy: File = File(logPath)
//        var logfilemy: File = File(logpathfilemy)
        if (!rootDir.exists()) {
            val isDir = rootDir.mkdir()
            if (!isDir) {
//                Toast.makeText(, "创建文件夹失败", Toast.LENGTH_SHORT).show()
                println("创建文件夹失败$rootDir")
                return
            }
        }
        if (!logdirmy.exists()) {
            val isloddir = logdirmy.mkdir()
//            var isFile = logdirmy.createNewFile()
            if (isloddir) {
                println("创建文件log成功$logPath")
//                Toast.makeText(this@audiotestMainActivity3, "创建文件成功", Toast.LENGTH_SHORT).show()
            } else {
                println("创建文件 log失败$logPath")
//                Toast.makeText(this@audiotestMainActivity3, "创建文件失败", Toast.LENGTH_SHORT).show()
                return
            }
        }



        if (!logFile.exists()) {

            val isFile = logFile.createNewFile()
            if (isFile) {
                println("创建文件log成功$logFilePath")
//                Toast.makeText(this@audiotestMainActivity3, "创建文件成功", Toast.LENGTH_SHORT).show()
            } else {
                println("创建文件 log失败$logFilePath")
//                Toast.makeText(this@audiotestMainActivity3, "创建文件失败", Toast.LENGTH_SHORT).show()
                return
            }
        }
//        else{
//            System.out.println("删除log 文件")
//            logfilemy.delete()
//            System.out.println("logwenjian 存在 重建"+logpathfilemy)
//            var isFile = logfilemy.createNewFile()
//            if (isFile) {
//                System.out.println("创建文件log成功"+logpathfilemy)
//
////                Toast.makeText(this@audiotestMainActivity3, "创建文件成功", Toast.LENGTH_SHORT).show()
//            } else {
//                System.out.println("创建文件 log失败"+logpathfilemy)
////                Toast.makeText(this@audiotestMainActivity3, "创建文件失败", Toast.LENGTH_SHORT).show()
//                return
//            }
//        }
//        logfileappendtxt("你好  啊  log")
        if (logFile.exists()) {
            logFile.writeText("你好 啊\r\n")
        }

    }

    init {

//        createmylogpath()


        println("初始化  ${2}.")
        //初始化canopen cli
        for (sdocli in SDO_LIST_CLIENT_MAP) {
            val cli = SdoClient(sdocli.value.first, sdocli.value.second)
            cli.network = network
            network.addsubscriber(cli)
        }

        for (sdoser in SDO_LIST_SERVER_MAP) {
            val ser = SdoServer(sdoser.value.first, sdoser.value.second)
            ser.networkmy = network
            network.addsubscriberserver(ser)
            addloclvarsdoserver_1(ser)
        }


    }

}

