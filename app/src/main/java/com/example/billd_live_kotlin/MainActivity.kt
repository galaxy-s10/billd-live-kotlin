package com.example.billd_live_kotlin

import MyAudioRecorder
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
import android.os.Environment
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.Error


class MainActivity : ComponentActivity() {
    private val REQUEST_CODE = 100
    private var mProjectionManager: MediaProjectionManager? = null
    private val RECORD_REQUEST_CODE = 129
    var mediaProjectionManager : MediaProjectionManager? = null
    var mediaProjection: MediaProjection? = null
    var mediaRecorder: MediaRecorder? =null
    fun checkScreenRecordPermission() {
        println("checkScreenRecordPermission")
        println(Build.VERSION_CODES.M)
        println(Build.VERSION.SDK_INT)
        println(PackageManager.PERMISSION_GRANTED)
        println("---ooodddds")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            println("权限kkkkkkk")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), RECORD_REQUEST_CODE)
        } else {
            // 权限已授予，可以读取文件
            println("可以读取文件")
        }
//        val outputFilePath = "/storage/emulated/0/aaa.txt"
        val outputFilePath = "/data/user/0/aaa.txt"
        val outputFile = File(outputFilePath)
        println(this.filesDir)
        println("本地路径")
//        println(outputFile.readText())
        val fileName = "file.txt" // 文本文件的路径和名称
        val content = "Hello, World!" // 要写入的文本内容

//        try {
//            val file = File(fileName)
//            val writer = BufferedWriter(FileWriter(file))
//            writer.write(content)
//            writer.close()
//            println("写入成功")
//        } catch (e: IOException) {
//            println("写入时发生错误：${e.message}")
//        }

//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.RECORD_AUDIO
//            ) != PackageManager.PERMISSION_GRANTED
//            ||ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            println("权限kkkkkkk")
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                ),
//                RECORD_REQUEST_CODE
//            )
//        } else {
//            // 已经授予了权限，可以进行录制屏幕的操作
//            println("---kkkkk")
//            val outputFilePath = "/storage/emulated/0/aaa.txt"
//            val outputFile = File(outputFilePath)
//            println(outputFile.readText())
//
//        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            println("没有WRITE_EXTERNAL_STORAGE权限了")
//            ActivityCompat.requestPermissions(this,
//                arrayOf(
//                    Manifest.permission.RECORD_AUDIO,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                ), REQUEST_CODE)
//
//
//        }else{
//            println("有WRITE_EXTERNAL_STORAGE权限了")
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_CODE)
//                println("startScreenRecording000")
//            } else {
//                println("startScreenRecording111")
////                val notificationBuilder = NotificationCompat.Builder(this,"CHANNEL_ID")
////                    .setSmallIcon(R.drawable.ic_launcher_foreground)
////                    .setContentTitle("屏幕录制")
////                    .setContentText("正在录制屏幕...")
////                    .setOngoing(true)
////                    .build()
//                  mProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
//                val intent = mProjectionManager?.createScreenCaptureIntent()
//
//            }
//
//        } else {
//            println("startScreenRecording222")
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("onActivityResult----")
//        var outputPath = Environment.getExternalStoragePublicDirectory(
//            Environment.DIRECTORY_MOVIES
//        ).absolutePath + "/screen_captured.mp4"

//        val outputFile: File = File(outputPath)

//        if (!outputFile.exists()) {
//            try {
//                var res = outputFile.createNewFile();
//                println(res)
//                println("kkdd")
//            }catch (e:Error){
//                println(e)
//                println("piddd")
//            }
//        }else{
//            println("已存在了")
//        }

//        println(outputFile)
        println("---outputFile")
//        mediaRecorder?.setOutputFile("/storage/emulated/0/Movies/kkk.mp4")

//        mediaRecorder?.prepare()
//        mediaRecorder?.setOutputFile("/storage/emulated/0/Movies/kkk.mp4")
//        mediaRecorder?.setOutputFile(outputFile.absolutePath)
//        mediaRecorder?.start()
        println("999999iii")
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        println("onActivityReenter----")


    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                println("权限ok")
//            } else {
//                println("权限错误")
//                // 权限被拒绝，你可以在这里处理权限被拒绝的情况
//            }
//        }
//    }




    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("---onCreate")
        val textview = TextView(this)
        textview.text = "helloss billd"
        startService(MediaProjectionService.createIntent(this))

        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        var mediaProjectionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
           val data: Intent? = result.data
            println("---222222")
            println(data)
            mediaProjection = mediaProjectionManager?.getMediaProjection(result.resultCode, data!!)
           println(mediaProjection)
//            val displayMetrics = DisplayMetrics()
////            windowManager.defaultDisplay.getMetrics(displayMetrics)
//            val screenWidth = displayMetrics.widthPixels
//            val screenHeight = displayMetrics.heightPixels
//            val screenDensity = displayMetrics.densityDpi
//            val virtualDisplay = mediaProjection?.createVirtualDisplay(
//                "ScreenCapture",
//                screenWidth,
//                screenHeight,
//                screenDensity,
//                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
//                null,
//                null,
//                null
//            )

//             mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//               MediaRecorder(this)
//           } else {
//               TODO("VERSION.SDK_INT < S")
//           }
            println("---mediaRecorder-111")

            val audiorec = MyAudioRecorder()
            audiorec.startAudioCapture(mediaProjection!!)
//            mediaRecorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE) // 或者使用其他音频源
//            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC) // 或者使用其他音频源
//            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.REMOTE_SUBMIX) // 或者使用其他音频源
//            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
//            mediaRecorder?.setOutputFile("/storage/emulated/0/Movies/kkk.mp4")
//
////            mediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
//            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
//            mediaRecorder?.setVideoEncodingBitRate(512 * 1000) // 自定义视频编码比特率
//            mediaRecorder?.setVideoFrameRate(30) // 自
            println("---mediaRecorder-222")


//
            println("---mediaRecorder")
//            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
//            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
//            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)

           println("ooppop")
           if (result.resultCode == RESULT_OK) {
               println("---333333")

            } else {
                // 处理权限被拒绝的情况
                println("---111111")
            }
        }
        val screenCaptureIntent = mediaProjectionManager?.createScreenCaptureIntent()
        textview.setOnClickListener{
            println("ddddddds===")
            //println(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO))

            checkScreenRecordPermission()
            //val intent = Intent(this,ScreenRecordingService::class.java)
            //startService(intent)
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                println("没有RECORD_AUDIO权限了")
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_CODE)
//            }else{
//                println("有RECORD_AUDIO权限了")
//            }

            //val intent = Intent(this,MediaProjectionService::class.java)
            mediaProjectionLauncher.launch(screenCaptureIntent)

            println(mediaProjection)
            println("dsdss")
           // audiorec.startAudioCapture(mediaProjection!!)

        }
        setContentView(textview)
        //setContentView(R.layout.billd_layout_one)
    }
}
