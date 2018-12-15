package com.github.windsekirun.yukarisynthesizer.utils

import androidx.lifecycle.MutableLiveData

/**
 * DokodemoYukariAndroidClient
 * Class: LivedataExtensions
 * Created by Pyxis on 12/15/18.
 *
 * Description:
 */

/**
 * get List or empty List with given [MutableLiveData<MutableList>]
 */
fun <T> MutableLiveData<MutableList<T>>.getList() = this.value.orEmpty().toMutableList()
