package com.example.billd_live_kotlin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : ComponentActivity() {
    private val REQUEST_CODE = 100
    private var mProjectionManager: MediaProjectionManager? = null
    private val RECORD_REQUEST_CODE = 129
    var textview: TextView?=null
    var mediaProjectionManager : MediaProjectionManager? = null
    var mediaProjection: MediaProjection? = null
    var mediaRecorder: MediaRecorder? =null
    fun checkScreenRecordPermission() {
        println("checkScreenRecordPermission")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), RECORD_REQUEST_CODE)
        } else {
            // 权限已授予，可以读取文件
            println("可以读取文件")
        }
    }

    override fun onStart() {
        println("onStart---")
        super.onStart()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("onActivityResult---")

        val intent = Intent(this, ScreenRecordingService::class.java)
        intent.putExtra("resultData", data)
        intent.putExtra("resultCode", resultCode)
        println("resultCoderesultCode---")
        println(data)
        println(resultCode)
        startForegroundService(intent)
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        println("onActivityReenter---")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("---onCreate")
        textview = TextView(this)
        textview?.text = "helloss billd"
        println("kkffffffff")

        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        textview?.setOnClickListener{
            println("ddddddds===")

            checkScreenRecordPermission()
            val REQUEST_CODE_SCREEN_CAPTURE = 1
            val screenCaptureIntent = mediaProjectionManager?.createScreenCaptureIntent()
            startActivityForResult(screenCaptureIntent!!, REQUEST_CODE_SCREEN_CAPTURE)

        }
        setContentView(textview)
        //setContentView(R.layout.billd_layout_one)
    }
}
