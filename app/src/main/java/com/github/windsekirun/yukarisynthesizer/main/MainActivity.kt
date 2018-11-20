package com.github.windsekirun.yukarisynthesizer.main

import android.os.Bundle

import com.github.windsekirun.baseapp.base.BaseActivity
import com.github.windsekirun.daggerautoinject.InjectActivity
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.databinding.MainActivityBinding

/**
 * DokodemoYukariAndroidClient
 * Class: ${NAME}
 * Created by Pyxis on 2018-11-20.
 *
 *
 * Description:
 */

@InjectActivity
class MainActivity : BaseActivity<MainActivityBinding>() {
    private var mViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        mViewModel = getViewModel(MainViewModel::class.java)
        mBinding.viewModel = mViewModel
    }
}