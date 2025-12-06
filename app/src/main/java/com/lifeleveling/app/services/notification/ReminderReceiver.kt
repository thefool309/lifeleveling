package com.lifeleveling.app.services.notification

import android.Manifest
import android.R
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.lifeleveling.app.BuildConfig
import com.lifeleveling.app.MainActivity
import com.lifeleveling.app.services.FirebaseCloudMessaging.LLFirebaseMessagingService
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.ILogger
import java.util.Calendar
import java.util.Date


/**
 * an exception for if the Next Occurrence calculation fails
 */
class NextOccurrenceCalcException(message: String) : Exception(message)
/**
 * A `BroadcastReceiver` that triggers scheduled reminder notifications.
 * When the user-defined reminder time occurs, this receiver builds and displays a local notification.
 * The notification’s intent typically opens the app or a specific screen related to the
 * reminder when tapped.
 */

class ReminderReceiver(val logger: ILogger = AndroidLogger()) : BroadcastReceiver() {

    companion object {
        const val TAG = "ReminderReceiver"
        const val CHANNEL_ID = "channel_id_reminder"
    }

    /**
     * An override function that modifies the behavior for what happens when a broadcast is received
     * @param context the application context
     * @param intent the notifications intent.
     */
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra("TITLE") ?: "Reminder"
        val message = intent?.getStringExtra("message") ?: "Reminder"
        val isDaily = intent?.getBooleanExtra("IS_DAILY", false) ?: false
        val timesPerDay = intent?.getLongExtra("TIMES_PER_DAY", 0) ?: 0
        val timesPerMonth = intent?.getLongExtra("TIMES_PER_MONTH", 0) ?: 0
        val dueAt = intent?.getLongExtra("DUE_AT", 0) ?: 0

        val nextOccurrence = calculateNextOccurrence(dueAt, isDaily, timesPerDay, timesPerMonth)

        if(nextOccurrence == -1L) {
            throw NextOccurrenceCalcException("No next occurrence found")
        }

        // defines what activity opens when the user taps the notification
        val onClickIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, onClickIntent, PendingIntent.FLAG_IMMUTABLE)
        val builder: NotificationCompat.Builder
        if (context != null) {
            // if context isn't null, then build a notification
            builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_dialog_email)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setWhen(System.currentTimeMillis())

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(LLFirebaseMessagingService.Companion.NOTIFICATION_ID, builder.build())

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                // triggerAtMillis uses the same time format as Java/Kotlin timestamps us everywhere.
                // Millis since the Unix epoch (Unix Standard Time in milliseconds)
                dueAt + nextOccurrence,
                pendingIntent
            )
            if (BuildConfig.DEBUG) {
                logger.d(ReminderScheduler.Companion.TAG, "Reminder scheduled to $title at ${Date(dueAt)}")
                logger.d(ReminderScheduler.Companion.TAG, "Time since Unix Epoch: $dueAt")
            }
        }
        else {
            // otherwise inform the developers that the context is somehow null and this function is being called outside the context
            // this case should never be hit but who knows. Better to have error checking and not need it than
            // need error checking and not have it ¯\_(OwO)_/¯
            logger.e(TAG, "context is null!")
        }
    }
    fun calculateNextOccurrence(
        currentDueAtMillis: Long,
        isDaily: Boolean,
        timesPerDay: Long,
        timesPerMonth: Long
    ) : Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentDueAtMillis
        val interval: Long
        if(timesPerDay > 0 || timesPerMonth > 0) {
            if (isDaily) {
                interval = AlarmManager.INTERVAL_DAY / timesPerDay
                calendar.add(Calendar.MILLISECOND, interval.toInt())
            }
            else {
                interval = when (timesPerMonth.toInt()) {
                    10 -> 3
                    9 -> 3
                    8 -> 3
                    7 -> 4
                    6 -> 5
                    5 -> 6
                    4 -> 7
                    3 -> 10
                    2 -> 14
                    1 -> 30
                    else -> 3 // default to once every 3 days if set any higher.
                             // If it's higher than this the user will pick daily
                }
                calendar.add(Calendar.DAY_OF_MONTH, interval.toInt())
            }
            return calendar.timeInMillis
        }
        else {
            logger.e(TAG, "current due at $currentDueAtMillis at ${calendar.timeInMillis}")
            logger.e(TAG, "either timesPerDay: $timesPerDay, timesPerMonth: $timesPerMonth")
            return -1
        }

    }
}

