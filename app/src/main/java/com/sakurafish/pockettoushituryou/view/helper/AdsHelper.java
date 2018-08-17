package com.sakurafish.pockettoushituryou.view.helper;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.pref.Pref;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AdsHelper {

    private static final String TAG = AdsHelper.class.getSimpleName();
    // Intent action filter string
    public static final String ACTION_BANNER_CLICK = "ACTION_BANNER_CLICK";
    // Intent extras key string
    public static final String INTENT_EXTRAS_KEY_CLASS = "INTENT_EXTRAS_KEY_CLASS";
    // click interval time millisecond
    public static final long CLICK_DELAY_MILLIS = 1000 * 60 * 60; // 60 min

    private final Context context;
    private final Pref pref;

    @Inject
    public AdsHelper(Context context, Pref pref) {
        this.context = context;
        this.pref = pref;
    }

    public boolean isIntervalOK() {
        // Need 60 min Interval
        long now = System.currentTimeMillis();
        long lastClickTime = getLastClickTimeMillis();
        if (lastClickTime == 0) return true;
        long passedTime = now - lastClickTime;
        return passedTime >= CLICK_DELAY_MILLIS;
    }

    public AdRequest getAdRequest() {
        String[] ids = context.getResources().getStringArray(R.array.admob_test_device);
        AdRequest.Builder builder = new AdRequest.Builder();
        for (String id : ids) {
            builder.addTestDevice(id);
        }
        return builder.build();
    }

    public long getLastClickTimeMillis() {
        return pref.getPrefLong(context.getString(R.string.PREF_LAST_AD_CLICK_TIME), 0);
    }

    public void setLastClickTimeMillis(final long date) {
        pref.setPrefLong(context.getString(R.string.PREF_LAST_AD_CLICK_TIME), date);
    }

    public long getIntervalTimeMillis(final long now, final long lastClickTimeMillis) {
        long intervalTimeMillis = CLICK_DELAY_MILLIS;
        if (lastClickTimeMillis != 0) {
            long passedTimeMillis = now - lastClickTimeMillis;
            intervalTimeMillis = CLICK_DELAY_MILLIS - passedTimeMillis;
            if (intervalTimeMillis > CLICK_DELAY_MILLIS) {
                intervalTimeMillis = CLICK_DELAY_MILLIS;
            }
        }
        return intervalTimeMillis;
    }
}
