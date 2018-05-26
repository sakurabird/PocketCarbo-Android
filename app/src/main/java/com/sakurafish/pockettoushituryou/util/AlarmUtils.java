package com.sakurafish.pockettoushituryou.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.sakurafish.pockettoushituryou.receiver.NotificationReceiver;

import java.util.Calendar;

public class AlarmUtils {

    private static final String TAG = AlarmUtils.class.getSimpleName();

    public static void registerAlarm(@NonNull Context context) {
        // Wake Up every day at PM12:00 for app message
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        // every day at scheduled time
        Calendar calendar = Calendar.getInstance();
        // if it's after or equal 12 pm schedule for next day
        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 12) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        PendingIntent pendingIntent = createAlarmIntent(context);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void unregisterAlarm(@NonNull Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        PendingIntent pendingIntent = createAlarmIntent(context);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
    }

    private static PendingIntent createAlarmIntent(@NonNull Context context) {
        Intent pendingIntent = NotificationReceiver.createIntent(context);
        return PendingIntent.getBroadcast(context, NotificationReceiver.NOTIFICATION_ID,
                pendingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}