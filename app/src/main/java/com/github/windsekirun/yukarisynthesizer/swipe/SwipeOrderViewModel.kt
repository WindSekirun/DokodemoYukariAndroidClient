package com.github.windsekirun.yukarisynthesizer.swipe

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.argsinjector.Extra
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.core.YukariOperator
import com.github.windsekirun.yukarisynthesizer.core.composer.EnsureMainThreadComposer
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import com.github.windsekirun.yukarisynthesizer.swipe.event.ReadyDisplayViewEvent
import com.github.windsekirun.yukarisynthesizer.utils.subscribe
import javax.inject.Inject


@InjectViewModel
class SwipeOrderViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {
    val itemData: MutableLiveData<MutableList<VoiceItem>> = MutableLiveData()
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
                if (error != null) return@subscribe
                itemData.value = data!!.toMutableList()
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
                it.apply { this.voiceEntries = itemData.value!! }
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
        val list = itemData.value!!
        list.remove(item)
        list.add(finalPosition, item)

        itemData.value = list
    }

    companion object {
        const val EXTRA_STORY_ITEM_ID = "3e156dc0-ee53-4529-88f1-edee6692f433"
    }

}