package com.github.windsekirun.yukarisynthesizer.core

import com.github.windsekirun.yukarisynthesizer.core.item.PhonomeItem
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem
import org.apache.commons.text.StringEscapeUtils
import org.redundent.kotlin.xml.xml

object SSMLBuilder {

    /**
     * process [StoryItem] to generate SSML document
     */
    fun process(item: StoryItem, minify: Boolean = false): String {
        val ssml = xml("speak") {
            attribute("version", item.version)

            for (voice in item.voiceEntries) {
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

                        -voice.phonomes.build()
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

    private fun List<PhonomeItem>.build(): String {
        return this.asSequence().map { it.toString() }.joinToString(separator = "") { it }
    }
}