package com.github.windsekirun.yukarisynthesizer.core.define

import com.github.windsekirun.baseapp.module.converter.PropertyEnumConverter
import java.io.Serializable

enum class VoiceEngine(val id: String, val originName: String) : Serializable {
    Yukari("Yukari", "sumire"),
    Maki("Maki", "maki"),
    Break("Break", ""),
    NONE("NONE", "none");

    class VoiceEngineConverter : PropertyEnumConverter<VoiceEngine, String>(
        VoiceEngine.values(), NONE, { this.id == it }, { it.id })
}