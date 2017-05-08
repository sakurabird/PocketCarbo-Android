package com.sakurafish.pockettoushituryou.view.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.databinding.FragmentFoodlistBinding;
import com.sakurafish.pockettoushituryou.databinding.ItemFoodlistBinding;
import com.sakurafish.pockettoushituryou.model.FoodsData;
import com.sakurafish.pockettoushituryou.model.KindsData;
import com.sakurafish.pockettoushituryou.view.adapter.ArrayRecyclerAdapter;
import com.sakurafish.pockettoushituryou.view.adapter.BindingHolder;
import com.sakurafish.pockettoushituryou.view.adapter.KindSpinnerAdapter;
import com.sakurafish.pockettoushituryou.viewmodel.FoodListViewModel;
import com.sakurafish.pockettoushituryou.viewmodel.FoodViewModel;

import javax.inject.Inject;

import static com.sakurafish.pockettoushituryou.model.KindsData.KINDS_ALL;

public class FoodListFragment extends BaseFragment {

    public static final String TAG = FoodListFragment.class.getSimpleName();

    private FragmentFoodlistBinding binding;
    private FoodListAdapter adapter;
    private FoodsData foodsData;
    private KindsData kindsData;

    @Inject
    FoodListViewModel viewModel;

    public static FoodListFragment newInstance(@IntRange(from = 1, to = 6) int type,
                                               @NonNull KindsData kindsData,
                                               @NonNull FoodsData foodsData) {
        FoodListFragment fragment = new FoodListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putParcelable("kindsData", kindsData);
        bundle.putParcelable("foodsData", foodsData);
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
        adapter = new FoodListAdapter(getContext());
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        kindsData = getArguments().getParcelable("kindsData");
        foodsData = getArguments().getParcelable("foodsData");

        viewModel.setKindsData(kindsData);
        viewModel.setFoodsData(foodsData);
        viewModel.setType(getArguments().getInt("type"));
        viewModel.createFoodViewModels();

        adapter.reset(viewModel.getFoodViewModels());

        // Creating adapter for spinner
        KindSpinnerAdapter dataAdapter = new KindSpinnerAdapter(getActivity());
        dataAdapter.setData(viewModel.getKindsList());
        binding.spinner.setAdapter(dataAdapter);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    viewModel.setKind(KINDS_ALL);
                } else {
                    viewModel.setKind(viewModel.getKindsList().get(position - 1).id);
                }
                adapter.reset(viewModel.getFoodViewModels());
                LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
                layoutManager.scrollToPositionWithOffset(0, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private static class FoodListAdapter
            extends ArrayRecyclerAdapter<FoodViewModel, BindingHolder<ItemFoodlistBinding>> {

        FoodListAdapter(@NonNull Context context) {
            super(context);
        }

        int rotationAngle = 0;

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
}
