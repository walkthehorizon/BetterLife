package com.beiluo.betterlife

import android.app.Application
import android.content.Context

class App : Application() {

    companion object{
        lateinit var instance:Application
        var mainActivity: MainActivity? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}