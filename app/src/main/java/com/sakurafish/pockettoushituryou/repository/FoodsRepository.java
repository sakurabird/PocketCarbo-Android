package com.sakurafish.pockettoushituryou.repository;

import android.database.Cursor;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.api.PocketCarboService;
import com.sakurafish.pockettoushituryou.model.Foods;
import com.sakurafish.pockettoushituryou.model.FoodsData;
import com.sakurafish.pockettoushituryou.model.Kinds;
import com.sakurafish.pockettoushituryou.model.OrmaDatabase;
import com.sakurafish.pockettoushituryou.view.helper.ResourceResolver;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class FoodsRepository {
    private final static String TAG = FoodsRepository.class.getSimpleName();

    private final PocketCarboService pocketCarboService;
    private final OrmaDatabase orma;
    private final ResourceResolver resourceResolver;

    private FoodsData foodsData;
    private boolean isDirty;

    @Inject
    FoodsRepository(PocketCarboService pocketCarboService, OrmaDatabase orma, ResourceResolver resourceResolver) {
        this.pocketCarboService = pocketCarboService;
        this.orma = orma;
        this.resourceResolver = resourceResolver;

        this.foodsData = new FoodsData();
        this.isDirty = true;
    }

    public FoodsData getFoodsData() {
        return foodsData;
    }

    public Single<FoodsData> findAll() {
        Timber.tag(TAG).d("findAll start");
        if (foodsData.getFoods() != null && !foodsData.getFoods().isEmpty()) {
            return Single.create(emitter -> {
                emitter.onSuccess(foodsData);
            });
        }

        if (isDirty) {
            return findAllFromRemote();
        } else {
            return findAllFromLocal();
        }
    }

    public Single<FoodsData> findAllFromRemote() {
        Timber.tag(TAG).d("findAllFromRemote start");
        return pocketCarboService.getFoodsData()
                .doOnSuccess(foodsData -> {
                    Timber.tag(TAG).d("*foods*size:" + foodsData.getFoods().size() + "  *kinds*size:" + foodsData.getKinds().size());
                    this.foodsData = foodsData;
                    updateAllAsync(this.foodsData);
                    if (foodsData.getFoods().isEmpty() || foodsData.getKinds().isEmpty()) {
                        Timber.tag(TAG).e("findAllFromRemote succeded. but foodsData is empty");
                        if (orma.relationOfFoods().isEmpty() || orma.relationOfKinds().isEmpty()) {
                            findAllFromAssets();
                        } else {
                            findAllFromLocal();
                        }
                    }
                })
                .doOnError(throwable -> {
                    Timber.tag(TAG).e("findAllFromRemote failed");
                    throwable.printStackTrace();
                    if (orma.relationOfFoods().isEmpty() || orma.relationOfKinds().isEmpty()) {
                        findAllFromAssets();
                    } else {
                        findAllFromLocal();
                    }
                });
    }

    private Single<FoodsData> findAllFromLocal() {
        Timber.tag(TAG).d("findAllFromLocal start");
        return orma.relationOfFoods().selector().executeAsObservable().toList()
                .flatMap(foodsList -> {
                    Timber.tag(TAG).e("findAllFromLocal loaded **foodList size:" + foodsList.size());
                    this.foodsData.setFoods(foodsList);
                    return orma.relationOfKinds().selector().executeAsObservable().toList();
                })
                .flatMap(kindsList -> {
                    Timber.tag(TAG).e("findAllFromLocal loaded **kindList size:" + kindsList.size());
                    this.foodsData.setKinds(kindsList);

                    // insert all data
                    updateAllAsync(this.foodsData);
                    return Single.create(emitter -> emitter.onSuccess(this.foodsData));
                });
    }

    private Single<FoodsData> findAllFromAssets() {
        Timber.tag(TAG).d("findAllFromAssets start");
        final String json = resourceResolver.loadJSONFromAsset(resourceResolver.getString(R.string.foods_and_kinds_file));
        final Gson gson = new Gson();
        this.foodsData = gson.fromJson(json, FoodsData.class);
        Timber.tag(TAG).d("findAllFromAssets 2 " + " *foods*size:" + foodsData.getFoods().size() + "  *kinds*size:" + foodsData.getKinds().size());
        updateAllAsync(this.foodsData);
        return Single.create(emitter -> emitter.onSuccess(this.foodsData));
    }

    public Single<FoodsData> findFromLocal(@IntRange(from = 1, to = 6) int typeId, int kindId) {
        Timber.tag(TAG).d("findFromLocal start type:" + typeId + " kind:" + kindId);
        if (kindId == 0) {
            return orma.relationOfFoods().selector().type_idEq(typeId).executeAsObservable().toList()
                    .flatMap(foodsList -> {
                        Timber.tag(TAG).d("findFromLocal2 foods size:" + foodsList.size());
                        this.foodsData.setFoods(foodsList);
                        return orma.relationOfKinds().selector().type_idEq(typeId).executeAsObservable().toList();
                    })
                    .flatMap(kindsList -> {
                        Timber.tag(TAG).d("findFromLocal3 kinds size:" + kindsList.size());
                        this.foodsData.setKinds(kindsList);
                        return Single.create(emitter -> emitter.onSuccess(this.foodsData));
                    });
        } else {
            return orma.relationOfFoods().selector().type_idEq(typeId).kind_idEq(kindId).executeAsObservable().toList()
                    .flatMap(foodsList -> {
                        Timber.tag(TAG).d("findFromLocal4 foods size:" + foodsList.size());
                        this.foodsData.setFoods(foodsList);
                        return orma.relationOfKinds().selector().type_idEq(typeId).executeAsObservable().toList();
                    })
                    .flatMap(kindsList -> {
                        Timber.tag(TAG).d("findFromLocal5 kinds size:" + kindsList.size());
                        this.foodsData.setKinds(kindsList);
                        return Single.create(emitter -> emitter.onSuccess(this.foodsData));
                    });
        }
    }

    public Single<FoodsData> findFromLocal(@Nullable String query) {
        Timber.tag(TAG).d("findFromLocal start query:" + query);
        String[] word = query.trim().replaceAll("　", " ").split(" ", 0);

        final StringBuilder builder = new StringBuilder();
        builder.append("SELECT \"foods\".* FROM \"foods\" WHERE (");
        for (int i = 0; i < word.length; i++) {
            String str = word[i];
            Timber.tag(TAG).d("findFromLocal each word:" + str);
            builder.append("(\"foods\".\"name\" LIKE \'%");
            builder.append(str);
            builder.append("%\' OR \"foods\".\"search_word\" LIKE \'%");
            builder.append(str);
            builder.append("%\')");
            if (i != word.length - 1) {
                builder.append(" AND ");
            } else {
                builder.append(")");
            }
        }
        Timber.tag(TAG).d("findFromLocal sql query:" + builder.toString());

        Cursor cursor = orma.getConnection().rawQuery(builder.toString());
        Timber.tag(TAG).d("findFromLocal cursor.getCount():" + cursor.getCount());
        List<Foods> foodsList = toFoodsList(cursor);
        this.foodsData.setFoods(foodsList);
        this.foodsData.setKinds(null);
        return Single.create(emitter -> emitter.onSuccess(this.foodsData));
    }

    public List<Foods> toFoodsList(Cursor cursor) {
        ArrayList<Foods> list = new ArrayList<>(cursor.getCount());
        try {
            for (int pos = 0; cursor.moveToPosition(pos); pos++) {
                Foods foods = orma.relationOfFoods().selector().newModelFromCursor(cursor);
                list.add(foods);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    private Disposable updateAllAsync(@NonNull final FoodsData foodsData) {
        Timber.tag(TAG).d("updateAllAsync start" + " *foods*size:" + foodsData.getFoods().size() + "  *kinds*size:" + foodsData.getKinds().size());
        return orma.transactionAsCompletable(() -> {
            for (Foods foods : foodsData.getFoods()) {
                orma.relationOfFoods().upsert(foods);
            }
            for (Kinds kinds : foodsData.getKinds()) {
                orma.relationOfKinds().upsert(kinds);
            }
        })
                .subscribe();
    }
}