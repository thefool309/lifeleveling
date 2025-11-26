package com.lifeleveling.app.services

import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lifeleveling.app.R
import com.lifeleveling.app.data.FirestoreRepository
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.runBlocking

/**
 * A service for handling Firebase Cloud Messaging interactions will handle receiving the messages and sending the notifications
 * Override base class methods to handle any events required by the application. All methods are invoked on a background thread,
 * and may be called when the app is in the background or not open
 * @see FirebaseMessagingService
 * @see android.app.Service
 * @see NotificationCompat
 * @author thefool309
 */
class LLFirebaseMessagingService(val logger: ILogger) : FirebaseMessagingService() {

    val CHANNEL_ID = "com.lifeleveling.app.FirebaseMessagingService"
    val TAG = "FirebaseMessagingService"

    val repo = FirestoreRepository()


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
            // create the NotificationCompat.Builder
            val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
        }
        // Check if message contains a notification payload.
        message.notification?.let {
            logger.d(TAG, "Message Notification Body: ${it.body}")
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
        // TODO: Implement this method to send token to your app server.
        runBlocking {   repo.setFirebaseToken(token, logger) }
        logger.d(TAG, "sendRegistrationTokenToServer($token)")


    }
}