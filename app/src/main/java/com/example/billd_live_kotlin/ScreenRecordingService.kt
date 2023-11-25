package com.example.billd_live_kotlin

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.hardware.display.DisplayManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Environment
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Timer
import kotlin.concurrent.timerTask


class ScreenRecordingService : Service() {
    private  var audioRecord: AudioRecord? = null
    private  var mediaRecorder: MediaRecorder = MediaRecorder()

    //    val mSampleRateInHZ = 16000
    val mSampleRateInHZ = 44100
    val channelData = AudioFormat.CHANNEL_IN_MONO
    val bitData = AudioFormat.ENCODING_PCM_16BIT
    var minBufferSize=0
    var mediaProjection: MediaProjection? = null
    var notificationId:Int = 1
    var notification:Notification?=null
    var duration =6000L


    override fun onCreate() {
        println("----onCreate")
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        println("----onBind")
        return null
    }

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
          notification = builder.setAutoCancel(false)
            .setContentTitle("5555") //标题
            .setContentText("666666666...") //内容
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher) //设置小图标
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))//设置大图标
            .build()
        startForeground(notificationId, notification)
    }


    /**
     * 初始化录音器
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ServiceCast", "MissingPermission")
    private fun initAudioRecord(resultCode: Int, intent: Intent) {
        minBufferSize = AudioRecord.getMinBufferSize(mSampleRateInHZ, channelData, bitData)
        val mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        //设置应用程序录制系统音频的能力
          mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, intent)
        //数据编码方式
        val format = AudioFormat.Builder()
            .setEncoding(bitData)
            .setSampleRate(mSampleRateInHZ)
            .setChannelMask(channelData)
            .build()
        val config = AudioPlaybackCaptureConfiguration.Builder(mediaProjection!!)
            .addMatchingUsage(AudioAttributes.USAGE_MEDIA) //设置捕获多媒体音频
            .addMatchingUsage(AudioAttributes.USAGE_GAME) //设置捕获游戏音频
            .build()
        audioRecord = AudioRecord.Builder()
            .setAudioFormat(format)
            .setAudioPlaybackCaptureConfig(config)
            .setBufferSizeInBytes(minBufferSize) //设置最小缓存区域
            .build()
        //做完准备工作，就可以开始录音了
        startRecord()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ServiceCast", "MissingPermission", "WrongConstant")
    private fun initVideoRecord(resultCode: Int, intent: Intent) {
        val mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        //设置应用程序录制系统音频的能力
          mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, intent)
        val dirPath = Environment.getExternalStorageDirectory().path
        //保存到本地录音文件名
        val videoFilePath= "$dirPath/Movies/vvv.mp4"
         createFile(videoFilePath)
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mediaRecorder.setVideoEncodingBitRate(1024 * 1000)
        mediaRecorder.setVideoFrameRate(30)
        mediaRecorder.setVideoSize(1280, 720)
        mediaRecorder.setOutputFile(videoFilePath)
        mediaRecorder.prepare()
        mediaProjection!!.createVirtualDisplay(
            "ScreenRecording",
            1280,
            720,
            resources.displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mediaRecorder.surface,
            null,
            null
        )
        mediaRecorder.start()
        val timer = Timer()
        timer.schedule(timerTask {
            // 在此处编写延迟执行的任务逻辑
            println("延迟任务执行于")
            mediaRecorder.stop()
            mediaRecorder.reset()
            mediaProjection!!.stop()
            stopForeground(notificationId)

            timer.cancel() // 取消定时器
        }, duration) // 三秒后执行，单位为毫秒

    }

    private fun createFile(name: String): File? {
        val dirPath = Environment.getExternalStorageDirectory().path
        val file = File(dirPath)
        if (!file.exists()) {
            file.mkdirs()
        }
        val filePath = dirPath + name
        val objFile = File(filePath)
        if (!objFile.exists()) {
            println("不存在1111")
            try {
                objFile.createNewFile()
                return objFile
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }else{
            println("存在11111")
            objFile.delete()
            objFile.createNewFile()
            return objFile
        }
        return null
    }

    /**
     * 开始录音
     */
    private fun startRecord() {
        println("开始录音开始录音")
        //承接音频数据的字节数组
        val mAudioData = ByteArray(320)
        //保存到本地录音文件名
        val tmpName="/Movies/ggg"
        //新建文件，承接音频数据
        val tmpFile = createFile(tmpName + ".pcm")
        val tmpOutFile = createFile(tmpName + ".wav")

        //开始录音
        audioRecord?.startRecording()
        var recing=true
        val timer = Timer()
        timer.schedule(timerTask {
            // 在此处编写延迟执行的任务逻辑
            println("延迟任务执行于")
            recing=false
            timer.cancel() // 取消定时器
        }, duration) // 三秒后执行，单位为毫秒
        println("线程起来了")
//        return
        Thread {
            try {
                val outputStream = FileOutputStream(tmpFile?.absoluteFile)
                while (recing) {
                    //循环从音频硬件读取音频数据录制到字节数组中
                    audioRecord?.read(mAudioData, 0, mAudioData.size)
                    //将字节数组写入到tmpFile文件
                    outputStream.write(mAudioData)
//                  println("----<<<${recing}")
                }
                PcmToWavUtil(mSampleRateInHZ,channelData,bitData).pcmToWav(tmpFile!!.absolutePath, tmpOutFile!!.absolutePath)
                audioRecord?.stop()
                mediaProjection!!.stop()
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(notificationId)
                outputStream.close()

                timer.cancel() // 取消定时器

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        println("----onStartCommand")
        val currentResultCode = intent.getIntExtra("resultCode", 0)
//        val currentResultData = intent.getIntExtra("resultData", null)
        val currentResultData = intent.getParcelableExtra<Intent>("resultData")
        if(currentResultData!=null){
            println("初始化录屏")
            initNotification()
            initAudioRecord(currentResultCode, currentResultData)
            initVideoRecord(currentResultCode, currentResultData)


        }else{
        println("取消录屏了")
            stopForeground(true)
        }
        return super.onStartCommand(intent, flags, startId)
    }

}