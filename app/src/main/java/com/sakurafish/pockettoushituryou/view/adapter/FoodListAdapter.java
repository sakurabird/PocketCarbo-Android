package com.sakurafish.pockettoushituryou.view.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.databinding.ItemFoodlistBinding;
import com.sakurafish.pockettoushituryou.viewmodel.FoodViewModel;

public class FoodListAdapter extends ArrayRecyclerAdapter<FoodViewModel, BindingHolder<ItemFoodlistBinding>> {

    public FoodListAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    public BindingHolder<ItemFoodlistBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BindingHolder<>(getContext(), parent, R.layout.item_foodlist);
    }

    @Override
    public void onBindViewHolder(BindingHolder<ItemFoodlistBinding> holder, int position) {
        final FoodViewModel viewModel = getItem(position);
        if (viewModel.isExpanded()) {
            holder.binding.expandArrow.setSelected(true);
            holder.binding.expandableLayout.expand(true);
        } else {
            holder.binding.expandArrow.setSelected(false);
            holder.binding.expandableLayout.collapse(true);
        }

        // Like button
        viewModel.setFabButtonState(holder.binding.starButton);
        holder.binding.starButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                viewModel.onClickFab();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                viewModel.onClickFab();
            }
        });

        // collapse or expand card
        viewModel.setOnClickListener(v -> {

            if (viewModel.isExpanded()) {
                holder.binding.expandArrow.setSelected(false);
                holder.binding.expandableLayout.collapse(true);
                viewModel.setExpanded(false);
            } else {
                holder.binding.expandArrow.setSelected(true);
                holder.binding.expandableLayout.expand(true);
                viewModel.setExpanded(true);
            }
        });

        holder.binding.setViewModel(viewModel);
        holder.binding.executePendingBindings();
    }
}