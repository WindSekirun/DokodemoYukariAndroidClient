package com.github.windsekirun.yukarisynthesizer.voice

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.github.windsekirun.baseapp.base.BaseActivity
import com.github.windsekirun.daggerautoinject.InjectActivity
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.PhonomeItem
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.databinding.PhonomeItemBinding
import com.github.windsekirun.yukarisynthesizer.databinding.VoiceDetailActivityBinding
import com.github.windsekirun.yukarisynthesizer.dialog.PhonomeHistoryFragment
import com.github.windsekirun.yukarisynthesizer.dialog.VoicePresetFragment
import com.github.windsekirun.yukarisynthesizer.main.event.InvokeBackEvent
import com.github.windsekirun.yukarisynthesizer.main.event.ShowPhonomeHistoryEvent
import com.github.windsekirun.yukarisynthesizer.main.event.ShowPresetDialogEvent
import com.github.windsekirun.yukarisynthesizer.voice.event.RefreshLayoutEvent
import org.greenrobot.eventbus.Subscribe

/**
 * DokodemoYukariAndroidClient
 * Class: VoiceDetailActivity
 * Created by Pyxis on 2018-11-26.
 *
 *
 * Description:
 */
@InjectActivity
class VoiceDetailActivity : BaseActivity<VoiceDetailActivityBinding>() {
    private lateinit var viewModel: VoiceDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.voice_detail_activity)
        viewModel = getViewModel(VoiceDetailViewModel::class.java)
        mBinding.viewModel = viewModel
        mBinding.setLifecycleOwner(this)

        mBinding.toolBar.inflateMenu(R.menu.menu_voice_detail)

        init()
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    @Subscribe
    fun onInvokeBackEvent(event: InvokeBackEvent) {
        onBackPressed()
    }

    @Subscribe
    fun onRefreshLayoutEvent(event: RefreshLayoutEvent) {
        addPredicateKeywords()
    }

    @Subscribe
    fun onShowPresetDialogEvent(event: ShowPresetDialogEvent) {
        showVoicePresetDialog(event.param, event.callback)
    }

    @Subscribe
    fun onShowPhonomeHistoryEvent(event: ShowPhonomeHistoryEvent) {
        showPhonomeHistoryDialog(event.callback)
    }

    private fun showPhonomeHistoryDialog(callback: (PhonomeItem) -> Unit) {
        val fragment = PhonomeHistoryFragment().apply {
            this.callback = callback
        }

        supportFragmentManager.beginTransaction()
            .add(fragment, "phonome-history").commit()
    }

    private fun showVoicePresetDialog(param: VoiceEngine, callback: (PresetItem) -> Unit) {
        val fragment = VoicePresetFragment().apply {
            this.selectedEngine = param
            this.callback = callback
        }

        supportFragmentManager.beginTransaction()
            .add(fragment, "voice-preset").commit()
    }

    private fun init() {
        mBinding.predicateLayout.usingCustomView { s ->
            val item = s as PhonomeItem
            val binding = PhonomeItemBinding.inflate(LayoutInflater.from(this@VoiceDetailActivity))
            binding.item = item
            binding.selected = viewModel.selectedPhonomeItem == item
            binding.imgDelete.setOnClickListener { viewModel.clickRemove(item) }
            binding.root.setOnClickListener { viewModel.clickItem(item) }

            binding.root
        }
    }

    private fun addPredicateKeywords() {
        val list = viewModel.itemData

        mBinding.predicateLayout.clear()
        mBinding.predicateLayout.setItems(list)
        mBinding.predicateLayout.notifyDataSetChanged()
    }

}