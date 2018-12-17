package com.github.windsekirun.yukarisynthesizer.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableInt
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.module.sheet.RoundedBottomSheetDialogFragment
import com.github.windsekirun.yukarisynthesizer.databinding.PresetDialogFragmentBinding

/**
 * DialogFragment for adjust preset
 */
class PresetDialogFragment : RoundedBottomSheetDialogFragment<PresetDialogFragmentBinding>() {
    val rangeProgress: ObservableInt = ObservableInt(10)
    val pitchProgress: ObservableInt = ObservableInt(5)
    val rateProgress: ObservableInt = ObservableInt(5)
    val volumeProgress: ObservableInt = ObservableInt(2)
    val title: ObservableString = ObservableString()

    lateinit var presetItem: PresetItem
    lateinit var callback: (PresetItem) -> Unit

    override fun createView(inflater: LayoutInflater, container: ViewGroup?) =
        PresetDialogFragmentBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragment = this

        if (presetItem.id != 0L) {
            volumeProgress.set((presetItem.volume * 2).toInt())
            rateProgress.set(((presetItem.rate * 10) - 5).toInt())
            pitchProgress.set(((presetItem.pitch* 10) - 5).toInt())
            rangeProgress.set((presetItem.range * 10).toInt())
        } else {
            volumeProgress.set(2)
            rateProgress.set(5)
            pitchProgress.set(5)
            rangeProgress.set(10)
            title.set(presetItem.title)
        }
    }

    fun clickSave(view: View) {
        presetItem.apply {
            this.title = this@PresetDialogFragment.title.get()
            this.volume = volumeProgress.get() * 0.5
            this.rate = ((rateProgress.get() + 1) * 0.1) + 0.5
            this.pitch = ((pitchProgress.get() + 1) * 0.1) + 0.5
            this.range = rateProgress.get() * 0.1
        }

        dismiss()
    }

    fun convertVolumeText(progress: Int): String = String.format("%.1f", progress * 0.5)
    fun convertRateText(progress: Int): String = String.format("%.1f", ((progress + 1) * 0.1) + 0.5)
    fun convertRangeText(progress: Int): String = String.format("%.1f", progress * 0.1)
}