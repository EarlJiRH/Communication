package com.zhs.communication

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

/**
 * ================================================
 * 类名：com.zhs.communication
 * 时间：2022/4/29 17:30
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 * ================================================
 * @author Admin
 */
class App : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        base?.let {
            MultiDex.install(base)
        }
    }
}