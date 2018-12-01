package com.github.windsekirun.yukarisynthesizer.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.windsekirun.baseapp.module.recycler.BaseRecyclerAdapter
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem
import com.github.windsekirun.yukarisynthesizer.databinding.StoryItemBinding
import com.github.windsekirun.yukarisynthesizer.main.story.event.ClickStoryItem

/**
 * DokodemoYukariAndroidClient
 * Class: StoryItemAdapter
 * Created by Pyxis on 2018-11-27.
 *
 *
 * Description:
 */
class StoryItemAdapter : BaseRecyclerAdapter<StoryItem, StoryItemBinding>() {

    override fun bind(binding: StoryItemBinding, item: StoryItem, position: Int) {
        binding.item = item
    }

    override fun onClickedItem(binding: StoryItemBinding, item: StoryItem, position: Int) {
        postEvent(ClickStoryItem(item, position))
    }

    override fun onLongClickedItem(binding: StoryItemBinding, item: StoryItem, position: Int): Boolean {
        return false
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): StoryItemBinding {
        return DataBindingUtil.inflate(inflater, R.layout.story_item, parent, false)
    }
}