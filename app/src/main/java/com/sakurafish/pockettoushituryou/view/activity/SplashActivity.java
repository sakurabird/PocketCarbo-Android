package com.sakurafish.pockettoushituryou.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.repository.FoodsRepository;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private static final int MINIMUM_LOADING_TIME = 1500;

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

        // TODO 後で消す
        new Handler().postDelayed(() -> {
            startActivity(MainActivity.createIntent(SplashActivity.this));
           SplashActivity.this.finish();
        }, MINIMUM_LOADING_TIME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.dispose();
    }

    private void loadPocketCarboDataForCache() {
        // TODO 後でDBにキャッシュするよう実装する
//        Disposable disposable = Single.zip(sessionsRepository.findAll(Locale.getDefault()),
//                mySessionsRepository.findAll(),
//                Single.timer(MINIMUM_LOADING_TIME, TimeUnit.MILLISECONDS),
//                (sessions, mySessions, obj) -> Observable.empty())
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
