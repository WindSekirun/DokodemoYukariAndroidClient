package com.github.windsekirun.yukarisynthesizer.dialog

import android.content.Context
import android.os.Bundle
import androidx.databinding.ObservableField
import com.github.windsekirun.baseapp.base.BaseDialog
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.databinding.PresetDialogBinding

/**
 * DokodemoYukariAndroidClient
 * Class: PresetDialog
 * Created by Pyxis on 12/8/18.
 *
 *
 * Description:
 */

class PresetDialog(context: Context) : BaseDialog<PresetDialogBinding>(context) {
    val selectedEngine = ObservableField<VoiceEngine>(VoiceEngine.Yukari)

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preset_dialog)
        mBinding.dialog = this
    }

    override fun show() {
        super.show()

    }
}