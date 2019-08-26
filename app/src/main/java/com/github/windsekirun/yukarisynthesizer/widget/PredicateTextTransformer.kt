package com.github.windsekirun.yukarisynthesizer.widget

import android.content.Context
import android.widget.TextView

/**
 * PredicateLayout
 * Class: PredicateTextTransformer
 * Created by Pyxis on 2018-01-02.
 *
 *
 * Description:
 */

interface PredicateTextTransformer<T> {
    fun generateNewText(
        context: Context,
        text: T,
        backgroundRes: Int?,
        size: Int,
        gravity: Int,
        color: Int
    ): TextView
}
