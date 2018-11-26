package com.github.windsekirun.yukarisynthesizer.core.util.composer

import com.github.windsekirun.baseapp.utils.ProgressDialogManager
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

/**
 * DokodemoYukariAndroidClient
 * Class: ProgressComposer
 * Created by Pyxis on 2018-11-26.
 *
 * Description:
 */
class ProgressComposer<T> : ObservableTransformer<T, T> {
    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.doOnSubscribe { ProgressDialogManager.instance.show() }
            .doOnTerminate { ProgressDialogManager.instance.clear() }
    }

}