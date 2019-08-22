package com.github.windsekirun.yukarisynthesizer.main.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.github.windsekirun.baseapp.base.BaseFragment
import com.github.windsekirun.daggerautoinject.InjectFragment
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import com.github.windsekirun.yukarisynthesizer.databinding.MainDetailsFragmentBinding
import com.github.windsekirun.yukarisynthesizer.dialog.BreakDialogFragment
import com.github.windsekirun.yukarisynthesizer.dialog.PlayDialogFragment
import com.github.windsekirun.yukarisynthesizer.dialog.VoiceHistoryFragment
import com.github.windsekirun.yukarisynthesizer.dialog.VoiceRecognitionFragment
import com.github.windsekirun.yukarisynthesizer.main.adapter.VoiceItemAdapter
import com.github.windsekirun.yukarisynthesizer.main.event.*
import com.github.windsekirun.yukarisynthesizer.main.impl.OnBackPressedListener
import com.github.windsekirun.yukarisynthesizer.utils.reveal.CircularRevealUtils
import com.github.windsekirun.yukarisynthesizer.utils.reveal.RevealSettingHolder
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

@InjectFragment
class MainDetailsFragment : BaseFragment<MainDetailsFragmentBinding>(), OnBackPressedListener {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var storyItem: StoryItem? = null
    var revealSetting: CircularRevealUtils.RevealSetting? = null

    private lateinit var viewModel: MainDetailsViewModel
    private lateinit var voiceItemAdapter: VoiceItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState) as View

        revealSetting?.let { CircularRevealUtils.revealEnter(view, it) }
        postEvent(SwapDetailEvent(false))

        return view
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): MainDetailsFragmentBinding {
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
    fun onClickVoiceItem(event: ClickVoiceItem) {
        viewModel.clickVoiceItem(event.item, event.position)
    }

    @Subscribe
    fun onCloseFragmentEvent(event: CloseFragmentEvent) {
        if (revealSetting != null) {
            RevealSettingHolder.revealSetting?.let {
                CircularRevealUtils.revealExit(mBinding.root, it) { exitDetails() }
            }
        } else {
            exitDetails()
        }
    }

    @Subscribe
    fun onToolbarMenuClickEvent(event: ToolbarMenuClickEvent) {
        if (ToolbarMenuClickEvent.checkAvailable(this.javaClass, event.mode)) {
            viewModel.clickMenuItem(event.mode)
        }
    }

    @Subscribe
    fun onSpeedDialClickEvent(event: SpeedDialClickEvent) {
        if (SpeedDialClickEvent.checkAvailable(this.javaClass, event.mode)) {
            viewModel.clickSpeedDial(event.mode)
        }
    }

    @Subscribe
    fun onShowBreakDialogEvent(event: ShowBreakDialogEvent) {
        showBreakDialog(event.callback, event.param)
    }

    @Subscribe
    fun onShowHistoryDialogEvent(event: ShowHistoryDialogEvent) {
        showHistoryDialog(event.callback)
    }

    @Subscribe
    fun onShowVoiceRecognitionEvent(event: ShowVoiceRecognitionEvent) {
        showVoiceRecognitionDialog(event.callback)
    }

    @Subscribe
    fun onShowPlayDialogEvent(event: ShowPlayDialogEvent) {
        showPlayDialog(event.param)
    }

    private fun exitDetails() {
        requireActivity().supportFragmentManager.popBackStackImmediate()
        postEvent(SwapDetailEvent(true))
    }

    private fun showBreakDialog(callback: (VoiceItem) -> Unit, param: VoiceItem) {
        val fragment = BreakDialogFragment().apply {
            this.voiceItem = param
            this.callback = callback
        }

        if (fragment.checkFragmentNotOpenState()) {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(fragment, "break").commit()
        }
    }

    private fun showHistoryDialog(callback: (VoiceItem) -> Unit) {
        val fragment = VoiceHistoryFragment().apply {
            this.callback = callback
        }

        if (fragment.checkFragmentNotOpenState()) {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(fragment, "history").commit()
        }
    }

    private fun showVoiceRecognitionDialog(callback: (String) -> Unit) {
        val fragment = VoiceRecognitionFragment().apply {
            this.callback = callback
        }

        if (fragment.checkFragmentNotOpenState()) {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(fragment, "voice").commit()
        }
    }

    private fun showPlayDialog(param: List<StoryItem>) {
        val fragment = PlayDialogFragment().apply {
            this.targetList = param.toMutableList()
        }

        if (fragment.checkFragmentNotOpenState()) {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(fragment, "play").commit()
        }
    }

    private fun DialogFragment.checkFragmentNotOpenState() =
        this.dialog == null || this.dialog?.isShowing == false
}