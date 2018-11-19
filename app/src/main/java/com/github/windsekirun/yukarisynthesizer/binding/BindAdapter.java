package com.github.windsekirun.yukarisynthesizer.binding;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.ImageView;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.github.windsekirun.yukarisynthesizer.net.Api;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.windsekirun.baseapp.module.recycler.BaseRecyclerAdapter;

import java.util.List;

public class BindAdapter {

    @BindingAdapter(value = {"imageUrl", "placeholder", "resolution"}, requireAll = false)
    public static void bindImage(ImageView imageView, String url, Drawable placeholder, String resolution) {
        if (TextUtils.isEmpty(resolution)) {
            resolution = Api.FILE_480;
        }

        if (!isFileUrl(url)) {
            url = Api.getServerImagePath(url, resolution);
        }

        Glide.with(imageView.getContext()).load(url).apply(RequestOptions.placeholderOf(placeholder)).into(imageView);
    }

    @BindingAdapter(value = {"circleImageUrl", "placeHolder", "resolution"}, requireAll = false)
    public static void bindCircleImage(ImageView imageView, String url, Drawable placeholder, String resolution) {
        if (TextUtils.isEmpty(resolution)) {
            resolution = Api.FILE_480;
        }

        if (!isFileUrl(url)) {
            url = Api.getServerImagePath(url, resolution);
        }

        Glide.with(imageView.getContext()).load(url).apply(RequestOptions.circleCropTransform().placeholder(placeholder)).into(imageView);
    }

    @BindingAdapter("items")
    public static <T> void bindItems(RecyclerView recyclerView, List<T> list) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) return;
        if (adapter instanceof BaseRecyclerAdapter) {
            ((BaseRecyclerAdapter) adapter).setItems(list);
        }
    }

    private static boolean isFileUrl(String url) {
        return !(url != null && !url.equals("")) || url.contains(Environment.getExternalStorageDirectory().getAbsolutePath());
    }
}