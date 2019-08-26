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
import com.github.windsekirun.yukarisynthesizer.main.event.ClickStoryItem
import com.github.windsekirun.yukarisynthesizer.main.event.RefreshFragmentEvent
import com.github.windsekirun.yukarisynthesizer.main.event.SpeedDialClickEvent
import com.github.windsekirun.yukarisynthesizer.main.event.ToggleFavoriteItem
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
class MainStoryFragment : BaseFragment<MainStoryFragmentBinding>() {
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
        viewModel.clickStoryItem(event.item)
    }

    @Subscribe
    fun onSpeedDialClickEvent(event: SpeedDialClickEvent) {
        if (SpeedDialClickEvent.checkAvailable(this.javaClass, event.mode)) {
            viewModel.clickStory()
        }
    }

    @Subscribe
    fun onToggleFavoriteItem(event: ToggleFavoriteItem) {
        viewModel.clickFavorite(event.item)
    }

    @Subscribe
    fun onRefreshFragmentEvent(event: RefreshFragmentEvent) {
        viewModel.refreshData()
    }
}