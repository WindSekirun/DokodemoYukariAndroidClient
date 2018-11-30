package com.github.windsekirun.yukarisynthesizer.main.details

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.argsinjector.Argument
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.YukariOperator
import com.github.windsekirun.yukarisynthesizer.core.composer.EnsureMainThreadComposer
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import com.github.windsekirun.yukarisynthesizer.main.details.event.CloseFragmentEvent
import com.github.windsekirun.yukarisynthesizer.utils.subscribe
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
    val itemData: MutableLiveData<List<VoiceItem>> = MutableLiveData()
    val title = ObservableString()

    private var changed: Boolean = false

    @Argument(ARGUMENT_STORY_ITEM)
    lateinit var storyItem: StoryItem

    @Inject
    lateinit var yukariOperator: YukariOperator

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        if (::storyItem.isInitialized) {
            bindItems(storyItem)
        } else {
            storyItem = StoryItem()
        }
    }

    fun onBackPressed() {
        if (changed) {
            save(true)
        } else {
            postEvent(CloseFragmentEvent())
        }
    }

    private fun bindItems(storyItem: StoryItem) {
        val disposable = yukariOperator.getVoiceListAssociatedStoryItem(storyItem)
            .compose(EnsureMainThreadComposer())
            .subscribe { data, error ->
                if (error != null) return@subscribe
                title.set(storyItem.title)
                itemData.value = data
            }

        addDisposable(disposable)
    }

    private fun save(autoClose: Boolean = false) {
        if (itemData.value!!.isEmpty()) return

        storyItem.apply {
            this.title = this@MainDetailsViewModel.title.get()
            this.voices.clear()
            this.voices.addAll(itemData.value!!)
        }

        val disposable = yukariOperator.addStoryItem(storyItem)
            .compose(EnsureMainThreadComposer())
            .subscribe { _, _ ->
                showToast(getString(R.string.saved))
                if (autoClose) postEvent(CloseFragmentEvent())
            }

        addDisposable(disposable)
    }

    companion object {
        const val ARGUMENT_STORY_ITEM = "69839952-9d5f-4364-ac24-14873be83d5f"
    }

}