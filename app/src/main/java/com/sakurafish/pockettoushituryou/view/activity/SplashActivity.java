package com.sakurafish.pockettoushituryou.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.sakurafish.pockettoushituryou.R;
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
        DataBindingUtil.setContentView(this, R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById(android.R.id.content).setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
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

    private void loadPocketCarboDataForCache() {
        if (!utils.isConnected()) {
            findAllDataFromLocal();
            return;
        }

        // Check new data
        Disposable disposable = foodsRepository.receiveDataVersion()
                .flatMap(dataVersion -> {
                    // get new data from remote
                    if (dataVersion != null && dataVersion.version > pref.getPrefInt(getString(R.string.PREF_DATA_VERSION))) {
                        return foodsRepository.findAllFromRemote()
                                .doOnSuccess(foodsData -> {
                                    pref.setPref(getString(R.string.PREF_DATA_VERSION), dataVersion.version);
                                });
                    } else {
                        return foodsRepository.findAllFromLocal();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    startNextActivity();
                })
                .subscribe(observable -> Timber.tag(TAG).d("Succeeded in loading foods."),
                        throwable -> Timber.tag(TAG).e(throwable, "Failed to load foods."));
        compositeDisposable.add(disposable);
    }

    private void findAllDataFromLocal() {
        Disposable disposable = Single.zip(foodsRepository.findAllFromLocal(),
                Single.timer(MINIMUM_LOADING_TIME, TimeUnit.MILLISECONDS),
                (foodsData, obj) -> Observable.empty())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    startNextActivity();
                })
                .subscribe(observable -> Timber.tag(TAG).d("Succeeded in loading sessions."),
                        throwable -> Timber.tag(TAG).e(throwable, "Failed to load sessions."));
        compositeDisposable.add(disposable);
    }

    private void startNextActivity() {
        if (isFinishing()) return;
        startActivity(MainActivity.createIntent(SplashActivity.this));
        SplashActivity.this.finish();
    }
}
