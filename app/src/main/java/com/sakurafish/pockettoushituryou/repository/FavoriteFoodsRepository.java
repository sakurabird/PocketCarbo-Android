package com.sakurafish.pockettoushituryou.repository;

import androidx.annotation.NonNull;

import com.sakurafish.pockettoushituryou.data.db.entity.FavoriteFoods;
import com.sakurafish.pockettoushituryou.data.db.entity.Foods;
import com.sakurafish.pockettoushituryou.data.db.entity.OrmaDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class FavoriteFoodsRepository {
    private final static String TAG = FavoriteFoodsRepository.class.getSimpleName();

    private final OrmaDatabase orma;

    @Inject
    FavoriteFoodsRepository(OrmaDatabase orma) {
        this.orma = orma;
    }

    public boolean isFavorite(int foodsId) {
        return !orma.relationOfFavoriteFoods().selector().foodsEq(foodsId).isEmpty();
    }

    public List<Foods> findAll() {
        List<FavoriteFoods> favoriteFoods = orma.relationOfFavoriteFoods().selector().orderByCreatedAtDesc().toList();
        List<Foods> foods = new ArrayList<>();
        for (FavoriteFoods favorite : favoriteFoods) {
            foods.add(favorite.getFoods());
        }
        return foods;
    }

    // TODO後で消す
    public Single<List<FavoriteFoods>> findAllFromLocal() {
        Timber.tag(TAG).d("findAllFromLocal start");
        return orma.relationOfFavoriteFoods().selector().orderByCreatedAtDesc().executeAsObservable().toList();
    }

    public Single<Integer> delete(@NonNull Foods foods) {
        return orma.relationOfFavoriteFoods().deleter().foodsEq(foods.getId()).executeAsSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable save(@NonNull Foods foods) {
        return orma.transactionAsCompletable(() -> {
            orma.relationOfFavoriteFoods().deleter().foodsEq(foods.getId()).execute();
            orma.relationOfFavoriteFoods().inserter().execute(new FavoriteFoods(foods, new Date()));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public String toString(int foodsId) {
        FavoriteFoods favoriteFoods = orma.relationOfFavoriteFoods().selector().foodsEq(foodsId).value();
        return favoriteFoods.toString();
    }
}