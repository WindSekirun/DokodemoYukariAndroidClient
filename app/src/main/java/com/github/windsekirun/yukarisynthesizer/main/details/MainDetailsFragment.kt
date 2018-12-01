package com.github.windsekirun.yukarisynthesizer.main.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.github.windsekirun.baseapp.base.BaseFragment
import com.github.windsekirun.daggerautoinject.InjectFragment
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem
import com.github.windsekirun.yukarisynthesizer.main.adapter.VoiceItemAdapter
import com.github.windsekirun.yukarisynthesizer.main.details.event.CloseFragmentEvent
import com.github.windsekirun.yukarisynthesizer.main.impl.OnBackPressedListener
import com.github.windsekirun.yukarisynthesizer.main.story.event.RefreshBarEvent
import com.github.windsekirun.yukarisynthesizer.utils.reveal.CircularRevealUtils
import com.github.windsekirun.yukarisynthesizer.utils.reveal.RevealSettingHolder
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
class MainDetailsFragment : BaseFragment<MainDetailsFragmentBinding>(), OnBackPressedListener {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var storyItem: StoryItem? = null
    var revealSetting: CircularRevealUtils.RevealSetting? = null

    private lateinit var viewModel: MainDetailsViewModel
    private lateinit var voiceItemAdapter: VoiceItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        if (revealSetting != null) {
            CircularRevealUtils.revealEnter(view!!, revealSetting!!) {

            }
        }

        return view
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): MainDetailsFragmentBinding {
        return DataBindingUtil.inflate(inflater, R.layout.main_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getViewModel(MainDetailsViewModel::class.java, viewModelFactory)

        mBinding.setLifecycleOwner(this)
        mBinding.viewModel = viewModel

        voiceItemAdapter = initRecyclerView(mBinding.recyclerView, VoiceItemAdapter::class.java)

        viewModel.loadData(storyItem)
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    @Subscribe
    fun onCloseFragmentEvent(event: CloseFragmentEvent) {
        if (revealSetting != null) {
            val revealSetting = RevealSettingHolder.revealSetting
            CircularRevealUtils.revealExit(mBinding.root, revealSetting!!) {
                exitDetails()
            }
        } else {
            exitDetails()
        }
    }

    private fun exitDetails() {
        EventBus.getDefault().post(RefreshBarEvent(false))
        activity!!.supportFragmentManager.popBackStackImmediate()
    }
}