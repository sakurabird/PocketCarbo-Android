package com.sakurafish.pockettoushituryou.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.util.AlarmUtils;

public class PackageReplacedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if ((intent == null) || !intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            return;
        }

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preference.getBoolean(context.getString(R.string.PREF_NOTIFICATION), true)) {
            return;
        }

        AlarmUtils.unregisterAlarm(context);
        AlarmUtils.registerAlarm(context);
    }
}
