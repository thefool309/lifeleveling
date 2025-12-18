package com.lifeleveling.app.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.tasks.await
import kotlin.Long
import kotlin.String

/**
 * A library of CRUD functions for our Firestore Cloud Database.
 * This is instantiated as an object, then the functions are called from the object.
 * This is for functions that do direct writes and reads from the firestore database.
 * @author Felipe
 * @author thefool309
 * @property db a shortened alias for `Firebase.firestore`
 * @property logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
 */
class FirestoreRepository(
    private val db: FirebaseFirestore = Firebase.firestore,
    private val logger: ILogger
) {

    /**
     * Updates the timestamp for when the last time the user's firestore data was updated
     * Avoid using this function inside another function that already writes to the user's collection.
     * It will cause a double firebase call.
     * @param userId The unique firebase ID of the user
     */
    private fun updateTimestamp(userId: String) {
        try {
            db.collection("users")
                .document(userId)
                .update("lastUpdate", FieldValue.serverTimestamp())
        }
        catch (e: Exception) {
            logger.e("Firestore", "Error Updating Timestamp", e)
        }
    }

    /**
     * Checks if the user has been created and makes an initial UsersData object
     * @param user A firebase user to check the information for.
     */
    suspend fun ensureUserCreated(user: FirebaseUser): Boolean {
        val uid = user.uid
        val docRef = db.collection("users").document(uid)

        val snap = docRef.get().await()
        val firstTime = !snap.exists()

        if (firstTime) {
            // first creation: write the full payload
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "displayName" to user.displayName,
                    "email" to user.email,
                    "photoUrl" to user.photoUrl?.toString(),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "lastUpdate" to FieldValue.serverTimestamp(),
                    "onboardingComplete" to false,
                )
            ).await()
        } else {
            // existing user: only bump lastUpdate (do NOT overwrite stats/lifePoints)
            docRef.update("lastUpdate", FieldValue.serverTimestamp()).await() //Update is a smaller payload
        }

        Log.d("FB", "users/$uid created=$firstTime")
        return firstTime
    }

    /**
     * This function creates a user data store in the Firestore Cloud Storage section of the project.
     * It takes a map of userData, with the key being the name of the field to be filled.
     * We use a suspend function because FirebaseFirestore is async
     * @param userData a map of userData, with the key being the name of the field to be filled
     * @param currentUser The user that will be updated in firestore
     * @author thefool309
     * @return UsersData?
     */
    suspend fun createUser(userData: Map<String, Any>, currentUser: FirebaseUser?): UsersData? {
//        val currentUser = auth.currentUser

        if (currentUser == null) {
            logger.e("Auth", "UID is null. Please authenticate user before calling CreateUser...")
            return null
        }

        val uid = currentUser.uid
        val docRef = db.collection("users")
                        .document(uid)

        val base = mapOf(
            "userId" to uid,
            "displayName" to userData["displayName"].toString(),
            "email" to userData["email"].toString(),
            "photoUrl" to userData["photoUrl"].toString(),
            "createdAt" to FieldValue.serverTimestamp(),
            "lastUpdate" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(base).await()
            val base = UsersBase(
                userId = uid,
                displayName = userData["displayName"].toString(),
                email = userData["email"].toString(),
                photoUrl = userData["photoUrl"].toString()
            )
            UsersData(userBase = base)
        }
        catch (e: Exception) {
            // unknown error saving user to Firebase
            logger.e("Firestore", "Error Saving User: ", e)
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
     * This function can be used to save one parameter or multiple.
     * @param userData a map of userData, with the key being the name of the field to be filled
     * @param userId The firebase id of the user to write to
     * @author thefool309
     * @return Boolean for a success check
     */
    suspend fun editUser(userId: String?, userData: Map<String, Any>) : Boolean {
        // the !! throws a null pointer exception if the currentUser is null
        // if the user is not authenticated then authenticate before calling this function
//        val userId: String = auth.currentUser!!.uid
        if(userId.isNullOrBlank()) {
            logger.e("Firestore", "UserId is null or blank. Please login to Firebase.")
            return false
        }
        if(userData.isEmpty()) {
            logger.e("Firestore", "No parameters provided to update for user.")
            return false
        }

        return try {
            val data = userData.toMutableMap().apply {
                put("lastUpdate", FieldValue.serverTimestamp())
            }
            db.collection("users")
                .document(userId)
                .update(data)
                .await()
            true
        }
        catch (e: Exception) {
            logger.e("Firestore", "Error Updating User: $userId", e)
            false
        }
    }

    // User information
//    /**
//     * This function is designed for specifically updating the users displayName.
//     * The displayName field is synonomous with a "username."
//     * This will take the new userName string and replace the value of the "displayName" field.
//     * @param userName A string representing the new display name for the user
//     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
//     * @author thefool309
//     * @return Boolean
//     * @see ILogger
//     */
//    suspend fun editDisplayName(userName: String, logger: ILogger) : Boolean {
//        val userId: String? = getUserId()
//        if(userId == null) {
//            logger.e("Auth","ID is null. Please login to firebase.")
//            return false
//        }
//        val docRef = db.collection("users")
//            .document(userId)
//        if(userName.isBlank()) {
//            logger.e("Invalid Parameter","User name is empty. Please add user name...")
//            return false
//        }
//        try {
//            docRef.update("displayName", userName)
//            .await()
//            updateTimestamp(userId, logger)
//            return true
//        }
//        catch (e: Exception) {
//            logger.e("Auth", "Error Updating User: ", e)
//            return false
//        }
//    }
//    /**
//     * A function for editing the Users "email" field.
//     * This will take the new email and update the field in Firestore Cloud storage.
//     * @param email A string containing the updated email.
//     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
//     * @author thefool309
//     * @return Boolean
//     * @see ILogger
//     */
//    suspend fun editEmail(email: String, logger: ILogger) : Boolean {
//        val userId: String? = getUserId()
//        if(userId == null) {
//            logger.e("Auth","ID is null. Please login to firebase.")
//            return false
//        }
//        val docRef = db.collection("users")
//            .document(userId)
//        if(email.isBlank()) {
//            logger.e("Invalid Parameter","User email is empty. Please add user email.")
//            return false
//        }
//        try {
//            docRef.update("email", email).await()
//            updateTimestamp(userId, logger)
//            return true
//        }
//        catch (e: Exception) {
//            logger.e("Firestore", "Error Updating User: ", e)
//            return false
//        }
//
//    }

//    /**
//     * A function for editing the value stored as the URL for the users photo they choose to represent themselves.
//     * @param url A string representing the URL of the user's photo they choose to represent themselves
//     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
//     * @author thefool309
//     * @return Boolean
//     * @see ILogger
//     */
//    suspend fun editPhotoUrl(url: String, logger: ILogger) : Boolean {
//        val userId: String? = getUserId()
//        if(userId == null) {
//            logger.e("Auth","ID is null. Please login to firebase.")
//            return false
//        }
//        val docRef = db.collection("users")
//            .document(userId)
//        if(url.isBlank()) {
//            logger.e("Invalid Parameter","Photo url is empty. Please add Photo url.")
//            return false
//        }
//        try {
//            docRef.update("photoUrl", url).await()
//            updateTimestamp(userId, logger)
//            return true
//        }
//        catch (e: Exception) {
//            logger.e("Auth", "Error Updating User: ", e)
//            return false
//        }
//    }

    // This can be done using editUserParameter, update the local value, pass it in as the new value to firebase
//    suspend fun incrementStreaks( logger: ILogger) : Boolean {
//        val userId: String? = getUserId()
//        if (userId == null) {
//            logger.e("Auth","User ID is empty. Please make sure you're signed in.")
//            return false
//        }
//        val docRef = db.collection("users")
//            .document(userId)
//
//        try{
//            val data = docRef.get().await()
//            var newStreaks = data["streaks"] as Long
//            docRef.update("streaks", ++newStreaks).await()
//            updateTimestamp(userId, logger)
//            return true
//        }
//        catch (e: Exception) {
//            logger.e("Firestore", "Error Updating User: ", e)
//            return false
//        }
//    }

      //functions for modifying stats below

//    suspend fun setStats(stats: Stats, logger: ILogger) : Boolean {
//        val userId: String? = getUserId()
//        if (userId == null) {
//            logger.e("Auth","ID is null. Please login to firebase.")
//            return false
//        }
//        val docRef = db.collection("users")
//            .document(userId)
//        try{
//            docRef.update("stats", stats).await()
//            updateTimestamp(userId, logger)
//            return true
//        }
//        catch (e: Exception){
//            logger.e("Firestore", "Error Updating User: ", e)
//            return false
//        }
//
//    }

//    suspend fun setCurrHealth(health: Long, logger: ILogger) : Boolean {
//        val userId: String? = getUserId()
//        if(userId == null) {
//            logger.e("Auth","ID is null. Please login to firebase.")
//            return false
//        }
//        val docRef = db.collection("users")
//            .document(userId)
//        try {
//            val data = docRef.get().await()
//            docRef.update("currHealth", health).await()
//            updateTimestamp(userId, logger)
//            return true
//        }
//        catch(e: Exception) {
//            logger.e("Firestore", "Error Updating User: ", e)
//            return false
//        }
//    }

//    suspend fun setCoins(coins: Long, logger: ILogger) : Boolean {
//        val userId: String? = getUserId()
//        if(userId == null) {
//            logger.e("Auth","ID is null. Please login to firebase.")
//            return false
//        }
//        val docRef = db.collection("users")
//            .document(userId)
//        try {
//            docRef.update("coinsBalance", coins).await()
//            updateTimestamp(userId, logger)
//            return true
//        }
//        catch(e: Exception) {
//            logger.e("Firestore", "Error Updating User: ", e)
//            return false
//        }
//    }

//    suspend fun addCoins(coins: Long, logger: ILogger) : Boolean {
//        val userId: String? = getUserId()
//        if(userId == null) {
//            logger.e("Auth","ID is null. Please login to firebase.")
//            return false
//        }
//        val docRef = db.collection("users")
//            .document(userId)
//        try {
//            val data = docRef.get().await()
//            var newCoinsBalance = data["coinsBalance"] as Long
//            newCoinsBalance += coins
//            docRef.update("coinsBalance", newCoinsBalance).await()
//            updateTimestamp(userId, logger)
//            return true
//        }
//        catch(e: Exception) {
//            logger.e("Firestore", "Error Updating User: ", e)
//            return false
//        }
//    }

//    suspend fun subtractCoins(coins: Long, logger: ILogger) : Boolean {
//        val userId: String? = getUserId()
//        if(userId == null) {
//            logger.e("Auth","ID is null. Please login to firebase.")
//            return false
//        }
//        val docRef = db.collection("users")
//            .document(userId)
//        try {
//            val data = docRef.get().await()
//            var newCoinsBalance = data["coinsBalance"] as Long
//            newCoinsBalance -= coins
//            docRef.update("coinsBalance", newCoinsBalance).await()
//            updateTimestamp(userId, logger)
//            return true
//        }
//        catch(e: Exception) {
//            logger.e("Firestore", "Error Updating User: ", e)
//            return false
//        }
//    }

//    // A toggler for setOnboardingComplete
//    suspend fun setOnboardingComplete(logger: ILogger) : Boolean {
//        val userId: String? = getUserId()
//        if(userId == null) {
//            logger.e("Auth","ID is null. Please login to firebase.")
//            return false
//        }
//        val docRef = db.collection("users")  // and waste a ton of time
//            .document(userId)
//        return try {
//            val data = docRef.get().await()
//            val onboarding = data["onboardingComplete"] as Boolean
//            if(onboarding) {
//                docRef.update("onboardingComplete", false).await()
//            }
//            else {
//                docRef.update("onboardingComplete", true).await()
//            }
//            updateTimestamp(userId, logger)
//            true
//        }
//        catch (e: Exception) {
//            logger.e("FireStore", "Error Updating User: ", e)
//            false
//        }
//    }

//    // By Velma
//    // an overload to pass in a specific value
//    suspend fun setOnboardingComplete(onboardingComplete: Boolean, logger: ILogger) : Boolean {
//        val userId: String? = getUserId()
//        if(userId == null) {
//            logger.e("Auth","ID is null. Please login to firebase.")
//            return false
//        }
//        val docRef = db.collection("users")
//        .document(userId)
//        try {
//            docRef.update("onboardingComplete", onboardingComplete).await()
//            updateTimestamp(userId, logger)
//            return true
//        }
//        catch (e: Exception) {
//            logger.e("FireStore", "Error Updating User: ", e)
//            return false
//        }
//    }

//    suspend fun incrementLevel(logger: ILogger) : Boolean {
//        val userId: String? = getUserId()
//        if(userId == null) {
//            logger.e("Auth","ID is null. Please login to firebase.")
//            return false
//        }
//        val docRef = db.collection("users")
//            .document(userId)
//        return try {
//            val data = docRef.get().await()
//            val curr = (data["level"] as? Number)?.toLong() ?: 1L
//            val next = curr + 1L
//            docRef.update("level", next).await()
//            updateTimestamp(userId, logger)
//            true
//        }
//        catch(e: Exception) {
//            logger.e("Firestore", "Error Updating User: ", e)
//            return false
//        }
//    }

//    // By Velma
//    suspend fun addXp(xp: Double, logger: ILogger) : Users? {
//        val userId: String? = getUserId()
//        if(userId == null) {
//            logger.e("Auth","ID is null. Please login to firebase.")
//            return null
//        }
//        val docRef = db.collection("users")
//            .document(userId)
//        return try {
//            val data = docRef.get().await()
//
//            // read either "currentXp" (new) or "currXp" (legacy)
//            val current = when (val raw = data["currentXp"] ?: data["currXp"]) {
//                is Number -> raw.toDouble()
//                is String -> raw.toDoubleOrNull() ?: 0.0
//                else -> 0.0
//            }
//            val newXp = current + xp
//            // write back to "currentXp" (canonical)
//            docRef.update("currentXp", newXp).await()
//
//            var user = getUser(userId, logger) ?: run {
//                logger.e("Auth", "Error Updating User: Please make sure you're logged in")
//                return null
//            }
//
//            if (newXp >= user.xpToNextLevel.toDouble()) {
//                if (!incrementLevel(logger)) {
//                    logger.e("Auth", "Level increment failed")
//                }
//                user = getUser(userId, logger) ?: return null
//                user.calculateXpToNextLevel()
//            }
//            user
//        } catch (e: Exception) {
//            logger.e("Firestore", "Error Updating User: ", e)
//            null
//        }
//    }

    /**
     * Pulls the user's information from the database and puts it into a UsersData object for local use
     * Pulls the UserBase information from firestore
     * Pulls all subcollections into proper lists.
     * Creates a UserData object and returns it
     * @param uID The ID of the user
     * @return A UsersData object for updating the state
     */
    suspend fun getUser(uID: String?): UsersData? {
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
        fun ts(key: String): com.google.firebase.Timestamp? =
            data[key] as? com.google.firebase.Timestamp

        // stats are stored as a nested map
        val statsMap = data["stats"] as? Map<*, *> ?: emptyMap<String, Any>()
        val stats = Stats(
            strength      = (statsMap["strength"] as? Number)?.toLong() ?: 0L,
            defense       = (statsMap["defense"] as? Number)?.toLong() ?: 0L,
            intelligence  = (statsMap["intelligence"] as? Number)?.toLong() ?: 0L,
            agility       = (statsMap["agility"] as? Number)?.toLong() ?: 0L,
            health        = (statsMap["health"] as? Number)?.toLong() ?: 0L,
        )

        val mostCompletedMap = data["mostCompletedReminder"] as? Map<*, *> ?: emptyMap<String, Any>()
        val mostCompleted = Pair(
            mostCompletedMap["name"] as? String ?: "",
            (mostCompletedMap["completed"] as? Number)?.toLong() ?: 0L
        )

        val user = UsersBase(
            userId             = data["userId"] as? String ?: uID,
            displayName        = data["displayName"] as? String ?: "",
            email              = data["email"] as? String ?: "",
            photoUrl           = data["photoUrl"] as? String ?: "",
            coinsBalance       = num("coinsBalance"),
            allCoinsEarned     = num("allCoinsEarned"),
            stats              = stats,
            onboardingComplete = data["onboardingComplete"] as? Boolean ?: false,
            createdAt          = ts("createdAt"),
            lastUpdate         = ts("lastUpdate"),
            level              = (data["level"] as? Number)?.toLong() ?: 1L,
            lifePointsUsed     = num("lifePointsUsed"),
            lifePointsTotal    = num("lifePointsTotal"),
            // support either "currentXp" (new) or "currXp" (legacy)
            currentXp          = if (data.containsKey("currentXp")) dbl("currentXp") else dbl("currXp"),
            currHealth         = num("currHealth"),
            badges             = emptyList(),   // TODO: map arrays if/when needed
            fightOrMeditate    = (data["fightOrMeditate"] as? Number)?.toInt() ?: 0,
            weekStreaksCompleted = num("weekStreaksCompleted"),
            monthStreaksCompleted = num("monthStreaksCompleted"),
            mostCompletedReminder = mostCompleted,
            isDarkTheme        = data["isDarkTheme"] as? Boolean ?: true,
        )

        // Load the reminders subcollection
        val remindersList = try {
            remindersCol(uID)
                .get().await().documents.mapNotNull { doc ->
                    doc.toObject(Reminder::class.java)?.copy(reminderId = doc.id)
                }
        } catch (e: Exception) {
            logger.e("Firestore", "Error loading user's reminders", e)
            emptyList()
        }

        // Load the streaks subcollection
        val streaksList = try {
            streaksCol(uID)
                .get().await().documents.mapNotNull { doc ->
                    doc.toObject(Streak::class.java)?.copy(streakId = doc.id)
                }
        } catch (e: Exception) {
            logger.e("Firestore", "Error loading user's streaks", e)
            emptyList()
        }

        return UsersData(
            userBase = user,
            reminders = remindersList,
            streaks = streaksList,
        )
    }

    // TODO: Adjust this to write the map of the badges list
//    suspend fun setBadgesLocked(newBadgesLocked: List<Badge>, logger: ILogger, uid: String?) : Boolean {
////        val uid: String? = getUserId()
//        if (uid == null) {
//            logger.e("Auth", "User ID is null. Please login to firebase.")
//            return false
//        }
//        val docRef = db.collection("users").document(uid)
//        try {
//            docRef.update("badgesLocked", newBadgesLocked).await()
//            updateTimestamp(uid)
//            return true
//        }
//        catch(e: Exception) {
//            logger.e("Firestore", "Error Updating User: ", e)
//            return false
//        }
//    }

//    suspend fun setBadgesUnlocked(newBadgesUnlocked: List<Badge>, logger: ILogger) : Boolean {
//        val uid: String? = getUserId()
//        if (uid == null) {
//            logger.e("Auth", "User ID is null. Please login to firebase.")
//            return false
//        }
//        val docRef = db.collection("users").document(uid)
//        try {
//            docRef.update("badgesUnlocked", newBadgesUnlocked).await()
//            updateTimestamp(uid, logger)
//            return true
//        }
//        catch(e: Exception) {
//            logger.e("Firestore", "Error Updating User: ", e)
//            return false
//        }
//    }

//    suspend fun setBadges(newBadgesLocked: List<Badge>, newBadgesUnlocked: List<Badge>, logger: ILogger) {
//        setBadgesLocked(newBadgesLocked, logger)
//        setBadgesUnlocked(newBadgesUnlocked, logger)
//    }

    /**
     * Tries to fully delete the currently signed-in user and their data.
     *
     * Method Flow:
     * 1. Checks for the user's ID. If there is not a user file associated with it, it will log it and stop.
     * 2. Delete any existing subcollections and logs errors but keeps going
     * 3. Delete the user document from the 'users' collection in Firestore Database
//     * 4. Delete the Firebase Auth user account.
     *
     * If any of the steps fail, method will log the problem and return false so the caller knows delete didn't fully complete
     *
     * @param uid The ID of the user to look for in firestore
     * @return 'true' If we made it through the delete steps without a major failure, 'false' otherwise.
     * @author fdesouza1992
     * **/
    suspend fun deleteUser(uid: String?): Boolean {
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
     * @param uid The ID of the user to search for
     * @return The Firestore document ID for this reminder, or `null` if something went wrong.
     * @author fdesouza1992
     * **/
    suspend fun createReminder(
        reminders: Reminder,
        uid: String?
    ): String? {
//        val uid = auth.currentUser?.uid
        if (uid == null) {
            logger.e("Reminders", "createReminder: user not authenticated.")
            return null
        }

        // Build the payload; let Firestore set timestamps
        val payload = hashMapOf(
            "reminderId" to (if (reminders.reminderId.isNotBlank()) reminders.reminderId else null),
            "title" to reminders.title,
            "notes" to reminders.notes,
            "dueAt" to reminders.startingAt,
            "isCompleted" to reminders.completed,
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
    /**
     * Updates the information stored for a specific reminder in the user's database
     * @param reminderId The id of the reminder to update
     * @param updates The value changes needed for the reminder object
     * @param uid The ID of the user to write to
     * @return A boolean for a success check of the write
     */
    suspend fun updateReminder(
        reminderId: String,
        updates: Map<String, Any?>,
        uid: String?
    ): Boolean {
//        val uid = getUserId()
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
        uid: String?
    ): Boolean {
//        val uid = getUserId()
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
    suspend fun deleteReminder(reminderId: String, uid: String?): Boolean {
//        val uid = getUserId()
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


//    // A wrapper method to get me the current user
//    suspend fun getCurrentUser(logger: ILogger): Users? {
//        val uid = getUserId()
//        if (uid == null) {
//            logger.e("Auth", "No user found with uid $uid; Please sign in.")
//            return null
//        }
//        return getUser(uid, logger)
//    }

//    // Method to set life points mirroring the behavior of existing setCoins and addCoins methods
//    suspend fun setLifePoints(lifePoints: Long, logger: ILogger): Boolean {
//        val uid = getUserId()
//        if (uid == null) {
//            logger.e("Auth","ID is null. Please login to firebase.")
//            return false
//        }
//        return try {
//            db.collection("users").document(uid)
//                .update("lifePoints", lifePoints)
//                .await()
//            updateTimestamp(uid, logger)
//            true
//        } catch (e: Exception) {
//            logger.e("Firestore", "Error updating lifePoints", e)
//            false
//        }
//    }

    // Firebase section of Felipe's bookkeeping function
    /**
     * Writes to the firestore logs for bookkeeping timestamps of different actions taken
     * @param provider The way that a user logged in. Usually saved as "password" or "google"
     * @param user The firebase user that logged in.
     * @author fdesouza1992
     */
    fun writeBookkeeping(provider: String, user: FirebaseUser) {
        Firebase.firestore.collection("authLogs")
            .add(
                mapOf(
                    "ts" to com.google.firebase.Timestamp.now(),
                    "source" to "emailPasswordLogin",
                    "provider" to provider,
                    "uid" to user.uid,
                    "email" to user.email,
                    "name" to (user.displayName ?: "")
                )
            )
            .addOnSuccessListener { doc -> Log.d("FB", "Auth Log doc: ${doc.id}") }
            .addOnFailureListener { e -> logger.w("FB", "Auth Log write failed: ${e.message}") }
    }

    /**
     * A reference to the path of the user's streaks collection
     * @param uid The ID of the user
     * @author Elyseia
     */
    fun streaksCol(uid: String) =
        db.collection("users")
            .document(uid)
            .collection("streaks")

    /**
     * Adds a streak to the user's streak collection
     * Flow:
     * 1. Creates an id for the streak.
     * 2. Creates a streak object using that idea and other information passed to it.
     * 3. Saves the new streak in firestore
     * 4. Returns the streak created
     * @param uid The user ID to be written in
     * @param streakBuilder The function that builds a streak that firestore will pass a streak ID to
     * @return Returns a full streak object
     * @author Elyseia
     */
    suspend fun createStreak(
        uid: String,
        streakBuilder: (String) -> Streak,
    ) : Streak {
        val ref = streaksCol(uid).document()
        val streak = streakBuilder(ref.id)
        ref.set(streak).await()
        updateTimestamp(uid)
        return streak
    }

    /**
     * Deletes a streak from the user's streak collection.
     * @param uid The user's unique ID
     * @param streakId The unique ID of the streak that is being deleted
     */
    suspend fun deleteStreak(
        uid: String,
        streakId: String,
    ) {
        streaksCol(uid)
            .document(streakId)
            .delete()
            .await()
        updateTimestamp(uid)
    }
}
