package com.sakurafish.pockettoushituryou.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.IntRange;
import android.support.annotation.UiThread;

import com.sakurafish.pockettoushituryou.model.FoodsData;
import com.sakurafish.pockettoushituryou.model.KindsData;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.sakurafish.pockettoushituryou.model.KindsData.KINDS_ALL;

public final class FoodListViewModel extends BaseObservable implements ViewModel {
    final static String TAG = "FoodListViewModel";

    private Context context;

    private ObservableList<FoodViewModel> viewModels;

    private int type;
    private FoodsData foodsData;
    private KindsData kindsData;

    private ArrayList<KindsData.Kinds> kindsList;

    private int selectedKindId;

    @Inject
    FoodListViewModel(Context context) {
        this.context = context;
        this.viewModels = new ObservableArrayList<>();

        this.selectedKindId = 0;
    }

    @Override
    public void destroy() {
    }

    public ObservableList<FoodViewModel> getFoodViewModels() {
        return this.viewModels;
    }

    public ArrayList<KindsData.Kinds> getKindsList() {
        return this.kindsList;
    }

    public void setKindsData(KindsData kindsData) {
        this.kindsData = kindsData;
    }

    public void setFoodsData(FoodsData foodsData) {
        this.foodsData = foodsData;
    }

    public void setType(@IntRange(from = 1, to = 6) int type) {
        this.type = type;

        this.kindsList = new ArrayList<>();
        ArrayList<String> list = new ArrayList<>();
        this.kindsData.kinds.stream().filter(kinds -> kinds.type_id == this.type).forEach(kinds -> {
            list.add(kinds.name);
            this.kindsList.add(kinds);
        });
    }

    @UiThread
    public void setKind(int selectedKindId) {
        this.selectedKindId = selectedKindId;
        createFoodViewModels();
    }

    @UiThread
    public void createFoodViewModels() {
//        Timber.tag(TAG).d("start createFoodViewModels type:" + type);
        if (kindsData == null || foodsData == null) return;

        List<FoodViewModel> foodViewModels = new ArrayList<>();
        for (FoodsData.Foods foods : this.foodsData.foods) {
            if (foods.type_id == this.type) {
                if (KINDS_ALL == this.selectedKindId || foods.kind_id == this.selectedKindId) {
                    foodViewModels.add(new FoodViewModel(context, foods));
                }
            }
        }
        viewModels.clear();
        viewModels.addAll(foodViewModels);
    }
}
