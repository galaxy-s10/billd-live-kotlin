import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Environment
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import java.io.File
import java.io.FileOutputStream
import androidx.core.app.ActivityCompat
import android.widget.TextView
import androidx.activity.ComponentActivity


class MyAudioRecorder : ComponentActivity(){
    private  var audioRecord: AudioRecord? = null
    private var isRecording = false


    @SuppressLint("MissingPermission", "ServiceCast")
    fun audioplus(){
        val sampleRate = 44100
        val channelConfig = AudioFormat.CHANNEL_IN_STEREO
        val audioFormat = AudioFormat.ENCODING_PCM_8BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        val filePath = "/storage/emulated/0/Movies/bbb.mp4"
        val audioFile = File(filePath)
//        if (audioFile.exists()) {
//            println("文件已存在")
//        } else {
//            if(audioFile.createNewFile()){
//                println("文件创建成功")
//            }else{
//                println("无法创建文件")
//            }
//        }
        val displayMetrics = DisplayMetrics()
        val windowManager = this.getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenDensity = displayMetrics.densityDpi

        val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, displayMetrics.widthPixels, displayMetrics.heightPixels)
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 6000000)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)

        val  mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)

        val surface = mediaCodec.createInputSurface()


        println(333333)
        var fileOutputStream: FileOutputStream? = null
        println(444444)
          fileOutputStream = FileOutputStream(audioFile)
//        println(555555)
//
        audioRecord.startRecording()
//
        val buffer = ByteArray(bufferSize)
        var bytesRead: Int

        while (true) {
            bytesRead = audioRecord.read(buffer, 0, bufferSize)
            fileOutputStream.write(buffer, 0, bytesRead)
        }


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("WrongConstant", "MissingPermission", "SuspiciousIndentation")
    fun handleAudio(mediaProjection: MediaProjection){
        val audioFormat = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(44100)
            .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
            .build()
        val config = AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
            .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        val bufferSize = AudioRecord.getMinBufferSize(
            audioFormat.sampleRate,
            audioFormat.channelMask,
            audioFormat.encoding
        )
        val audioRecord = AudioRecord.Builder()
            .setAudioPlaybackCaptureConfig(config)
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(bufferSize)
            .build()


        audioRecord.startRecording()


        val outputFilePath = "/storage/emulated/0/Movies/aaa.mp4"

        val buffer = ByteArray(bufferSize)

        val outputFile = File(outputFilePath)
        val outputStream = FileOutputStream(outputFile)
        var bytesRead: Int
        println("fffffffffffff")
        while (true) {
            if(audioRecord!=null){
                bytesRead = audioRecord!!.read(buffer, 0, buffer.size)
                if (bytesRead == AudioRecord.ERROR_INVALID_OPERATION || bytesRead == AudioRecord.ERROR_BAD_VALUE) {
                    // 发生错误，处理错误情况
                    break
                }
                if (bytesRead != 0) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }

        }

    }

    fun handleMediaRecorder(){
        println("handleMediaRecorderhandleMediaRecorder")
        val mediaRecorder =  MediaRecorder()
        println(1111)
        mediaRecorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        println(22222)

        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        println(333)

        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder?.setOutputFile("/storage/emulated/0/Movies/aaa.mp4")
            mediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder?.setVideoEncodingBitRate(512 * 1000) // 自定义视频编码比特率
            mediaRecorder?.setVideoFrameRate(30)
        mediaRecorder.prepare();
        mediaRecorder.start();
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun startAudioCapture(mediaProjection: MediaProjection,textview: TextView) {
        println("dddddddddddd")
//        handleAudio(mediaProjection)
//        handleMediaRecorder()
//        audioplus()
    }

    fun stopRecording() {
        isRecording = false
    }
}