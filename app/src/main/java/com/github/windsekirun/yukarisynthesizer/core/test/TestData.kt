package com.github.windsekirun.yukarisynthesizer.core.test

import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.PhonomeItem
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import com.github.windsekirun.yukarisynthesizer.core.utils.YukariUtils
import io.objectbox.Box

/**
 * DokodemoYukariAndroidClient
 * Class: TestData
 * Created by Pyxis on 11/28/18.
 *
 * Description:
 */

//@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun sm30193805Test(phonomeBox: Box<PhonomeItem>, voiceBox: Box<VoiceItem>): StoryItem {
    // normal preset
    val yukari = PresetItem(VoiceEngine.Yukari, 1.4, 1.2).apply { title = "Yukari-1.4-1.2" }
    val maki = PresetItem(VoiceEngine.Maki, 1.4).apply { title = "Maki-1.4" }

    // content from https://www.nicovideo.jp/watch/sm30193805
    val yukari1 = buildVoiceItem(VoiceEngine.Yukari, yukari, phonomeBox) {
        voice("皆さんこんにちは、結月ゆかりです")
    }

    val maki1 = buildVoiceItem(VoiceEngine.Maki, maki, phonomeBox) {
        voice("弦巻", "ツル’／マ’キ")
        voice("マキです")
    }

    val yukari2 = buildVoiceItem(VoiceEngine.Yukari, yukari, phonomeBox) {
        voice("突然ですけど私、スーパーハカーになりました！")
    }

    val maki2 = buildVoiceItem(VoiceEngine.Maki, maki, phonomeBox) {
        voice("なろうと思って簡単になれるものじゃないぞ")
    }

    val maki3 = buildVoiceItem(VoiceEngine.Maki, maki, phonomeBox) {
        voice("あとハカーじゃなくてハッカーね")
    }

    val yukari3 = buildVoiceItem(VoiceEngine.Yukari, yukari, phonomeBox) {
        voice("ゆかりさんの華麗なハッキング技術で")
        voice("お前たちの個人情報を丸裸にしてやる！")
    }

    val yukari4 = buildVoiceItem(VoiceEngine.Yukari, yukari, phonomeBox) {
        voice("具体的には")
        voice("PC", "パソコン")
        voice("の")
        voice("D", "ディー")
        voice("ドライブの中身を晒してやる！")
    }

    val maki4 = buildVoiceItem(VoiceEngine.Maki, maki, phonomeBox) {
        voice("やめてください！社会的に死ぬ人が出るのでやめてください！")
    }

    val voices = mutableListOf<VoiceItem>().apply {
        this.add(yukari1)
        this.add(maki1)
        this.add(yukari2)
        this.add(maki2)
        this.add(maki3)
        this.add(yukari3)
        this.add(yukari4)
        this.add(maki4)
    }

    voiceBox.put(voices)
    val ids = voices.map { it.id }

    return StoryItem().apply {
        title = "sm30193805"
        voicesIds = ids
        voiceEntries = voices
        majorEngine = YukariUtils.findMajorEngine(this)
    }
}
