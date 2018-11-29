package com.github.windsekirun.yukarisynthesizer.main.details

import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.argsinjector.Argument
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem

import javax.inject.Inject

/**
 * DokodemoYukariAndroidClient
 * Class: MainDetailsViewModel
 * Created by Pyxis on 2018-11-27.
 *
 *
 * Description:
 */

@InjectViewModel
class MainDetailsViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {
    @Argument(ARGUMENT_STORY_ITEM) lateinit var storyItem: StoryItem

    companion object {
        const val ARGUMENT_STORY_ITEM = "69839952-9d5f-4364-ac24-14873be83d5f"
    }

}