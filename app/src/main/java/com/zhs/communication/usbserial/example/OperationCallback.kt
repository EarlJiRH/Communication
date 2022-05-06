package com.zhs.communication.usbserial.example

/**
 * ================================================
 * 类名：com.zhs.communication.usbserial.example
 * 时间：2022/5/6 13:51
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 * ================================================
 * @author Admin
 */
interface OperationCallback {
    /**负责日志操作
     * @param log 日常日志
     * */
    fun printLog(log: String) {

    }

    /**负责语音消息相关处理
     *@param message 语音消息内容
     * */
    fun playAudioMessage(message: String) {

    }

    /**负责提交异常日志
     * @param log 异常日志
     */
    fun commitErrorLog(log: String) {


    }
}