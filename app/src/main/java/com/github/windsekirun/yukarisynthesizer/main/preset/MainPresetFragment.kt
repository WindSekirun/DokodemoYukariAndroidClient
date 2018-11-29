package com.github.windsekirun.yukarisynthesizer.main.preset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.github.windsekirun.baseapp.base.BaseFragment
import com.github.windsekirun.daggerautoinject.InjectFragment
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.databinding.MainPresetFragmentBinding
import com.github.windsekirun.yukarisynthesizer.main.adapter.PresetItemAdapter
import javax.inject.Inject

/**
 * DokodemoYukariAndroidClient
 * Class: ${NAME}
 * Created by Pyxis on 2018-11-26.
 *
 *
 * Description:
 */

@InjectFragment
class MainPresetFragment() : BaseFragment<MainPresetFragmentBinding>() {
    @Inject lateinit var mViewModelFactory: ViewModelProvider.Factory
    private var mViewModel: MainPresetViewModel? = null
    private lateinit var presetItemAdapter: PresetItemAdapter

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): MainPresetFragmentBinding {
        return DataBindingUtil.inflate(inflater, R.layout.main_story_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = getViewModel(MainPresetViewModel::class.java, mViewModelFactory)
        mBinding.viewModel = mViewModel

        presetItemAdapter = initRecyclerView(mBinding.recyclerView, PresetItemAdapter::class.java)
    }
}