package com.github.windsekirun.yukarisynthesizer.main.story

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.github.windsekirun.baseapp.base.BaseFragment
import com.github.windsekirun.daggerautoinject.InjectFragment
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.databinding.MainStoryFragmentBinding
import com.github.windsekirun.yukarisynthesizer.main.adapter.StoryItemAdapter
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
class MainStoryFragment() : BaseFragment<MainStoryFragmentBinding>() {
    @Inject lateinit var mViewModelFactory: ViewModelProvider.Factory
    private var mViewModel: MainStoryViewModel? = null
    private lateinit var mStoryItemAdapter: StoryItemAdapter

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): MainStoryFragmentBinding {
        return DataBindingUtil.inflate(inflater, R.layout.main_story_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = getViewModel(MainStoryViewModel::class.java, mViewModelFactory)
        mBinding.viewModel = mViewModel

        mStoryItemAdapter = initRecyclerView(mBinding.recyclerView, StoryItemAdapter::class.java)
    }
}