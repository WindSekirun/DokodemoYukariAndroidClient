package com.github.windsekirun.yukarisynthesizer.core.item

import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
class StoryItem constructor() {
    @Id
    var id: Long = 0
    var version: String = "1.1"
    var title: String = ""
    var regDate: Date = Date()
    var localPath: String = ""
    var voicesId: List<Long> = mutableListOf()
    var favoriteFlag: Boolean = false

    @Transient
    var regDateFormat: String = ""

    @Transient
    var majorEngine: VoiceEngine = VoiceEngine.NONE

    @Transient
    var voices: List<VoiceItem> = mutableListOf()
}