package com.sakurafish.pockettoushituryou.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Utils {

    private final Context context;

    @Inject
    public Utils(Context context) {
        this.context = context;
    }

    /**
     * ネットワーク接続チェック
     *
     * @return
     */
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            return cm.getActiveNetworkInfo().isConnected();
        }
        return false;
    }
}
