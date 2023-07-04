package com.beiluo.betterlife

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.beiluo.betterlife.databinding.ActivityMainBinding
import com.beiluo.betterlife.notification.LifeNotificationListenerService
import com.beiluo.betterlife.test.TestActivity


class MainActivity : AppCompatActivity() {

    private var code = -1

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    private val manager by lazy {
        NotificationManagerCompat.from(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.mainActivity = this
        setContentView(binding.root)
        binding.tvOpen.setOnClickListener {
            if(code !=-1){
                return@setOnClickListener
            }
            setStatus(0)
            startListen()
        }
        binding.tvGoTest.setOnClickListener {
            createNotificationForNormal()
        }
    }

    fun setStatus(code: Int) {
        this.code = code
        when (code) {
            0 -> binding.tvOpen.text = "正在链接..."
            200 -> binding.tvOpen.text = "已开启"
            -1 -> binding.tvOpen.text = "一键开启监控"
        }
    }

    private fun startListen() {
        if (NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)) {
            val intent = Intent(this, LifeNotificationListenerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } else {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }
    }

    private fun createNotificationForNormal() {
        // 适配8.0及以上 创建渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                TestActivity.CHANNEL_ID,
                "停车提醒",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "描述"
                setShowBadge(false) // 是否在桌面显示角标
            }
            manager.createNotificationChannel(channel)
        }
        // 点击意图 // setDeleteIntent 移除意图
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        // 构建配置
        val mBuilder = NotificationCompat.Builder(this, TestActivity.CHANNEL_ID)
            .setContentTitle("交管12123消息(测试)") // 标题
            .setContentText("您的小型汽车于xxx在xxx路未按规定停放已被记录，请立即驶离，未及时驶离的，将依法予以处罚，谢谢配合！") // 文本
            .setSmallIcon(R.mipmap.ic_launcher) // 小图标
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)) // 大图标
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 7.0 设置优先级
            .setContentIntent(pendingIntent) // 跳转配置
            .setAutoCancel(true) // 是否自动消失（点击）or mManager.cancel(mNormalNotificationId)、cancelAll、setTimeoutAfter()
        // 发起通知
        manager.notify(8888, mBuilder.build())
    }
}