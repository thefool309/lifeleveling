package com.lifeleveling.app.services.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.lifeleveling.app.BuildConfig
import com.lifeleveling.app.data.Reminders
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.ILogger


/**
 * and exception for if the DueDate comes back as null
 */
class ReminderDueDateIsNullException(message: String) : Exception(message)
/**
 * a class for taking the `Reminders` data and scheduling notifications for it.
 * @param context the application context
 * @param logger an interface typed object for modifying the logging behavior in this class. Defaults to an `AndroidLogger`
 * @see ILogger
 * @see AndroidLogger
 */
class ReminderScheduler(private val context: Context, val logger: ILogger = AndroidLogger()) {

    companion object {
        const val TAG = "ReminderScheduler"
    }
    /**
     * Schedule a reminder. Takes in the reminder and handles the rest of the legwork for you
     * can be used to Schedule a new reminder, or to
     * @param reminder the reminder to have a notification scheduled for it
     */
    // @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun schedule(reminder: Reminders) {
        val intent: Intent
        if(reminder.dueAt != null) {
            intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("TITLE", reminder.title)
                putExtra("ID", reminder.reminderId)
                putExtra("DUE_AT", reminder.dueAt.toDate().time)
                putExtra("IS_DAILY", reminder.isDaily)
                putExtra("TIMES_PER_DAY", reminder.timesPerDay)
                putExtra("TIMES_PER_MONTH", reminder.timesPerMonth)
            }
        }
        else {
            logger.d(TAG, "Reminder schedule failed for ${reminder.title}")
            throw ReminderDueDateIsNullException("Reminder schedule failed for ${reminder.title} : ${reminder.reminderId}")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.reminderId.hashCode(),
            intent,
             PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (BuildConfig.DEBUG) { logger.d(TAG, "Reminder intent created for ${reminder.title}") }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // an example of how to set it to go off at a specific date.
        // uncomment this block and change triggerAtMillis parameter
        // in setExactAndAllowWhileIdle to use the val triggerAt

//        val calendar = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, 20)
//            set(Calendar.MINUTE, 30)
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//            add(Calendar.DAY_OF_MONTH, 1)
//        }
//
//        val triggerAt = /*calendar.timeInMillis*/ // uncomment this for the calendar defined time
//            System.currentTimeMillis() + 10_000 // uncomment this for ten seconds from now


            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                // triggerAtMillis uses the same time format as Java/Kotlin timestamps us everywhere.
                // Millis since the Unix epoch (Unix Standard Time in milliseconds)
                reminder.dueAt.toDate().time,
                pendingIntent
            )
            if (BuildConfig.DEBUG) {
                logger.d(TAG, "Reminder scheduled to ${reminder.title} at ${reminder.dueAt.toDate()}")
                logger.d(TAG, "Time since Unix Epoch: ${reminder.dueAt.toDate().time}")
            }


    }

    /**
     * cancel a reminder. Should cancel any scheduled notifications for a specific reminder.
     * @param reminder the reminder to cancel all notifications for
     */
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

        if(BuildConfig.DEBUG) {
            logger.d(
                TAG,
                "Reminder cancelled: reminderId ${reminder.reminderId}: reminder name ${reminder.title}"
            )
        }
    }
}