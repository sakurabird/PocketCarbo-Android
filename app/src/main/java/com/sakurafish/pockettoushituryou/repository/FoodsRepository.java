package com.sakurafish.pockettoushituryou.repository;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.api.PocketCarboService;
import com.sakurafish.pockettoushituryou.model.FoodsData;
import com.sakurafish.pockettoushituryou.model.OrmaDatabase;
import com.sakurafish.pockettoushituryou.view.helper.ResourceResolver;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

@Singleton
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
            Timber.tag(TAG).d("findAll start2");
            return Single.create(emitter -> {
                emitter.onSuccess(foodsData);
            });
        }

        if (isDirty) {
            Timber.tag(TAG).d("findAll start3");
            return findAllFromRemote();
        } else {
            Timber.tag(TAG).d("findAll start4");
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

    private Disposable updateAllAsync(@NonNull final FoodsData foodsData) {
        Timber.tag(TAG).d("updateAllAsync start" + " *foods*size:" + foodsData.getFoods().size() + "  *kinds*size:" + foodsData.getKinds().size());
        return orma.transactionAsCompletable(() -> {
            orma.relationOfFoods().deleter().execute();
            orma.relationOfKinds().deleter().execute();
            foodsData.getFoods().forEach(orma.relationOfFoods()::upsert);
            foodsData.getKinds().forEach(orma.relationOfKinds()::upsert);
        })
                .subscribe();
    }
}