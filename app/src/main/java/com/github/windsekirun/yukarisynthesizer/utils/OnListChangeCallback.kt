package com.github.windsekirun.yukarisynthesizer.utils

import androidx.databinding.ObservableList

class OnListChangeCallback<T>() : ObservableList.OnListChangedCallback<ObservableList<T>>() {
    private var callback: (() -> Unit)? = null

    constructor(callback: () -> Unit) : this() {
        this.callback = callback
    }

    override fun onChanged(sender: ObservableList<T>?) {
        callback?.invoke()
    }

    override fun onItemRangeRemoved(
        sender: ObservableList<T>?,
        positionStart: Int,
        itemCount: Int
    ) {
        callback?.invoke()
    }

    override fun onItemRangeMoved(
        sender: ObservableList<T>?,
        fromPosition: Int,
        toPosition: Int,
        itemCount: Int
    ) {
        callback?.invoke()
    }

    override fun onItemRangeInserted(
        sender: ObservableList<T>?,
        positionStart: Int,
        itemCount: Int
    ) {
        callback?.invoke()
    }

    override fun onItemRangeChanged(
        sender: ObservableList<T>?,
        positionStart: Int,
        itemCount: Int
    ) {
        callback?.invoke()
    }
}