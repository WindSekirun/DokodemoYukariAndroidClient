package com.github.windsekirun.yukarisynthesizer.main.preset

import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LifecycleOwner
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.composer.EnsureMainThreadComposer
import com.github.windsekirun.baseapp.utils.subscribe
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.core.YukariOperator
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.main.event.ShowPresetDialogEvent
import com.github.windsekirun.yukarisynthesizer.main.story.MainStoryViewModel
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

@InjectViewModel
class MainPresetViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {
    val itemData = ObservableArrayList<PresetItem>()

    @Inject
    lateinit var yukariOperator: YukariOperator

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        loadData()
    }

    fun clickPreset() {
        clickPresetItem(PresetItem())
    }

    fun clickPresetItem(item: PresetItem) {
        val event = ShowPresetDialogEvent(item) {
            yukariOperator.addPresetItem(it)
                .subscribe { data, error ->
                    if (error != null || data == null) {
                        Log.e(MainStoryViewModel::class.java.simpleName, "onResume: ", error)
                        return@subscribe
                    }

                    loadData()
                }.addTo(compositeDisposable)
        }

        postEvent(event)
    }

    private fun loadData() {
        val disposable = yukariOperator.getPresetList()
            .compose(EnsureMainThreadComposer())
            .subscribe { data, error ->
                if (error != null || data == null) {
                    Log.e(MainStoryViewModel::class.java.simpleName, "onResume: ", error)
                    return@subscribe
                }
                itemData.clear()
                itemData.addAll(data)
            }

        addDisposable(disposable)
    }
}