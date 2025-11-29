package com.lifeleveling.app.services

import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lifeleveling.app.R
import com.lifeleveling.app.data.FirestoreRepository
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.runBlocking

/**
 * A service for handling Firebase Cloud Messaging interactions will handle receiving the messages and sending the notifications
 * Override base class methods to handle any events required by the application. All methods are invoked on a background thread,
 * and may be called when the app is in the background or not open
 * @property channelID and arbitrary channelID I selected for the notification channel
 * @property logTag the tag for the logger to point you back to this file
 * @property repo a `FirestoreRepository()` object for saving FcmTokens
 * @see FirebaseMessagingService
 * @see android.app.Service
 * @see NotificationCompat
 * @author thefool309
 */
class LLFirebaseMessagingService() : FirebaseMessagingService() {


    var logger: ILogger = AndroidLogger()
    val channelID = "LifeLevelingFirebaseMessagingService"
    val logTag = "FirebaseMessagingService"

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
            logger.d(logTag, "Message ID: " + message.messageId)
        }
        if (message.data.isNotEmpty()) {

            if(com.lifeleveling.app.BuildConfig.DEBUG) {
                logger.d(logTag, "Message data payload: " + message.data)
            }
            // create the NotificationCompat.Builder
            val builder = NotificationCompat.Builder(applicationContext, channelID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
        }
        // Check if message contains a notification payload.
        message.notification?.let {
            logger.d(logTag, "Message Notification Body: ${it.body}")
        }
    }
    override fun onNewToken(token: String) {
        logger.d(logTag, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }
    private fun sendRegistrationToServer(token: String?) {
        // send token to the Firestore.
        runBlocking {   repo.setFirebaseToken(token, logger) }
        logger.d(logTag, "sendRegistrationTokenToServer($token)")
    }
}