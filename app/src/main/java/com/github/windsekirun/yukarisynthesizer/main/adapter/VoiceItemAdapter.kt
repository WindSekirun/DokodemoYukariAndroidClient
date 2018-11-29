package com.github.windsekirun.yukarisynthesizer.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.windsekirun.baseapp.module.recycler.BaseRecyclerAdapter
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import com.github.windsekirun.yukarisynthesizer.databinding.MainDetailsItemBinding
import com.github.windsekirun.yukarisynthesizer.main.details.event.ClickVoiceItem

/**
 * DokodemoYukariAndroidClient
 * Class: StoryItemAdapter
 * Created by Pyxis on 2018-11-27.
 *
 *
 * Description:
 */
class VoiceItemAdapter : BaseRecyclerAdapter<VoiceItem, MainDetailsItemBinding>() {

    override fun bind(binding: MainDetailsItemBinding, item: VoiceItem, position: Int) {
        binding.item = item
    }

    override fun onClickedItem(binding: MainDetailsItemBinding, item: VoiceItem, position: Int) {
        postEvent(ClickVoiceItem(item, position))
    }

    override fun onLongClickedItem(binding: MainDetailsItemBinding, item: VoiceItem, position: Int): Boolean {
        return false
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): MainDetailsItemBinding {
        return DataBindingUtil.inflate(inflater, R.layout.main_details_item, parent, false)
    }
}