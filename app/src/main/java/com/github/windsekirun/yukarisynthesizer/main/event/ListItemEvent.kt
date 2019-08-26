package com.github.windsekirun.yukarisynthesizer.main.event

import com.github.windsekirun.yukarisynthesizer.core.item.PhonomeItem
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem

/**
 * Event for handle click event about PhonomeItem
 */
class ClickPhonomeItem(val item: PhonomeItem)

/**
 * Event for handle click event about PresetItem
 */
class ClickPresetItem(val item: PresetItem)

/**
 * Event for handle click event about StoryItem
 */
class ClickStoryItem(val item: StoryItem, val position: Int)

/**
 * Event for handle click event about VoiceItem
 */
class ClickVoiceItem(val item: VoiceItem, val position: Int)

/**
 * Event for handle favorite event with StoryItem
 */
class ToggleFavoriteItem(val item: StoryItem)