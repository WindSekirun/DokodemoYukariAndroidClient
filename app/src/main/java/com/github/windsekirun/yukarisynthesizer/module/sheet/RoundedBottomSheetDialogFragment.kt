package com.github.windsekirun.yukarisynthesizer.module.sheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.github.windsekirun.yukarisynthesizer.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class RoundedBottomSheetDialogFragment<T : ViewDataBinding> : BottomSheetDialogFragment() {
    lateinit var binding: T

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
}