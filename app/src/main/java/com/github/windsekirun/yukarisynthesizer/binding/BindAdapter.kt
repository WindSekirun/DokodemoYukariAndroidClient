package com.github.windsekirun.yukarisynthesizer.binding

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.leinardi.android.speeddial.SpeedDialView

/**
 * set of [BindingAdapter] in DataBinding
 */
object BindAdapter {

    @JvmStatic
    @BindingAdapter(value = ["imageUrl", "placeholder"], requireAll = false)
    fun bindImage(imageView: ImageView, url: String, placeholder: Drawable) {
        Glide.with(imageView.context).load(url).apply(RequestOptions.placeholderOf(placeholder))
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter(value = ["circleImageUrl"], requireAll = false)
    fun bindCircleImage(imageView: ImageView, url: String) {
        Glide.with(imageView.context).load(url).apply(RequestOptions.circleCropTransform())
            .into(imageView)
    }

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