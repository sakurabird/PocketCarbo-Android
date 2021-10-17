package com.sakurafish.pockettoushituryou.view.adapter;

import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

public class BindingHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    public final T binding;

    public BindingHolder(@NonNull Context context, @NonNull ViewGroup parent,
                         @LayoutRes int layoutResId) {
        super(LayoutInflater.from(context).inflate(layoutResId, parent, false));
        binding = DataBindingUtil.bind(itemView);
    }
}
