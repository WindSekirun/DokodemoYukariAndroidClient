package com.github.windsekirun.yukarisynthesizer.utils

import android.os.Looper
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

/**
 * [Observable] wrapper of [ObservableArrayList], within [ObservableList.OnListChangedCallback.onChanged]
 */
class ObservableArrayListChangeObservable<T>(private val observableArrayList: ObservableArrayList<T>) :
    Observable<ObservableArrayListChangeEvent<T>>() {

    override fun subscribeActual(observer: Observer<in ObservableArrayListChangeEvent<T>>) {
        if (!checkMainThread(observer)) {
            return
        }
        val listener = Listener(observableArrayList, observer)
        observer.onSubscribe(listener)
        observableArrayList.addOnListChangedCallback(listener.onListChangedCallback)
    }

    private inner class Listener internal constructor(
        private val observableArrayList: ObservableArrayList<T>,
        observer: Observer<in ObservableArrayListChangeEvent<T>>
    ) : MainThreadDisposable() {
        internal val onListChangedCallback: ObservableList.OnListChangedCallback<ObservableArrayList<T>>

        init {
            this.onListChangedCallback = object : ObservableList.OnListChangedCallback<ObservableArrayList<T>>() {
                override fun onChanged(observableArrayList: ObservableArrayList<T>) {
                    observer.onNext(ObservableArrayListChangeEvent(observableArrayList))
                }

                override fun onItemRangeChanged(
                    observableArrayList: ObservableArrayList<T>,
                    positionStart: Int, itemCount: Int
                ) {
                }

                override fun onItemRangeInserted(
                    observableArrayList: ObservableArrayList<T>,
                    positionStart: Int, itemCount: Int
                ) {
                }

                override fun onItemRangeMoved(
                    observableArrayList: ObservableArrayList<T>,
                    positionStart: Int, positionEnd: Int, itemCount: Int
                ) {
                }

                override fun onItemRangeRemoved(
                    observableArrayList: ObservableArrayList<T>,
                    positionStart: Int, itemCount: Int
                ) {
                }
            }
        }

        override fun onDispose() {
            observableArrayList.removeOnListChangedCallback(onListChangedCallback)
        }
    }

    private fun checkMainThread(observer: Observer<*>): Boolean {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            observer.onError(
                IllegalStateException(
                    "Expected to be called on the main thread but was " + Thread.currentThread().name
                )
            )
            return false
        }
        return true
    }
}

/**
 * data class for changes event of [ObservableArrayList]
 */
data class ObservableArrayListChangeEvent<T>(val list: ObservableArrayList<T>)

/**
 * Extension methods for returning [ObservableArrayList]
 */
fun <T> ObservableArrayList<T>.propertyChanges() = ObservableArrayListChangeObservable(this)