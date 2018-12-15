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
import com.github.windsekirun.yukarisynthesizer.main.story.MainStoryViewModel
import javax.inject.Inject

/**
 * DokodemoYukariAndroidClient
 * Class: MainPresetViewModel
 * Created by Pyxis on 2018-11-27.
 *
 *
 * Description:
 */

@InjectViewModel
class MainPresetViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {
    val itemData = ObservableArrayList<PresetItem>()

    @Inject
    lateinit var yukariOperator: YukariOperator

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        val disposable = yukariOperator.getPresetList()
            .compose(EnsureMainThreadComposer())
            .subscribe { data, error ->
                if (error != null || data == null) {
                    Log.e(MainStoryViewModel::class.java.simpleName, "onResume: ", error)
                    return@subscribe
                }
                itemData.addAll(data)
            }

        addDisposable(disposable)
    }

    fun clickPreset() {

    }

}