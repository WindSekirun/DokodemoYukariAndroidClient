package com.github.windsekirun.yukarisynthesizer.voice

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LifecycleOwner
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.argsinjector.Extra
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.YukariOperator
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.PhonomeItem
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import com.github.windsekirun.yukarisynthesizer.main.event.InvokeBackEvent
import com.github.windsekirun.yukarisynthesizer.main.event.ShowPhonomeHistoryEvent
import com.github.windsekirun.yukarisynthesizer.main.event.ShowVoicePresetEvent
import com.github.windsekirun.yukarisynthesizer.utils.propertyChanges
import com.github.windsekirun.yukarisynthesizer.utils.subscribe
import com.github.windsekirun.yukarisynthesizer.voice.event.RefreshLayoutEvent
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import java.util.*
import javax.inject.Inject

/**
 * DokodemoYukariAndroidClient
 * Class: VoiceDetailViewModel
 * Created by Pyxis on 2018-11-26.
 *
 *
 * Description:
 */
@InjectViewModel
class VoiceDetailViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {
    val selectedEngine = ObservableField<VoiceEngine>()
    val selectedPreset = ObservableString()
    val selectedDeceptions = ObservableString()
    val selectedText = ObservableString()
    val voiceOriginLength = ObservableInt()
    val itemData = ObservableArrayList<PhonomeItem>()
    var selectedPhonomeItem: PhonomeItem? = null

    private var selectedPresetItem: PresetItem = PresetItem()
    private var selectedPhonomeIndex = -1
    private var originalVoiceItem: VoiceItem = VoiceItem()
    private var changed: Boolean = false

    @Inject
    lateinit var yukariOperator: YukariOperator
    @Extra(EXTRA_VOICE_ID)
    var voiceId: Long = 0

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        loadData()
        itemData.propertyChanges()
            .subscribe { _, _ ->
                changed = true
                refreshFlexBox()
            }.addTo(compositeDisposable)
    }

    fun clickToolbarMenuItem(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_voice_copy -> copyVoice()
            R.id.menu_voice_remove -> removeVoice()
            R.id.menu_voice_origin -> backToOrigin()
        }

        return true
    }

    fun clickToolbarNavigation(view: View) {
        postEvent(InvokeBackEvent())
    }

    fun onBackPressed() {
        if (changed || itemData.isNotEmpty()) {
            save()
        } else {
            finishActivity(VoiceDetailActivity::class.java)
        }
    }

    fun clickPresetSelect(view: View) {
        val event = ShowVoicePresetEvent(selectedEngine.get() ?: VoiceEngine.Yukari) {
            selectedPresetItem = it
            selectedPreset.set(it.title)
        }

        postEvent(event)
    }

    fun clickYukari(view: View) {
        selectedEngine.set(VoiceEngine.Yukari)
        selectedPresetItem = PresetItem()
        selectedPreset.set("")
    }

    fun clickMaki(view: View) {
        selectedEngine.set(VoiceEngine.Maki)
        selectedPresetItem = PresetItem()
        selectedPreset.set("")
    }

    fun clickEnter(view: View) {
        if (selectedText.get().isEmpty()) return

        if (selectedPhonomeIndex != -1) {
            val phonomeItem = itemData[selectedPhonomeIndex].apply {
                this.origin = selectedText.get()
                this.phoneme = selectedDeceptions.get()
            }

            itemData[selectedPhonomeIndex] = phonomeItem
        } else {
            val phonomeItem = PhonomeItem(selectedText.get(), selectedDeceptions.get())
            itemData.add(phonomeItem)
        }

        refreshFlexBox()
        selectedText.set("")
        selectedDeceptions.set("")
    }


    fun clickHistory(view: View) {
        val event = ShowPhonomeHistoryEvent {
            itemData.add(it)
            refreshFlexBox()
        }
        postEvent(event)
    }

    fun clickItem(item: PhonomeItem) {
        selectedPhonomeItem = item
        selectedPhonomeIndex = itemData.indexOf(item)
        selectedText.set(item.origin)
        selectedDeceptions.set(item.phoneme)

        refreshFlexBox()
    }

    fun clickRemove(item: PhonomeItem) {
        selectedPhonomeItem = null
        selectedPhonomeIndex = -1
        selectedText.set("")
        selectedDeceptions.set("")

        itemData.remove(item)
        refreshFlexBox()
    }

    fun handleEditorAction(view: TextView, actionId: Int?, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            clickEnter(view)
            return true
        }
        return false
    }

    private fun loadData() {
        if (voiceId == 0L) {
            selectedEngine.set(VoiceEngine.Yukari)
            return
        }

        val disposable = yukariOperator.getVoiceItem(voiceId)
            .flatMap {
                Observables.combineLatest(
                    yukariOperator.getPhonomeListAssociatedVoiceItem(it),
                    Observable.just(it)
                )
            }
            .subscribe { data, error ->
                if (error != null || data == null) {
                    Log.e(TAG, "ignore error", error)
                    return@subscribe
                }

                changed = false

                itemData.clear()
                itemData.addAll(data.first)

                originalVoiceItem = data.second
                selectedEngine.set(data.second.engine)
                selectedPresetItem = data.second.preset
                selectedPreset.set(data.second.preset.title)

                refreshFlexBox()
            }

        addDisposable(disposable)
    }

    private fun refreshFlexBox() {
        val sum = itemData.asSequence().map { it.origin.length }.sum()

        voiceOriginLength.set(sum)
        postEvent(RefreshLayoutEvent())
    }

    private fun save() {
        if (!validateSaveCondition()) return

        yukariOperator.addPhonomeItems(itemData)
            .flatMap {
                originalVoiceItem.apply {
                    engine = selectedEngine.get() ?: VoiceEngine.Yukari
                    preset = selectedPresetItem
                    breakTime = 0
                    regDate = Date() // modified to current time
                    phonomeIds = it
                    phonomes = itemData
                    bindContentOrigin()
                }

                yukariOperator.addVoiceItem(originalVoiceItem)
            }.subscribe { data, error ->
                if (error != null || data == null) {
                    Log.e(TAG, "save", error)
                    return@subscribe
                }

                showToast("Saved.")

                val bundle = Bundle().apply {
                    putLong(EXTRA_EDIT_VOICE_ID, data.id)
                }

                setResult(Activity.RESULT_OK, bundle)
                finishActivity(VoiceDetailActivity::class.java)
            }.addTo(compositeDisposable)
    }

    private fun validateSaveCondition(): Boolean {
        if (selectedPresetItem.id == 0L) {
            showToast("Please select preset.")
            return false
        }

        return true
    }

    private fun copyVoice() {
        if (validateSaveCondition()) {
            originalVoiceItem.apply { id = 0L }
            save()
        }
    }

    private fun removeVoice() {
        val bundle = Bundle().apply {
            putBoolean(EXTRA_REQUEST_DELETE, true)
        }

        setResult(Activity.RESULT_OK, bundle)
        finishActivity(VoiceDetailActivity::class.java)
    }

    private fun backToOrigin() {
        loadData()
    }

    companion object {
        const val EXTRA_VOICE_ID = "45c57318-1d67-4ace-800e-7e3257a79b8b"
        const val EXTRA_EDIT_VOICE_ID = "03c005ff-415a-4ed8-a5f0-a3cddc78c3c9"
        const val EXTRA_REQUEST_DELETE = "1f6cf925-1bc7-42b7-a17d-b8cc6db1140b"
        private val TAG: String = VoiceDetailViewModel::class.java.simpleName
    }
}