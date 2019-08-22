package com.github.windsekirun.yukarisynthesizer.utils

import android.util.Log
import io.reactivex.Observable
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.disposables.Disposable

@CheckReturnValue
fun <T> Observable<T>.subscribe(callback: (T?, Throwable?) -> Unit): Disposable {
    return this.subscribe({
        callback.invoke(it, null)
    }, {
        Log.e("RxExtensions", "error", it)
        callback.invoke(null, it)
    })
}


fun Disposable?.safeDispose() {
    if (this != null && !this.isDisposed) this.dispose()
}
