package com.github.windsekirun.yukarisynthesizer.core.item

import com.github.windsekirun.yukarisynthesizer.core.base.LongListConverter
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.io.Serializable
import java.util.*

@Entity
class StoryItem() : Serializable {
    @Id
    var id: Long = 0
    var version: String = "1.1"
    var title: String = ""
    var regDate: Date = Date()
    var localPath: String = ""
    var favoriteFlag: Boolean = false

    @Convert(converter = VoiceEngine.VoiceEngineConverter::class, dbType = String::class)
    var majorEngine: VoiceEngine = VoiceEngine.NONE

    @Convert(converter = LongListConverter::class, dbType = String::class)
    var voicesIds: List<Long> = mutableListOf()

    @Transient
    var regDateFormat: String = ""

    @Transient
    var voiceEntries: List<VoiceItem> = mutableListOf()

    constructor(id: Long, version: String, title: String, regDate: Date, localPath: String, favoriteFlag: Boolean)
            : this() {
        this.id = id
        this.version = version
        this.title = title
        this.regDate = regDate
        this.localPath = localPath
        this.favoriteFlag = favoriteFlag
    }
}