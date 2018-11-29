package com.github.windsekirun.yukarisynthesizer.main.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.github.windsekirun.baseapp.base.BaseFragment
import com.github.windsekirun.daggerautoinject.InjectFragment
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.databinding.MainDetailsFragmentBinding
import javax.inject.Inject


/**
 * DokodemoYukariAndroidClient
 * Class: ${NAME}
 * Created by Pyxis on 2018-11-29.
 *
 *
 * Description:
 */

@InjectFragment
class MainDetailsFragment : BaseFragment<MainDetailsFragmentBinding>() {
    @Inject lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var mViewModel: MainDetailsViewModel

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): MainDetailsFragmentBinding {
        return DataBindingUtil.inflate(inflater, R.layout.main_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = getViewModel(MainDetailsViewModel::class.java, mViewModelFactory)
        mBinding.viewModel = mViewModel
    }
}