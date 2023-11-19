import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import java.io.File
import java.io.FileOutputStream


class MyAudioRecorder {
    private  var audioRecord: AudioRecord? = null
    private var isRecording = false

    @SuppressLint("WrongConstant", "MissingPermission")
//    fun startRecording() {
//        audioRecord = AudioRecord.Builder().build()
//        isRecording = true
//        audioRecord.startRecording()
//        audioRecord.stop()
//        audioRecord.release()
//    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun startAudioCapture(mediaProjection: MediaProjection) {
        val sampleRate = 44100 // 采样率
        val channelConfig = AudioFormat.CHANNEL_OUT_MONO // 声道配置
//        val audioFormat = AudioFormat.ENCODING_PCM_16BIT // 音频格式
        val audioFormat = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
            .setSampleRate(sampleRate)
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
          audioRecord = AudioRecord.Builder()
            .setAudioPlaybackCaptureConfig(config)
            .setAudioFormat(audioFormat)
            .build()
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build())
            .setAudioFormat(AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                .setSampleRate(sampleRate)
                .setChannelMask(channelConfig)
                .build())
            .setBufferSizeInBytes(bufferSize)
            .build()

        audioTrack.play()
        println(audioTrack)
        println("audioTrackaudioTrack")

        val audioData: ByteArray = ByteArray(0) // 准备音频数据
            audioTrack?.write(audioData, 0, audioData.size)


//        val outputFilePath = "/storage/emulated/0/Movies/aa.mp4"
//        val outputFilePath = "/storage/emulated/0/Movies/cc.pcm"
        val outputFilePath = "/storage/emulated/0/aaa.txt"
//        val outputFilePath = "aaa.txt"

        var outputPath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MOVIES
        ).absolutePath + "/screen_captured.mp4"

        println("333333333")
        println(outputPath)

        val buffer = ByteArray(bufferSize)
        println("33333332222222")

        val outputFile = File(outputFilePath)
        println("2222222222222")
//        println(outputFile.readText())
//        println(audioRecord)
//        println(outputFile.outputStream())
//        val outputStream = FileOutputStream(outputFile)
//        val outputStream = outputFile.outputStream()
        println("111111111")

        audioRecord?.startRecording()
        var bytesRead: Int
        println("fffffffffffff")
//        while (true) {
//            if(audioRecord!=null){
//                bytesRead = audioRecord!!.read(buffer, 0, buffer.size)
//                if (bytesRead == AudioRecord.ERROR_INVALID_OPERATION || bytesRead == AudioRecord.ERROR_BAD_VALUE) {
//                    // 发生错误，处理错误情况
//                    break
//                }
//                if (bytesRead != 0) {
//                    println("ddddddsssss")
//                    println(bytesRead)
////                    outputStream.write(buffer, 0, bytesRead)
//                }
//            }
//
//        }




    }

    fun stopRecording() {
        isRecording = false
    }
}