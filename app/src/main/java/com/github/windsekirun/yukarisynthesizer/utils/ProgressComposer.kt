package com.github.windsekirun.yukarisynthesizer.utils

import com.github.windsekirun.baseapp.utils.ProgressDialogManager
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class ProgressComposer<T> : ObservableTransformer<T, T> {
    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.doOnSubscribe { ProgressDialogManager.instance.show() }
            .doFinally { ProgressDialogManager.instance.clear() }
    }

}