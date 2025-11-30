package com.lifeleveling.app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lifeleveling.app.MainActivity
import com.lifeleveling.app.R
import com.lifeleveling.app.data.FirestoreRepository
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.runBlocking

/**
 * A service for handling Firebase Cloud Messaging interactions will handle receiving the messages and sending the notifications
 * Override base class methods to handle any events required by the application. All methods are invoked on a background thread,
 * and may be called when the app is in the background or not open
 * @property CHANNEL_ID and arbitrary channelID I selected for the notification channel
 * @property TAG the tag for the logger to point you back to this file
 * @property repo a `FirestoreRepository()` object for saving FcmTokens
 * @see FirebaseMessagingService
 * @see android.app.Service
 * @see NotificationCompat
 * @author thefool309
 */
class LLFirebaseMessagingService() : FirebaseMessagingService() {
    var logger: ILogger = AndroidLogger()
companion object {
    const val CHANNEL_ID = "LifeLevelingFirebaseMessagingService"
    const val TAG = "FirebaseMessagingService"
}
    val repo = FirestoreRepository()

    constructor(logger: ILogger) : this() {
        this.logger = logger
    }


    /**
     * handles receiving the message from firebase, and creating and displaying the notification
     * @param message The `RemoteMessage` from firebase
     * @see FirebaseMessagingService.onMessageReceived
     * @see android.app.Service
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if(com.lifeleveling.app.BuildConfig.DEBUG) {
            logger.d(TAG, "Message ID: " + message.messageId)
        }
        if (message.data.isNotEmpty()) {

            if(com.lifeleveling.app.BuildConfig.DEBUG) {
                logger.d(TAG, "Message data payload: " + message.data)
            }
        }
        // Check if message contains a notification payload.
        message.notification?.let {
            logger.d(TAG, "Message Notification Body: ${it.body}")
            it.body?.let { body: String -> sendNotification(body) }
        }

    }
    override fun onNewToken(token: String) {
        logger.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }
    private fun sendRegistrationToServer(token: String?) {
        // send token to the Firestore.
        runBlocking {   repo.setFirebaseToken(token, logger) }
        logger.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    /**
     * the function that carries out the logic  of creating the notification and sending it.
     *
     */

    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        
        val channelId = CHANNEL_ID
        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentText(messageBody)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setContentTitle() //must be placed at the end since we overrode it's local functionality

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, channelId, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }
}

/**
 * extension function for NotificationCompat.Builder that gives us a default ContentTitle for notifications.
 * must be placed last on the NotificationCompat.Builder() if using the default,
 * so we should make it a habit to always place anything we extend last
 * @see LLFirebaseMessagingService.sendNotification
 */
private fun NotificationCompat.Builder.setContentTitle() : NotificationCompat.Builder {
    setContentTitle("Life Leveling")
    return this
}

