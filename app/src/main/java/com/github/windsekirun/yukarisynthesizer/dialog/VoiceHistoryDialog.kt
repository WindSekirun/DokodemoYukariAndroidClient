package com.github.windsekirun.yukarisynthesizer.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableArrayList
import com.github.windsekirun.baseapp.base.BaseDialog
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.core.YukariOperator
import com.github.windsekirun.yukarisynthesizer.core.annotation.OrderType
import com.github.windsekirun.yukarisynthesizer.core.composer.EnsureMainThreadComposer
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem
import com.github.windsekirun.yukarisynthesizer.core.item.VoiceItem_
import com.github.windsekirun.yukarisynthesizer.databinding.VoiceHistoryDialogBinding
import com.github.windsekirun.yukarisynthesizer.main.adapter.VoiceItemAdapter
import com.github.windsekirun.yukarisynthesizer.utils.subscribe
import io.reactivex.disposables.Disposable
import javax.inject.Inject


/**
 * DokodemoYukariAndroidClient
 * Class: PlayDialog
 * Created by Pyxis on 12/1/18.
 *
 *
 * Description:
 */

class VoiceHistoryDialog(context: Context) : BaseDialog<VoiceHistoryDialogBinding>(context) {
    val searchTitle = ObservableString()
    val itemData = ObservableArrayList<VoiceItem>()

    @Inject
    lateinit var yukariOperator: YukariOperator

    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.voice_history_dialog)
        mBinding.dialog = this

        setOnDismissListener {
            if (::disposable.isInitialized && !disposable.isDisposed) disposable.dispose()
        }
    }

    fun show(callback: (VoiceItem) -> Unit) {
        super.show()

        MainApplication.appComponent!!.inject(this)
        val voiceItemAdapter = initRecyclerView(mBinding.recyclerView, VoiceItemAdapter::class.java)
        voiceItemAdapter.voiceItemClickListener = object : VoiceItemAdapter.OnVoiceItemClickListener {
            override fun onClick(voiceItem: VoiceItem) {
                callback.invoke(voiceItem)
                dismiss()
            }
        }

        searchData()
    }

    fun clickClose(view: View) {
        dismiss()
    }

    fun clickSearch(view: View) {
        searchData()
    }

    private fun searchData() {
        if (::disposable.isInitialized && !disposable.isDisposed) disposable.dispose()

        disposable = yukariOperator.getVoiceList(
            searchTitle = searchTitle.get(),
            orderBy = OrderType.OrderFlags.DESCENDING to VoiceItem_.regDate
        )
            .compose(EnsureMainThreadComposer())
            .subscribe { data, error ->
                if (error != null) return@subscribe
                itemData.clear()
                itemData.addAll(data!!)
            }
    }

}