package com.github.windsekirun.yukarisynthesizer.binding

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

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
}