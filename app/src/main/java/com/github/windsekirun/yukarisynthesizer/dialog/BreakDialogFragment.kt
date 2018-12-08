package com.github.windsekirun.yukarisynthesizer.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableInt
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import com.github.windsekirun.yukarisynthesizer.databinding.BreakDialogFragmentBinding
import com.github.windsekirun.yukarisynthesizer.module.sheet.RoundedBottomSheetDialogFragment

class BreakDialogFragment : RoundedBottomSheetDialogFragment<BreakDialogFragmentBinding>() {
    val progress: ObservableInt = ObservableInt(0)
    lateinit var voiceItem: VoiceItem
    lateinit var callback: (VoiceItem) -> Unit

    override fun createView(inflater: LayoutInflater, container: ViewGroup?) =
        BreakDialogFragmentBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragment = this

        if (voiceItem.breakTime == 0L) {
            progress.set(0)
        } else {
            progress.set((voiceItem.breakTime.toInt() / 100) - 1)
        }
    }

    fun clickSave(view: View) {
        val result = (progress.get() + 1) * 100
        voiceItem.apply {
            this.breakTime = result.toLong()
            this.engine = VoiceEngine.Break
        }

        callback.invoke(voiceItem)
        dismiss()
    }

    fun convertTimeText(progress: Int): String = "${((progress + 1) * 100).toDouble() / 1000.0}s"
}