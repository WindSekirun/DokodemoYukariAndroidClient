package com.github.windsekirun.yukarisynthesizer.core.utils

import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem

object YukariUtils {

    fun findMajorEngine(storyItem: StoryItem): VoiceEngine =
        storyItem.voiceEntries.map { it.engine }
            .filter { it != VoiceEngine.Break }
            .groupBy { it }
            .mapValues { it.value.size }
            .toList()
            .maxBy { it.second }
            ?.first ?: VoiceEngine.NONE

}