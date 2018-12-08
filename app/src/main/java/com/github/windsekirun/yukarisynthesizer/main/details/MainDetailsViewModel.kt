package com.github.windsekirun.yukarisynthesizer.main.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.benoitquenaudon.rxdatabinding.databinding.RxObservableBoolean
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.activityresult.RxActivityResult
import com.github.windsekirun.baseapp.module.reference.ActivityReference
import com.github.windsekirun.baseapp.utils.ProgressDialogManager
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.YukariOperator
import com.github.windsekirun.yukarisynthesizer.core.composer.EnsureMainThreadComposer
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import com.github.windsekirun.yukarisynthesizer.dialog.BreakTimeDialog
import com.github.windsekirun.yukarisynthesizer.dialog.PlayDialog
import com.github.windsekirun.yukarisynthesizer.dialog.VoiceHistoryDialog
import com.github.windsekirun.yukarisynthesizer.main.details.dialog.MainDetailsVoiceFragment
import com.github.windsekirun.yukarisynthesizer.main.details.event.CloseFragmentEvent
import com.github.windsekirun.yukarisynthesizer.main.details.event.MenuClickBarEvent
import com.github.windsekirun.yukarisynthesizer.swipe.SwipeOrderActivity
import com.github.windsekirun.yukarisynthesizer.swipe.SwipeOrderViewModel
import com.github.windsekirun.yukarisynthesizer.utils.propertyChanges
import com.github.windsekirun.yukarisynthesizer.utils.subscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.toFile
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
    val itemData: MutableLiveData<MutableList<VoiceItem>> = MutableLiveData()
    val title = ObservableString()

    @Inject
    lateinit var yukariOperator: YukariOperator

    private val favorite: ObservableBoolean = ObservableBoolean()
    private var changed: Boolean = false
    private lateinit var storyItem: StoryItem
    private val changeObserver = Observer<List<VoiceItem>> {
        changed = true
    }

    override fun onCleared() {
        super.onCleared()
        itemData.removeObserver(changeObserver)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        itemData.removeObserver(changeObserver)
    }

    fun loadData(storyItem: StoryItem?) {
        this.storyItem = storyItem ?: StoryItem()
        bindItems(storyItem == null)
    }

    fun onBackPressed() {
        if (!changed || (title.isEmpty && itemData.value!!.isEmpty())) postEvent(CloseFragmentEvent())
        save(autoClose = true)
    }

    fun clickMenuItem(mode: MenuClickBarEvent.Mode) {
        when (mode) {
            MenuClickBarEvent.Mode.Play -> requestSynthesis()
            MenuClickBarEvent.Mode.Star -> toggleFavorite()
            MenuClickBarEvent.Mode.Remove -> removeStoryItem()
        }
    }

    fun clickAddVoice(mode: MainDetailsVoiceFragment.Mode) {
        when (mode) {
            MainDetailsVoiceFragment.Mode.Voice -> addVoice()
            MainDetailsVoiceFragment.Mode.Break -> addBreak()
            MainDetailsVoiceFragment.Mode.History -> addVoiceHistory()
        }
    }

    fun clickSwipeOrder() {
        save(swipeOrder = true)
    }

    private fun bindItems(initial: Boolean) {
        if (initial) {
            itemData.value = mutableListOf()
            observeEvent()
            return
        }

        val disposable = yukariOperator.getVoiceListAssociatedStoryItem(storyItem)
            .compose(EnsureMainThreadComposer())
            .subscribe { data, error ->
                if (error != null) return@subscribe
                title.set(storyItem.title)
                favorite.set(storyItem.favoriteFlag)
                itemData.value = data!!.toMutableList()

                observeEvent()
            }

        addDisposable(disposable)
    }

    private fun save(autoClose: Boolean = false, swipeOrder: Boolean = false) {
        if (itemData.value!!.isEmpty()) return

        storyItem.apply {
            this.title = this@MainDetailsViewModel.title.get()
            this.favoriteFlag = favorite.get()
            this.voiceEntries = itemData.value!!
        }

        val disposable = yukariOperator.addStoryItem(storyItem)
            .compose(EnsureMainThreadComposer())
            .subscribe { _, _ ->
                if (autoClose) postEvent(CloseFragmentEvent())
                if (swipeOrder) moveToSwipeOrderActivity()
            }

        addDisposable(disposable)
    }

    private fun observeEvent() {
        itemData.observeForever(changeObserver)

        val disposable =
            Observables.combineLatest(title.propertyChanges(), RxObservableBoolean.propertyChanges(favorite))
                .subscribe { _, _ -> changed = true }
        addDisposable(disposable)
    }

    private fun requestSynthesis() {
        if (itemData.value!!.isEmpty()) return

        val ids = itemData.value!!.map { it.id }
        val contentEqual = checkEqualContent(ids, storyItem.voicesIds)
        if (contentEqual && (storyItem.localPath.isNotEmpty() && storyItem.localPath.toFile().canRead())) {
            // if itemData and voiceIds is equal and localPath is valid, we don't need to synthesis this timing.
            // just play sounds.
            playVoices()
        }

        storyItem.apply {
            this.title = this@MainDetailsViewModel.title.get()
            this.favoriteFlag = favorite.get()
            this.voiceEntries = itemData.value!!
        }

        ProgressDialogManager.instance.show()

        val disposable = yukariOperator.addStoryItem(storyItem)
            .flatMapSingle { yukariOperator.requestSynthesis(storyItem) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                ProgressDialogManager.instance.clear()
                if (error != null) {
                    showAlertDialog(error.message ?: "unknown error")
                    return@subscribe
                }

                storyItem = data!!
                playVoices()
            }

        addDisposable(disposable)
    }

    private fun playVoices() {
        val playDialog = PlayDialog(ActivityReference.getActivtyReference()!!)
        playDialog.show(listOf(storyItem))
    }

    private fun toggleFavorite() {
        favorite.set(!favorite.get())
        if (favorite.get()) {
            showToast(getString(R.string.main_details_favorite_on))
        } else {
            showToast(getString(R.string.main_details_favorite_off))
        }
    }

    private fun removeStoryItem() {
        ProgressDialogManager.instance.show()

        val disposable = yukariOperator.removeStoryItem(storyItem)
            .compose(EnsureMainThreadComposer())
            .subscribe { _, error ->
                ProgressDialogManager.instance.clear()
                if (error != null) return@subscribe

                showAlertDialog(getString(R.string.main_details_removed)) { _, _ ->
                    changed = false
                    onBackPressed()
                }
            }

        addDisposable(disposable)
    }

    private fun addBreak() {
        val breakTimeDialog = BreakTimeDialog(ActivityReference.getActivtyReference()!!)
        breakTimeDialog.show(VoiceItem()) {
            val disposable = yukariOperator.addVoiceItem(it)
                .compose(EnsureMainThreadComposer())
                .subscribe { data, error ->
                    if (error != null) return@subscribe
                    val list = itemData.value!!
                    list.add(data!!)
                    itemData.value = list
                }

            addDisposable(disposable)
        }
    }

    private fun addVoice() {

    }

    private fun addVoiceHistory() {
        val voiceHistoryDialog = VoiceHistoryDialog(ActivityReference.getActivtyReference()!!)
        voiceHistoryDialog.show {
            val list = itemData.value!!
            list.add(it)
            itemData.value = list
        }
    }

    private fun moveToSwipeOrderActivity() {
        val bundle = Bundle()
        bundle.putLong(SwipeOrderViewModel.EXTRA_STORY_ITEM_ID, storyItem.id)

        val disposable = RxActivityResult.result()
            .toObservable()
            .flatMap { yukariOperator.getStoryItem(storyItem.id) }
            .subscribe { data, error ->
                if (error != null) return@subscribe
                loadData(data)
            }

        RxActivityResult.startActivityForResult(
            SwipeOrderActivity::class.java,
            bundle,
            ActivityReference.getActivtyReference() as? AppCompatActivity
        )

        addDisposable(disposable)
    }

    private fun checkEqualContent(target: List<Long?>?, original: List<Long>): Boolean {
        val ids = target?.toTypedArray() ?: arrayOfNulls(storyItem.voicesIds.size)
        return ids contentEquals original.toTypedArray()
    }
}