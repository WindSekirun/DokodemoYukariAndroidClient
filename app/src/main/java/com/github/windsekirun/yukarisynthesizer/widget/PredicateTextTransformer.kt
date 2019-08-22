package com.github.windsekirun.yukarisynthesizer.widget

import android.content.Context
import android.widget.TextView

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
