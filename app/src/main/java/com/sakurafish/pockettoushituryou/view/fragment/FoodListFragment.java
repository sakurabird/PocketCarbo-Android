package com.sakurafish.pockettoushituryou.view.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.databinding.FragmentFoodlistBinding;
import com.sakurafish.pockettoushituryou.repository.FoodsRepository;
import com.sakurafish.pockettoushituryou.rxbus.EventWithMessage;
import com.sakurafish.pockettoushituryou.rxbus.FoodsUpdatedEvent;
import com.sakurafish.pockettoushituryou.rxbus.RxBus;
import com.sakurafish.pockettoushituryou.view.activity.MainActivity;
import com.sakurafish.pockettoushituryou.view.adapter.FoodListAdapter;
import com.sakurafish.pockettoushituryou.view.adapter.KindSpinnerAdapter;
import com.sakurafish.pockettoushituryou.view.adapter.SortSpinnerAdapter;
import com.sakurafish.pockettoushituryou.view.helper.ShowcaseHelper;
import com.sakurafish.pockettoushituryou.viewmodel.FoodListViewModel;
import com.sakurafish.pockettoushituryou.viewmodel.FoodViewModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.sakurafish.pockettoushituryou.model.FoodsData.KINDS_ALL;
import static com.sakurafish.pockettoushituryou.repository.FoodsRepository.EVENT_DB_UPDATED;
import static com.sakurafish.pockettoushituryou.view.activity.SearchResultActivity.EXTRA_QUERY;
import static com.sakurafish.pockettoushituryou.view.helper.ShowcaseHelper.EVENT_SHOWCASE_MAINACTIVITY_FINISHED;
import static com.sakurafish.pockettoushituryou.view.helper.ShowcaseHelper.SHOWCASE_DELAY;

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
    private int sort = 0; // デフォルトは名前順;
    private String query = "";
    private boolean sortToast;
    private RxBus rxBus;

    @Inject
    FoodListViewModel viewModel;

    @Inject
    CompositeDisposable compositeDisposable;

    @Inject
    FoodsRepository foodsRepository;

    @Inject
    ShowcaseHelper showcaseHelper;

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
        this.sortToast = false;
        initView();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRxBus();
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
        unregisterRxBus();
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
        initSortSpinner();
    }

    private void initKindsSpinner() {
        if (this.listType != ListType.NORMAL) {
            kindId = KINDS_ALL;
            viewModel.setKindSpinnerVisibility(View.GONE);
            return;
        }

        // Creating foodListAdapter for spinner
        kindSpinnerAdapter = new KindSpinnerAdapter(getActivity());
        binding.kindSpinner.setAdapter(kindSpinnerAdapter);
        binding.kindSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void initSortSpinner() {
        SortSpinnerAdapter adapter = new SortSpinnerAdapter(getActivity());
        binding.sortSpinner.setAdapter(adapter);
        binding.sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sort = position;
                if (position < 0 || position > 3) sort = 0;
                if (sortToast) {
                    Toast.makeText(getActivity(), SortSpinnerAdapter.texts[position] + "にソートしました。", Toast.LENGTH_SHORT).show();
                } else {
                    sortToast = true;
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
                Disposable disposable = viewModel.getFoodViewModelList(typeId, kindId, sort)
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

        showTutorialOnce();
    }

    private void showTutorialOnce() {
        if (!showcaseHelper.isShowcaseMainActivityFinished() || showcaseHelper.isShowcaseFoodListFragmentFinished())
            return;
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            if (activity.getCurrentPagerPosition() + 1 != typeId) {
                return;
            }
        }

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(SHOWCASE_DELAY);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), ShowcaseHelper.SHOWCASE_ID_FOODLISTFRAGMENT);
        sequence.setConfig(config);

        // Kind Spinner tutorial
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(binding.kindSpinner)
                        .setContentText(R.string.tutorial_kind_text)
                        .setDismissText(android.R.string.ok)
                        .withRectangleShape()
                        .setDismissOnTouch(true)
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(binding.sortSpinner)
                        .setContentText(R.string.tutorial_sort_text)
                        .setDismissText(android.R.string.ok)
                        .withRectangleShape()
                        .setDismissOnTouch(true)
                        .build()
        );

        // List tutorial
        if (binding.recyclerView.getChildAt(0) != null) {
            View view = binding.recyclerView.getChildAt(0);
            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(getActivity())
                            .setTarget(view)
                            .setContentText(R.string.tutorial_food_list_text)
                            .setDismissText(android.R.string.ok)
                            .withRectangleShape()
                            .setListener(new IShowcaseListener() {
                                @Override
                                public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {
                                }

                                @Override
                                public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                                    LayoutInflater inflater = getActivity().getLayoutInflater();
                                    View dialoglayout = inflater.inflate(R.layout.view_tutorial_finished, null);
                                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                    alert.setView(dialoglayout);
                                    alert.setPositiveButton("OK", (dialog, whichButton) -> showcaseHelper.setPrefShowcaseFoodListFragmentFinished(true));
                                    alert.show();
                                }
                            })
                            .setDismissOnTouch(true)
                            .build()
            );
        }
        sequence.start();
    }

    private void initRxBus() {
        rxBus = RxBus.getIntanceBus();
        registerRxBus(FoodsUpdatedEvent.class, rxBusMessage -> {
            // DBに食品データが全て追加された
            if (TextUtils.equals(rxBusMessage.getMessage(), EVENT_DB_UPDATED)) {
                showFoods();
            }
        });
        registerRxBus(EventWithMessage.class, rxBusMessage -> {
            // MainActivityのチュートリアルが表示済みになった
            if (TextUtils.equals(rxBusMessage.getMessage(), EVENT_SHOWCASE_MAINACTIVITY_FINISHED)) {
                showTutorialOnce();
            }
        });
    }

    public <T> void registerRxBus(Class<T> eventType, Consumer<T> action) {
        Disposable disposable = rxBus.doSubscribe(eventType, action, throwable -> Timber.tag(TAG).e(throwable, "Failed to register RxBus"));
        rxBus.addSubscription(this, disposable);
    }

    public void unregisterRxBus() {
        rxBus.unSubscribe(this);
    }
}
