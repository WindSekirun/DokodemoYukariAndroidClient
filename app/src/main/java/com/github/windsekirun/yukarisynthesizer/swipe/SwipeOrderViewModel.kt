package com.github.windsekirun.yukarisynthesizer.swipe

import android.app.Activity
import androidx.databinding.ObservableArrayList
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.argsinjector.Extra
import com.github.windsekirun.baseapp.module.composer.EnsureMainThreadComposer
import com.github.windsekirun.baseapp.utils.subscribe
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.core.YukariOperator
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import com.github.windsekirun.yukarisynthesizer.swipe.event.ReadyDisplayViewEvent
import pyxis.uzuki.live.richutilskt.utils.toFile
import javax.inject.Inject

/**
 * DokodemoYukariAndroidClient
 * Class: SwipeOrderViewModel
 * Created by Pyxis on 2018-11-26.
 *
 *
 * Description:
 */
@InjectViewModel
class SwipeOrderViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {
    val itemData = ObservableArrayList<VoiceItem>()
    var changed: Boolean = false

    @Extra(EXTRA_STORY_ITEM_ID)
    var storyItemId: Long = 0

    @Inject
    lateinit var yukariOperator: YukariOperator

    fun loadData() {
        val disposable = yukariOperator.getStoryItem(storyItemId)
            .flatMap { yukariOperator.getVoiceListAssociatedStoryItem(it) }
            .compose(EnsureMainThreadComposer())
            .subscribe { data, error ->
                if (error != null || data == null) return@subscribe
                itemData.addAll(data)
                postEvent(ReadyDisplayViewEvent())
            }

        addDisposable(disposable)
    }

    fun onBackPressed() {
        if (!changed) {
            setResult(Activity.RESULT_OK)
            finishActivity(SwipeOrderActivity::class.java)
        }

        val disposable = yukariOperator.getStoryItem(storyItemId)
            .flatMap {
                // if ordering is complete, we have to remove synthesis data cause it doesn't match.
                if (it.localPath.isNotEmpty()) {
                    val file = it.localPath.toFile()
                    file.delete()
                }

                it.apply {
                    this.voiceEntries = itemData
                    this.localPath = ""
                }
                yukariOperator.addStoryItem(it)
            }
            .compose(EnsureMainThreadComposer())
            .subscribe { _, error ->
                if (error != null) return@subscribe

                setResult(Activity.RESULT_OK)
                finishActivity(SwipeOrderActivity::class.java)
            }

        addDisposable(disposable)
    }

    fun changeOrder(finalPosition: Int, item: VoiceItem) {
        itemData.remove(item)
        itemData.add(finalPosition, item)
        changed = true
    }

    fun removeItem(item: VoiceItem) {
        itemData.remove(item)
        changed = true
    }

    companion object {
        const val EXTRA_STORY_ITEM_ID = "3e156dc0-ee53-4529-88f1-edee6692f433"
    }

}