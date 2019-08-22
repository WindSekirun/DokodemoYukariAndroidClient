package com.github.windsekirun.yukarisynthesizer.core.item

import com.github.windsekirun.baseapp.module.converter.LongListConverter
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.io.Serializable
import java.util.*

@Entity
class VoiceItem() : Serializable {
    @Id
    var id: Long = 0

    @Convert(converter = VoiceEngine.VoiceEngineConverter::class, dbType = String::class)
    var engine: VoiceEngine = VoiceEngine.Yukari

    @Convert(converter = PresetItem.PresetItemConverter::class, dbType = String::class)
    var preset: PresetItem = PresetItem(VoiceEngine.NONE, 1.0)

    var breakTime: Long = 0
    var regDate: Date = Date()
    var contentOrigin: String = ""

    @Convert(converter = LongListConverter::class, dbType = String::class)
    var phonomeIds: List<Long> = mutableListOf()

    @Transient
    var phonomes: MutableList<PhonomeItem> = mutableListOf()

    constructor(engine: VoiceEngine, preset: PresetItem) : this() {
        this.engine = engine
        this.preset = preset
    }

    constructor(breakTime: Long) : this() {
        this.breakTime = breakTime
    }

    constructor(id: Long, engine: VoiceEngine, preset: PresetItem, breakTime: Long) : this() {
        this.id = id
        this.engine = engine
        this.preset = preset
        this.breakTime = breakTime
    }

    fun bindContentOrigin() {
        contentOrigin = phonomes.asSequence().map { it.origin }.joinToString(separator = "") { it }
    }

    override fun toString(): String {
        return "VoiceItem(engine=$engine, content=$phonomes)"
    }
}
