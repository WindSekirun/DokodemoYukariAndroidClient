package com.github.windsekirun.yukarisynthesizer.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.windsekirun.baseapp.module.recycler.BaseRecyclerAdapter
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import com.github.windsekirun.yukarisynthesizer.databinding.VoiceItemBinding
import com.github.windsekirun.yukarisynthesizer.main.event.ClickVoiceItem

/**
 * DokodemoYukariAndroidClient
 * Class: StoryItemAdapter
 * Created by Pyxis on 2018-11-27.
 *
 *
 * Description:
 */
class VoiceItemAdapter : BaseRecyclerAdapter<VoiceItem, VoiceItemBinding>() {
    var voiceItemClickListener: ((VoiceItem) -> Unit)? = null

    override fun bind(binding: VoiceItemBinding, item: VoiceItem, position: Int) {
        binding.item = item
        binding.adapter = this
    }

    override fun onClickedItem(binding: VoiceItemBinding, item: VoiceItem, position: Int) {
        if (voiceItemClickListener != null) {
            voiceItemClickListener?.invoke(item)
        } else {
            postEvent(ClickVoiceItem(item, position))
        }
    }

    override fun onLongClickedItem(binding: VoiceItemBinding, item: VoiceItem, position: Int): Boolean {
        return false
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): VoiceItemBinding {
        return DataBindingUtil.inflate(inflater, R.layout.voice_item, parent, false)
    }

    fun convertBreakTimeFormat(breakTime: Long): String = "${breakTime.toDouble() / 1000.0}s"
}