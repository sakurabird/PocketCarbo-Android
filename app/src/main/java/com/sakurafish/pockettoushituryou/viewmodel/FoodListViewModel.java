package com.sakurafish.pockettoushituryou.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.IntRange;
import android.support.annotation.MainThread;

import com.sakurafish.pockettoushituryou.model.FoodsData;
import com.sakurafish.pockettoushituryou.repository.FoodsRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public final class FoodListViewModel extends BaseObservable implements ViewModel {
    final static String TAG = "FoodListViewModel";

    private FoodsRepository foodsRepository;

    private ObservableList<FoodViewModel> viewModels;

    private int type;

    @Inject
    FoodListViewModel(FoodsRepository foodsRepository) {
        this.foodsRepository = foodsRepository;
        this.viewModels = new ObservableArrayList<>();
    }

    @Override
    public void destroy() {
    }

    public ObservableList<FoodViewModel> getFoodViewModels() {
        return this.viewModels;
    }

    public void setType(@IntRange(from = 1, to = 6) int type) {
        this.type = type;
    }

    @MainThread
    public void renderFoods() {
        List<FoodViewModel> foodViewModels = new ArrayList<>();
        for (FoodsData.Foods foods : foodsRepository.getFoodsData().foods) {
            if (foods.type_id == this.type) {
                foodViewModels.add(new FoodViewModel(foods));
            }
        }

        viewModels.clear();
        viewModels.addAll(foodViewModels);
    }
}
