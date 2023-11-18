package com.example.billd_live_kotlin

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

class ScreenRecordingService : Service() {
    private val notificationBuilder = NotificationCompat.Builder(this, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("屏幕录制")
        .setContentText("正在录制屏幕...")
        .setOngoing(true)
        .build()
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

}

class MainActivity : ComponentActivity() {
    private val REQUEST_CODE = 100
    private var mProjectionManager: MediaProjectionManager? = null

    fun checkScreenRecordPermission() {
        println("checkScreenRecordPermission")
        println(Build.VERSION_CODES.M)
        println(Build.VERSION.SDK_INT)
        println(PackageManager.PERMISSION_GRANTED)
        println("---")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_CODE)
                println("startScreenRecording000")
            } else {
                println("startScreenRecording111")
//                val notificationBuilder = NotificationCompat.Builder(this,"CHANNEL_ID")
//                    .setSmallIcon(R.drawable.ic_launcher_foreground)
//                    .setContentTitle("屏幕录制")
//                    .setContentText("正在录制屏幕...")
//                    .setOngoing(true)
//                    .build()
                  mProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                val intent = mProjectionManager?.createScreenCaptureIntent()
                startActivityForResult(intent,REQUEST_CODE)

            }
        } else {
            println("startScreenRecording222")
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val screenCapturer = ScreenCapturer(resultCode, data, mProjectionManager)
        screenCapturer.startCapturing()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                println("权限ok")
            } else {
                println("权限错误")
                // 权限被拒绝，你可以在这里处理权限被拒绝的情况
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textview = TextView(this)
        textview.text = "helloss billd"

        textview.setOnClickListener{
            println("ddddddds===")
            //println(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO))

            checkScreenRecordPermission()
        }
        setContentView(textview)
        //setContentView(R.layout.billd_layout_one)
    }
}
