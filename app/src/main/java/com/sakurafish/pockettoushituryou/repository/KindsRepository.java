package com.sakurafish.pockettoushituryou.repository;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.api.PocketCarboService;
import com.sakurafish.pockettoushituryou.model.Kinds;
import com.sakurafish.pockettoushituryou.model.KindsData;
import com.sakurafish.pockettoushituryou.model.OrmaDatabase;
import com.sakurafish.pockettoushituryou.view.helper.ResourceResolver;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class KindsRepository {

    private final static String TAG = KindsRepository.class.getSimpleName();

    private final PocketCarboService pocketCarboService;
    private final OrmaDatabase orma;
    private final ResourceResolver resourceResolver;

    private KindsData kindsData;
    private boolean isDirty;

    @Inject
    KindsRepository(PocketCarboService pocketCarboService, OrmaDatabase orma, ResourceResolver resourceResolver) {
        this.pocketCarboService = pocketCarboService;
        this.orma = orma;
        this.resourceResolver = resourceResolver;

        this.kindsData = new KindsData();
        this.isDirty = true;
    }

    public KindsData getKindsData() {
        return kindsData;
    }

    public Single<KindsData> findAll() {
        Timber.tag(TAG).d("findAll start");
        if (kindsData.getKinds() != null && !kindsData.getKinds().isEmpty()) {
            return Single.create(emitter -> {
                emitter.onSuccess(kindsData);
            });
        }

        if (isDirty) {
            return findAllFromRemote();
        } else {
            return findAllFromLocal();
        }
    }

    public Single<KindsData> findAllFromRemote() {
        // Remoteからの取得が失敗したらローカルcacheから取得。ローカルcacheが空ならアセットの初期データから取得。
        return pocketCarboService.getKindsData()
                .doOnSuccess(kindsData -> {
                    Timber.tag(TAG).d("@@@kind******size:" + kindsData.getKinds().size());
                    this.kindsData = kindsData;
                    updateAllAsync(this.kindsData);
                    if (kindsData.getKinds().isEmpty()) {
                        Timber.tag(TAG).e("findAllFromRemote failed kindsData.getKinds is empty");
                        findAllFromLocal();
                    }
                })
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                    Timber.tag(TAG).e("findAllFromRemote failed");
                    if (orma.relationOfKinds().isEmpty()) {
                        findAllFromAssets();
                    } else {
                        findAllFromLocal();
                    }
                })
                .doFinally(() -> {
                    Timber.tag(TAG).e("findAllFromRemote doFinally");
                    if (orma.relationOfKinds().isEmpty()) {
                        findAllFromAssets();
                    } else {
                        findAllFromLocal();
                    }
                });
    }

    private Single<KindsData> findAllFromLocal() {
        Timber.tag(TAG).d("findAllFromLocal start");
        return orma.relationOfKinds().selector().executeAsObservable().toList()
                .flatMap(kindsList -> {
                    Timber.tag(TAG).e("findAllFromLocal loaded **kindList size:" + kindsList.size());
                    if (kindsList.size() == 0) {
                        Timber.tag(TAG).e("findAllFromLocal data empty");
                        return findAllFromAssets();
                    } else {
                        Timber.tag(TAG).e("findAllFromLocal loaded kindList size:" + kindsList.size());
                        this.kindsData.setKinds(kindsList);
                        updateAllAsync(this.kindsData);
                        return Single.create(emitter -> emitter.onSuccess(this.kindsData));
                    }
                });
    }

    private Single<KindsData> findAllFromAssets() {
        Timber.tag(TAG).d("findAllFromAssets start");
        final String json = resourceResolver.loadJSONFromAsset(resourceResolver.getString(R.string.kinds_file));
        final Gson gson = new Gson();
        this.kindsData = gson.fromJson(json, KindsData.class);
        updateAllAsync(this.kindsData);
        return Single.create(emitter -> emitter.onSuccess(this.kindsData));
    }

    public Single<List<Kinds>> findFromLocal(@IntRange(from = 1, to = 6) int typeId) {
        Timber.tag(TAG).d("findFromLocal start type:" + typeId);
        return orma.relationOfKinds().selector().type_idEq(typeId).executeAsObservable().toList()
                .flatMap(kindsList -> Single.create(emitter -> emitter.onSuccess(kindsList)));
    }

    private void updateAllAsync(@NonNull final KindsData kindsData) {
        orma.transactionAsCompletable(() -> {
            orma.relationOfKinds().deleter().execute();
            kindsData.getKinds().forEach(orma.relationOfKinds()::upsert);
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
