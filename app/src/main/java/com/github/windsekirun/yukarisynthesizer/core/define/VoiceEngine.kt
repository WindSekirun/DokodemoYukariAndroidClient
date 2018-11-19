package com.github.windsekirun.yukarisynthesizer.core.define

import com.github.windsekirun.yukarisynthesizer.core.base.PropertyEnumConverter

enum class VoiceEngine(val id: String, val originName: String) {
    Yukari("Yukari", "sumire"),
    Maki("Maki", "maki"),
    Ai("Ai", "anzu"),
    NONE("NONE", "none");

    class VoiceEngineConverter : PropertyEnumConverter<VoiceEngine, String>(
        VoiceEngine.values(),
            NONE, { this.id == it }, { it.id })
}
