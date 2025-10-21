package com.lifeleveling.app.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.tasks.await


class FirestoreRepository {
    private val db = Firebase.firestore

    suspend fun ensureUserCreated(user: FirebaseUser): Boolean {
        val uid = user.uid
        val docRef = db.collection("users").document(uid)

        val snap = docRef.get().await()
        val firstTime = !snap.exists()

        // Compose the write payload using your Users model defaults
        val model = Users(
            userId = uid,
            displayName = user.displayName.orEmpty(),
            email = user.email.orEmpty(),
            photoUrl = user.photoUrl?.toString().orEmpty(),
            coinsBalance = 0L,
            stats = mapOf(
                "agility" to 0L,
                "defense" to 0L,
                "healthPoints" to 0L,
                "strength" to 0L
            ),
            streaks = 0L,
            onboardingComplete = false,
            createdAt = null,
            lastUpdate = null,
        )

        val data = mutableMapOf<String, Any?>(
            "userId" to model.userId,
            "displayName" to model.displayName,
            "email" to model.email,
            "photoUrl" to model.photoUrl,
            "coinsBalance" to model.coinsBalance,
            "stats" to model.stats,
            "streaks" to model.streaks,
            "onboardingComplete" to model.onboardingComplete,
            "createdAt" to model.createdAt,
            "lastUpdate" to FieldValue.serverTimestamp()
        )

        if (firstTime) {
            data["createdAt"] = FieldValue.serverTimestamp()
        }

        // merge = idempotent; won’t blow away future fields
        docRef.set(data, SetOptions.merge()).await()

        Log.d("FB", "users/$uid created=$firstTime")
        return firstTime
    }


    // Function to create user and store in firebase
    // returns null on failure. We use a suspend function because
    // FirebaseFirestore is async
    suspend fun createUser(userData: Map<String, Any>, logger: ILogger): Users? {

        val currentUser = FirebaseAuth.getInstance().currentUser

        return if (currentUser != null) {
            val uid = currentUser.uid
            val docRef = db.collection("users")
                            .document(uid)

            val result = Users(
                userId = uid,
                displayName = userData["displayName"].toString(),
                email = userData["email"].toString(),
                photoUrl = userData["photoUrl"].toString(),
                createdAt = Timestamp.now(),
                lastUpdate = Timestamp.now()
            )
            try {
                docRef.set(result).await()
                result
            }
            catch (e: Exception) {
                // unknown error saving user to Firebase
                logger.e("Firestore", "Error Saving User: ", e)
                null
            }

        } else {
            // No user is signed in
            logger.e("Auth", "UID is null. Please authenticate user before calling CreateUser...")
            null
        }

    }

    // function to edit user in firebase
    suspend fun editUser(userData: Map<String, Any>, logger: ILogger) : Boolean {
        // the !! throws a null pointer exception if the currentUser is null
        // if the user is not authenticated then authenticate before calling this function
        val userId: String = FirebaseAuth.getInstance().currentUser!!.uid
        var result: Boolean
        try {
            db.collection("users")
                .document(userId)
                .update(userData)
                .await()
            result = true
        }
        catch (e: Exception) {
            logger.e("Auth", "Error Updating User: ", e)
            result = false
        }
        return result
    }

    suspend fun editUserName(userName: String, logger: ILogger) : Boolean {
        var result: Boolean = false
        val userId: String = FirebaseAuth.getInstance().currentUser!!.uid
        val docRef = db.collection("users")
        .document(userId)
        try {
            docRef.update("displayName", userName)
            .await()
            result = true
        }
        catch (e: Exception) {
            logger.e("Auth", "Error Updating User: ", e)
        }
        return result
    }

    // TODO: function to retrieve user information from firebase
    fun getUser(uID: String, logger: ILogger): Users {
        val result = Users()
        return result
    }

    private fun remindersCol(uid: String) =
        //Firebase.firestore.collection("users").document(uid).collection("reminders")
        db.collection("users").document(uid).collection("reminders")

    // Creates a new reminder for the current user.
    suspend fun createReminder(
        reminders: Reminders,
        logger: ILogger
    ): String? {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            logger.e("Reminders", "createReminder: user not authenticated.")
            return null
        }

        // Build the payload; let Firestore set timestamps
        val payload = hashMapOf(
            "reminderId" to (if (reminders.reminderId.isNotBlank()) reminders.reminderId else null),
            "title" to reminders.title,
            "notes" to reminders.notes,
            "dueAt" to reminders.dueAt,
            "isCompleted" to reminders.isCompleted,
            "completedAt" to reminders.completedAt,
            "createdAt" to FieldValue.serverTimestamp(),
            "lastUpdate" to FieldValue.serverTimestamp()
        ).filterValues { it != null } // don't write null reminderId if empty

        return try {
            val docRef = if (reminders.reminderId.isBlank()) {
                remindersCol(uid).document() // auto id
            } else {
                remindersCol(uid).document(reminders.reminderId)
            }

            // Persist reminderId inside the doc for simple mapping
            val finalPayload = payload.toMutableMap().apply {
                put("reminderId", docRef.id)
            }

            docRef.set(finalPayload, SetOptions.merge()).await()
            logger.d("Reminders", "createReminder: created ${docRef.id}")
            docRef.id
        } catch (e: Exception) {
            logger.e("Reminders", "createReminder failed", e)
            null
        }
    }

    // Update a reminder by id
    suspend fun updateReminder(
        reminderId: String,
        updates: Map<String, Any?>,
        logger: ILogger
    ): Boolean {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            logger.e("Reminders", "updateReminder: user not authenticated.")
            return false
        }
        return try {
            val payload = updates.toMutableMap().apply {
                this["lastUpdate"] = FieldValue.serverTimestamp()
            }
            remindersCol(uid).document(reminderId).update(payload).await()
            logger.d("Reminders", "updateReminder: $reminderId")
            true
        } catch (e: Exception) {
            logger.e("Reminders", "updateReminder failed", e)
            false
        }
    }

    // Mark a reminder complete/incomplete and set/unset completedAt automatically.
    suspend fun setReminderCompleted(
        reminderId: String,
        isCompleted: Boolean,
        logger: ILogger
    ): Boolean {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            logger.e("Reminders", "setReminderCompleted: user not authenticated.")
            return false
        }
        return try {
            val payload = hashMapOf<String, Any?>(
                "isCompleted" to isCompleted,
                "completedAt" to if (isCompleted) FieldValue.serverTimestamp() else null,
                "lastUpdate" to FieldValue.serverTimestamp()
            )
            remindersCol(uid).document(reminderId).update(payload).await()
            logger.d("Reminders", "setReminderCompleted: $reminderId -> $isCompleted")
            true
        } catch (e: Exception) {
            logger.e("Reminders", "setReminderCompleted failed", e)
            false
        }
    }

}
