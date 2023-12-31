package com.beiluo.betterlife

import android.app.Application
import android.content.Context
import com.azhon.appupdate.manager.DownloadManager
import com.beiluo.betterlife.update.AppInit
import com.google.gson.GsonBuilder
import com.huawei.agconnect.remoteconfig.AGConnectConfig
import com.tencent.bugly.crashreport.CrashReport

class App : Application() {

    companion object {
        lateinit var instance: Application
        var mainActivity: MainActivity? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CrashReport.initCrashReport(applicationContext, "c0f39436e7", false)
    }

}