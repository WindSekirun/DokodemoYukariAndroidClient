package com.github.windsekirun.yukarisynthesizer.main

import android.view.MenuItem
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.main.event.*
import com.github.windsekirun.yukarisynthesizer.main.preset.MainPresetFragment
import com.github.windsekirun.yukarisynthesizer.main.story.MainStoryFragment
import com.leinardi.android.speeddial.SpeedDialActionItem
import javax.inject.Inject

/**
 * DokodemoYukariAndroidClient
 * Class: MainViewModel
 * Created by Pyxis on 2018-11-20.
 *
 *
 * Description:
 */

@InjectViewModel
class MainViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {
    var shownDetail: Boolean = false
    val toolbarTitle = ObservableString()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        toolbarTitle.set(getString(R.string.story_title))
    }

    fun clickSpeedDial(actionItem: SpeedDialActionItem): Boolean {
        postEvent(CloseSpeedDialEvent())

        clickSpeedDialEvent(
            when (actionItem.id) {
                R.id.menu_dial_story -> {
                    replaceMain()
                    SpeedDialClickEvent.Mode.Story
                }
                R.id.menu_dial_preset -> {
                    replacePreset()
                    SpeedDialClickEvent.Mode.Preset
                }
                R.id.menu_dial_voice -> SpeedDialClickEvent.Mode.Voice
                R.id.menu_dial_break -> SpeedDialClickEvent.Mode.Break
                R.id.menu_dial_history -> SpeedDialClickEvent.Mode.History
                R.id.menu_dial_stt -> SpeedDialClickEvent.Mode.STT
                else -> SpeedDialClickEvent.Mode.Story
            }
        )

        return true
    }

    fun clickToolbarMenuItem(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home_setting -> moveSetting()
            R.id.menu_home_info -> moveInfo()
            R.id.menu_details_play -> clickToolbarEvent(ToolbarMenuClickEvent.Mode.Play)
            R.id.menu_details_save -> clickToolbarEvent(ToolbarMenuClickEvent.Mode.Save)
            R.id.menu_details_top_order -> clickToolbarEvent(ToolbarMenuClickEvent.Mode.TopOrder)
            R.id.menu_details_remove -> clickToolbarEvent(ToolbarMenuClickEvent.Mode.Remove)
        }

        return true
    }

    fun clickNavigationBottomView(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_page_story -> replaceMain()
            R.id.menu_page_preset -> replacePreset()
//            R.id.menu_page_play -> postEvent(AddFragmentEvent(MainStoryFragment(), true, false, false))
        }

        return true
    }

    fun clickToolbarNavigation(view: View) {
        if (shownDetail) {
            postEvent(InvokeBackEvent())
        }
    }

    private fun moveSetting() {

    }

    private fun moveInfo() {

    }

    private fun clickSpeedDialEvent(mode: SpeedDialClickEvent.Mode) {
        postEvent(SpeedDialClickEvent(mode))
    }

    private fun clickToolbarEvent(mode: ToolbarMenuClickEvent.Mode) {
        postEvent(ToolbarMenuClickEvent(mode))
    }

    private fun replaceMain() {
        if (shownDetail) {
            postEvent(SwapDetailEvent(true))
        }

        toolbarTitle.set(getString(R.string.story_title))
        postEvent(AddFragmentEvent(MainStoryFragment(), true, false, false))
    }

    private fun replacePreset() {
        if (shownDetail) {
            postEvent(SwapDetailEvent(true))
        }

        toolbarTitle.set(getString(R.string.preset_title))
        postEvent(AddFragmentEvent(MainPresetFragment(), true, false, false))
    }
}