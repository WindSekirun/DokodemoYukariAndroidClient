package com.github.windsekirun.yukarisynthesizer.main

import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication

import javax.inject.Inject

/**
 * DokodemoYukariAndroidClient
 * Class: MainViewModel
 * Created by Pyxis on 2018-11-20.
 *
 *
 * Description:
 */

@InjectViewModel
class MainViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {

}