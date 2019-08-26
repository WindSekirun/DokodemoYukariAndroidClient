package com.github.windsekirun.yukarisynthesizer.main.preset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.github.windsekirun.baseapp.base.BaseFragment
import com.github.windsekirun.daggerautoinject.InjectFragment
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.databinding.MainPresetFragmentBinding
import com.github.windsekirun.yukarisynthesizer.dialog.PresetDialogFragment
import com.github.windsekirun.yukarisynthesizer.main.adapter.PresetItemAdapter
import com.github.windsekirun.yukarisynthesizer.main.event.ClickPresetItem
import com.github.windsekirun.yukarisynthesizer.main.event.ShowPresetDialogEvent
import com.github.windsekirun.yukarisynthesizer.main.event.SpeedDialClickEvent
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
class MainPresetFragment : BaseFragment<MainPresetFragmentBinding>() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainPresetViewModel
    private lateinit var presetItemAdapter: PresetItemAdapter

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): MainPresetFragmentBinding {
        return DataBindingUtil.inflate(inflater, R.layout.main_preset_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getViewModel(MainPresetViewModel::class.java, viewModelFactory)
        mBinding.viewModel = viewModel

        presetItemAdapter = initRecyclerView(mBinding.recyclerView, PresetItemAdapter::class.java)
    }

    @Subscribe
    fun onSpeedDialClickEvent(event: SpeedDialClickEvent) {
        if (SpeedDialClickEvent.checkAvailable(this.javaClass, event.mode)) {
            viewModel.clickPreset()
        }
    }

    @Subscribe
    fun onClickPresetItem(event: ClickPresetItem) {
        viewModel.clickPresetItem(event.item)
    }

    @Subscribe
    fun onShowPresetDialogEvent(event: ShowPresetDialogEvent) {
        showPresetDialog(event.param, event.callback)
    }

    private fun showPresetDialog(param: PresetItem, callback: (PresetItem) -> Unit) {
        val fragment = PresetDialogFragment().apply {
            this.presetItem = param
            this.callback = callback
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .add(fragment, "preset").commit()
    }
}