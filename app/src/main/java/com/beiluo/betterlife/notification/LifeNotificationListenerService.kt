package com.beiluo.betterlife.notification

import android.R
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.AlarmClock
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import com.beiluo.betterlife.App
import com.beiluo.betterlife.MainActivity
import java.util.Calendar
import java.util.TimeZone


class LifeNotificationListenerService : NotificationListenerService() {

    companion object {
        val TAG = LifeNotificationListenerService::class.simpleName
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(
            1,
            getNotification(this, "为了美好生活", "辅助开启中，请勿移除")
        );//创建一个通知，创建通知前记得获取开启通知权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requestRebind(ComponentName(this, LifeNotificationListenerService::class.java))
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn ?: return
        Log.d(TAG, sbn.toString())

        val extras = sbn.notification?.extras ?: return
        // 获取接收消息的抬头
        val title = extras.getString(Notification.EXTRA_TITLE)
        // 获取接收消息的内容
        val text = extras.getString(Notification.EXTRA_TEXT) ?: return

        Log.d(TAG, "title: $title text: $text")

        if (text.contains("未按规定停放") || text.contains("已被记录") || text.contains("立即驶离") || text.contains(
                "谢谢配合"
            )
        ) {
            createSystemClock(text)
        }
        baseContext
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        super.onNotificationPosted(sbn, rankingMap)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "onListenerConnected")
        Toast.makeText(applicationContext, "已连接", Toast.LENGTH_LONG).show()
        App.mainActivity?.setStatus(200)
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        App.mainActivity?.setStatus(-1)
        Log.d(TAG, "onListenerDisconnected")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 通知侦听器断开连接 - 请求重新绑定
            requestRebind(ComponentName(this, LifeNotificationListenerService::class.java))
        }
    }

    private fun createSystemClock(text: String) {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM)
        Calendar.getInstance(TimeZone.getDefault())
        intent.putExtra(AlarmClock.EXTRA_MINUTES, Calendar.getInstance().get(Calendar.MINUTE) + 1)
        intent.putExtra(AlarmClock.EXTRA_MINUTES, Calendar.getInstance().get(Calendar.MINUTE) + 1)
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, text)
        intent.putExtra(AlarmClock.EXTRA_VIBRATE, true);
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private fun createAlarm() {
        val alarmManager =
            application.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pi = PendingIntent.getService(
            applicationContext, 8888,
            Intent(), PendingIntent.FLAG_UPDATE_CURRENT
        )
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000L, pi
        )
    }

    private fun getNotification(context: Context, title: String, text: String): Notification? {
        val isSilent = true //是否静音
        val isOngoing = true //是否持续(为不消失的常驻通知)
        val channelName = "服务常驻通知"
        val channelId = "Service_Id"
        val category = Notification.CATEGORY_SERVICE
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val nfIntent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, nfIntent, PendingIntent.FLAG_IMMUTABLE)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setContentIntent(pendingIntent) //设置PendingIntent
            .setSmallIcon(com.beiluo.betterlife.R.mipmap.ic_launcher) //设置状态栏内的小图标
            .setContentTitle(title) //设置标题
            .setContentText(text) //设置内容
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) //设置通知公开可见
            .setOngoing(isOngoing) //设置持续(不消失的常驻通知)
            .setCategory(category) //设置类别
            .setPriority(NotificationCompat.PRIORITY_MAX) //优先级为：重要通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //安卓8.0以上系统要求通知设置Channel,否则会报错
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC //锁屏显示通知
            notificationManager.createNotificationChannel(notificationChannel)
            builder.setChannelId(channelId)
        }
        return builder.build()
    }
}