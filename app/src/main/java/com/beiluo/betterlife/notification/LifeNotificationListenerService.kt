package com.beiluo.betterlife.notification

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.AlarmManagerCompat

class LifeNotificationListenerService : NotificationListenerService() {

    companion object {
        val TAG = LifeNotificationListenerService::class.simpleName
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
            createAlarm()
        }
        baseContext
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        super.onNotificationPosted(sbn, rankingMap)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "onListenerConnected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "onListenerDisconnected")
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
}