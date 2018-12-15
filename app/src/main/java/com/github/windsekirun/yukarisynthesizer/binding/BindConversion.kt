package com.github.windsekirun.yukarisynthesizer.binding

import android.view.View
import androidx.databinding.BindingConversion

/**
 * set of [BindConversion] in DataBinding
 */
object BindConversion {

    @JvmStatic
    @BindingConversion
    fun convertBooleanToVisibility(visible: Boolean): Int {
        return if (visible) View.VISIBLE else View.GONE
    }
}