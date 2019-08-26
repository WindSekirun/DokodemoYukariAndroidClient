package com.github.windsekirun.yukarisynthesizer.module.sheet

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.github.windsekirun.baseapp.data.PhotoPickType
import com.github.windsekirun.baseapp.impl.BaseInterface
import com.github.windsekirun.yukarisynthesizer.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.disposables.CompositeDisposable

/**
 * Base class for generate BottomSheetDialogFragment
 */
abstract class RoundedBottomSheetDialogFragment<T : ViewDataBinding> : BottomSheetDialogFragment(), BaseInterface {
    lateinit var binding: T
    protected val compositeDisposable = CompositeDisposable()

    abstract fun createView(inflater: LayoutInflater, container: ViewGroup?): T

    override fun getTheme(): Int = R.style.BottomSheetDialogThemeLight

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = createView(inflater, container)
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        compositeDisposable.clear()
    }

    override fun onTakePhotoResult(imagePath: String?, type: PhotoPickType?) {

    }
}