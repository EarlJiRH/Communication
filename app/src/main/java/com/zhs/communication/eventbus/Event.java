package com.zhs.communication.eventbus;

/**
 * ================================================
 * 作    者：Jirh
 * 版    本：1.0
 * 创建日期：2019/4/18
 * 描    述：EventBus事件总线的Base实体
 * 修订历史：
 * ================================================
 */
public class Event<T> {

    private int code;
    private T data;

    public Event(int code) {
        this.code = code;
    }

    public Event(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
