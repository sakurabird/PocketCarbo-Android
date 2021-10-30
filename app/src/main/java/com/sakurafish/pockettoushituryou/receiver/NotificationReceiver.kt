package com.sakurafish.pockettoushituryou.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.ui.main.MainActivity

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 通知が設定画面から許可されていなかったら通知しない
        val preference = PreferenceManager.getDefaultSharedPreferences(context)
        if (!preference.getBoolean(context.getString(R.string.PREF_NOTIFICATION), true)) {
            return
        }
        val lastNo =
            preference.getInt(context.getString(R.string.PREF_APP_MESSAGE_NO), 0) // 通知済みメッセージ番号
        val messageNo =
            context.resources.getInteger(R.integer.APP_MESSAGE_NO) // consts.xmlで定義されている数字

        // 既に通知済みのメッセージなら通知しない
        if (messageNo <= lastNo) {
            return
        }
        showNotification(context, preference, messageNo)
    }

    private fun showNotification(context: Context, preference: SharedPreferences, messageNo: Int) {
        val title = context.getString(R.string.notification_title)
        val text = context.getString(R.string.APP_MESSAGE_TEXT)
        val openIntent = MainActivity.createIntent(context)
        openIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent =
            PendingIntent.getActivity(context, 0, openIntent, flags)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description =
                context.getString(R.string.notification_channel_description)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        val mNotification = builder
            .setTicker(title)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(text)
            )
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .build()
        notificationManager.notify(NOTIFICATION_ID, mNotification)
        val editor = preference.edit()
        editor.putInt(context.getString(R.string.PREF_APP_MESSAGE_NO), messageNo)
        editor.apply()
    }

    companion object {
        private val TAG = NotificationReceiver::class.java.simpleName
        const val NOTIFICATION_CHANNEL_ID = "pocketcarbo_channel_id_01"
        const val NOTIFICATION_ID = 33

        @JvmStatic
        fun createIntent(context: Context?): Intent {
            return Intent(context, NotificationReceiver::class.java)
        }
    }
}