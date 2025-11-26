package com.lifeleveling.app.services

import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lifeleveling.app.R
import com.lifeleveling.app.util.ILogger

/**
 *
 * @param message The `RemoteMessage` from firebase
 * @see FirebaseMessagingService.onMessageReceived
 * @see android.app.Service
 */
class LLFirebaseMessagingService(val logger: ILogger) : FirebaseMessagingService() {

    val CHANNEL_ID = "com.lifeleveling.app.FirebaseMessagingService"
    val TAG = "FirebaseMessagingService"



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
    }
}