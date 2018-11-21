package com.github.windsekirun.yukarisynthesizer.core

import com.github.windsekirun.yukarisynthesizer.core.item.Phonome
import com.github.windsekirun.yukarisynthesizer.core.item.SSMLItem
import org.apache.commons.text.StringEscapeUtils
import org.redundent.kotlin.xml.xml

object SSMLBuilder {

    fun process(item: SSMLItem, minify: Boolean = false): String {
        val ssml = xml("speak") {
            attribute("version", item.version)

            for (voice in item.voices) {
                if (voice.breakTime != 0L) {
                    "break" {
                        attribute("time", "${voice.breakTime}ms")
                    }

                    continue
                }

                "voice" {
                    attribute("name", voice.engine.originName)

                    "prosody" {
                        val preset = voice.preset
                        attributes(
                            "rate" to preset.rate,
                            "pitch" to preset.pitch,
                            "range" to preset.range,
                            "volume" to preset.volume
                        )

                        -voice.contentPhonemes.build()
                    }
                }
            }
        }

        var result = ssml.toString()
        if (minify) {
            result = result.replace("\n", "")
            result = result.replace("\t", "")
        }
        result = StringEscapeUtils.unescapeHtml4(result)
        return result
    }

    private fun List<Phonome>.build(): String {
        return this.asSequence().map { it.toString() }.joinToString(separator = "") { it }
    }
}