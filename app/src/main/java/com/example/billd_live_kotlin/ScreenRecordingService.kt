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
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Timer
import kotlin.concurrent.timerTask


class ScreenRecordingService : Service() {
    private  var audioRecord: AudioRecord? = null
    val mSampleRateInHZ = 16000
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
    }

    private fun pcmToWave(inFileName: String, outFileName: String) {
        var `in`: FileInputStream? = null
        var out: FileOutputStream? = null
        var totalAudioLen: Long = 0
        val longSampleRate = mSampleRateInHZ.toLong()
        var totalDataLen = totalAudioLen + 36
        val channels = 1 //你录制是单声道就是1 双声道就是2（如果错了声音可能会急促等）
        val byteRate = 16 * longSampleRate * channels / 8
        val data = ByteArray(minBufferSize)
        try {
            `in` = FileInputStream(inFileName)
            out = FileOutputStream(outFileName)
            totalAudioLen = `in`.channel.size()
            totalDataLen = totalAudioLen + 36
            writeWaveFileHeader(
                out,
                totalAudioLen,
                totalDataLen,
                longSampleRate,
                channels,
                byteRate
            )
            while (`in`.read(data) != -1) {
                out.write(data)
            }
            `in`.close()
            out.close()
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun writeWaveFileHeader(
        out: FileOutputStream, totalAudioLen: Long, totalDataLen: Long, longSampleRate: Long,
        channels: Int, byteRate: Long
    ) {
        val header = ByteArray(44)
        header[0] = 'R'.code.toByte() // RIFF
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xffL).toByte() //数据大小
        header[5] = (totalDataLen shr 8 and 0xffL).toByte()
        header[6] = (totalDataLen shr 16 and 0xffL).toByte()
        header[7] = (totalDataLen shr 24 and 0xffL).toByte()
        header[8] = 'W'.code.toByte() //WAVE
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        //FMT Chunk
        header[12] = 'f'.code.toByte() // 'fmt '
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte() //过渡字节
        //数据大小
        header[16] = 16 // 4 bytes: size of 'fmt ' chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        //编码方式 10H为PCM编码格式
        header[20] = 1 // format = 1
        header[21] = 0
        //通道数
        header[22] = channels.toByte()
        header[23] = 0
        //采样率，每个通道的播放速度
        header[24] = (longSampleRate and 0xffL).toByte()
        header[25] = (longSampleRate shr 8 and 0xffL).toByte()
        header[26] = (longSampleRate shr 16 and 0xffL).toByte()
        header[27] = (longSampleRate shr 24 and 0xffL).toByte()
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byteRate and 0xffL).toByte()
        header[29] = (byteRate shr 8 and 0xffL).toByte()
        header[30] = (byteRate shr 16 and 0xffL).toByte()
        header[31] = (byteRate shr 24 and 0xffL).toByte()
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (1 * 16 / 8).toByte()
        header[33] = 0
        //每个样本的数据位数
        header[34] = 16
        header[35] = 0
        //Data chunk
        header[36] = 'd'.code.toByte() //data
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xffL).toByte()
        header[41] = (totalAudioLen shr 8 and 0xffL).toByte()
        header[42] = (totalAudioLen shr 16 and 0xffL).toByte()
        header[43] = (totalAudioLen shr 24 and 0xffL).toByte()
        out.write(header, 0, 44)
    }


    /**
     * 初始化录音器
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ServiceCast")
    private fun initAudioRecord(resultCode: Int, intent: Intent) {
        initNotification()
        println("initAudioRecordinitAudioRecord")
          minBufferSize = AudioRecord.getMinBufferSize(mSampleRateInHZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        val mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        //设置应用程序录制系统音频的能力
        val mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, intent)
        val builder = AudioRecord.Builder()
        builder.setAudioFormat(AudioFormat.Builder()
            .setSampleRate(mSampleRateInHZ) //设置采样率（一般为可选的三个-> 8000Hz 、16000Hz、44100Hz）
            .setChannelMask(AudioFormat.CHANNEL_IN_MONO) //音频通道的配置，可选的有-> AudioFormat.CHANNEL_IN_MONO 单声道，CHANNEL_IN_STEREO为双声道，立体声道，选择单声道就行
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT).build()) //音频数据的格式，可选的有-> AudioFormat.ENCODING_PCM_8BIT，AudioFormat.ENCODING_PCM_16BIT
            .setBufferSizeInBytes(minBufferSize) //设置最小缓存区域
        val config = AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
            .addMatchingUsage(AudioAttributes.USAGE_MEDIA) //设置捕获多媒体音频
            .addMatchingUsage(AudioAttributes.USAGE_GAME) //设置捕获游戏音频
            .build()
        //将 AudioRecord 设置为录制其他应用播放的音频
        builder.setAudioPlaybackCaptureConfig(config)
        try {
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                audioRecord = builder.build()
            }
        } catch (e: Exception) {
            e.printStackTrace()
           println("录音器错误")
        }
        //做完准备工作，就可以开始录音了
        startRecord()
    }

    private fun createFile(name: String): File? {
        val dirPath = Environment.getExternalStorageDirectory().path
        println(dirPath)
        println("ddddirpath")
        val file = File(dirPath)
        if (!file.exists()) {
            file.mkdirs()
        }
        val filePath = dirPath + name
        val objFile = File(filePath)
        if (!objFile.exists()) {
            try {
                objFile.createNewFile()
                return objFile
            } catch (e: IOException) {
                e.printStackTrace()
            }
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
//        val tmpName = System.currentTimeMillis().toString()
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
        }, 5000L) // 三秒后执行，单位为毫秒
        Thread {
            try {
                val outputStream = FileOutputStream(tmpFile?.absoluteFile)

                while (recing) {
                    //循环从音频硬件读取音频数据录制到字节数组中
                    audioRecord?.read(mAudioData, 0, mAudioData.size)
                    //将字节数组写入到tmpFile文件
                    outputStream.write(mAudioData)
                    println("----<<<${recing}")

                }
                timer.cancel() // 取消定时器

                outputStream.close()
                //将.pcm文件转换为.wav文件
//                pcmToWave(tmpFile?.absolutePath!!, tmpOutFile?.absolutePath!!)

                val channels = 2 // 声道数
                val bitDepth = 16 // 位深度

                val converter = PcmToWavConverter()
                converter.convertPcmToWav(tmpFile!!, tmpOutFile!!, mSampleRateInHZ, channels, bitDepth, minBufferSize)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        println("----onStartCommand")
        val currentResultCode = intent.getIntExtra("resultCode", 0)
        val resultData = intent.getParcelableExtra<Intent>("resultData")
        initAudioRecord(currentResultCode, resultData!!)
        return super.onStartCommand(intent, flags, startId)
    }

}