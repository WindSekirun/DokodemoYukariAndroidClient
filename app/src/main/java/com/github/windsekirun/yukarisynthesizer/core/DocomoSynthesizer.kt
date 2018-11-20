package com.github.windsekirun.yukarisynthesizer.core

import android.content.Context
import android.util.Log
import com.github.windsekirun.yukarisynthesizer.core.item.SSMLItem
import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler
import nl.bravobit.ffmpeg.FFmpeg
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.Okio
import pyxis.uzuki.live.richutilskt.utils.runAsync
import pyxis.uzuki.live.richutilskt.utils.runOnUiThread
import java.io.File


/**
 * DokodemoYukariAndroidClient
 * Class: DocomoSynthesizer
 * Created by Pyxis on 2018-11-20.
 *
 * Description:
 */
object DocomoSynthesizer {

    private val TAG = DocomoSynthesizer::class.java.simpleName

    fun process(context: Context, item: SSMLItem, apiKey: String, callback: (Int, File) -> Unit) {
        runAsync {
            val pcmFile = File(
                context.getExternalFilesDir(null),
                "/pcm/%s.pcm".format(item.title)
            )

            val mp3File = File(
                context.getExternalFilesDir(null),
                "/mp3/%s.mp3".format(item.title)
            )

            pcmFile.parentFile.mkdirs()
            mp3File.parentFile.mkdirs()

            val url = "https://api.apigw.smt.docomo.ne.jp/aiTalk/v1/textToSpeech?APIKEY=$apiKey"
            val ssml = SSMLBuilder.process(item, true)
            val body = RequestBody.create(MediaType.parse("application/ssml+xml"), ssml)

            val request = Request.Builder().url(url)
                .addHeader("Content-Type", "application/ssml+xml")
                .addHeader("Accept", "audio/L16")
                .post(body)
                .build()

            val okHttpClient = OkHttpClient()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful && response.body() != null) {
                val sink = Okio.buffer(Okio.sink(pcmFile))
                sink.writeAll(response.body()!!.source())
                sink.close()
                encodePcmToMp3(context, pcmFile, mp3File, callback)
            } else {
                runOnUiThread { callback(-1, pcmFile) }
            }
        }
    }

    fun checkLibrarySupported(context: Context): Boolean {
        return FFmpeg.getInstance(context).isSupported
    }

    private fun encodePcmToMp3(
        context: Context, pcmFile: File, mp3File: File, callback: (Int, File) -> Unit
    ) {
        // ffmpeg -y -ac 1 -ar 16000 -f s16be -i ${pcmFile.absolutePath} -c:a libmp3lame -q:a 2 ${mp3File.absolutePath}
        val cmd = arrayOf(
            "-y", "-ac", "1", "-ar", "16000", "-f", "s16be", "-i", pcmFile.absolutePath,
            "-c:a", "libmp3lame", "-q:a", "2", mp3File.absolutePath
        )

        val ffmpeg = FFmpeg.getInstance(context)
        ffmpeg.execute(cmd, object : ExecuteBinaryResponseHandler() {
            override fun onFinish() {
                super.onFinish()
                Log.d(TAG, "onFinish")
            }

            override fun onSuccess(message: String?) {
                super.onSuccess(message)
                Log.d(TAG, "onSuccess: $message")
                callback(0, mp3File)
            }

            override fun onFailure(message: String?) {
                super.onFailure(message)
                Log.d(TAG, "onFailure: $message")
                callback.invoke(-1, mp3File)
            }

            override fun onProgress(message: String?) {
                super.onProgress(message)
            }

            override fun onStart() {
                super.onStart()
                Log.d(TAG, "onStart")
            }
        })
    }
}