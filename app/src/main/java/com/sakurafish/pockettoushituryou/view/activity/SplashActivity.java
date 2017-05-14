package com.sakurafish.pockettoushituryou.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.repository.FoodsRepository;
import com.sakurafish.pockettoushituryou.repository.KindsRepository;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private static final int MINIMUM_LOADING_TIME = 1500;

    @Inject
    CompositeDisposable compositeDisposable;

    @Inject
    KindsRepository kindsRepository;

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
        Timber.tag(TAG).d("test1");
        kindsRepository.findAll()
                .subscribeOn(Schedulers.io())
                .flatMap(kindsData -> {
                    Timber.tag(TAG).d("test2");
                    return foodsRepository.findAll();
                })
                .doOnError(throwable -> {
                    Timber.tag(TAG).d("test3");
                    foodsRepository.findAll();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (isFinishing()) return;
                    startActivity(MainActivity.createIntent(SplashActivity.this));
                    SplashActivity.this.finish();
                })
                .subscribe(observable -> Timber.tag(TAG).d("Succeeded in loading data."),
                        throwable -> Timber.tag(TAG).e(throwable, "Failed to load data."));


//        Disposable disposable = Single.zip(kindsRepository.findAll(),
//                foodsRepository.findAll(),
//                Single.timer(MINIMUM_LOADING_TIME, TimeUnit.MILLISECONDS),
//                (kindsData, foodsData, obj) -> Observable.empty())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doFinally(() -> {
//                    if (isFinishing()) return;
//                    startActivity(MainActivity.createIntent(SplashActivity.this));
//                    SplashActivity.this.finish();
//                })
//                .subscribe(observable -> Timber.tag(TAG).d("Succeeded in loading sessions."),
//                        throwable -> Timber.tag(TAG).e(throwable, "Failed to load sessions."));
//        compositeDisposable.add(disposable);
    }
}
