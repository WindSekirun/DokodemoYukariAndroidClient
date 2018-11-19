package com.github.windsekirun.yukarisynthesizer.core.item

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class SSMLItem {
    @Id
    var id: Long = 0
    var version: String = "1.1"
    var voices: List<VoiceItem> = mutableListOf()

    constructor(voices: List<VoiceItem>) {
        this.voices = voices
    }
}