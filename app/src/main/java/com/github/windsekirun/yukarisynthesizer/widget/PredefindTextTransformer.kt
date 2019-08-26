package com.github.windsekirun.yukarisynthesizer.widget

import android.content.Context
import android.util.TypedValue
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 * PredicateLayout
 * Class: PredefindTextTransformer
 * Created by Pyxis on 2018-01-02.
 *
 *
 * Description:
 */

class PredefindTextTransformer<T>(private val mContext: Context) : PredicateTextTransformer<T> {


    override fun generateNewText(
        context: Context, text: T, backgroundRes: Int?,
        size: Int, gravity: Int, color: Int
    ): TextView {
        val textView = TextView(mContext)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size.toFloat())
        textView.text = String.format(" %s ", text)
        textView.gravity = gravity
        textView.setTextColor(color)

        if (backgroundRes != null) {
            textView.background = ContextCompat.getDrawable(context, backgroundRes)
        }
        return textView
    }
}
