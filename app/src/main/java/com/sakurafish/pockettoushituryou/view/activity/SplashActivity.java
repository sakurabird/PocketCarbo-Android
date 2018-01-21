package com.sakurafish.pockettoushituryou.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.model.DataVersion;
import com.sakurafish.pockettoushituryou.model.FoodsData;
import com.sakurafish.pockettoushituryou.pref.Pref;
import com.sakurafish.pockettoushituryou.repository.FoodsRepository;
import com.sakurafish.pockettoushituryou.util.Utils;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private static final int MINIMUM_LOADING_TIME = 1000;

    private int retrieved_data_verion;

    @Inject
    FirebaseAnalytics firebaseAnalytics;
    @Inject
    Pref pref;
    @Inject
    Utils utils;
    @Inject
    CompositeDisposable compositeDisposable;
    @Inject
    FoodsRepository foodsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, new Bundle());
        retrieved_data_verion = 0;

        DataBindingUtil.setContentView(this, R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById(android.R.id.content).setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setLaunchCount();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadPocketCarboDataForCache();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.dispose();
    }

    private void setLaunchCount() {
        if (pref == null) return;
        int launchCount = pref.getPrefInt(getString(R.string.PREF_LAUNCH_COUNT));
        pref.setPref(getString(R.string.PREF_LAUNCH_COUNT), ++launchCount);
    }

    private void loadPocketCarboDataForCache() {
        if (!utils.isConnected()) {
            findAllDataFromLocal();
            return;
        }

        Disposable disposable = foodsRepository.receiveDataVersion()
                .flatMap(dataVersion -> {
                    Timber.tag(TAG).d("receiveDataVersion");
                    retrieved_data_verion = dataVersion.version;
                    if (isNeedUpdateData(dataVersion)) {
                        return foodsRepository.findAllFromRemote();
                    }
                    return Single.create(emitter -> emitter.onSuccess(new FoodsData()));
                })
                .flatMap(foodsData -> {
                    Timber.tag(TAG).d("result foodsData check");
                    if (foodsData != null
                            && foodsData.getFoods() != null && foodsData.getFoods().size() > 0
                            && foodsData.getKinds() != null && foodsData.getKinds().size() > 0) {
                        Timber.tag(TAG).d("Succeeded in loading foods from remote.");
                        setPrefUpdated();
                    }
                    return Single.create(emitter -> emitter.onSuccess(retrieved_data_verion));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> findAllDataFromLocal())
                .subscribe(o -> Timber.tag(TAG).d("finish."),
                        throwable -> Timber.tag(TAG).e(throwable, "Failed to load foods."));

        compositeDisposable.add(disposable);
    }

    private void findAllDataFromLocal() {
        if (retrieved_data_verion > 0 && retrieved_data_verion == pref.getPrefInt(getString(R.string.PREF_DATA_VERSION))) {
            Timber.tag(TAG).d("DBは最新に保たれている");
            startNextActivity();
            return;
        }

        Timber.tag(TAG).d("findAllDataFromLocal");
        Disposable disposable = Single.zip(foodsRepository.findAllFromLocal(),
                Single.timer(MINIMUM_LOADING_TIME, TimeUnit.MILLISECONDS),
                (foodsData, obj) -> Observable.empty())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    startNextActivity();
                })
                .subscribe(observable -> Timber.tag(TAG).d("Succeeded in loading foods from local."),
                        throwable -> Timber.tag(TAG).e(throwable, "Failed to load foods from local."));
        compositeDisposable.add(disposable);
    }

    private void startNextActivity() {
        if (isFinishing()) return;
        startActivity(MainActivity.createIntent(SplashActivity.this));
        SplashActivity.this.finish();
    }

    private boolean isNeedUpdateData(DataVersion dataVersion) {
        Timber.tag(TAG).d("Check new data pref:" + pref.getPrefInt(getString(R.string.PREF_DATA_VERSION)) + " server:" + dataVersion.version);
        if (dataVersion != null && dataVersion.version > pref.getPrefInt(getString(R.string.PREF_DATA_VERSION))) {
            Timber.tag(TAG).d("New data found.");
            return true;
        }

        // バグ対応 強制的にアップデート
        if (dataVersion != null
                && utils.getVersionCode() <= 5 && !pref.getPrefBool("DATA_UPDATED_1.2", false)) {
            Timber.tag(TAG).d("(ver1.2) data wasn't updated.");
            return true;
        }
        Timber.tag(TAG).d("currently data is up-to-date.");
        return false;
    }

    private void setPrefUpdated() {
        pref.setPref(getString(R.string.PREF_DATA_VERSION), retrieved_data_verion);
        if (utils.getVersionCode() <= 5) {
            pref.setPref("DATA_UPDATED_1.2", true);
        }
    }
}
