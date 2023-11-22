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
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
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
//    val mSampleRateInHZ = 16000
    val mSampleRateInHZ = 44100
    val channelData = AudioFormat.CHANNEL_IN_MONO
    val bitData = AudioFormat.ENCODING_PCM_16BIT


    var minBufferSize=0

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
        val notification = builder.setAutoCancel(false)
            .setContentTitle("5555") //标题
            .setContentText("666666666...") //内容
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher) //设置小图标
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))//设置大图标
            .build()
        startForeground(1, notification)
        println("startForegroundstartForeground")
    }


    /**
     * 初始化录音器
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ServiceCast", "MissingPermission")
    private fun initAudioRecord(resultCode: Int, intent: Intent) {
        println("initAudioRecordinitAudioRecord")
//        return
        minBufferSize = AudioRecord.getMinBufferSize(mSampleRateInHZ, channelData, bitData)
        val mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        //设置应用程序录制系统音频的能力
        val mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, intent)
        //数据编码方式
        val format = AudioFormat.Builder()
            .setEncoding(bitData)
            .setSampleRate(mSampleRateInHZ)
            .setChannelMask(channelData)
            .build()
        val config = AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
            .addMatchingUsage(AudioAttributes.USAGE_MEDIA) //设置捕获多媒体音频
//            .addMatchingUsage(AudioAttributes.USAGE_GAME) //设置捕获游戏音频
            .build()
        println("ddddddsdsd")
        audioRecord = AudioRecord.Builder()
            .setAudioFormat(format)
            .setAudioPlaybackCaptureConfig(config)
            .setBufferSizeInBytes(minBufferSize) //设置最小缓存区域
            .build()
        //做完准备工作，就可以开始录音了
        startRecord()
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
        }, 10000L) // 三秒后执行，单位为毫秒
        Thread {
            try {
                val outputStream = FileOutputStream(tmpFile?.absoluteFile)
                val channels = 1 // 声道数
                val bitDepth = 16 // 位深度
//                val converter = PcmToWavConverter()

                while (recing) {
                    //循环从音频硬件读取音频数据录制到字节数组中
                    audioRecord?.read(mAudioData, 0, mAudioData.size)
                    //将字节数组写入到tmpFile文件
                    outputStream.write(mAudioData)
//                  println("----<<<${recing}")
                }
                PcmToWavUtil(mSampleRateInHZ,channelData,bitData).pcmToWav(tmpFile!!.absolutePath, tmpOutFile!!.absolutePath)
//                converter.pcmToWave(tmpFile!!.absolutePath, tmpOutFile!!.absolutePath, mSampleRateInHZ, channels, bitDepth, minBufferSize)
                audioRecord?.stop()
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
        initNotification()
        val currentResultCode = intent.getIntExtra("resultCode", 0)
//        val currentResultData = intent.getIntExtra("resultData", null)
        val currentResultData = intent.getParcelableExtra<Intent>("resultData")
        if(currentResultData!=null){
            println("初始化录屏")
            initAudioRecord(currentResultCode, currentResultData)
        }else{
        println("取消录屏了")
        }
        return super.onStartCommand(intent, flags, startId)
    }

}