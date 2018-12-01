package com.github.windsekirun.yukarisynthesizer.binding;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.github.windsekirun.baseapp.module.recycler.BaseRecyclerAdapter;

import java.util.List;

public class JavaBindsAdapter {

    @BindingAdapter("items")
    public static <T> void bindItems(RecyclerView recyclerView, List<T> list) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) return;
        if (adapter instanceof BaseRecyclerAdapter && list != null) {
            ((BaseRecyclerAdapter) adapter).setItems(list);
        }
    }

}
