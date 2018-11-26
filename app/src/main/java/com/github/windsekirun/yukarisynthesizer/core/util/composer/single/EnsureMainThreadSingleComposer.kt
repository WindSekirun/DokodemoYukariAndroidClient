package com.github.windsekirun.yukarisynthesizer.core.util.composer.single

import io.reactivex.*
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
class EnsureMainThreadSingleComposer<T> : SingleTransformer<T, T> {
    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
