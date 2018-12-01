package com.github.windsekirun.yukarisynthesizer.main.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.YukariOperator
import com.github.windsekirun.yukarisynthesizer.core.composer.EnsureMainThreadComposer
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import com.github.windsekirun.yukarisynthesizer.main.details.event.CloseFragmentEvent
import com.github.windsekirun.yukarisynthesizer.utils.propertyChanges
import com.github.windsekirun.yukarisynthesizer.utils.subscribe
import io.objectbox.kotlin.applyChangesToDb
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

    lateinit var storyItem: StoryItem
    @Inject
    lateinit var yukariOperator: YukariOperator

    private var changed: Boolean = false
    private val changeObserver = Observer<List<VoiceItem>> {
        changed = true
    }

    override fun onCleared() {
        super.onCleared()
        itemData.removeObserver(changeObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        itemData.removeObserver(changeObserver)
    }

    fun loadData(storyItem: StoryItem?) {
        this.storyItem = storyItem ?: StoryItem()
        bindItems(storyItem == null)
    }

    fun onBackPressed() {
        if (changed) {
            save(true)
        } else {
            postEvent(CloseFragmentEvent())
        }
    }

    private fun bindItems(initial: Boolean) {
        if (initial) {
            observeEvent()
            return
        }

        val disposable = yukariOperator.getVoiceListAssociatedStoryItem(storyItem)
            .compose(EnsureMainThreadComposer())
            .subscribe { data, error ->
                if (error != null) return@subscribe
                title.set(storyItem.title)
                itemData.value = data

                observeEvent()
            }

        addDisposable(disposable)
    }

    private fun save(autoClose: Boolean = false) {
        if (itemData.value!!.isEmpty()) return

        storyItem.apply {
            this.title = this@MainDetailsViewModel.title.get()
        }

        val disposable = yukariOperator.addStoryItem(storyItem)
            .compose(EnsureMainThreadComposer())
            .subscribe { _, _ ->
                storyItem.voices.applyChangesToDb(true) {
                    this.addAll(itemData.value!!)
                }

                showToast(getString(R.string.saved))
                if (autoClose) postEvent(CloseFragmentEvent())
            }

        addDisposable(disposable)
    }

    private fun observeEvent() {
        itemData.observeForever(changeObserver)
        val disposable = title.propertyChanges().subscribe { _, _ -> changed = true }
        addDisposable(disposable)
    }
}