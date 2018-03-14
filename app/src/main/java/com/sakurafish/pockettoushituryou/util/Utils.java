package com.sakurafish.pockettoushituryou.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

    /**
     * version codeを取得する
     *
     * @return
     */
    public int getVersionCode() {
        PackageManager manager = context.getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * version nameを取得する
     *
     * @return
     */
    public String getVersionName() {
        PackageManager manager = context.getPackageManager();
        PackageInfo info;
        String version;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;
            // for debug
            if (version.toLowerCase().contains("-debug")) {
                version = version.replace("-debug", "");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return version;
    }
}
