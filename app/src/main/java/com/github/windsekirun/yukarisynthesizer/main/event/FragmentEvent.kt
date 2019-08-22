package com.github.windsekirun.yukarisynthesizer.main.event

import androidx.fragment.app.Fragment
import com.github.windsekirun.yukarisynthesizer.main.details.MainDetailsFragment
import com.github.windsekirun.yukarisynthesizer.main.preset.MainPresetFragment
import com.github.windsekirun.yukarisynthesizer.main.story.MainStoryFragment

class AddFragmentEvent<T : Fragment>(
    val fragment: T, val animated: Boolean = false, val backStack: Boolean = false,
    val reveal: Boolean = false
)

class SwapDetailEvent(val exitDetail: Boolean = false)


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

class CloseFragmentEvent

class InvokeBackEvent

class CloseSpeedDialEvent

class RefreshFragmentEvent