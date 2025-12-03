package com.lifeleveling.app.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.lifeleveling.app.MainActivity
import com.lifeleveling.app.services.LLFirebaseMessagingService.Companion.NOTIFICATION_ID
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.ILogger

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

    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra("title") ?: "Reminder"
        val message = intent?.getStringExtra("message") ?: "Reminder"
        // defines what activity opens when the user taps the notification
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val builder: NotificationCompat.Builder
        if (context != null) {
            builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setWhen(System.currentTimeMillis())

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
        else {
            logger.e(TAG, "context is null!")
        }
    }
}