package com.sakurafish.pockettoushituryou.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.shared.AlarmUtils;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            return;
        }

        // 通知が設定画面から許可されていたらアラームセット
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preference.getBoolean(context.getString(R.string.PREF_NOTIFICATION), true)) {
            return;
        }

        AlarmUtils.unregisterAlarm(context);
        AlarmUtils.registerAlarm(context);
    }
}
