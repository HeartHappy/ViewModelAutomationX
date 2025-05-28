package com.hearthappy.vma

import android.app.Application
import android.content.Context
import com.hjq.toast.ToastUtils

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
        activityProvider.init(this)
        ToastUtils.init(this)
    }

    companion object {
        val activityProvider by lazy { ActivityProvider() }
        var appContext: Context? = null
    }

}

