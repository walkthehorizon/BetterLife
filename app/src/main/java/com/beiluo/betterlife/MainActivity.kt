package com.beiluo.betterlife

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.beiluo.betterlife.databinding.ActivityMainBinding
import com.beiluo.betterlife.notification.LifeNotificationListenerService
import com.beiluo.betterlife.test.TestActivity

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.tvOpen.setOnClickListener {
            startListen()
        }
        binding.tvGoTest.setOnClickListener {
            startActivity(Intent(this,TestActivity::class.java))
        }
        Log.e("MainActivity","11111111111111111111111111111111${binding.tvOpen}")
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
}