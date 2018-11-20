package com.github.windsekirun.yukarisynthesizer.core

import android.content.Context
import com.github.windsekirun.yukarisynthesizer.core.item.SSMLItem
import okhttp3.*
import okio.Okio
import pyxis.uzuki.live.richutilskt.utils.asDateString
import pyxis.uzuki.live.richutilskt.utils.runAsync
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder


/**
 * DokodemoYukariAndroidClient
 * Class: DocomoSynthesizer
 * Created by Pyxis on 2018-11-20.
 *
 * Description:
 */
object DocomoSynthesizer {

    fun process(context: Context, item: SSMLItem, apiKey: String) {
        runAsync {
            val pcmFile = File(
                context.externalCacheDir,
                "/pcm/%s.pcm".format(System.currentTimeMillis().asDateString())
            )

            val url = "https://api.apigw.smt.docomo.ne.jp/aiTalk/v1/textToSpeech?APIKEY=$apiKey"
            val ssml = SSMLBuilder.process(item, true)
            val body = RequestBody.create(MediaType.parse("application/ssml+xml"), ssml)

            val request = Request.Builder().url(url)
                .addHeader("Content-Type", "application/ssml+xml")
                .addHeader("Accept", "audio/L16") // binary file
                .post(body)
                .build()

            val okHttpClient = OkHttpClient()

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val sink = Okio.buffer(Okio.sink(pcmFile))
                    sink.writeAll(response.body()?.source())
                    sink.close()

                    encodeWav(context, item)
                }
            })
        }
    }

    private fun encodeWav(context: Context, item: SSMLItem) {

    }

    private fun writeWavHeader(
        outputStream: OutputStream, channels: Short,
        sampleRate: Int, bitDepth: Short
    ) {
        val littleBytes = ByteBuffer
            .allocate(14)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putShort(channels)
            .putInt(sampleRate)
            .putInt(sampleRate * channels * (bitDepth / 8))
            .putShort((channels * (bitDepth / 8)).toShort())
            .putShort(bitDepth)
            .array()


        outputStream.write(byteArrayOf(
            'R'.toByte(), 'I'.toByte(), 'F'.toByte(), 'F'.toByte(), // Chunk ID
            0, 0, 0, 0, // Chunk Size
            'W'.toByte(), 'A'.toByte(), 'V'.toByte(), 'E'.toByte(), // Format
            'f'.toByte(), 'm'.toByte(), 't'.toByte(), ' '.toByte(), //Chunk ID
            16, 0, 0, 0, // Chunk Size
            1, 0, // AudioFormat
            littleBytes[0], littleBytes[1], // Num of Channels
            littleBytes[2], littleBytes[3], littleBytes[4], littleBytes[5], // SampleRate
            littleBytes[6], littleBytes[7], littleBytes[8], littleBytes[9], // Byte Rate
            littleBytes[10], littleBytes[11], // Block Align
            littleBytes[12], littleBytes[13], // Bits Per Sample
            'd'.toByte(), 'a'.toByte(), 't'.toByte(), 'a'.toByte(), // Chunk ID
            0, 0, 0, 0 //Chunk Size
        ))
    }
}