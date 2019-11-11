package com.sakurafish.pockettoushituryou.repository;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sakurafish.pockettoushituryou.data.db.entity.Foods;
import com.sakurafish.pockettoushituryou.data.db.entity.Foods_Selector;
import com.sakurafish.pockettoushituryou.data.db.entity.Kinds;
import com.sakurafish.pockettoushituryou.data.db.entity.OrmaDatabase;
import com.sakurafish.pockettoushituryou.data.local.FoodsData;
import com.sakurafish.pockettoushituryou.data.local.LocalJsonResolver;
import com.sakurafish.pockettoushituryou.rxbus.FoodsUpdatedEvent;
import com.sakurafish.pockettoushituryou.rxbus.RxBus;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
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

    private final OrmaDatabase orma;
    private final Context context;
    private final Moshi moshi;

    private FoodsData foodsData;

    @Inject
    FoodsRepository(OrmaDatabase orma, Context context, Moshi moshi) {
        this.orma = orma;
        this.context = context;
        this.moshi = moshi;

        this.foodsData = new FoodsData();
    }

    public Single<FoodsData> findAllFromAssets() {
        final String json;
        try {
            json = LocalJsonResolver.loadJsonFromAsset(context, "json/foods_and_kinds.json");
            JsonAdapter<FoodsData> jsonAdapter = moshi.adapter(FoodsData.class);
            this.foodsData = jsonAdapter.fromJson(json);
            assert this.foodsData != null;
            updateAllAsync(this.foodsData);
            return Single.create(emitter -> emitter.onSuccess(this.foodsData));
        } catch (IOException e) {
            e.printStackTrace();
            return Single.create(emitter -> emitter.onError(e));
        }
    }

    public Single<FoodsData> findFromLocal(@IntRange(from = 1, to = 6) int typeId, int kindId, int sort) {

        Foods_Selector selector = orma.relationOfFoods().selector();
        selector.typeIdEq(typeId);
        if (kindId != 0) {
            selector.kindIdEq(kindId);
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
                selector.orderByCarbohydratePer100gAsc();
                break;
            case 3:
                // 糖質量の少ない順
                selector.orderByCarbohydratePer100gDesc();
                break;
        }

        return selector.executeAsObservable().toList()
                .flatMap(foodsList -> {
                    this.foodsData.setFoods(foodsList);
                    return orma.relationOfKinds().selector().typeIdEq(typeId).executeAsObservable().toList();
                })
                .flatMap(kindsList -> {
                    this.foodsData.setKinds(kindsList);
                    return Single.create(emitter -> emitter.onSuccess(this.foodsData));
                });
    }

    public Single<FoodsData> findFromLocal(@Nullable String query) {
        if (query == null) {
            // 基本的にqueryがemptyなことはないが、特定の端末でNPEが発生するためこの処理を入れた(ver2.3)
            query = "";
        }
        String[] word = query.trim().replaceAll("　", " ").split(" ", 0);

        final StringBuilder builder = new StringBuilder();
        builder.append("SELECT \"foods\".* FROM \"foods\" INNER JOIN \"kinds\" ON \"kinds\".\"id\" = \"foods\".\"kind_id\" WHERE (");
        for (int i = 0; i < word.length; i++) {
            String str = word[i];

            if (word.length > 1) {
                builder.append("(");
            }
            builder.append("(\"foods\".\"name\" LIKE \'%");
            builder.append(str);
            builder.append("%\' OR \"foods\".\"search_word\" LIKE \'%");
            builder.append(str);
            builder.append("%\')");
            builder.append(" OR ");
            builder.append("(\"kinds\".\"name\" LIKE \'%");
            builder.append(str);
            builder.append("%\' OR \"kinds\".\"search_word\" LIKE \'%");
            builder.append(str);
            builder.append("%\')");

            if (word.length > 1) {
                builder.append(")");
                if (i != word.length - 1) {
                    builder.append(" AND ");
                }
            }
        }
        builder.append(") ORDER BY \"foods\".\"name\" ASC");

        Cursor cursor = orma.getConnection().rawQuery(builder.toString());
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