package com.sakurafish.pockettoushituryou.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.sakurafish.pockettoushituryou.model.FavoriteFoods;
import com.sakurafish.pockettoushituryou.model.Foods;
import com.sakurafish.pockettoushituryou.model.Kinds;
import com.sakurafish.pockettoushituryou.repository.FavoriteFoodsRepository;
import com.sakurafish.pockettoushituryou.repository.FoodsRepository;
import com.sakurafish.pockettoushituryou.view.fragment.FoodListFragment.ListType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import timber.log.Timber;

import static com.sakurafish.pockettoushituryou.view.fragment.FoodListFragment.ListType.NORMAL;

public final class FoodListViewModel extends BaseObservable implements ViewModel {
    final static String TAG = FoodListViewModel.class.getSimpleName();

    private Context context;
    private FoodsRepository foodsRepository;
    private FavoriteFoodsRepository favoriteFoodsRepository;

    private List<Kinds> kindsList;
    private List<Foods> foodsList;

    private ListType listType;
    private int foodsVisibility;
    private int kindSpinnerVisibility;
    private int emptyMessageVisibility;

    @Inject
    FoodListViewModel(Context context, FoodsRepository foodsRepository, FavoriteFoodsRepository favoriteFoodsRepository) {
        this.context = context;
        this.foodsRepository = foodsRepository;
        this.favoriteFoodsRepository = favoriteFoodsRepository;

        this.kindsList = new ArrayList<>();
        this.foodsList = new ArrayList<>();

        this.listType = NORMAL;
        setFoodsVisibility(View.VISIBLE);
        setKindSpinnerVisibility(View.VISIBLE);
        setEmptyMessageVisibility(View.GONE);
    }

    @Override
    public void destroy() {
    }

    public int getFoodsVisibility() {
        return foodsVisibility;
    }

    public void setFoodsVisibility(int foodsVisibility) {
        this.foodsVisibility = foodsVisibility;
    }

    public int getKindSpinnerVisibility() {
        return kindSpinnerVisibility;
    }

    public void setKindSpinnerVisibility(int kindSpinnerVisibility) {
        this.kindSpinnerVisibility = kindSpinnerVisibility;
    }

    public int getEmptyMessageVisibility() {
        return emptyMessageVisibility;
    }

    public void setEmptyMessageVisibility(int emptyMessageVisibility) {
        this.emptyMessageVisibility = emptyMessageVisibility;
    }

    public List<Kinds> getKindsList() {
        return this.kindsList;
    }

    public Single<List<FoodViewModel>> getFoodViewModelList(@IntRange(from = 1, to = 6) int typeId, int kindId) {
        this.listType = NORMAL;
        return foodsRepository.findFromLocal(typeId, kindId)
                .map(foodsData -> {
                    kindsList.clear();
                    kindsList.addAll(foodsData.getKinds());
                    foodsList.clear();
                    foodsList.addAll(foodsData.getFoods());
                    Timber.tag(TAG).d("getFoodViewModelList local data loaded type:" + typeId + " kinds size:" + foodsData.getKinds().size() + " foods size:" + foodsData.getFoods().size());

                    return getFoodViewModels();
                });
    }

    public Single<List<FoodViewModel>> getFoodViewModelList(@Nullable String query) {
        this.listType = ListType.SEARCH_RESULT;
        return foodsRepository.findFromLocal(query)
                .map(foodsData -> {
                    kindsList.clear();
                    foodsList.clear();
                    foodsList.addAll(foodsData.getFoods());
                    Timber.tag(TAG).d("getFoodViewModelList local data loaded foods size:" + foodsData.getFoods().size());

                    return getFoodViewModels();
                });
    }

    public Single<List<FoodViewModel>> getFoodViewModelListFavorites() {
        this.listType = ListType.FAVORITES;
        return favoriteFoodsRepository.findAllFromLocal()
                .map(favoriteFoodsList -> {
                    kindsList.clear();
                    foodsList.clear();
                    for (FavoriteFoods favoriteFoods : favoriteFoodsList) {
                        foodsList.add(favoriteFoods.foods);
                    }
                    Timber.tag(TAG).d("getFoodViewModelListFavorites local data loaded foods size:" + favoriteFoodsList.size());
                    return getFoodViewModels();
                });
    }

    @NonNull
    private List<FoodViewModel> getFoodViewModels() {
        List<FoodViewModel> foodViewModels = new ArrayList<>();
        for (Foods foods : foodsList) {
            foodViewModels.add(new FoodViewModel(this.context, this.favoriteFoodsRepository, foods));
        }
        setViewsVisiblity(foodViewModels);

        return foodViewModels;
    }

    private void setViewsVisiblity(List<FoodViewModel> foodViewModels) {
        if (foodViewModels.size() > 0) {
            setFoodsVisibility(View.VISIBLE);
            setKindSpinnerVisibility(this.listType == NORMAL ? View.VISIBLE : View.GONE);
            setEmptyMessageVisibility(View.GONE);
        } else {
            setFoodsVisibility(View.GONE);
            setKindSpinnerVisibility(View.GONE);
            setEmptyMessageVisibility(View.VISIBLE);
        }
    }
}
