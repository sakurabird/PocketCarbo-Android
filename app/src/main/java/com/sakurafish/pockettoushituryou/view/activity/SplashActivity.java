package com.sakurafish.pockettoushituryou.view.activity;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.pref.Pref;
import com.sakurafish.pockettoushituryou.repository.FoodsRepository;

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

    FirebaseAnalytics firebaseAnalytics;

    @Inject
    Pref pref;
    @Inject
    CompositeDisposable compositeDisposable;
    @Inject
    FoodsRepository foodsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, new Bundle());

        DataBindingUtil.setContentView(this, R.layout.activity_splash);

        findViewById(android.R.id.content).setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        setLaunchCount();

        findAllDataFromAssets();
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

    private void findAllDataFromAssets() {
        int dataVersion = getResources().getInteger(R.integer.data_version);
        if (dataVersion == pref.getPrefInt(getString(R.string.PREF_DATA_VERSION))) {
            Timber.tag(TAG).d("DB is maintained latest version:%d", dataVersion);
            Disposable disposable = Observable.interval(MINIMUM_LOADING_TIME, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        startNextActivity();
                    });
            compositeDisposable.add(disposable);
            return;
        }

        Disposable disposable = Single.zip(foodsRepository.findAllFromAssets(),
                Single.timer(MINIMUM_LOADING_TIME, TimeUnit.MILLISECONDS),
                (foodsData, obj) -> Observable.empty())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(this::startNextActivity)
                .subscribe((objectObservable) -> {
                            pref.setPref(getString(R.string.PREF_DATA_VERSION), dataVersion);
                            Timber.tag(TAG).d("Succeeded in loading foods from Assets.");
                        },
                        throwable -> Timber.tag(TAG).e(throwable, "Failed to load foods from Assets."));
        compositeDisposable.add(disposable);
    }

    private void startNextActivity() {
        if (isFinishing()) return;
        startActivity(MainActivity.createIntent(SplashActivity.this));
        SplashActivity.this.finish();
    }
}
