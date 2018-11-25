package com.github.windsekirun.yukarisynthesizer.core.test

import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.Phonome
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem

class TestItemBuilder {
    private val phonomes = mutableListOf<Phonome>()

    fun voice(origin: String, phonome: String = "") {
        phonomes.add(Phonome(origin, phonome))
    }

    fun build() = phonomes
}

fun buildVoiceItem(engine: VoiceEngine, presetItem: PresetItem,
                   setup: TestItemBuilder.() -> Unit): VoiceItem {
    val builder = TestItemBuilder()
    builder.setup()
    return VoiceItem(engine, presetItem, builder.build())
}