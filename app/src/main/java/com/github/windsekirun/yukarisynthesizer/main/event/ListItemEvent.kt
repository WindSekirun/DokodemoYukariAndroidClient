package com.github.windsekirun.yukarisynthesizer.main.event

import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem

class ClickPresetItem(val item: PresetItem)

class ClickStoryItem(val item: StoryItem, val position: Int)

class ClickVoiceItem(val item: VoiceItem, val position: Int)

class ToggleFavoriteItem(val item: StoryItem)