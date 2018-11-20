package com.github.windsekirun.yukarisynthesizer.core.item

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
class SSMLItem {
    @Id
    var id: Long = 0
    var version: String = "1.1"
    var title: String = ""
    var regDate: Date = Date()
    var localPath: String = ""
    var voices: List<VoiceItem> = mutableListOf()

    constructor(title: String, voices: List<VoiceItem>) {
        this.title = title
        this.voices = voices
    }
}