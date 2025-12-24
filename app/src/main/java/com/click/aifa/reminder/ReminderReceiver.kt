package com.click.aifa.reminder

import android.app.*
import android.content.*
import androidx.core.app.NotificationCompat
import com.click.aifa.R
import java.util.*

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val title = intent.getStringExtra("title") ?: "Reminder"
        val message = intent.getStringExtra("message") ?: ""
        val repeatMonthly = intent.getBooleanExtra("repeatMonthly", false)
        val triggerTime = intent.getLongExtra("triggerTime", 0L)

        // 🔔 Show notification
        val notification = NotificationCompat.Builder(context, "REMINDER_CHANNEL")
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)

        // 🔁 Schedule next month if enabled
        if (repeatMonthly) {
            val cal = Calendar.getInstance().apply {
                timeInMillis = triggerTime
                add(Calendar.MONTH, 1)
            }

            ReminderScheduler.schedule(
                context,
                cal.timeInMillis,
                title,
                message,
                true
            )
        }
    }
}
