package com.zhs.communication.eventbus

/**
 * ================================================
 * 类名：com.zhs.communication.eventbus
 * 时间：2022/4/29 13:57
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 * ================================================
 * @author Admin
 */
data class LogMessage(
    var message: String? = "测试数据",
    var level: MessageLevel? = MessageLevel.Debug
)

enum class MessageLevel {
    Verbose, Debug, Info, Warn, Error, Assert
}

data class SpeakMessage(
    var speak: String = "测试消息",
)


