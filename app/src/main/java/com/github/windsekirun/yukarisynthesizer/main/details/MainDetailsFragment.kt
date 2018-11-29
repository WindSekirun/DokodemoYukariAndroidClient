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
import com.github.windsekirun.yukarisynthesizer.main.adapter.VoiceItemAdapter
import com.github.windsekirun.yukarisynthesizer.main.details.event.CloseFragmentEvent
import com.github.windsekirun.yukarisynthesizer.main.impl.OnBackButtonClickListener
import com.github.windsekirun.yukarisynthesizer.main.story.event.RefreshBarEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
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
class MainDetailsFragment : BaseFragment<MainDetailsFragmentBinding>(), OnBackButtonClickListener {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MainDetailsViewModel
    private lateinit var voiceItemAdapter: VoiceItemAdapter
    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): MainDetailsFragmentBinding {
        return DataBindingUtil.inflate(inflater, R.layout.main_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getViewModel(MainDetailsViewModel::class.java, viewModelFactory)
        mBinding.viewModel = viewModel

        voiceItemAdapter = initRecyclerView(mBinding.recyclerView, VoiceItemAdapter::class.java)
    }

    override fun onClickBack() {
        viewModel.onBackPressed()
    }

    @Subscribe
    fun onCloseFragmentEvent(event: CloseFragmentEvent) {
        EventBus.getDefault().post(RefreshBarEvent(false))
        activity!!.supportFragmentManager.popBackStackImmediate()
    }
}