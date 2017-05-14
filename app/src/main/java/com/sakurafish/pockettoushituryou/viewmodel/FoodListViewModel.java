package com.sakurafish.pockettoushituryou.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.support.annotation.IntRange;

import com.sakurafish.pockettoushituryou.model.Foods;
import com.sakurafish.pockettoushituryou.model.Kinds;
import com.sakurafish.pockettoushituryou.repository.FoodsRepository;
import com.sakurafish.pockettoushituryou.repository.KindsRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import timber.log.Timber;

public final class FoodListViewModel extends BaseObservable implements ViewModel {
    final static String TAG = FoodListViewModel.class.getSimpleName();

    private Context context;
    private KindsRepository kindsRepository;
    private FoodsRepository foodsRepository;

    private List<Kinds> kindsList;
    private List<Foods> foodsList;

    @Inject
    FoodListViewModel(Context context, KindsRepository kindsRepository, FoodsRepository foodsRepository) {
        this.context = context;
        this.kindsRepository = kindsRepository;
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
        return Single.zip(kindsRepository.findFromLocal(typeId),
                foodsRepository.findFromLocal(typeId, kindId),
                (kindsList1, foodsList1) -> {
                    Timber.tag(TAG).d("getFoodViewModelList local data loaded kinds size:" + kindsList1.size() + " foods size:" + foodsList1.size());
                    kindsList.clear();
                    kindsList.addAll(kindsList1);
                    foodsList.clear();
                    foodsList.addAll(foodsList1);

                    List<FoodViewModel> foodViewModels = new ArrayList<>();
                    for (Foods foods : foodsList) {
                        foodViewModels.add(new FoodViewModel(context, foods));
                    }
                    return foodViewModels;
                });
    }
}
