package com.github.windsekirun.yukarisynthesizer.main.preset

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.composer.EnsureMainThreadComposer
import com.github.windsekirun.baseapp.module.reference.ActivityReference
import com.github.windsekirun.baseapp.utils.subscribe
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.core.YukariOperator
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.dialog.PresetDialog
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
    val itemData: MutableLiveData<List<PresetItem>> = MutableLiveData()

    @Inject
    lateinit var yukariOperator: YukariOperator

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        val disposable = yukariOperator.getPresetList()
            .compose(EnsureMainThreadComposer())
            .subscribe { data, error ->
                if (error != null) {
                    Log.e(MainStoryViewModel::class.java.simpleName, "onResume: ", error)
                    return@subscribe
                }
                itemData.value = data
            }

        addDisposable(disposable)
    }

    fun clickPreset() {
        val presetDialog = PresetDialog(ActivityReference.getActivtyReference()!!)
        presetDialog.show()
    }

}