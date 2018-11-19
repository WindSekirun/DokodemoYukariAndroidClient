package com.github.windsekirun.yukarisynthesizer.core.item

import com.github.windsekirun.yukarisynthesizer.core.base.PropertyJsonConverter
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.io.Serializable

@Entity
class PresetItem : Serializable {
    @Id
    var id: Long = 0

    @Convert(converter = VoiceEngine.VoiceEngineConverter::class, dbType = String::class)
    var engine: VoiceEngine = VoiceEngine.Yukari

    var rate: Double = 1.0
    var pitch: Double = 1.0
    var range: Double = 1.0
    var volume: Double = 1.0

    constructor(engine: VoiceEngine, rate: Double) {
        this.engine = engine
        this.rate = rate
    }

    constructor(engine: VoiceEngine, rate: Double, pitch: Double) {
        this.engine = engine
        this.rate = rate
        this.pitch = pitch
    }

    class PresetItemConverter : PropertyJsonConverter<PresetItem>(PresetItem::class.java)
}
