package com.github.windsekirun.yukarisynthesizer.main

import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.google.android.material.bottomappbar.BottomAppBar
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
    var pagePosition: Int = -1
    var currentFabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER

    fun moveSettingActivity() {

    }
}