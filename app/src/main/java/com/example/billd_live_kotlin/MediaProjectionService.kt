package com.example.billd_live_kotlin

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat

class MediaProjectionService : Service() {
    private lateinit var mediaProjection: MediaProjection

    override fun onBind(intent: Intent): IBinder? {
        println("MediaProjectionService---onBind")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("MediaProjectionService---onStartCommand")

        // 获取传递的 MediaProjection 对象
//        mediaProjection = intent?.getParcelableExtra(EXTRA_MEDIA_PROJECTION) as? MediaProjection
        // 设置前台服务通知
        createNotificationChannel()
        val notification: Notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        // 在这里执行与屏幕捕获相关的操作

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        println("MediaProjectionService---onDestroy")

        super.onDestroy()
        // 停止屏幕捕获操作
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Media Projection Service")
            .setContentText("Running")

        return notificationBuilder.build()
    }

    companion object {
        private const val EXTRA_MEDIA_PROJECTION = "extra_media_projection"
        private const val CHANNEL_ID = "media_projection_service_channel"
        private const val CHANNEL_NAME = "Media Projection Service"
        private const val NOTIFICATION_ID = 1

        fun createIntent(context: Context): Intent {
            val intent = Intent(context, MediaProjectionService::class.java)
//            intent.putExtra(EXTRA_MEDIA_PROJECTION, mediaProjection)
            return intent
        }
    }
}