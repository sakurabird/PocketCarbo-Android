package com.sakurafish.pockettoushituryou.repository;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.util.Utils;

import org.jsoup.Jsoup;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * GooglePlayのサイトを参照してリリースされているバージョンと端末にインストールされているバージョンが違ったらアップデートを促す
 */
public class ReleasedVersionRepository {
    private final static String TAG = ReleasedVersionRepository.class.getSimpleName();

    private AppCompatActivity activity;
    private Utils utils;

    @Inject
    ReleasedVersionRepository(AppCompatActivity activity, Utils utils) {
        this.activity = activity;
        this.utils = utils;
    }

    public void checkReleasedVersion() {
        Single.create((SingleOnSubscribe<String>) emitter -> {
            try {
                String version = Jsoup.connect(activity.getString(R.string.app_url))
                        .timeout(30000)
                        .userAgent("Mozilla") //http://stackoverflow.com/a/36780250/2845202
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                emitter.onSuccess(version);
            } catch (Exception ex) {
                emitter.onError(ex);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<String>() {
                    @Override
                    public void onSuccess(String version) {
                         String thisVersion = utils.getVersionName();
                        if (TextUtils.isEmpty(thisVersion)) return;
                        if (!version.equals(thisVersion)) {
                            Timber.tag(TAG).d("different version! received version: %1s this app's version:%2s"
                                    , version, thisVersion);
                            pleaseUpdate();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.tag(TAG).e(e);
                    }
                });
    }

    private void pleaseUpdate() {
        //アップデートへ誘導する
        new MaterialDialog.Builder(activity)
                .theme(Theme.LIGHT)
                .title(activity.getString(R.string.ask_update_title))
                .content(activity.getString(R.string.ask_update_message))
                .positiveText(activity.getString(android.R.string.ok))
                .negativeText(activity.getString(android.R.string.cancel))
                .onPositive((dialog, which) -> {
                    // Google Play
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.app_url))));
                    dialog.dismiss();
                })
                .show();
    }
}