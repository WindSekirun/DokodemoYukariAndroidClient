package com.github.windsekirun.yukarisynthesizer.setting

import android.os.Bundle

import com.github.windsekirun.baseapp.base.BaseActivity
import com.github.windsekirun.daggerautoinject.InjectActivity
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.databinding.SettingActivityBinding

/**
 * DokodemoYukariAndroidClient
 * Class: ${NAME}
 * Created by Pyxis on 2018-12-19.
 *
 *
 * Description:
 */

@InjectActivity
class SettingActivity : BaseActivity<SettingActivityBinding>() {
    private var mViewModel: SettingViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_activity)
        mViewModel = getViewModel(SettingViewModel::class.java)
        mBinding.setViewModel(mViewModel)
    }
}