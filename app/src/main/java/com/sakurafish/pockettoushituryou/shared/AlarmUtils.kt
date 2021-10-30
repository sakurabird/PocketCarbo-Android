package com.sakurafish.pockettoushituryou.shared

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import com.sakurafish.pockettoushituryou.receiver.NotificationReceiver
import java.util.*

object AlarmUtils {
    private val TAG = AlarmUtils::class.java.simpleName

    @JvmStatic
    fun registerAlarm(context: Context) {
        // Wake Up every day at PM12:00 for app message
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // every day at scheduled time
        val calendar = Calendar.getInstance()
        // if it's after or equal 12 pm schedule for next day
        if (Calendar.getInstance()[Calendar.HOUR_OF_DAY] >= 12) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        calendar[Calendar.HOUR_OF_DAY] = 12
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        val pendingIntent = createAlarmIntent(context)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent
        )
    }

    @JvmStatic
    fun unregisterAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = createAlarmIntent(context)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
    }

    private fun createAlarmIntent(context: Context): PendingIntent {
        val pendingIntent = NotificationReceiver.createIntent(context)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        return PendingIntent.getBroadcast(
            context, NotificationReceiver.NOTIFICATION_ID,
            pendingIntent, flags
        )
    }
}