package com.sakurafish.pockettoushituryou.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.IntRange;
import android.support.annotation.MainThread;

import com.sakurafish.pockettoushituryou.model.FoodsData;
import com.sakurafish.pockettoushituryou.model.KindsData;
import com.sakurafish.pockettoushituryou.repository.FoodsRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public final class FoodListViewModel extends BaseObservable implements ViewModel {
    final static String TAG = "FoodListViewModel";

    private FoodsRepository foodsRepository;

    private ObservableList<FoodViewModel> viewModels;

    private int type;

    // TODO 後で消すかも
    private String[] kindNames;

    private ArrayList<KindsData.Kinds> kindsList;

    private int selectedKindId;

    @Inject
    FoodListViewModel(FoodsRepository foodsRepository) {
        this.foodsRepository = foodsRepository;
        this.viewModels = new ObservableArrayList<>();

        this.selectedKindId = 0;
    }

    @Override
    public void destroy() {
    }

    public ObservableList<FoodViewModel> getFoodViewModels() {
        return this.viewModels;
    }

    @Bindable
    public String[] getKindNames() {
        // TODO 後で消すかも
        return this.kindNames;
    }

    public ArrayList<KindsData.Kinds> getKindsList() {
        return this.kindsList;
    }

    public void setType(@IntRange(from = 1, to = 6) int type) {
        this.type = type;

        this.kindsList = new ArrayList<>();
        ArrayList<String> list = new ArrayList<>();
        for (KindsData.Kinds kinds : foodsRepository.getKindsData().kinds) {
            if (kinds.type_id == this.type) {
                list.add(kinds.name);
                this.kindsList.add(kinds);
            }
        }
        this.kindNames = list.toArray(new String[list.size()]);
//        this.kindsList = foodsRepository.getKindsData().kinds;
    }

    @MainThread
    public void setKind(int selectedKindId) {
        this.selectedKindId = selectedKindId;
        renderFoods();
    }

    @MainThread
    public void renderFoods() {
        Timber.tag(TAG).d("start renderFoods type:" + type);
        if (foodsRepository == null) return;

        List<FoodViewModel> foodViewModels = new ArrayList<>();
        for (FoodsData.Foods foods : foodsRepository.getFoodsData().foods) {
            if (foods.type_id == this.type) {
                if (this.selectedKindId == 0 || foods.kind_id == this.selectedKindId) {
                    foodViewModels.add(new FoodViewModel(foods));
                }
            }
        }

        viewModels.clear();
        viewModels.addAll(foodViewModels);
    }
}
