package com.click.aifa.reminder

import android.app.*
import android.content.Context
import android.content.Intent

object ReminderScheduler {

    fun schedule(
        context: Context,
        triggerTime: Long,
        title: String,
        message: String,
        repeatMonthly: Boolean
    ) {

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("repeatMonthly", repeatMonthly)
            putExtra("triggerTime", triggerTime)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            triggerTime.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
}
