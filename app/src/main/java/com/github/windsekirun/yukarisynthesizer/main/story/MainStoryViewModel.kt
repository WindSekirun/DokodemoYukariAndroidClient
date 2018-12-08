package com.github.windsekirun.yukarisynthesizer.main.story

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
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

    @Inject
    lateinit var yukariOperator: YukariOperator

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        val disposable = yukariOperator.getStoryList()
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
}