package com.lifeleveling.app.services

import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lifeleveling.app.R
class LLFirebaseMessagingService : FirebaseMessagingService() {

    val CHANNEL_ID = "com.lifeleveling.app.FirebaseMessagingService"


    /**
     * handles recieving the message from firebase, and creating and displaying the notification
     * @param message The `RemoteMessage` from firebase
     * @see FirebaseMessagingService.onMessageReceived
     * @see android.app.Service
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // create the NotificationCompat.Builder
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
    }

    fun onNewToken() {

    }
}