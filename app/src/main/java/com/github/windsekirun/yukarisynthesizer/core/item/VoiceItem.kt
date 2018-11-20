package com.github.windsekirun.yukarisynthesizer.core.item

import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.Phonome
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.io.Serializable

@Entity
class VoiceItem: Serializable {
    @Id
    var id: Long = 0

    @Convert(converter = VoiceEngine.VoiceEngineConverter::class, dbType = String::class)
    var engine: VoiceEngine = VoiceEngine.Yukari

    @Convert(converter = PresetItem.PresetItemConverter::class, dbType = String::class)
    var preset: PresetItem = PresetItem(VoiceEngine.NONE, 1.0)

    var contentOrigin: String = ""
    var contentPhonemes: List<Phonome> = mutableListOf()
    var breakTime: Long = 0

    constructor(engine: VoiceEngine, preset: PresetItem, contentPhonemes: List<Phonome>) {
            this.engine = engine
        this.preset = preset
        this.contentPhonemes = contentPhonemes
        this.contentOrigin = contentPhonemes.asSequence().map { it.origin }.joinToString(separator = "") { it }
    }

    constructor(breakTime: Long) {
        this.breakTime = breakTime
    }

    override fun toString(): String {
        return "VoiceItem(engine=$engine, contentPhonemes=$contentPhonemes)"
    }

    companion object {
        fun addBreak(timeMs: Long) = VoiceItem(timeMs)
    }
}
