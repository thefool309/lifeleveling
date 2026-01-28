package com.lifeleveling.app.services.FirebaseCloudMessaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lifeleveling.app.BuildConfig
import com.lifeleveling.app.MainActivity
import com.lifeleveling.app.R
import com.lifeleveling.app.auth.AuthModel
import com.lifeleveling.app.data.FirestoreRepository
import com.lifeleveling.app.services.FirebaseCloudMessaging.LLWorker
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.runBlocking

/**
 * A special system managed service for handling Firebase Cloud Messaging interactions. This is created and managed by the OS. This will handle receiving the messages and sending the notifications
 * Override base class methods to handle any events required by the application. All methods are invoked on a background thread,
 * and may be called when the app is in the background or not open
 * @property CHANNEL_ID and arbitrary channelID I selected for the notification channel
 * @property TAG the tag for the logger to point you back to this file
 * @property repo a `FirestoreRepository()` object for saving FcmTokens
 * @see com.google.firebase.messaging.FirebaseMessagingService
 * @see android.app.Service
 * @see androidx.core.app.NotificationCompat
 * @author thefool309
 */
class LLFirebaseMessagingService() : FirebaseMessagingService() {
    var logger: ILogger = AndroidLogger()
companion object {
    const val CHANNEL_ID = "LifeLevelingFirebaseMessagingService" // TODO: this string should be seperated out into an R.string
    const val TAG = "FirebaseMessagingService"
    const val NOTIFICATION_ID = 1   // may be taken out later if we decide to have more than one type of notification
}
    val repo = FirestoreRepository(logger = logger, db = Firebase.firestore)
    val authModel: AuthModel = AuthModel(logger = logger)

    constructor(logger: ILogger) : this() {
        this.logger = logger
    }


    /**
     * Called when a message is received.
     * This should complete within 20 seconds. Taking longer may interfere with your ability to complete your work and may affect pending messages.
     * This is also called when a notification message is received while the app is in the foreground.
     *
     * some sources say 20 and others say 10 seconds. I am opting to roll with the 10 second timeline to be safe
     *
     * handles receiving the message from firebase, and creating and displaying the notification.
     * the "main" function of this service.
     * is an override of the base classes onMessageReceived, and inherits its functionality and triggers
     *
     * @param message The `RemoteMessage` from firebase
     * @see FirebaseMessagingService.onMessageReceived
     * @see android.app.Service
     * @see LLFirebaseMessagingService.sendNotification
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if(BuildConfig.DEBUG) {
            logger.d(TAG, "Message ID: " + message.messageId)
        }
        if (message.data.isNotEmpty()) {

            if(BuildConfig.DEBUG) {
                logger.d(TAG, "Message data payload: " + message.data)
            }
            // this section is for handling any other operations that may need to be carried out upon receiving a notification
            if (false/*check if the process will need a worker thread or not*/) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                // notifications technically fall under this category, but are handled below with message.notification?.let
                handleNow()
            }
        }
        // Check if message contains a notification payload.
        message.notification?.let {
            logger.d(TAG, "Message Notification Body: ${it.body}")
            it.body?.let { body: String -> sendNotification(body) }
        }

    }
    /**
     * Persist token to third-party servers. uses FirestoreRepository.setFirebaseToken to do the leg work of creating the token
     * will need to setup script on the firebase console that automatically empties any records over 270 days old,
     * in line with firebase best practices
     *
     * @param token The new token.
     */
    override fun onNewToken(token: String) {
        logger.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }

    /**
     * A wrapper function for starting a coroutine to send the registration to the server.
     */
    private fun sendRegistrationToServer(token: String?) {
        val uid = authModel.currentUser?.uid /* ?: error("User not logged in") */
        if (uid == null || token == null) {
            logger.w(TAG, "User ID is null or empty; token not sent")
            return
        }
        // send token to the Firestore.
        runBlocking { repo.setFirebaseToken(token, uid) }
        logger.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    /**
     * Create and display a simple notification containing the received FCM message.
     * @param messageBody a string containing the messageBody from the FCM message
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
            .setContentTitle("Life Leveling") // TODO: put string in R.strings

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, channelId, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }


    /**
     * Schedule async work using WorkManager. a template for future functions that may make use of different Worker derived classes
     */
    private fun scheduleJob() {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(LLWorker::class.java).build()
        WorkManager.Companion.getInstance(this).beginWith(work).enqueue()
        // [END dispatch_job]
    }

    /**
     * any logic that can be handled within ten seconds
     */
    private fun handleNow() {
        logger.d(TAG, "handleNow()")
    }

}