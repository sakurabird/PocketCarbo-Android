package com.sakurafish.pockettoushituryou.view.helper;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.sakurafish.pockettoushituryou.R;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AdsHelper {

    private static final String TAG = AdsHelper.class.getSimpleName();

    private final Context context;

    @Inject
    public AdsHelper(Context context) {
        this.context = context;
    }

    public AdRequest getAdRequest() {
        String[] ids = context.getResources().getStringArray(R.array.admob_test_device);
        AdRequest.Builder builder = new AdRequest.Builder();
        for (String id : ids) {
            builder.addTestDevice(id);
        }
        return builder.build();
    }
}
