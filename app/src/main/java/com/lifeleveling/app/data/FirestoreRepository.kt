package com.lifeleveling.app.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.tasks.await
import kotlin.Long

/**
 * A library of CRUD functions for our Firestore Cloud Database.
 * This is instantiated as an object, then the functions are called from the object.
 * This is for access to private member variables(properties),
 * to simplify the use of `Firebase.firestore` and `Firebase.auth`.
 * @author Felipe
 * @author thefool309
 * @property db a shortened alias for `Firebase.firestore`
 * @property auth a shortened alias for `Firebase.auth`
 */
class FirestoreRepository {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    /**
     * Velma wuz here :3
     */
    // Helper functions
    private fun getUserId() : String? {
        return auth.currentUser?.uid
    }

    private fun updateTimestamp(userId: String, logger: ILogger) {
        try {
            db.collection("users")
                .document(userId)
                .update("lastUpdate", Timestamp.now())
        }
        catch (e: Exception) {
            logger.e("Firestore", "Error Updating Timestamp", e)
        }
    }
    /**
     * >^w^<
     */

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
            stats = Stats(
                agility = 0L,
                defense = 0L,
                intelligence = 0L,
                strength = 0L,
                health = 0L
            ),
            streaks = 0L,
            onboardingComplete = false,
            createdAt = null,
            lastUpdate = null,
            level = 1L,
            lifePoints = 4L,        // Adding some life points to demo
            currentXp = 0.0,
            // xpToNextLevel is derived in Users, and we are not storing it
            currHealth = 10,
            badgesLocked = emptyList(),
            badgesUnlocked = emptyList(),
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
            "lastUpdate" to FieldValue.serverTimestamp(),
            "level" to model.level,
            "lifePoints" to model.lifePoints,
            "currentXp" to model.currentXp,
            "currHealth" to model.currHealth,
            "badgesLocked" to model.badgesLocked,
            "badgesUnlocked" to model.badgesUnlocked,
        )

        if (firstTime) {
            // first creation: write the full payload
            docRef.set(data, SetOptions.merge()).await()
        } else {
            // existing user: only bump lastUpdate (do NOT overwrite stats/lifePoints)
            docRef.set(mapOf("lastUpdate" to FieldValue.serverTimestamp()), SetOptions.merge()).await()
        }

        Log.d("FB", "users/$uid created=$firstTime")
        return firstTime
    }

    /**
     * This function creates a user data store in the Firestore Cloud Storage section of the project.
     * It takes a map of userData, with the key being the name of the field to be filled,
     * and an ILogger, which is an interface defined in this project to make the code more detachable.
     * We use a suspend function because FirebaseFirestore is async
     * @param userData a map of userData, with the key being the name of the field to be filled
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     * @author thefool309
     * @return Users?
     * @see ILogger
     */
    suspend fun createUser(userData: Map<String, Any>, logger: ILogger): Users? {
        val currentUser = auth.currentUser

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

    // function to edit user in firebase this function is unsafe and can
    // make dangerous type mismatches between the database and the code
    // Use at your own peril
    /**
     * This Function is now defunct and deprecated in favor of the more specific functions for updating specific fields.
     * If you really feel you want to use this function, use it with caution, because storing the wrong data type,
     * can actually cause a cascading failure in the getUser function, causing fields to be blank in the user object
     * @param userData a map of userData, with the key being the name of the field to be filled
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     * @author thefool309
     * @return Boolean
     * @see ILogger
     */
    suspend fun editUser(userData: Map<String, Any>, logger: ILogger) : Boolean {
        // the !! throws a null pointer exception if the currentUser is null
        // if the user is not authenticated then authenticate before calling this function
        val userId: String = auth.currentUser!!.uid
        var result: Boolean
        try {
            db.collection("users")
                .document(userId)
                .update(userData)
                .await()
            result = true
            updateTimestamp(userId, logger)
        }
        catch (e: Exception) {
            logger.e("Auth", "Error Updating User: ", e)
            result = false
        }
        return result
    }

    // User information
    /**
     * This function is designed for specifically updating the users displayName.
     * The displayName field is synonyms with a "username."
     * This will take the new userName string and replace the value of the "displayName" field.
     * @param userName A string representing the new display name for the user
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     * @author thefool309
     * @return Boolean
     * @see ILogger
     */
    suspend fun editDisplayName(userName: String, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        if(userName.isBlank()) {
            logger.e("Invalid Parameter","User name is empty. Please add user name...")
            return false
        }
        try {
            docRef.update("displayName", userName)
            .await()
            updateTimestamp(userId, logger)
            return true
        }
        catch (e: Exception) {
            logger.e("Auth", "Error Updating User: ", e)
            return false
        }
    }
    /**
     * A function for editing the Users "email" field.
     * This will take the new email and update the field in Firestore Cloud storage.
     * @param email A string containing the updated email.
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     * @author thefool309
     * @return Boolean
     * @see ILogger
     */
    suspend fun editEmail(email: String, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        if(email.isBlank()) {
            logger.e("Invalid Parameter","User email is empty. Please add user email.")
            return false
        }
        try {
            docRef.update("email", email).await()
            updateTimestamp(userId, logger)
            return true
        }
        catch (e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
            return false
        }

    }

    /**
     * A function for editing the value stored as the URL for the users photo they choose to represent themselves.
     * @param url A string representing the URL of the user's photo they choose to represent themselves
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     * @author thefool309
     * @return Boolean
     * @see ILogger
     */
    suspend fun editPhotoUrl(url: String, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        if(url.isBlank()) {
            logger.e("Invalid Parameter","Photo url is empty. Please add Photo url.")
            return false
        }
        try {
            docRef.update("photoUrl", url).await()
            updateTimestamp(userId, logger)
            return true
        }
        catch (e: Exception) {
            logger.e("Auth", "Error Updating User: ", e)
            return false
        }
    }

    suspend fun incrementStreaks( logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if (userId == null) {
            logger.e("Auth","User ID is empty. Please make sure you're signed in.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)

        try{
            val data = docRef.get().await()
            var newStreaks = data["streaks"] as Long
            docRef.update("streaks", ++newStreaks).await()
            updateTimestamp(userId, logger)
            return true
        }
        catch (e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
            return false
        }
    }

      //functions for modifying stats below

    suspend fun setStats(stats: Stats, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if (userId == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        try{
            docRef.update("stats", stats).await()
            updateTimestamp(userId, logger)
            return true
        }
        catch (e: Exception){
            logger.e("Firestore", "Error Updating User: ", e)
            return false
        }

    }

    suspend fun setCurrHealth(health: Long, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        try {
            val data = docRef.get().await()
            docRef.update("currHealth", health).await()
            updateTimestamp(userId, logger)
            return true
        }
        catch(e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
            return false
        }
    }

    suspend fun setCoins(coins: Long, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        try {
            docRef.update("coinsBalance", coins).await()
            updateTimestamp(userId, logger)
            return true
        }
        catch(e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
            return false
        }
    }

    suspend fun addCoins(coins: Long, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        try {
            val data = docRef.get().await()
            var newCoinsBalance = data["coinsBalance"] as Long
            newCoinsBalance += coins
            docRef.update("coinsBalance", newCoinsBalance).await()
            updateTimestamp(userId, logger)
            return true
        }
        catch(e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
            return false
        }
    }

    suspend fun subtractCoins(coins: Long, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        try {
            val data = docRef.get().await()
            var newCoinsBalance = data["coinsBalance"] as Long
            newCoinsBalance -= coins
            docRef.update("coinsBalance", newCoinsBalance).await()
            updateTimestamp(userId, logger)
            return true
        }
        catch(e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
            return false
        }
    }

    // A toggler for setOnboardingComplete
    suspend fun setOnboardingComplete(logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")  // and waste a ton of time
            .document(userId)
        return try {
            val data = docRef.get().await()
            val onboarding = data["onboardingComplete"] as Boolean
            if(onboarding) {
                docRef.update("onboardingComplete", false).await()
            }
            else {
                docRef.update("onboardingComplete", true).await()
            }
            updateTimestamp(userId, logger)
            true
        }
        catch (e: Exception) {
            logger.e("FireStore", "Error Updating User: ", e)
            false
        }
    }

    // By Velma
    // an overload to pass in a specific value
    suspend fun setOnboardingComplete(onboardingComplete: Boolean, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
        .document(userId)
        try {
            docRef.update("onboardingComplete", onboardingComplete).await()
            updateTimestamp(userId, logger)
            return true
        }
        catch (e: Exception) {
            logger.e("FireStore", "Error Updating User: ", e)
            return false
        }
    }

    suspend fun incrementLevel(logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        return try {
            val data = docRef.get().await()
            val curr = (data["level"] as? Number)?.toLong() ?: 1L
            val next = curr + 1L
            docRef.update("level", next).await()
            updateTimestamp(userId, logger)
            true
        }
        catch(e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
            return false
        }
    }

    // By Velma
    suspend fun addXp(xp: Double, logger: ILogger) : Users? {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return null
        }
        val docRef = db.collection("users")
            .document(userId)
        return try {
            val data = docRef.get().await()

            // read either "currentXp" (new) or "currXp" (legacy)
            val current = when (val raw = data["currentXp"] ?: data["currXp"]) {
                is Number -> raw.toDouble()
                is String -> raw.toDoubleOrNull() ?: 0.0
                else -> 0.0
            }
            val newXp = current + xp
            // write back to "currentXp" (canonical)
            docRef.update("currentXp", newXp).await()

            var user = getUser(userId, logger) ?: run {
                logger.e("Auth", "Error Updating User: Please make sure you're logged in")
                return null
            }

            if (newXp >= user.xpToNextLevel.toDouble()) {
                if (!incrementLevel(logger)) {
                    logger.e("Auth", "Level increment failed")
                }
                user = getUser(userId, logger) ?: return null
                user.calculateXpToNextLevel()
            }
            user
        } catch (e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
            null
        }
    }

    suspend fun getUser(uID: String?, logger: ILogger): Users? {
        if (uID.isNullOrBlank()) {
            logger.e("Auth", "User ID null/blank; sign in first.")
            return null
        }

        val docRef = try {
            db.collection("users").document(uID)
        } catch (e: Exception) {
            logger.e("Auth", "Error getting user docRef", e)
            return null
        }

        val snap = docRef.get().await()
        if (!snap.exists()) {
            logger.e("Firestore", "users/$uID does not exist.")
            return null
        }

        val data = snap.data ?: run {
            logger.e("Firestore", "users/$uID has no data.")
            return null
        }

        fun num(key: String): Long =
            (data[key] as? Number)?.toLong() ?: 0L
        fun dbl(key: String): Double =
            when (val raw = data[key]) {
                is Number -> raw.toDouble()
                is String -> raw.toDoubleOrNull() ?: 0.0
                else -> 0.0
            }
        fun ts(key: String): Timestamp? =
            data[key] as? Timestamp

        // stats are stored as a nested map
        val statsMap = data["stats"] as? Map<*, *> ?: emptyMap<String, Any>()
        val stats = Stats(
            agility       = (statsMap["agility"] as? Number)?.toLong() ?: 0L,
            defense       = (statsMap["defense"] as? Number)?.toLong() ?: 0L,
            intelligence  = (statsMap["intelligence"] as? Number)?.toLong() ?: 0L,
            strength      = (statsMap["strength"] as? Number)?.toLong() ?: 0L,
            health        = (statsMap["health"] as? Number)?.toLong() ?: 0L,
        )

        val user = Users(
            userId             = data["userId"] as? String ?: uID,
            displayName        = data["displayName"] as? String ?: "",
            email              = data["email"] as? String ?: "",
            photoUrl           = data["photoUrl"] as? String ?: "",
            coinsBalance       = num("coinsBalance"),
            stats              = stats,
            streaks            = num("streaks"),
            onboardingComplete = data["onboardingComplete"] as? Boolean ?: false,
            createdAt          = ts("createdAt"),
            lastUpdate         = ts("lastUpdate"),
            level              = (data["level"] as? Number)?.toLong() ?: 1L,
            lifePoints         = num("lifePoints"),
            // support either "currentXp" (new) or "currXp" (legacy)
            currentXp          = if (data.containsKey("currentXp")) dbl("currentXp") else dbl("currXp"),
            currHealth         = num("currHealth"),
            badgesLocked       = emptyList(),   // map arrays if/when needed
            badgesUnlocked     = emptyList(),
        )

        // derive fields
        user.calculateXpToNextLevel()
        user.calculateMaxHealth()
        return user
    }

    // TODO: add setBadgesLocked() and setBadgesUnlocked() to the users crud
    suspend fun setBadgesLocked(newBadgesLocked: List<Badge>, logger: ILogger) : Boolean {
        val uid: String? = getUserId()
        if (uid == null) {
            logger.e("Auth", "User ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users").document(uid)
        try {
            docRef.update("badgesLocked", newBadgesLocked).await()
            updateTimestamp(uid, logger)
            return true
        }
        catch(e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
            return false
        }
    }

    suspend fun setBadgesUnlocked(newBadgesUnlocked: List<Badge>, logger: ILogger) : Boolean {
        val uid: String? = getUserId()
        if (uid == null) {
            logger.e("Auth", "User ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users").document(uid)
        try {
            docRef.update("badgesUnlocked", newBadgesUnlocked).await()
            updateTimestamp(uid, logger)
            return true
        }
        catch(e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
            return false
        }
    }

    suspend fun setBadges(newBadgesLocked: List<Badge>, newBadgesUnlocked: List<Badge>, logger: ILogger) {
        setBadgesLocked(newBadgesLocked, logger)
        setBadgesUnlocked(newBadgesUnlocked, logger)
    }

    /**
     * Tries to fully delete the currently signed-in user and their data.
     *
     * Method Flow:
     * 1. Grabs the current user ID. If we don't have one, it will log it and stop.
     * 2. Delete any existing subcollections and logs errors but keeps going
     * 3. Delete the user document from the 'users' collection in Firestore Database
     * 4. Delete the Firebase Auth user account.
     *
     * If any of the steps fail, method will log the problem and return false so the caller knows delete didn't fully complete
     *
     * @param logger Used to log errors and warnings during the delete process
     * @return 'true' If we made it through the delete steps without a major failure, 'false' otherwise.
     * @author fdesouza1992
     * **/
    suspend fun deleteUser(logger: ILogger): Boolean {
        val uid: String? = getUserId()
        if (uid == null) {
            logger.e("Auth", "User ID is null. Please login to firebase.")
            return false
        }

        return try {
            // Delete subcollections (Just reminders for now)
            try {
                val remindersSnap = remindersCol(uid).get().await()
                if (!remindersSnap.isEmpty) {
                    val batch = db.batch()
                    for (doc in remindersSnap.documents) {
                        batch.delete(doc.reference)
                    }
                    batch.commit().await()
                }
            } catch (e: Exception) {
                // Log but continue so we at least try to delete the user document & auth user
                logger.e("Firestore", "Failed to delete reminders for user $uid", e)
            }

            // Delete user document in Firestore
            try {
                db.collection("users").document(uid).delete().await()
            } catch (e: Exception) {
                logger.e("Firestore", "Failed to delete user document for $uid", e)
                return false
            }

            // Delete Firebase Auth user
            val currentUser = auth.currentUser
            if (currentUser != null) {
                try {
                    currentUser.delete().await()
                } catch (e: Exception) {
                    // Firestore doc is already gone.
                    logger.e("Auth", "Failed to delete Firebase Auth user for $uid", e)
                    return false
                }
            } else {
                logger.w("Auth", "No Firebase Auth user found for $uid during deleteUser.")
            }

            true
        } catch (e: Exception) {
            logger.e("Firestore", "deleteUser failed for $uid", e)
            false
        }
    }

    /**
     * Helper to get this user's 'reminders' collection in Firestore.
     *
     * We use this to keep the path logic in one place 'users/{uid}/reminders'.
     *
     * @param uid The user's unique Firestore/Firebase Auth ID.
     * @return A reference to that user's 'reminders' collection.
     * @author fdesouza1992
     * **/
    private fun remindersCol(uid: String) =
        //Firebase.firestore.collection("users").document(uid).collection("reminders")
        db.collection("users").document(uid).collection("reminders")

    /**
     * Creates or updates a reminder for the currently signed-in user.
     *
     * Current Flow:  (May need to be updated as we progress)
     * 1. Check that we have a logged-in user; if not, log it and return null.
     * 2. Builds the reminder payload, letting Firestore handle server timestamps.
     * 3. If `reminders.reminderId` is blank, a new doc with an auto ID is created, otherwise writes to that specific document.
     * 4. Ensures the `reminderId` field inside the document matches the doc ID to facilitate with mapping later.
     *
     * On success, we log the created reminder and return its document ID.
     * On failure, we log the error and return null so the caller can handle it.
     *
     * @param reminders The reminder data we want to store.
     * @param logger Used to log success or failures during write.
     * @return The Firestore document ID for this reminder, or `null` if something went wrong.
     * @author fdesouza1992
     * **/
    suspend fun createReminder(
        reminders: Reminders,
        logger: ILogger
    ): String? {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            logger.e("Reminders", "createReminder: user not authenticated.")
            return null
        }

        // Build the payload; let Firestore set timestamps
        val payload = hashMapOf(
            "reminderId" to (reminders.reminderId.ifBlank { null }),
            "title" to reminders.title,
            "notes" to reminders.notes,
            "dueAt" to reminders.dueAt,
            "isCompleted" to reminders.isCompleted,
            "completedAt" to reminders.completedAt,
            "createdAt" to FieldValue.serverTimestamp(),
            "lastUpdate" to FieldValue.serverTimestamp(),
            "isDaily" to reminders.isDaily,
            "timesPerDay" to reminders.timesPerDay,
            "timesPerMonth" to reminders.timesPerMonth,
            "colorToken" to reminders.colorToken,
            "iconName" to reminders.iconName
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
        val uid = getUserId()
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
        val uid = getUserId()
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

    // Deletes a reminder
    suspend fun deleteReminder(reminderId: String, logger: ILogger): Boolean {
        val uid = getUserId()
        if (uid == null) {
            logger.e("Reminders", "deleteReminder: user not authenticated.")
            return false
        }
        return try {
            remindersCol(uid).document(reminderId).delete().await()
            logger.d("Reminders", "deleteReminder: $reminderId")
            true
        } catch (e: Exception) {
            logger.e("Reminders", "deleteReminder failed", e)
            false
        }
    }

    // Fetch a single reminder

    // Fetch a list of reminders

    // Realtime stream of reminders (ordered by dueAt)


    // A wrapper method to get me the current user
    suspend fun getCurrentUser(logger: ILogger): Users? {
        val uid = getUserId()
        if (uid == null) {
            logger.e("Auth", "No user found with uid $uid; Please sign in.")
            return null
        }
        return getUser(uid, logger)
    }

    // Method to set life points mirroring the behavior of existing setCoins and addCoins methods
    suspend fun setLifePoints(lifePoints: Long, logger: ILogger): Boolean {
        val uid = getUserId()
        if (uid == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return false
        }
        return try {
            db.collection("users").document(uid)
                .update("lifePoints", lifePoints)
                .await()
            updateTimestamp(uid, logger)
            true
        } catch (e: Exception) {
            logger.e("Firestore", "Error updating lifePoints", e)
            false
        }
    }

    /**
     * Resets the current user’s stats back to 0 and refunds all spent points into their lifePoints pool.
     *
     * Flow:
     * 1. Look up the currently signed-in user’s ID.
     * 2. Load their user document and read the current stats + lifePoints.
     * 3. Sum all points spent across Health, Agility, Intelligence, Defense, and Strength.
     * 4. Add those spent points back into lifePoints.
     * 5. Write zeroed-out stats and the new lifePoints total back to Firestore.
     * 6. Update the user’s timestamp so other parts of the app know the data changed.
     *
     * Example:
     *  Stats: H=5, A=7, I=3, D=8, S=17  (total 40)
     *  lifePoints = 5
     *  After reset → all stats = 0, lifePoints = 45.
     *
     * @param logger Used to log any errors while resetting life points.
     * @return true if the Firestore update succeeds, false otherwise.
     * @author fdesouza1992
     */
    suspend fun resetLifePoints(logger: ILogger): Boolean {
        val uid = getUserId()
        if (uid == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return false
        }

        // Loads current user
        val user = getUser(uid, logger) ?: return false
        val stats = user.stats

        val usedLifePoint = stats.strength + stats.defense + stats.intelligence + stats.agility + stats.health
        val currentLifePointsPool = user.lifePoints
        val newLifePointsPool = usedLifePoint+currentLifePointsPool

        val docRef = db.collection("users").document(uid)
        return try {
            val resetStats = Stats(
                agility = 0L,
                defense = 0L,
                intelligence = 0L,
                strength = 0L,
                health = 0L
            )

            docRef.update(
                mapOf(
                    "stats" to resetStats,
                    "lifePoints" to newLifePointsPool,
                )
            ).await()

            updateTimestamp(uid, logger)
            true
        } catch (e: Exception) {
            logger.e("Firestore", "Error resetting life points", e)
            false
        }
    }
}
