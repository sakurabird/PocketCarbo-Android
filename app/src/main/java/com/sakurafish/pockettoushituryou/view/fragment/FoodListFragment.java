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
import com.sakurafish.pockettoushituryou.repository.FoodsRepository;
import com.sakurafish.pockettoushituryou.view.adapter.FoodListAdapter;
import com.sakurafish.pockettoushituryou.view.adapter.KindSpinnerAdapter;
import com.sakurafish.pockettoushituryou.viewmodel.FoodListViewModel;
import com.sakurafish.pockettoushituryou.viewmodel.FoodViewModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.sakurafish.pockettoushituryou.model.FoodsData.KINDS_ALL;
import static com.sakurafish.pockettoushituryou.view.activity.SearchResultActivity.EXTRA_QUERY;

public class FoodListFragment extends BaseFragment {

    public static final String TAG = FoodListFragment.class.getSimpleName();

    public enum ListType {
        NORMAL,
        SEARCH_RESULT,
        FAVORITES
    }

    private static final String EXTRA_LIST_TYPE = "listType";
    private static final String EXTRA_TYPE = "type";

    private FragmentFoodlistBinding binding;
    private KindSpinnerAdapter kindSpinnerAdapter;
    private FoodListAdapter foodListAdapter;

    private ListType listType;
    private int typeId;
    private int kindId = KINDS_ALL;
    private String query = "";

    @Inject
    FoodListViewModel viewModel;

    @Inject
    CompositeDisposable compositeDisposable;

    @Inject
    FoodsRepository foodsRepository;

    public static FoodListFragment newInstance(@NonNull ListType list_type, @IntRange(from = 1, to = 6) int type) {
        FoodListFragment fragment = new FoodListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_LIST_TYPE, list_type);
        bundle.putInt(EXTRA_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static FoodListFragment newInstance(@NonNull ListType list_type, @Nullable String query) {
        FoodListFragment fragment = new FoodListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_LIST_TYPE, list_type);
        bundle.putString(EXTRA_QUERY, query);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static FoodListFragment newInstance(@NonNull ListType list_type) {
        FoodListFragment fragment = new FoodListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_LIST_TYPE, list_type);
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.typeId = savedInstanceState.getInt(EXTRA_TYPE);
            this.listType = (ListType) savedInstanceState.getSerializable(EXTRA_LIST_TYPE);
            this.kindId = savedInstanceState.getInt("kindId");
            this.query = savedInstanceState.getString(EXTRA_QUERY);
            Timber.tag(TAG).d("onCreate type:" + this.typeId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_foodlist, container, false);
        binding.setViewModel(viewModel);

        this.typeId = getArguments().getInt(EXTRA_TYPE, 0);
        this.listType = (ListType) getArguments().getSerializable(EXTRA_LIST_TYPE);
        this.kindId = getArguments().getInt("kindId");
        this.query = getArguments().getString(EXTRA_QUERY);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        showFoods();
    }

    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.destroy();
        compositeDisposable.dispose();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_TYPE, this.typeId);
        outState.putInt("kindId", this.kindId);
        outState.putString(EXTRA_QUERY, this.query);
        outState.putSerializable(EXTRA_LIST_TYPE, this.listType);
    }

    private void initView() {
        foodListAdapter = new FoodListAdapter(getContext());
        binding.recyclerView.setAdapter(foodListAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        initKindsSpinner();
    }

    private void initKindsSpinner() {
        if (this.listType != ListType.NORMAL) {
            kindId = KINDS_ALL;
            viewModel.setKindSpinnerVisibility(View.GONE);
            return;
        }

        // Creating foodListAdapter for spinner
        kindSpinnerAdapter = new KindSpinnerAdapter(getActivity());
        binding.spinner.setAdapter(kindSpinnerAdapter);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    kindId = KINDS_ALL;
                } else {
                    kindId = viewModel.getKindsList().get(position - 1).id;
                }
                showFoods();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void showFoods() {
        switch (this.listType) {
            case NORMAL:
                Disposable disposable = viewModel.getFoodViewModelList(typeId, kindId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::renderFoods,
                                throwable -> Timber.tag(TAG).e(throwable, "Failed to show foods.")
                        );
                compositeDisposable.add(disposable);
                break;

            case SEARCH_RESULT:
                disposable = viewModel.getFoodViewModelList(query)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::renderFoods,
                                throwable -> Timber.tag(TAG).e(throwable, "Failed to show foods.")
                        );
                compositeDisposable.add(disposable);
                break;

            case FAVORITES:
                disposable = viewModel.getFoodViewModelListFavorites()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::renderFoods,
                                throwable -> Timber.tag(TAG).e(throwable, "Failed to show foods.")
                        );
                compositeDisposable.add(disposable);
                break;
        }
    }

    private void renderFoods(List<FoodViewModel> foodViewModels) {
        Timber.tag(TAG).d("renderFoods start type:" + typeId + " foodViewModels.size" + foodViewModels.size());
        if (binding.recyclerView.getLayoutManager() == null) {
            LinearLayoutManager lm = new LinearLayoutManager(getContext());
            binding.recyclerView.setLayoutManager(lm);
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
        layoutManager.scrollToPositionWithOffset(0, 0);
        if (viewModel.getKindSpinnerVisibility() == View.VISIBLE) {
            kindSpinnerAdapter.setData(viewModel.getKindsList());
        }
        foodListAdapter.reset(foodViewModels);
    }
}
