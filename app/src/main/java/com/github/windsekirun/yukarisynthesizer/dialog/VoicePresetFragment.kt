package com.github.windsekirun.yukarisynthesizer.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableArrayList
import com.github.windsekirun.baseapp.module.composer.EnsureMainThreadComposer
import com.github.windsekirun.baseapp.utils.subscribe
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.core.YukariOperator
import com.github.windsekirun.yukarisynthesizer.core.annotation.OrderType
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem
import com.github.windsekirun.yukarisynthesizer.core.item.PresetItem_
import com.github.windsekirun.yukarisynthesizer.databinding.VoicePresetFragmentBinding
import com.github.windsekirun.yukarisynthesizer.main.adapter.PresetItemAdapter
import com.github.windsekirun.yukarisynthesizer.module.sheet.RoundedBottomSheetDialogFragment
import com.github.windsekirun.yukarisynthesizer.utils.safeDispose
import io.reactivex.disposables.Disposable
import javax.inject.Inject

/**
 * DialogFragment to select preset
 */
class VoicePresetFragment : RoundedBottomSheetDialogFragment<VoicePresetFragmentBinding>() {
    val itemData = ObservableArrayList<PresetItem>()

    @Inject
    lateinit var yukariOperator: YukariOperator
    lateinit var callback: (PresetItem) -> Unit
    lateinit var selectedEngine: VoiceEngine

    private var disposable: Disposable? = null

    override fun createView(inflater: LayoutInflater, container: ViewGroup?) =
        VoicePresetFragmentBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragment = this

        MainApplication.appComponent.inject(this)
        val presetItemAdapter =
            initRecyclerView<PresetItemAdapter>(binding.recyclerView, PresetItemAdapter::class.java)
        presetItemAdapter.presetItemClickListener = {
            callback.invoke(it)
            dismiss()
        }

        searchData()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        disposable.safeDispose()
    }

    private fun searchData() {
        disposable.safeDispose()

        disposable = yukariOperator.getPresetList(
            orderBy = OrderType.OrderFlags.ASCENDING to PresetItem_.title,
            voiceEngine = selectedEngine
        )
            .compose(EnsureMainThreadComposer())
            .subscribe { data, error ->
                if (error != null || data == null) return@subscribe
                itemData.clear()
                itemData.addAll(data)
            }
    }
}