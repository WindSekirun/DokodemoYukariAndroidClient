package com.github.windsekirun.yukarisynthesizer.utils

import android.util.Log
import io.reactivex.functions.Consumer

object CommonUtils {
    @JvmField
    val ignoreError = Consumer<Throwable> { t -> Log.e("ignore", t.message, t) }
}