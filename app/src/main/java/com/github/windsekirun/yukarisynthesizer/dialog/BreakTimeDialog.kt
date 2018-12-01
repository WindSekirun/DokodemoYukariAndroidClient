package com.github.windsekirun.yukarisynthesizer.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableInt
import com.github.windsekirun.baseapp.base.BaseDialog
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import com.github.windsekirun.yukarisynthesizer.databinding.BreakTimeDialogBinding


/**
 * DokodemoYukariAndroidClient
 * Class: PlayDialog
 * Created by Pyxis on 12/1/18.
 *
 *
 * Description:
 */

class BreakTimeDialog(context: Context) : BaseDialog<BreakTimeDialogBinding>(context) {
    val progress: ObservableInt = ObservableInt(0)
    lateinit var callback: (VoiceItem) -> Unit
    lateinit var voiceItem: VoiceItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.break_time_dialog)
        mBinding.dialog = this
    }

    fun show(voiceItem: VoiceItem, callback: (VoiceItem) -> Unit) {
        super.show()
        this.callback = callback
        this.voiceItem = voiceItem
        if (voiceItem.breakTime == 0L) {
            progress.set(0)
        } else {
            progress.set((voiceItem.breakTime.toInt() / 100) - 1)
        }
    }

    fun clickClose(view: View) {
        dismiss()
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