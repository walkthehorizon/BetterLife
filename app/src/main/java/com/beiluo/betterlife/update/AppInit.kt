package com.beiluo.betterlife.update

data class AppInit(
    val versionCode: Int = 0,
    val versionName: String = "",
    val isForce: Boolean = true,
    val apkUrl: String = "",
    val desc:String = ""
)