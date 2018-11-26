package com.github.windsekirun.yukarisynthesizer.core.util.composer.single

import com.github.windsekirun.baseapp.utils.ProgressDialogManager
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer

/**
 * DokodemoYukariAndroidClient
 * Class: ProgressSingleComposer
 * Created by Pyxis on 2018-11-26.
 *
 *
 * Description:
 */
class ProgressSingleComposer<T> : SingleTransformer<T, T> {
    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.doOnSubscribe { ProgressDialogManager.instance.show() }
            .doOnSuccess { ProgressDialogManager.instance.clear() }
            .doOnError { ProgressDialogManager.instance.clear() }
    }

}
