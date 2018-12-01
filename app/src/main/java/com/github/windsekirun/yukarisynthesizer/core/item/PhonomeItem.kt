package com.github.windsekirun.yukarisynthesizer.core.item

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import org.redundent.kotlin.xml.xml
import java.io.Serializable
import java.util.*

@Entity
class PhonomeItem() : Serializable {

    @Id
    var id: Long = 0
    var origin: String = ""
    var phoneme: String = ""
    var regDate: Date = Date()

    lateinit var voices: ToMany<VoiceItem>

    constructor(origin: String) : this() {
        this.origin = origin
    }

    constructor(origin: String, phoneme: String) : this() {
        this.origin = origin
        this.phoneme = phoneme
    }

    constructor(id: Long, origin: String, phoneme: String, regDate: Date) : this() {
        this.id = id
        this.origin = origin
        this.phoneme = phoneme
        this.regDate = regDate
    }

    override fun toString(): String {
        return this.build()
    }

    companion object {
        @JvmStatic
        fun PhonomeItem.build(): String {
            if (this.phoneme.isEmpty()) return this.origin
            return xml("phoneme") {
                attribute("ph", this@build.phoneme)
                -this@build.origin
            }.toString()
        }
    }
}