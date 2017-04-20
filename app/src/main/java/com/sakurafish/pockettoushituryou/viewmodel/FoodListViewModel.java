package com.sakurafish.pockettoushituryou.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.sakurafish.pockettoushituryou.network.FoodsData;
import com.sakurafish.pockettoushituryou.repository.FoodsRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static timber.log.Timber.tag;

public final class FoodListViewModel extends BaseObservable implements ViewModel {
    final static String TAG = "FoodListViewModel";

    private FoodsRepository foodsRepository;

    private ObservableList<FoodViewModel> viewModels;

    @Inject
    FoodListViewModel(FoodsRepository foodsRepository) {
        this.foodsRepository = foodsRepository;
        tag(TAG).d("FoodListViewModel@@@@@@@@@@ size:" + foodsRepository.getFoodsData().foods.size());
        this.viewModels = new ObservableArrayList<>();
    }

    @Override
    public void destroy() {
    }

    public ObservableList<FoodViewModel> getFoodViewModels() {
        return this.viewModels;
    }

    public void renderFoods() {
        List<FoodViewModel> foodViewModels = new ArrayList<>();
        for (FoodsData.Foods foods : foodsRepository.getFoodsData().foods) {
            foodViewModels.add(new FoodViewModel(foods));
        }

        viewModels.clear();
        viewModels.addAll(foodViewModels);
    }
}
