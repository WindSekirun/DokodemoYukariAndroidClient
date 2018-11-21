package com.github.windsekirun.yukarisynthesizer.main

import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.core.DocomoSynthesizer
import com.github.windsekirun.yukarisynthesizer.core.SSMLBuilder
import com.github.windsekirun.yukarisynthesizer.core.buildVoiceItem
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.core.item.SSMLItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import pyxis.uzuki.live.richutilskt.utils.runOnUiThread
import java.io.File

import javax.inject.Inject

/**
 * DokodemoYukariAndroidClient
 * Class: MainViewModel
 * Created by Pyxis on 2018-11-20.
 *
 *
 * Description:
 */

@InjectViewModel
class MainViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {
    var wavFile: File? = null
    val ssmlText = ObservableString()

    fun clickRequest(view: View) {
        if (!DocomoSynthesizer.checkLibrarySupported(getApplication())) {
            showToast("Not Supported Device")
            return;
        }

        val apiKey = "67375458343230674173395a64654730682f61774830736e39597859634331476d4276496b756a6e467544"

        // normal preset
        val yukari = PresetItem(VoiceEngine.Yukari, 1.4, 1.2)
        val maki = PresetItem(VoiceEngine.Maki, 1.4)
        val ai = PresetItem(VoiceEngine.Ai, 1.4)

        // content from https://www.nicovideo.jp/watch/sm30193805
        val yukari1 = buildVoiceItem(VoiceEngine.Yukari, yukari) {
            voice("皆さんこんにちは、結月ゆかりです")
        }

        val maki1 = buildVoiceItem(VoiceEngine.Maki, maki) {
            voice("弦巻", "ツル’／マ’キ")
            voice("マキです")
        }

        val ai1 = buildVoiceItem(VoiceEngine.Ai, ai) {
            voice("ゆっくり霊夢です")
        }

        val yukari2 = buildVoiceItem(VoiceEngine.Yukari, yukari) {
            voice("突然ですけど私、スーパーハカーになりました！")
        }

        val ai2 = buildVoiceItem(VoiceEngine.Ai, ai) {
            voice("この人いきなり何言ってんだ…")
        }

        val maki2 = buildVoiceItem(VoiceEngine.Maki, maki) {
            voice("なろうと思って簡単になれるものじゃないぞ")
        }

        val maki3 = buildVoiceItem(VoiceEngine.Maki, maki) {
            voice("あとハカーじゃなくてハッカーね")
        }

        val yukari3 = buildVoiceItem(VoiceEngine.Yukari, yukari) {
            voice("ゆかりさんの華麗なハッキング技術で")
            voice("お前たちの個人情報を丸裸にしてやる！")
        }

        val yukari4 = buildVoiceItem(VoiceEngine.Yukari, yukari) {
            voice("具体的には")
            voice("PC", "パソコン")
            voice("の")
            voice("D", "ディー")
            voice("ドライブの中身を晒してやる！")
        }

        val ai3 = buildVoiceItem(VoiceEngine.Ai, ai) {
            voice("やめてください！社会的に死ぬ人が出るのでやめてください！")
        }

        val voices = mutableListOf<VoiceItem>().apply {
            this.add(yukari1)
            this.add(VoiceItem.addBreak(200))
            this.add(maki1)
            this.add(VoiceItem.addBreak(200))
            this.add(ai1)
            this.add(VoiceItem.addBreak(200))
            this.add(yukari2)
            this.add(VoiceItem.addBreak(200))
            this.add(ai2)
            this.add(VoiceItem.addBreak(200))
            this.add(maki2)
            this.add(VoiceItem.addBreak(100))
            this.add(maki3)
            this.add(VoiceItem.addBreak(200))
            this.add(yukari3)
            this.add(VoiceItem.addBreak(100))
            this.add(yukari4)
            this.add(VoiceItem.addBreak(200))
            this.add(ai3)
        }

        val ssmlItem = SSMLItem("sm30193805", voices)
        ssmlText.set(SSMLBuilder.process(ssmlItem, true))

        DocomoSynthesizer.process(getApplication(), ssmlItem, apiKey) { result, file ->
            wavFile = file
            runOnUiThread { showToast("Result $result"); }
        }
    }

    fun clickPlay(view: View) {
        if (wavFile == null) return

        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(getApplication(), Uri.fromFile(wavFile))
        mediaPlayer.prepare()
        mediaPlayer.start()
    }
}