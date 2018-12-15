package com.github.windsekirun.yukarisynthesizer.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.windsekirun.baseapp.module.recycler.BaseRecyclerAdapter
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import com.github.windsekirun.yukarisynthesizer.databinding.MainPresetItemBinding
import com.github.windsekirun.yukarisynthesizer.main.event.ClickPresetItem

/**
 * DokodemoYukariAndroidClient
 * Class: StoryItemAdapter
 * Created by Pyxis on 2018-11-27.
 *
 *
 * Description:
 */
class PresetItemAdapter : BaseRecyclerAdapter<PresetItem, MainPresetItemBinding>() {
    var presetItemClickListener: ((PresetItem) -> Unit)? = null

    override fun bind(binding: MainPresetItemBinding, item: PresetItem, position: Int) {
        binding.item = item
    }

    override fun onClickedItem(binding: MainPresetItemBinding, item: PresetItem, position: Int) {
        if (presetItemClickListener != null) {
            presetItemClickListener?.invoke(item)
        } else {
            postEvent(ClickPresetItem(item))
        }
    }

    override fun onLongClickedItem(binding: MainPresetItemBinding, item: PresetItem, position: Int): Boolean {
        return false
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): MainPresetItemBinding {
        return DataBindingUtil.inflate(inflater, R.layout.main_preset_item, parent, false)
    }
}