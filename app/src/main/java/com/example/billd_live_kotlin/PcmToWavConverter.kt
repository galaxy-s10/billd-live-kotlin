package com.example.billd_live_kotlin

import java.io.*

class PcmToWavConverter {
    fun convertPcmToWav(pcmFile: File, wavFile: File, sampleRate: Int, channels: Int, bitDepth: Int,bufferSize: Int) {
        val totalAudioLen = pcmFile.length()
        val totalDataLen = totalAudioLen + 36

        val data = ByteArray(bufferSize)
        val inputStream = FileInputStream(pcmFile)
        val outputStream = FileOutputStream(wavFile)
        println("outputStreamoutputStream")
        println(outputStream)
        addWavHeader(outputStream, totalAudioLen, totalDataLen, sampleRate, channels, bitDepth)

        var bytesRead: Int
        while (inputStream.read(data).also { bytesRead = it } != -1) {
            outputStream.write(data, 0, bytesRead)
        }

        inputStream.close()
        outputStream.close()
    }

    private fun addWavHeader(outputStream: FileOutputStream, totalAudioLen: Long, totalDataLen: Long, sampleRate: Int, channels: Int, bitDepth: Int) {
        val header = ByteArray(44)

        // ChunkID, must be "RIFF"
        header[0] = 'R'.toByte()
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()

        // ChunkSize
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()

        // Format, must be "WAVE"
        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()

        // Subchunk1ID, must be "fmt "
        header[12] = 'f'.toByte()
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()

        // Subchunk1Size
        header[16] = 16 // 16 for PCM
        header[17] = 0
        header[18] = 0
        header[19] = 0

        // AudioFormat, PCM = 1
        header[20] = 1
        header[21] = 0

        // NumChannels
        header[22] = channels.toByte()
        header[23] = 0

        // SampleRate
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = (sampleRate shr 8 and 0xff).toByte()
        header[26] = (sampleRate shr 16 and 0xff).toByte()
        header[27] = (sampleRate shr 24 and 0xff).toByte()

        // ByteRate
        val byteRate = sampleRate * channels * bitDepth / 8
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()

        // BlockAlign
        val blockAlign = channels * bitDepth / 8
        header[32] = (blockAlign and 0xff).toByte()
        header[33] = (blockAlign shr 8 and 0xff).toByte()

        // BitsPerSample
        header[34] = bitDepth.toByte()
        header[35] = 0

        // Subchunk2ID, must be "data"
        header[36] = 'd'.toByte()
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()

        // Subchunk2Size
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = (totalAudioLen shr 8 and 0xff).toByte()
        header[42] = (totalAudioLen shr 16 and 0xff).toByte()
        header[43] = (totalAudioLen shr 24 and 0xff).toByte()

        outputStream.write(header, 0, 44)
    }
}