package com.sakurafish.pockettoushituryou.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.support.annotation.NonNull;
import android.view.View;

import com.sakurafish.pockettoushituryou.view.helper.ShowcaseHelper;

import javax.inject.Inject;

public class HelpViewModel extends BaseObservable implements ViewModel {
    final static String TAG = HelpViewModel.class.getSimpleName();

    private Context context;
    private ShowcaseHelper showcaseHelper;

    private String url;

    @Inject
    HelpViewModel(@NonNull Context context, @NonNull ShowcaseHelper showcaseHelper) {
        this.context = context;
        this.showcaseHelper = showcaseHelper;

        // assetsに置いてあるhelp.html
        setUrl("file:///android_asset/www/help.html");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    @Override
    public void destroy() {
        // Nothing to do
    }

    public void onClickTurorialResetButton(View view) {
        showcaseHelper.resetShowCase();
    }
}
