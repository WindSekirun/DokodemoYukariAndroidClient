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
import com.github.windsekirun.yukarisynthesizer.main.details.MainDetailsFragment
import com.github.windsekirun.yukarisynthesizer.main.story.event.ClickStoryItem
import com.github.windsekirun.yukarisynthesizer.main.story.event.RefreshBarEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
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
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainStoryViewModel
    private lateinit var storyItemAdapter: StoryItemAdapter

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): MainStoryFragmentBinding {
        return DataBindingUtil.inflate(inflater, R.layout.main_story_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getViewModel(MainStoryViewModel::class.java, viewModelFactory)
        mBinding.setLifecycleOwner(this)
        mBinding.viewModel = viewModel

        storyItemAdapter = initRecyclerView(mBinding.recyclerView, StoryItemAdapter::class.java)
    }

    @Subscribe
    fun onClickStoryItem(event: ClickStoryItem) {
        val fragment = MainDetailsFragment().apply {
            this.storyItem = event.item
        }

        EventBus.getDefault().post(RefreshBarEvent(true))

        activity!!.supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }
}