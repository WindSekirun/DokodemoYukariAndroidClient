package com.github.windsekirun.yukarisynthesizer.main.details

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.benoitquenaudon.rxdatabinding.databinding.RxObservableBoolean
import com.github.windsekirun.baseapp.base.BaseViewModel
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
import com.github.windsekirun.yukarisynthesizer.dialog.PlayDialog
import com.github.windsekirun.yukarisynthesizer.main.details.event.CloseFragmentEvent
import com.github.windsekirun.yukarisynthesizer.main.details.event.MenuClickBarEvent
import com.github.windsekirun.yukarisynthesizer.utils.propertyChanges
import com.github.windsekirun.yukarisynthesizer.utils.subscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
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
    private val favorite: ObservableBoolean = ObservableBoolean()
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

    fun clickMenuItem(mode: MenuClickBarEvent.Mode) {
        when (mode) {
            MenuClickBarEvent.Mode.Play -> requestSynthesis()
            MenuClickBarEvent.Mode.Star -> toggleFavorite()
            MenuClickBarEvent.Mode.Remove -> removeStoryItem()
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
                favorite.set(storyItem.favoriteFlag)
                itemData.value = data

                observeEvent()
            }

        addDisposable(disposable)
    }

    private fun save(autoClose: Boolean = false) {
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
}