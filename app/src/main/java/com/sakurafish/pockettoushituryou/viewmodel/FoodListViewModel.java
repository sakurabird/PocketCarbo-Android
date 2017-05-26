package com.sakurafish.pockettoushituryou.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.sakurafish.pockettoushituryou.model.Foods;
import com.sakurafish.pockettoushituryou.model.Kinds;
import com.sakurafish.pockettoushituryou.repository.FoodsRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import timber.log.Timber;

public final class FoodListViewModel extends BaseObservable implements ViewModel {
    final static String TAG = FoodListViewModel.class.getSimpleName();

    private Context context;
    private FoodsRepository foodsRepository;

    private List<Kinds> kindsList;
    private List<Foods> foodsList;

    @Inject
    FoodListViewModel(Context context, FoodsRepository foodsRepository) {
        this.context = context;
        this.foodsRepository = foodsRepository;

        this.kindsList = new ArrayList<>();
        this.foodsList = new ArrayList<>();
    }

    @Override
    public void destroy() {
    }

    public List<Kinds> getKindsList() {
        return this.kindsList;
    }

    public Single<List<FoodViewModel>> getFoodViewModelList(@IntRange(from = 1, to = 6) int typeId, int kindId) {
        return foodsRepository.findFromLocal(typeId, kindId)
                .map(foodsData -> {
                    Timber.tag(TAG).d("getFoodViewModelList local data loaded kinds size:" + foodsData.getKinds().size() + " foods size:" + foodsData.getFoods().size());
                    kindsList.clear();
                    kindsList.addAll(foodsData.getKinds());
                    foodsList.clear();
                    foodsList.addAll(foodsData.getFoods());

                    return getFoodViewModels();
                });
    }

    public Single<List<FoodViewModel>> getFoodViewModelList(@Nullable String query) {
        return foodsRepository.findFromLocal(query)
                .map(foodsData -> {
                    Timber.tag(TAG).d("getFoodViewModelList local data loaded foods size:" + foodsData.getFoods().size());
                    kindsList.clear();
                    foodsList.clear();
                    foodsList.addAll(foodsData.getFoods());

                    return getFoodViewModels();
                });
    }

    @NonNull
    private List<FoodViewModel> getFoodViewModels() {
        List<FoodViewModel> foodViewModels = new ArrayList<>();
        for (Foods foods : foodsList) {
            foodViewModels.add(new FoodViewModel(context, foods));
        }
        return foodViewModels;
    }
}
