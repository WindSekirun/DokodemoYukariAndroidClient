package com.github.windsekirun.yukarisynthesizer.main.details

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableArrayList
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.folderChooser
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.activityresult.RxActivityResult
import com.github.windsekirun.baseapp.module.composer.EnsureMainThreadComposer
import com.github.windsekirun.baseapp.module.reference.ActivityReference
import com.github.windsekirun.baseapp.utils.ProgressDialogManager
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.YukariOperator
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.PhonomeItem
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import com.github.windsekirun.yukarisynthesizer.main.event.*
import com.github.windsekirun.yukarisynthesizer.swipe.SwipeOrderActivity
import com.github.windsekirun.yukarisynthesizer.swipe.SwipeOrderViewModel
import com.github.windsekirun.yukarisynthesizer.utils.OnListChangeCallback
import com.github.windsekirun.yukarisynthesizer.utils.subscribe
import com.github.windsekirun.yukarisynthesizer.voice.VoiceDetailActivity
import com.github.windsekirun.yukarisynthesizer.voice.VoiceDetailViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.impl.F0
import pyxis.uzuki.live.richutilskt.utils.RPermission
import pyxis.uzuki.live.richutilskt.utils.toFile
import java.io.File
import java.io.IOException
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
    val itemData = ObservableArrayList<VoiceItem>()
    val title = ObservableString()

    @Inject
    lateinit var yukariOperator: YukariOperator

    private var changed: Boolean = false
    private lateinit var storyItem: StoryItem

    fun loadData(storyItem: StoryItem?) {
        this.storyItem = storyItem ?: StoryItem()
        bindItems(storyItem == null)
    }

    fun onBackPressed() {
        if (!changed || (title.isEmpty && itemData.isEmpty())) {
            postEvent(CloseFragmentEvent())
        } else {
            save(autoClose = true)
        }
    }

    fun clickMenuItem(mode: ToolbarMenuClickEvent.Mode) {
        when (mode) {
            ToolbarMenuClickEvent.Mode.Play -> requestSynthesis()
            ToolbarMenuClickEvent.Mode.Save -> requestSynthesisOtherPath()
            ToolbarMenuClickEvent.Mode.TopOrder -> save(swipeOrder = true)
            ToolbarMenuClickEvent.Mode.Remove -> removeStoryItem()

        }
    }

    fun clickSpeedDial(mode: SpeedDialClickEvent.Mode) {
        when (mode) {
            SpeedDialClickEvent.Mode.Voice -> addVoice()
            SpeedDialClickEvent.Mode.Break -> addBreak()
            SpeedDialClickEvent.Mode.History -> addVoiceHistory()
            SpeedDialClickEvent.Mode.STT -> addSTT()
        }
    }

    fun clickVoiceItem(item: VoiceItem, position: Int) {
        val originId = item.id
        val bundle = Bundle().apply {
            putLong(VoiceDetailViewModel.EXTRA_VOICE_ID, originId)
        }

        RxActivityResult.result()
            .flatMapObservable {
                val id = it.data?.getLongExtra(VoiceDetailViewModel.EXTRA_EDIT_VOICE_ID, 0) ?: 0L
                val requestDelete =
                    it.data?.getBooleanExtra(VoiceDetailViewModel.EXTRA_REQUEST_DELETE, false)
                        ?: false

                if (requestDelete) {
                    yukariOperator.removeVoiceItem(id, true)
                        .map { VoiceItem() }
                } else {
                    if (id != 0L) {
                        yukariOperator.getVoiceItem(id)
                    } else {
                        Observable.error(IOException())
                    }
                }
            }
            .subscribe { data, error ->
                if (error != null || data == null) {
                    Log.e(TAG, "addVoice", error)
                    return@subscribe
                }

                if (data.id != 0L) {
                    itemData[position] = data
                } else {
                    itemData.removeAt(position)
                }
            }.addTo(compositeDisposable)

        RxActivityResult.startActivityForResult(VoiceDetailActivity::class.java, bundle)
    }

    private fun bindItems(initial: Boolean) {
        if (initial) {
            observeEvent()
            return
        }

        val disposable = yukariOperator.getVoiceListAssociatedStoryItem(storyItem)
            .compose(EnsureMainThreadComposer())
            .subscribe { data, error ->
                if (error != null || data == null) return@subscribe
                title.set(storyItem.title)
                itemData.clear()
                itemData.addAll(data)
                observeEvent()
            }

        addDisposable(disposable)
    }

    private fun save(autoClose: Boolean = false, swipeOrder: Boolean = false) {
        if (itemData.isEmpty()) {
            postEvent(CloseFragmentEvent())
            return
        }

        storyItem.apply {
            this.title = this@MainDetailsViewModel.title.get()
            this.voiceEntries = itemData
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
        title.propertyChanges()
            .subscribe { _, _ -> changed = true }
            .addTo(compositeDisposable)

        itemData.addOnListChangedCallback(OnListChangeCallback<PhonomeItem> {
            changed = true
        })
    }

    private fun requestSynthesis() {
        requestSynthesis {
            playVoices()
        }
    }

    private fun requestSynthesis(callback: () -> Unit) {
        if (itemData.isEmpty()) return

        storyItem.apply {
            this.title = this@MainDetailsViewModel.title.get()
            this.voiceEntries = itemData
        }

        ProgressDialogManager.instance.show()

        val disposable = yukariOperator.addStoryItem(storyItem)
            .flatMap { yukariOperator.getSynthesisData(it) }
            .flatMapSingle {
                storyItem = it
                yukariOperator.requestSynthesis(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                ProgressDialogManager.instance.clear()
                if (error != null || data == null) {
                    showAlertDialog(error?.message ?: "unknown error")
                    return@subscribe
                }

                storyItem = data
                callback()
            }

        addDisposable(disposable)
    }

    private fun requestSynthesisOtherPath() {
        requestPermission(F0 {
            MaterialDialog(checkNotNull(ActivityReference.getActivtyReference())).show {
                folderChooser { _, folder ->
                    requestSynthesis {
                        val origin = storyItem.localPath.toFile()
                        val newPath = File(folder.absolutePath, "${storyItem.title}.mp3")
                        origin.copyTo(newPath, true)

                        runOnUiThread {
                            showToast(
                                context.getString(R.string.main_detail_saved_other_path).format(
                                    newPath.absolutePath
                                )
                            )
                        }
                    }
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun playVoices() {
        val event = ShowPlayDialogEvent(listOf(storyItem))
        postEvent(event)
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
        val event = ShowBreakDialogEvent(VoiceItem()) {
            val disposable = yukariOperator.addVoiceItem(it)
                .compose(EnsureMainThreadComposer())
                .subscribe { data, error ->
                    if (error != null || data == null) return@subscribe
                    itemData.add(data)
                }

            addDisposable(disposable)
        }

        postEvent(event)
    }

    private fun addVoice() {
        RxActivityResult.result()
            .flatMapObservable {
                val id = it.data?.getLongExtra(VoiceDetailViewModel.EXTRA_EDIT_VOICE_ID, 0) ?: 0L
                if (id != 0L) {
                    yukariOperator.getVoiceItem(id)
                } else {
                    Observable.error(IOException("Unexpected error"))
                }
            }
            .subscribe { data, error ->
                if (error != null || data == null) {
                    Log.e(TAG, "addVoice", error)
                    return@subscribe
                }

                itemData.add(data)
            }.addTo(compositeDisposable)

        RxActivityResult.startActivityForResult(VoiceDetailActivity::class.java)
    }

    private fun addSTT() {
        val event = ShowVoiceRecognitionEvent { voiceResult ->
            val splitResult = voiceResult.split(" ").map { it.trim() }
            val phonomes = splitResult.map { PhonomeItem(it, "") }.toMutableList()

            val disposable = yukariOperator.getPresetList()
                .flatMap {
                    Observables.combineLatest(
                        yukariOperator.getDefaultPresetItem(VoiceEngine.Yukari),
                        yukariOperator.addPhonomeItems(phonomes)
                    )
                }
                .subscribe { data, error ->
                    if (error != null || data == null) {
                        return@subscribe
                    }

                    val preset = data.first
                    val phonomeIds = data.second

                    val voiceItem = VoiceItem().apply {
                        this.engine = preset.engine
                        this.preset = preset
                        this.breakTime = 0
                        this.phonomes = phonomes
                        this.phonomeIds = phonomeIds
                        bindContentOrigin()
                    }

                    itemData.add(voiceItem)
                }

            addDisposable(disposable)
        }

        requestPermission({ code ->
            if (code == RPermission.PERMISSION_GRANTED) {
                postEvent(event)
            } else {
                showToast(getString(R.string.main_detail_record_permission))
            }
        }, arrayOf(Manifest.permission.RECORD_AUDIO))
    }

    private fun addVoiceHistory() {
        val event = ShowHistoryDialogEvent { itemData.add(it) }
        postEvent(event)
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

    companion object {
        private val TAG = MainDetailsViewModel::class.java.simpleName
    }
}