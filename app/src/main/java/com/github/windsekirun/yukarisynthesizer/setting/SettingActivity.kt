package com.github.windsekirun.yukarisynthesizer.setting

import android.os.Bundle

import com.github.windsekirun.baseapp.base.BaseActivity
import com.github.windsekirun.daggerautoinject.InjectActivity
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.databinding.SettingActivityBinding

@InjectActivity
class SettingActivity : BaseActivity<SettingActivityBinding>() {
    lateinit var mViewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_activity)
        mViewModel = getViewModel(SettingViewModel::class.java)
        mBinding.viewModel = mViewModel
    }
}