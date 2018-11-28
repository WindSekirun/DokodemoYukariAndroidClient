package com.github.windsekirun.yukarisynthesizer.core.test

import androidx.annotation.VisibleForTesting
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.PhonomeItem
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem

//@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
class TestItemBuilder {
    private val phonomes = mutableListOf<PhonomeItem>()

    fun voice(origin: String, phonome: String = "") {
        phonomes.add(PhonomeItem(origin, phonome))
    }

    fun build() = phonomes
}

fun buildVoiceItem(engine: VoiceEngine, presetItem: PresetItem,
                   setup: TestItemBuilder.() -> Unit): VoiceItem {
    val builder = TestItemBuilder()
    builder.setup()
    return VoiceItem(engine, presetItem, builder.build())
}