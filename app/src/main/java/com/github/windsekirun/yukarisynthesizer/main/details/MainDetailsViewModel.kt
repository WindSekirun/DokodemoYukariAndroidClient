package com.github.windsekirun.yukarisynthesizer.main.details

import androidx.databinding.ObservableField
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.benoitquenaudon.rxdatabinding.databinding.RxObservableField
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.argsinjector.Argument
import com.github.windsekirun.baseapp.module.composer.EnsureMainThreadComposer
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.YukariOperator
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import com.github.windsekirun.yukarisynthesizer.main.details.event.CloseFragmentEvent
import com.github.windsekirun.yukarisynthesizer.utils.propertyChanges
import com.github.windsekirun.yukarisynthesizer.utils.subscribe
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
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
    val itemData = ObservableField<List<VoiceItem>>(mutableListOf())
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

        Observables.combineLatest(RxObservableField.propertyChanges(itemData), title.propertyChanges())
            .subscribe { _, _ ->
                changed = true
            }.addTo(compositeDisposable)
    }

    fun onBackPressed() {
        if (changed) {
            save(true)
        } else {
            postEvent(CloseFragmentEvent())
        }
    }

    private fun bindItems(storyItem: StoryItem) {
        itemData.set(storyItem.voices.toList())
        title.set(storyItem.title)
    }

    private fun save(autoClose: Boolean = false) {
        if (itemData.get()!!.isEmpty()) return

        storyItem.apply {
            this.title = this@MainDetailsViewModel.title.get()
            this.voices.clear()
            this.voices.addAll(itemData.get()!!)
        }

        yukariOperator.addStoryItem(storyItem)
            .compose(EnsureMainThreadComposer())
            .subscribe { _, _ ->
                showToast(getString(R.string.saved))
                if (autoClose) postEvent(CloseFragmentEvent())
            }.addTo(compositeDisposable)
    }

    companion object {
        const val ARGUMENT_STORY_ITEM = "69839952-9d5f-4364-ac24-14873be83d5f"
    }

}