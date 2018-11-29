@file:JvmName("RxExtensions")

package com.github.windsekirun.yukarisynthesizer.utils

import android.util.Log
import io.reactivex.Observable
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 * DokodemoYukariAndroidClient
 * Class: RxExtensions
 * Created by Pyxis on 11/28/18.
 *
 * Description:
 */

@CheckReturnValue
fun <T> Observable<T>.subscribe(callback: (T?, Throwable?) -> Unit): Disposable {
    return this.subscribe({
        callback.invoke(it, null)
    }, {
        callback.invoke(null, it)
    })
}

@JvmField
val ignoreError = Consumer<Throwable> { t -> Log.e("ignore", t.message, t) }