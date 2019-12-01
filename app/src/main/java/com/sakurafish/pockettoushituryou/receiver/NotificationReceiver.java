package com.sakurafish.pockettoushituryou.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.view.activity.MainActivity;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();

    public static final String NOTIFICATION_CHANNEL_ID = "pocketcarbo_channel_id_01";
    public static final int NOTIFICATION_ID = 33;

    public static Intent createIntent(Context context) {
        return new Intent(context, NotificationReceiver.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 通知が設定画面から許可されていなかったら通知しない
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preference.getBoolean(context.getString(R.string.PREF_NOTIFICATION), true)) {
            return;
        }

        final int lastNo = preference.getInt(context.getString(R.string.PREF_APP_MESSAGE_NO), 0); // 通知済みメッセージ番号
        int messageNo = context.getResources().getInteger(R.integer.APP_MESSAGE_NO); // consts.xmlで定義されている数字

        // 既に通知済みのメッセージなら通知しない
        if (messageNo <= lastNo) {
            return;
        }
        showNotification(context, preference, messageNo);
    }

    private void showNotification(Context context, SharedPreferences preference, int messageNo) {

        String title = context.getString(R.string.notification_title);
        String text = context.getString(R.string.APP_MESSAGE_TEXT);

        Intent openIntent = MainActivity.Companion.createIntent(context);
        openIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription(context.getString(R.string.notification_channel_description));
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        Notification mNotification = builder
                .setTicker(title)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setWhen(System.currentTimeMillis())
                .build();

        notificationManager.notify(NOTIFICATION_ID, mNotification);

        final SharedPreferences.Editor editor = preference.edit();
        editor.putInt(context.getString(R.string.PREF_APP_MESSAGE_NO), messageNo);
        editor.apply();
    }
}
