package com.sakurafish.pockettoushituryou.repository;

import android.database.Cursor;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.api.PocketCarboService;
import com.sakurafish.pockettoushituryou.model.DataVersion;
import com.sakurafish.pockettoushituryou.model.Foods;
import com.sakurafish.pockettoushituryou.model.FoodsData;
import com.sakurafish.pockettoushituryou.model.Foods_Selector;
import com.sakurafish.pockettoushituryou.model.Kinds;
import com.sakurafish.pockettoushituryou.model.OrmaDatabase;
import com.sakurafish.pockettoushituryou.rxbus.FoodsUpdatedEvent;
import com.sakurafish.pockettoushituryou.rxbus.RxBus;
import com.sakurafish.pockettoushituryou.view.helper.ResourceResolver;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class FoodsRepository {
    private final static String TAG = FoodsRepository.class.getSimpleName();

    public final static String EVENT_DB_UPDATED = "EVENT_DB_UPDATED";

    private final PocketCarboService pocketCarboService;
    private final OrmaDatabase orma;
    private final ResourceResolver resourceResolver;

    private FoodsData foodsData;

    @Inject
    FoodsRepository(PocketCarboService pocketCarboService, OrmaDatabase orma, ResourceResolver resourceResolver) {
        this.pocketCarboService = pocketCarboService;
        this.orma = orma;
        this.resourceResolver = resourceResolver;

        this.foodsData = new FoodsData();
    }

    public Single<DataVersion> receiveDataVersion() {
        Timber.tag(TAG).d("receiveDataVersion start");
        return pocketCarboService.getDataVersion()
                .doOnSuccess(dataVersion -> Timber.tag(TAG).d("receiveDataVersion succeeded version:" + dataVersion.version))
                .doOnError(throwable -> {
                    Timber.tag(TAG).e("receiveDataVersion failed");
                    throwable.printStackTrace();
                });
    }

    @Deprecated
    public Single<FoodsData> findAllFromRemote() {
        Timber.tag(TAG).d("findAllFromRemote start");
        return pocketCarboService.getFoodsData()
                .doOnSuccess(foodsData -> {
                    Timber.tag(TAG).d("findAllFromRemote *foods*size:" + foodsData.getFoods().size() + "  *kinds*size:" + foodsData.getKinds().size());
                    if (foodsData.getFoods().isEmpty() || foodsData.getKinds().isEmpty()) {
                        Timber.tag(TAG).e("findAllFromRemote succeded. but foodsData is empty");
                        if (orma.relationOfFoods().isEmpty() || orma.relationOfKinds().isEmpty()) {
                            findAllFromAssets();
                        } else {
                            findAllFromLocal();
                        }
                    } else {
                        this.foodsData = foodsData;
                        updateAllAsync(this.foodsData);
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

    public Single<FoodsData> findAllFromLocal() {
        Timber.tag(TAG).d("findAllFromLocal start");
        if (orma.relationOfFoods().isEmpty() || orma.relationOfKinds().isEmpty()) {
            return findAllFromAssets();
        }

        return orma.relationOfFoods().selector().executeAsObservable().toList()
                .flatMap(foodsList -> {
                    Timber.tag(TAG).d("findAllFromLocal loaded **foodList size:" + foodsList.size());
                    this.foodsData.setFoods(foodsList);
                    return orma.relationOfKinds().selector().executeAsObservable().toList();
                })
                .flatMap(kindsList -> {
                    Timber.tag(TAG).d("findAllFromLocal loaded **kindList size:" + kindsList.size());
                    this.foodsData.setKinds(kindsList);

                    return Single.create(emitter -> emitter.onSuccess(this.foodsData));
                });
    }

    public Single<FoodsData> findAllFromAssets() {
        Timber.tag(TAG).d("findAllFromAssets start");
        final String json = resourceResolver.loadJSONFromAsset(resourceResolver.getString(R.string.foods_and_kinds_file));
        final Gson gson = new Gson();
        this.foodsData = gson.fromJson(json, FoodsData.class);
        Timber.tag(TAG).d("findAllFromAssets 2 " + " *foods*size:" + foodsData.getFoods().size() + "  *kinds*size:" + foodsData.getKinds().size());
        updateAllAsync(this.foodsData);
        return Single.create(emitter -> emitter.onSuccess(this.foodsData));
    }

    public Single<FoodsData> findFromLocal(@IntRange(from = 1, to = 6) int typeId, int kindId, int sort) {
        Timber.tag(TAG).d("findFromLocal start type:" + typeId + " kind:" + kindId + " sort:" + sort);

        Foods_Selector selector = orma.relationOfFoods().selector();
        selector.type_idEq(typeId);
        if (kindId != 0) {
            selector.kind_idEq(kindId);
        }
        switch (sort) {
            case 0:
                // 食品名順(default)
                selector.orderByNameAsc();
                break;
            case 1:
                // 食品名逆順
                selector.orderByNameDesc();
                break;
            case 2:
                // 糖質量の少ない順
                selector.orderByCarbohydrate_per_100gAsc();
                break;
            case 3:
                // 糖質量の少ない順
                selector.orderByCarbohydrate_per_100gDesc();
                break;
        }

        return selector.executeAsObservable().toList()
                .flatMap(foodsList -> {
                    Timber.tag(TAG).d("findFromLocal1 foods size:" + foodsList.size());
                    this.foodsData.setFoods(foodsList);
                    return orma.relationOfKinds().selector().type_idEq(typeId).executeAsObservable().toList();
                })
                .flatMap(kindsList -> {
                    Timber.tag(TAG).d("findFromLocal2 kinds size:" + kindsList.size());
                    this.foodsData.setKinds(kindsList);
                    return Single.create(emitter -> emitter.onSuccess(this.foodsData));
                });
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
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> {
                    Timber.tag(TAG).d("updateAllAsync completed");
                    RxBus rxBus = RxBus.getIntanceBus();
                    rxBus.post(new FoodsUpdatedEvent(EVENT_DB_UPDATED));
                })
                .subscribe();
    }
}