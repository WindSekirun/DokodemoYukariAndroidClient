package com.github.windsekirun.yukarisynthesizer.core.test

import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import io.objectbox.kotlin.applyChangesToDb

/**
 * DokodemoYukariAndroidClient
 * Class: TestData
 * Created by Pyxis on 11/28/18.
 *
 * Description:
 */

//@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun sm30193805Test(): StoryItem {
    // normal preset
 val yukari = PresetItem(VoiceEngine.Yukari, 1.4, 1.2).apply { title = "Yukari-1.4-1.2" }
    val maki = PresetItem(VoiceEngine.Maki, 1.4).apply { title = "Maki-1.4" }

    // content from https://www.nicovideo.jp/watch/sm30193805
    val yukari1 = buildVoiceItem(VoiceEngine.Yukari, yukari) {
        voice("皆さんこんにちは、結月ゆかりです")
    }

    val maki1 = buildVoiceItem(VoiceEngine.Maki, maki) {
        voice("弦巻", "ツル’／マ’キ")
        voice("マキです")
    }

    val yukari2 = buildVoiceItem(VoiceEngine.Yukari, yukari) {
        voice("突然ですけど私、スーパーハカーになりました！")
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

    val maki4 = buildVoiceItem(VoiceEngine.Maki, maki) {
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

    val storyItem = StoryItem().apply { title = "sm30193805" }

    storyItem.voices.addAll(voices)
    return storyItem
}
