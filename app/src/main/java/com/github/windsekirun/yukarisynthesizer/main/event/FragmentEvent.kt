package com.github.windsekirun.yukarisynthesizer.main.event

import androidx.fragment.app.Fragment
import com.github.windsekirun.yukarisynthesizer.main.details.MainDetailsFragment
import com.github.windsekirun.yukarisynthesizer.main.preset.MainPresetFragment
import com.github.windsekirun.yukarisynthesizer.main.story.MainStoryFragment

/**
 * Event for add fragment
 */
class AddFragmentEvent<T : Fragment>(
    val fragment: T, val animated: Boolean = false, val backStack: Boolean = false,
    val reveal: Boolean = false
)

/**
 * Event for swap Toolbar, SpeedDial
 */
class SwapDetailEvent(val exitDetail: Boolean = false)

/**
 * Event for handle Click event of Toolbar within proper class
 */
class ToolbarMenuClickEvent(val mode: Mode) {
    enum class Mode {
        Play, Save, TopOrder, Remove, Story, Preset
    }

    companion object {
        fun checkAvailable(cls: Class<*>, mode: Mode): Boolean = when (cls) {
            MainDetailsFragment::class.java ->
                listOf(Mode.Play, Mode.Save, Mode.TopOrder, Mode.Remove).any { it == mode }
            MainStoryFragment::class.java -> mode == Mode.Story
            MainPresetFragment::class.java -> mode == Mode.Preset
            else -> false
        }
    }
}

/**
 * Event for handle Click event of SpeedDial within proper class
 */
class SpeedDialClickEvent(val mode: Mode) {
    enum class Mode {
        Voice, Break, History, STT, Preset, Story
    }

    companion object {
        fun checkAvailable(cls: Class<*>, mode: SpeedDialClickEvent.Mode): Boolean = when (cls) {
            MainDetailsFragment::class.java ->
                listOf(Mode.Voice, Mode.Break, Mode.History, Mode.STT).any { it == mode }
            MainStoryFragment::class.java -> mode == Mode.Story
            MainPresetFragment::class.java -> mode == Mode.Preset
            else -> false
        }
    }
}

/**
 * Event for close Fragment
 */
class CloseFragmentEvent

/**
 * Event for invoke back event
 */
class InvokeBackEvent

/**
 * Event for close SpeedDial
 */
class CloseSpeedDialEvent

/**
 * Event for refresh fragment
 */
class RefreshFragmentEvent