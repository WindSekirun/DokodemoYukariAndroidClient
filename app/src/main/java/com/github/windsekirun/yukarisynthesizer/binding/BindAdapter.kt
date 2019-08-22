package com.github.windsekirun.yukarisynthesizer.binding

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.leinardi.android.speeddial.SpeedDialView

object BindAdapter {

    @JvmStatic
    @BindingAdapter("actionSelected")
    fun bindActionSelected(view: SpeedDialView, listener: SpeedDialView.OnActionSelectedListener) {
        view.setOnActionSelectedListener(listener)
    }

    @JvmStatic
    @BindingAdapter("actionSelected")
    fun bindActionSelectedToolbar(view: Toolbar, listener: Toolbar.OnMenuItemClickListener) {
        view.setOnMenuItemClickListener(listener)
    }

    @JvmStatic
    @BindingAdapter("title")
    fun setTitle(view: Toolbar, text: String) {
        view.title = text
    }

    @JvmStatic
    @BindingAdapter("navigationItemClicked")
    fun bindNavigationItemClick(view: Toolbar, listener: View.OnClickListener) {
        view.setNavigationOnClickListener(listener)
    }

    @JvmStatic
    @BindingAdapter("navigationItemClicked")
    fun bindNavigationItemClick(
        view: BottomNavigationView,
        listener: BottomNavigationView.OnNavigationItemSelectedListener
    ) {
        view.setOnNavigationItemSelectedListener(listener)
    }

    @BindingAdapter("onEditorAction")
    @JvmStatic
    fun bindEditorAction(
        editText: EditText,
        onEditActionListener: TextView.OnEditorActionListener
    ) {
        editText.setOnEditorActionListener(onEditActionListener)
    }
}