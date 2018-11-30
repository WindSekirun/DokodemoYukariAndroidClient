package com.github.windsekirun.yukarisynthesizer.core.composer

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * DokodemoYukariAndroidClient
 * Class: EnsureMainThreadComposer
 * Created by Pyxis on 2018-11-26.
 *
 *
 * Description:
 */
class EnsureMainThreadComposer<T> : ObservableTransformer<T, T> {
    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
