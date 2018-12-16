package com.github.windsekirun.yukarisynthesizer.main.event

import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.PhonomeItem
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem

/**
 * Event for show BreakDialogFragment
 */
class ShowBreakDialogEvent(val param: VoiceItem, val callback: (VoiceItem) -> Unit)

/**
 * Event for show VoiceHistoryFragment
 */
class ShowHistoryDialogEvent(val callback: (VoiceItem) -> Unit)

/**
 * Event for show VoiceRecognitionFragment
 */
class ShowVoiceRecognitionEvent(val callback: (String) -> Unit)

/**
 * Event for show VoicePresetFragment
 */
class ShowPresetDialogEvent(val param: VoiceEngine, val callback: (PresetItem) -> Unit)

/**
 * Event for show PhonomeHistoryFragment
 */
class ShowPhonomeHistoryEvent( val callback: (PhonomeItem) -> Unit)

/**
 * Event for show PlayDialogFragment
 */
class ShowPlayDialogEvent(val param: List<StoryItem>)