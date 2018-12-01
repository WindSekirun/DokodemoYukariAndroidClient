package com.github.windsekirun.yukarisynthesizer.main.details.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.windsekirun.yukarisynthesizer.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.main_details_voice_fragment.*


/**
 * DokodemoYukariAndroidClient
 * Class: MainDrawerFragment
 * Created by Pyxis on 2018-11-26.
 *
 *
 * Description:
 */
class MainDetailsVoiceFragment : BottomSheetDialogFragment() {
    var onMenuClickListener: ((Mode) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_details_voice_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        addVoice.setOnClickListener {
            onMenuClickListener?.invoke(Mode.Voice)
            dismissFragment()
        }
        addBreak.setOnClickListener {
            onMenuClickListener?.invoke(Mode.Break)
            dismissFragment()
        }
        addHistory.setOnClickListener {
            onMenuClickListener?.invoke(Mode.History)
            dismissFragment()
        }
    }

    private fun dismissFragment() {
        dismiss()
    }

    enum class Mode {
        Voice, Break, History
    }
}