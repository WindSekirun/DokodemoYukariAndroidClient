package com.github.windsekirun.yukarisynthesizer.core

import okhttp3.*
import pyxis.uzuki.live.richutilskt.utils.runAsync
import java.io.IOException

/**
 * DokodemoYukariAndroidClient
 * Class: DocomoSynthesizer
 * Created by Pyxis on 2018-11-20.
 *
 * Description:
 */
object DocomoSynthesizer {

    fun process(ssml: String, apiKey: String) {
        runAsync {
            val url = "https://api.apigw.smt.docomo.ne.jp/aiTalk/v1/textToSpeech?APIKEY=$apiKey"

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

                }

            })
        }
    }
}