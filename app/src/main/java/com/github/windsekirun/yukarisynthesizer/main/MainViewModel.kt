package com.github.windsekirun.yukarisynthesizer.main

import android.view.MenuItem
import android.view.View
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.reference.ActivityReference
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.dialog.PresetDialog
import com.github.windsekirun.yukarisynthesizer.main.details.MainDetailsFragment
import com.github.windsekirun.yukarisynthesizer.main.event.AddFragmentEvent
import com.github.windsekirun.yukarisynthesizer.main.event.InvokeBackEvent
import com.github.windsekirun.yukarisynthesizer.main.event.SpeedDialClickEvent
import com.github.windsekirun.yukarisynthesizer.main.event.ToolbarMenuClickEvent
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

    fun clickSpeedDial(actionItem: SpeedDialActionItem): Boolean {
        clickSpeedDialEvent(
            when (actionItem.id) {
                R.id.menu_dial_story -> SpeedDialClickEvent.Mode.Story
                R.id.menu_dial_preset -> SpeedDialClickEvent.Mode.Preset
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
}