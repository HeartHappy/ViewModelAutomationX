package com.hearthappy.vma

import android.app.Application
import android.content.Context
import com.hjq.toast.ToastUtils

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
        ToastUtils.init(this)
    }

    companion object {
        var appContext: Context? = null
    }

}

