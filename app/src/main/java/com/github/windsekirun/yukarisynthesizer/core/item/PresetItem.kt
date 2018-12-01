package com.github.windsekirun.yukarisynthesizer.core.item

import com.github.windsekirun.yukarisynthesizer.core.base.PropertyJsonConverter
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.io.Serializable
import java.util.*

@Entity
class PresetItem() : Serializable {
    @Id
    var id: Long = 0
    var title: String = ""
    var regDate: Date = Date()

    @Convert(converter = VoiceEngine.VoiceEngineConverter::class, dbType = String::class)
    var engine: VoiceEngine = VoiceEngine.Yukari

    var rate: Double = 1.0
    var pitch: Double = 1.0
    var range: Double = 1.0
    var volume: Double = 1.0

    constructor(engine: VoiceEngine, rate: Double) : this() {
        this.engine = engine
        this.rate = rate
    }

    constructor(engine: VoiceEngine, rate: Double, pitch: Double) : this() {
        this.engine = engine
        this.rate = rate
        this.pitch = pitch
    }

    constructor(
        id: Long, title: String, regDate: Date, engine: VoiceEngine,
        rate: Double, pitch: Double, range: Double, volume: Double
    ) : this() {
        this.id = id
        this.title = title
        this.regDate = regDate
        this.engine = engine
        this.range = range
        this.rate = rate
        this.pitch = pitch
        this.volume = volume
    }

    class PresetItemConverter : PropertyJsonConverter<PresetItem>(PresetItem::class.java)
}
