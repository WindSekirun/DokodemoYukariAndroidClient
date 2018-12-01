package com.github.windsekirun.yukarisynthesizer.utils

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.github.windsekirun.baseapp.module.reference.ActivityReference
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine

/**
 * DokodemoYukariAndroidClient
 * Class: CharacterBindUtils
 * Created by Pyxis on 2018-11-27.ㅓ
 *
 *
 * Description:
 */
object CharacterBindUtils {

    @JvmStatic
    fun getCharacterImg(voiceEngine: VoiceEngine): Drawable? {
        val resId = when (voiceEngine) {
            VoiceEngine.Yukari -> R.drawable.ic_yukari_selected
            VoiceEngine.Maki -> R.drawable.ic_maki_selected
            VoiceEngine.NONE -> R.drawable.character_icon_selected
        }

        val context = ActivityReference.getContext()
        return ContextCompat.getDrawable(context, resId)
    }
}
