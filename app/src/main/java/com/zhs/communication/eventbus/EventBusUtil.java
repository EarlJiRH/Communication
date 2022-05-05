package com.zhs.communication.eventbus;

import org.greenrobot.eventbus.EventBus;

/**
 * ================================================
 * 作    者：Jirh
 * 版    本：1.0
 * 创建日期：2019/4/18
 * 描    述：EventBus封装工具类
 * 修订历史：
 * ================================================
 */
public class EventBusUtil {
    /**
     * 注册EventBus
     */
    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    /**
     * 解绑EventBus
     */
    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    /**
     * 分发EventBus普通事件
     */
    public static void sendEvent(Event event) {
        EventBus.getDefault().post(event);
    }

    /**
     * 分发EventBus粘性事件
     */
    public static void sendStickyEvent(Event event) {
        EventBus.getDefault().postSticky(event);
    }

}
