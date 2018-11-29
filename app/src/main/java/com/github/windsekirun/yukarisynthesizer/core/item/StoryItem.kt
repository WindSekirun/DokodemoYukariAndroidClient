package com.github.windsekirun.yukarisynthesizer.core.item

import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import java.util.*

@Entity
class StoryItem() {
    @Id
    var id: Long = 0
    var version: String = "1.1"
    var title: String = ""
    var regDate: Date = Date()
    var localPath: String = ""
    var favoriteFlag: Boolean = false

    @Backlink(to = "stories")
    lateinit var  voices: ToMany<VoiceItem>

    @Transient
    var regDateFormat: String = ""

    @Transient
    var majorEngine: VoiceEngine = VoiceEngine.NONE

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