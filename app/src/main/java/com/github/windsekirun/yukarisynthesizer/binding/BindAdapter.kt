package com.github.windsekirun.yukarisynthesizer.binding

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.leinardi.android.speeddial.SpeedDialView

object BindAdapter {

    @JvmStatic
    @BindingAdapter(value = ["imageUrl", "placeholder"], requireAll = false)
    fun bindImage(imageView: ImageView, url: String, placeholder: Drawable) {
        Glide.with(imageView.context).load(url).apply(RequestOptions.placeholderOf(placeholder)).into(imageView)
    }

    @JvmStatic
    @BindingAdapter(value = ["circleImageUrl"], requireAll = false)
    fun bindCircleImage(imageView: ImageView, url: String) {
        Glide.with(imageView.context).load(url).apply(RequestOptions.circleCropTransform()).into(imageView)
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
    @BindingAdapter("navigationItemClicked")
    fun bindNavigationItemClick(view: Toolbar, listener: View.OnClickListener) {
        view.setNavigationOnClickListener(listener)
    }
}