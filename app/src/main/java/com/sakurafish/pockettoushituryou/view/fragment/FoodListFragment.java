package com.sakurafish.pockettoushituryou.view.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.databinding.FragmentFoodlistBinding;
import com.sakurafish.pockettoushituryou.databinding.ItemFoodlistBinding;
import com.sakurafish.pockettoushituryou.view.adapter.ArrayRecyclerAdapter;
import com.sakurafish.pockettoushituryou.view.adapter.BindingHolder;
import com.sakurafish.pockettoushituryou.viewmodel.FoodListViewModel;
import com.sakurafish.pockettoushituryou.viewmodel.FoodViewModel;

import javax.inject.Inject;

public class FoodListFragment extends BaseFragment {

    public static final String TAG = FoodListFragment.class.getSimpleName();

    private FragmentFoodlistBinding binding;
    private Adapter adapter;

    @Inject
    FoodListViewModel viewModel;

    public static FoodListFragment newInstance(@IntRange(from = 1, to = 6) int type) {
        FoodListFragment fragment = new FoodListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }


    public FoodListFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    @Override
    public void onDetach() {
        viewModel.destroy();
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_foodlist, container, false);
        binding.setViewModel(viewModel);

        initView();
        return binding.getRoot();
    }

    private void initView() {
        adapter = new Adapter(getContext(), viewModel.getFoodViewModels());
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.setType(getArguments().getInt("type"));
        viewModel.renderFoods();
    }

    private static class Adapter
            extends ArrayRecyclerAdapter<FoodViewModel, BindingHolder<ItemFoodlistBinding>> {

        public Adapter(@NonNull Context context, @NonNull ObservableList<FoodViewModel> list) {
            super(context, list);

            list.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<FoodViewModel>>() {
                @Override
                public void onChanged(ObservableList<FoodViewModel> contributorViewModels) {
                    notifyDataSetChanged();
                }

                @Override
                public void onItemRangeChanged(ObservableList<FoodViewModel> contributorViewModels, int i, int i1) {
                    notifyItemRangeChanged(i, i1);
                }

                @Override
                public void onItemRangeInserted(ObservableList<FoodViewModel> contributorViewModels, int i, int i1) {
                    notifyItemRangeInserted(i, i1);
                }

                @Override
                public void onItemRangeMoved(ObservableList<FoodViewModel> contributorViewModels, int i, int i1,
                                             int i2) {
                    notifyItemMoved(i, i1);
                }

                @Override
                public void onItemRangeRemoved(ObservableList<FoodViewModel> contributorViewModels, int i, int i1) {
                    notifyItemRangeRemoved(i, i1);
                }
            });
        }

        @Override
        public BindingHolder<ItemFoodlistBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BindingHolder<>(getContext(), parent, R.layout.item_foodlist);
        }

        @Override
        public void onBindViewHolder(BindingHolder<ItemFoodlistBinding> holder, int position) {
            FoodViewModel viewModel = getItem(position);
            ItemFoodlistBinding itemBinding = holder.binding;
            itemBinding.setViewModel(viewModel);
            itemBinding.executePendingBindings();
        }
    }
}
