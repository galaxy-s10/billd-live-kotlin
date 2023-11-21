package com.example.billd_live_kotlin

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.BitmapFactory
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class MediaProjectionService : Service() {
    private lateinit var mediaProjection: MediaProjection

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        initNotification()
    }

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
            .setContentTitle("录音服务") //标题
            .setContentText("录音服务正在运行...") //内容

        return notificationBuilder.build()
    }

    /**
     * 初始化通知栏
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initNotification() {
        val builder: Notification.Builder
        val channelID = "MRecordService"
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelID, "录音服务", NotificationManager.IMPORTANCE_HIGH)
        channel.enableLights(true) //设置提示灯
//        channel.lightColor = Color.RED //设置提示灯颜色
        channel.setShowBadge(true) //显示logo
        channel.description = "4444444444" //设置描述
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC //设置锁屏可见 VISIBILITY_PUBLIC=可见
        manager.createNotificationChannel(channel)
        builder = Notification.Builder(this, channelID)
        val notification = builder.setAutoCancel(false)
            .setContentTitle("5555") //标题
            .setContentText("666666666...") //内容
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher) //设置小图标
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))//设置大图标
            .build()
        startForeground(1, notification)
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