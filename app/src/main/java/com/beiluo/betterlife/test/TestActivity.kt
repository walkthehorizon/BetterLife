package com.beiluo.betterlife.test

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.AlarmClock
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.beiluo.betterlife.MainActivity
import com.beiluo.betterlife.R
import com.beiluo.betterlife.databinding.ActivityTestBinding
import java.util.Calendar
import java.util.TimeZone

class TestActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityTestBinding.inflate(layoutInflater)
    }
    private val manager by lazy {
        NotificationManagerCompat.from(this)
    }

    companion object {
        const val CHANNEL_ID = "9999"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.tvSend.setOnClickListener {
            createNotificationForNormal()
        }
        binding.tvCreateAlarm.setOnClickListener {
            createSystemClock()
        }
    }

    private fun createSystemClock() {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM)
        Calendar.getInstance(TimeZone.getDefault())
        intent.putExtra(AlarmClock.EXTRA_MINUTES, Calendar.getInstance().get(Calendar.MINUTE)+1)
        intent.putExtra(AlarmClock.EXTRA_MINUTES, Calendar.getInstance().get(Calendar.MINUTE)+1)
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, "狼来了1111111111111111")
        intent.putExtra(AlarmClock.EXTRA_VIBRATE, true);
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private fun createAlarm(){
        val alarmManager =
            application.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pi = PendingIntent.getService(
            applicationContext, 8888,
            Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT
        )
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP, 5000L, pi
        )
    }

    private fun createNotificationForNormal() {
        // 适配8.0及以上 创建渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "停车提醒",
                NotificationManager.IMPORTANCE_DEFAULT
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
        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("交管12123消息") // 标题
            .setContentText("您的小型汽车于xxx在xxx路未按规定停放已被记录，请立即驶离，未及时驶离的，将依法予以处罚，谢谢配合！") // 文本
            .setSmallIcon(R.mipmap.ic_launcher) // 小图标
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)) // 大图标
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 7.0 设置优先级
            .setContentIntent(pendingIntent) // 跳转配置
            .setAutoCancel(true) // 是否自动消失（点击）or mManager.cancel(mNormalNotificationId)、cancelAll、setTimeoutAfter()
        // 发起通知
        manager.notify(8888, mBuilder.build())
    }

}