package com.github.windsekirun.yukarisynthesizer.main.story

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.core.YukariOperator
import com.github.windsekirun.yukarisynthesizer.core.composer.EnsureMainThreadComposer
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem
import com.github.windsekirun.yukarisynthesizer.utils.subscribe
import javax.inject.Inject

/**
 * DokodemoYukariAndroidClient
 * Class: MainStoryViewModel
 * Created by Pyxis on 2018-11-26.
 *
 *
 * Description:
 */

@InjectViewModel
class MainStoryViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {
    val itemData: MutableLiveData<List<StoryItem>> = MutableLiveData()

    @Inject lateinit var yukariOperator: YukariOperator

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        val disposable = yukariOperator.getStoryList()
            .compose(EnsureMainThreadComposer())
            .subscribe { data, error ->
                if (error != null) return@subscribe
                itemData.value = data
            }

        addDisposable(disposable)
    }

}