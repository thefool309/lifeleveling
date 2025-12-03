package com.lifeleveling.app.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import com.lifeleveling.app.data.Reminders


class ReminderScheduler(private val context: Context) {
    /**
     * Schedule a reminder. Takes in the reminder and handles the rest of the legwork for you
     * can be used to Schedule a new reminder, or to
     * @param reminder the reminder to have a notification scheduled for it
     */
    fun schedule(reminder: Reminders) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", reminder.title)
            putExtra("reminderId", reminder.reminderId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.reminderId.hashCode(),
            intent,
             PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // an example of how to set it to go off at a specific date.
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_MONTH, 1)
        }

        val triggerAt = calendar.timeInMillis

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            // triggerAtMillis uses the same time format as Java/Kotlin timestamps us everywhere.
            // Millis since the Unix epoch (Unix Standard Time in milliseconds)
            System.currentTimeMillis() + 10_000, // TODO: find out how to get the timestamps we need to remindAt
            pendingIntent
        )
    }

    fun cancel(reminder: Reminders) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

    }
}