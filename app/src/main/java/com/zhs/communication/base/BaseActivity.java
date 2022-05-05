package com.zhs.communication.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zhs.communication.eventbus.Event;
import com.zhs.communication.eventbus.EventBusUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * ================================================
 * 作    者：Admin
 * 版    本：1.0
 * 创建日期：2019/4/18
 * 描    述：添加EventBus和ButterKnife逻辑的BaseActivity
 * 修订历史：
 * ================================================
 */
public abstract class BaseActivity extends AppCompatActivity {

//    public final String TAG = this.getClass().getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isRegisterEventBus()) {
            EventBusUtil.register(this);
        }
        setContentView(getContentViewResId());
        init(savedInstanceState);
    }

    /**
     * 获取资源文件
     *
     * @return 资源文件地址
     */
    public abstract int getContentViewResId();

    /**
     * 初始化页面数据操作
     *
     * @param savedInstanceState Bundle
     */
    public abstract void init(Bundle savedInstanceState);

    /**
     * 是否注册事件分发
     *
     * @return true绑定EventBus事件分发，默认不绑定，子类需要绑定的话复写此方法返回true.
     */
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusCome(Event event) {
        if (event != null) {
            receiveEvent(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyEventBusCome(Event event) {
        if (event != null) {
            receiveStickyEvent(event);
        }
    }

    /**
     * 接收到分发到事件
     *
     * @param event 事件
     */
    protected void receiveEvent(Event event) {

    }

    /**
     * 接受到分发的粘性事件
     *
     * @param event 粘性事件
     */
    protected void receiveStickyEvent(Event event) {

    }

    @Override
    protected void onDestroy() {
        if (isRegisterEventBus()) {
            EventBusUtil.unregister(this);
        }
        super.onDestroy();
    }
}
