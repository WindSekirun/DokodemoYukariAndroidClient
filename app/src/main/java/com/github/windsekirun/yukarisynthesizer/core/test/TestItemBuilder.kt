package com.github.windsekirun.yukarisynthesizer.core.test

import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.PhonomeItem
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import io.objectbox.Box

class TestItemBuilder {
    private val phonomes = mutableListOf<PhonomeItem>()

    fun voice(origin: String, phonome: String = "") {
        phonomes.add(PhonomeItem(origin, phonome))
    }

    fun build() = phonomes
}

fun buildVoiceItem(
    engine: VoiceEngine, presetItem: PresetItem, phonomeBox: Box<PhonomeItem>,
    setup: TestItemBuilder.() -> Unit
): VoiceItem {
    val builder = TestItemBuilder()
    builder.setup()

    val voiceItem = VoiceItem().apply {
        this.engine = engine
        this.preset = presetItem
    }

    val list = builder.build()
    phonomeBox.put(list)
    val ids = list.map { it.id }

    voiceItem.apply {
        phonomeIds = ids
        phonomes.addAll(list)
        bindContentOrigin()
    }

    return voiceItem
}