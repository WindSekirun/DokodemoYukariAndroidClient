package com.github.windsekirun.yukarisynthesizer.swipe

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemDragListener
import com.github.windsekirun.baseapp.base.BaseActivity
import com.github.windsekirun.baseapp.module.reference.ActivityReference
import com.github.windsekirun.daggerautoinject.InjectActivity
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import com.github.windsekirun.yukarisynthesizer.databinding.SwipeOrderActivityBinding
import com.github.windsekirun.yukarisynthesizer.swipe.event.ReadyDisplayViewEvent
import com.github.windsekirun.yukarisynthesizer.utils.CharacterBindUtils
import org.greenrobot.eventbus.Subscribe


@InjectActivity
class SwipeOrderActivity : BaseActivity<SwipeOrderActivityBinding>() {
    private lateinit var viewModel: SwipeOrderViewModel
    private lateinit var swipeOrderAdapter: SwipeOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.swipe_order_activity)
        viewModel = getViewModel(SwipeOrderViewModel::class.java)
        mBinding.viewModel = viewModel
        mBinding.setLifecycleOwner(this)

        mBinding.toolBar.setNavigationOnClickListener { viewModel.onBackPressed() }

        viewModel.loadData()
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    @Subscribe
    fun onReadyDisplayViewEvent(event: ReadyDisplayViewEvent) {
        initSwipeOrder()
    }

    private fun initSwipeOrder() {
        swipeOrderAdapter = SwipeOrderAdapter(viewModel.itemData)
        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.adapter = swipeOrderAdapter
        mBinding.recyclerView.orientation = Orientation.VERTICAL_LIST_WITH_VERTICAL_DRAGGING

        // disable any swipe function (we don't need it)
        mBinding.recyclerView.orientation?.removeSwipeDirectionFlag(Direction.RIGHT)
        mBinding.recyclerView.orientation?.removeSwipeDirectionFlag(Direction.LEFT)

        mBinding.recyclerView.dragListener = object : OnItemDragListener<VoiceItem> {
            override fun onItemDragged(previousPosition: Int, newPosition: Int, item: VoiceItem) {
                viewModel.changed = true
            }

            override fun onItemDropped(initialPosition: Int, finalPosition: Int, item: VoiceItem) {
                viewModel.changed = true
                if (initialPosition != finalPosition) {
                    viewModel.changeOrder(finalPosition, item)
                }
            }
        }
    }

    class SwipeOrderAdapter(dataSet: List<VoiceItem> = emptyList()) :
        DragDropSwipeAdapter<VoiceItem, SwipeOrderAdapter.ViewHolder>(dataSet) {

        class ViewHolder(itemView: View) : DragDropSwipeAdapter.ViewHolder(itemView) {
            val imgCharacter: ImageView = itemView.findViewById(R.id.imgCharacter)
            val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
            val txtDesc: TextView = itemView.findViewById(R.id.txtDesc)
            val imgDrag: ImageView = itemView.findViewById(R.id.imgDrag)
        }

        override fun getViewHolder(itemView: View) = SwipeOrderAdapter.ViewHolder(itemView)

        override fun getViewToTouchToStartDraggingItem(item: VoiceItem, viewHolder: ViewHolder, position: Int): View? {
            return viewHolder.imgDrag
        }

        override fun onBindViewHolder(item: VoiceItem, viewHolder: ViewHolder, position: Int) {
            val context = ActivityReference.getContext()
            val characterImg = CharacterBindUtils.getCharacterImg(item.engine)
            val title = if (item.engine == VoiceEngine.Break) {
                context.getString(R.string.break_time)
            } else {
                context.getString(R.string.preset_details_format).format(item.engine.id, item.preset.title)
            }

            val desc = if (item.engine == VoiceEngine.Break) {
                convertBreakTimeFormat(item.breakTime)
            } else {
                item.contentOrigin
            }

            viewHolder.imgCharacter.setImageDrawable(characterImg)
            viewHolder.txtTitle.text = title
            viewHolder.txtDesc.text = desc
        }

        private fun convertBreakTimeFormat(breakTime: Long): String = "${breakTime.toDouble() / 1000.0}s"
    }
}

typealias Direction = DragDropSwipeRecyclerView.ListOrientation.DirectionFlag
typealias Orientation = DragDropSwipeRecyclerView.ListOrientation