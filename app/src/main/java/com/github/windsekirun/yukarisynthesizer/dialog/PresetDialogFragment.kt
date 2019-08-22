package com.github.windsekirun.yukarisynthesizer.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.databinding.PresetDialogFragmentBinding
import com.github.windsekirun.yukarisynthesizer.module.sheet.RoundedBottomSheetDialogFragment

class PresetDialogFragment : RoundedBottomSheetDialogFragment<PresetDialogFragmentBinding>() {
    val selectedEngine = ObservableField<VoiceEngine>()
    val rangeProgress: ObservableInt = ObservableInt()
    val pitchProgress: ObservableInt = ObservableInt()
    val rateProgress: ObservableInt = ObservableInt()
    val volumeProgress: ObservableInt = ObservableInt()
    val title: ObservableString = ObservableString()

    lateinit var presetItem: PresetItem
    lateinit var callback: (PresetItem) -> Unit

    override fun createView(inflater: LayoutInflater, container: ViewGroup?) =
        PresetDialogFragmentBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragment = this

        if (presetItem.id != 0L) {
            selectedEngine.set(presetItem.engine)
            title.set(presetItem.title)
            volumeProgress.set((presetItem.volume * 2).toInt())
            rateProgress.set(((presetItem.rate * 10) - 6).toInt())
            pitchProgress.set(((presetItem.pitch * 10) - 6).toInt())
            rangeProgress.set((presetItem.range * 10).toInt())
        } else {
            selectedEngine.set(VoiceEngine.Yukari)
            title.set("")
            volumeProgress.set(2)
            rateProgress.set(4)
            pitchProgress.set(4)
            rangeProgress.set(10)

        }
    }

    fun clickSave(view: View) {
        presetItem.apply {
            this.engine = selectedEngine.get() ?: VoiceEngine.Yukari
            this.title = this@PresetDialogFragment.title.get()
            this.volume = convertVolumeText(volumeProgress.get()).toDouble()
            this.rate = convertRateText(rateProgress.get()).toDouble()
            this.pitch = convertRateText(pitchProgress.get()).toDouble()
            this.range = convertRangeText(rangeProgress.get()).toDouble()
        }

        callback.invoke(presetItem)

        dismiss()
    }

    fun clickYukari(view: View) {
        selectedEngine.set(VoiceEngine.Yukari)
    }

    fun clickMaki(view: View) {
        selectedEngine.set(VoiceEngine.Maki)
    }

    fun convertVolumeText(progress: Int): String = String.format("%.1f", progress * 0.5)
    fun convertRateText(progress: Int): String = String.format("%.1f", ((progress + 1) * 0.1) + 0.5)
    fun convertRangeText(progress: Int): String = String.format("%.1f", progress * 0.1)
}