package com.sakurafish.pockettoushituryou.repository;

import com.sakurafish.pockettoushituryou.api.DummyData;
import com.sakurafish.pockettoushituryou.api.DummyDataService;
import com.sakurafish.pockettoushituryou.api.PocketCarboService;
import com.sakurafish.pockettoushituryou.model.FoodsData;
import com.sakurafish.pockettoushituryou.model.KindsData;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import timber.log.Timber;

import static android.content.ContentValues.TAG;

@Singleton
public class FoodsRepository {

    private PocketCarboService pocketCarboService;
    private DummyDataService dummyDataService;
    private KindsData kindsData;

    private FoodsData foodsData;

    @Inject
    FoodsRepository(PocketCarboService pocketCarboService, DummyDataService dummyDataService) {
        this.pocketCarboService = pocketCarboService;
        this.dummyDataService = dummyDataService;
    }

    public KindsData getKindsData() {
        return kindsData;
    }

    public FoodsData getFoodsData() {
        return foodsData;
    }

    public Single<KindsData> findKindsData() {
        return pocketCarboService.getKindsData()
                .doOnSuccess(kindsData -> {
                    Timber.tag(TAG).d("@@@**********kind******size:" + kindsData.kinds.size() + " 1件目:" + kindsData.kinds.get(0).name);
                    this.kindsData = kindsData;
                })
                .retry(3)
                .doOnError(Throwable::printStackTrace);
    }

    public Single<FoodsData> findFoodsData() {
        return pocketCarboService.getFoodsData()
                .doOnSuccess(foodsData -> {
                    Timber.tag(TAG).d("@@@**********food******size:" + foodsData.foods.size() + " 1件目:" + foodsData.foods.get(0).name);
                    this.foodsData = foodsData;
                })
                .retry(3)
                .doOnError(Throwable::printStackTrace);
    }

    // TODO
    public Single<DummyData> requestDummyData() {
        return dummyDataService.getDummyData()
                .doOnSuccess(dummyData -> {
                    Timber.tag(TAG).d("dummy data:" + dummyData.code);
                })
                .doOnError(Throwable::printStackTrace);
    }

}
