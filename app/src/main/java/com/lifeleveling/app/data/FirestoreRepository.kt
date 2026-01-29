package com.lifeleveling.app.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.tasks.await
import kotlin.Long
import com.google.firebase.ktx.Firebase
import kotlin.String
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import com.google.firebase.firestore.Transaction
import kotlin.math.max

/**
 * A library of CRUD functions for our Firestore Cloud Database.
 * This is instantiated as an object, then the functions are called from the object.
 * This is for functions that do direct writes and reads from the firestore database.
 * @author fdesouza1992
 * @author thefool309
 * @property db a shortened alias for `Firebase.firestore`
 * @property logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
 * @property TAG a tag added for debugging purposes. we chose a centralized tag so we could quickly identify what file any log is coming from
 */
class FirestoreRepository(
    private val db: FirebaseFirestore,
    private val logger: ILogger,
) {
    companion object {
        private const val TAG = "FirestoreRepository"
    }

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
            logger.e(TAG, "Error Updating Timestamp", e)
        }
    }

    //region User Creation

    /**
     * Checks if the user has been created and makes an initial UsersData object
     * @param user A firebase user to check the information for.
     */
    suspend fun ensureUserCreated(
        user: FirebaseUser,
    ): UsersBase {
        val uid = user.uid
        val docRef = db.collection("users").document(uid)

        val snap = docRef.get().await()
        val firstTime = !snap.exists()

        if (firstTime) {
            val now = Timestamp.now()
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
                    "completedBadges" to mapOf(
                        "E2QAGgjfhPGAQf2fo17W" to now,
                    )
                )
            ).await()

            //Return the new base data
            return UsersBase(
                userId = uid,
                displayName = user.displayName ?: "",
                email = user.email ?: "",
                photoUrl = user.photoUrl?.toString() ?: "",
                createdAt = now,
                onboardingComplete = false,
                completedBadges = mapOf(
                    "E2QAGgjfhPGAQf2fo17W" to now,
                )
            )
        } else {
            // existing user: only bump lastUpdate (do NOT overwrite stats/lifePoints)
            docRef.update("lastUpdate", FieldValue.serverTimestamp()).await() //Update is a smaller payload
            return snap.toObject(UsersBase::class.java) ?: UsersBase()
        }
    }

    /**
     * This function creates a user data store in the Firestore Cloud Storage section of the project.
     * It takes a map of userData, with the key being the name of the field to be filled.
     * We use a suspend function because FirebaseFirestore is async
     * @param userData a map of userData, with the key being the name of the field to be filled
     * @param currentUser The user that will be updated in firestore
     * @author thefool309, fdesouza1992
     * @return UsersData?
     */
    suspend fun createUser(
        userData: Map<String, Any>,
        currentUser: FirebaseUser?,
    ): UsersData? {
        if (currentUser == null) {
            logger.e("Auth", "UID is null. Please authenticate user before calling CreateUser...")
            return null
        }

        val uid = currentUser.uid
        val docRef = db.collection("users")
                        .document(uid)
        val now = Timestamp.now()

        val base = mapOf(
            "userId" to uid,
            "displayName" to userData["displayName"].toString(),
            "email" to userData["email"].toString(),
            "photoUrl" to userData["photoUrl"].toString(),
            "createdAt" to FieldValue.serverTimestamp(),
            "lastUpdate" to FieldValue.serverTimestamp(),
            "completedBadges" to mapOf(
                "E2QAGgjfhPGAQf2fo17W" to now
            )
        )
        return try {
            docRef.set(base).await()
            val base = UsersBase(
                userId = uid,
                displayName = userData["displayName"].toString(),
                email = userData["email"].toString(),
                photoUrl = userData["photoUrl"].toString(),
                completedBadges = mapOf(
                    "E2QAGgjfhPGAQf2fo17W" to now
                )
            )
            UsersData(userBase = base)
        }
        catch (e: Exception) {
            // unknown error saving user to Firebase
            logger.e(TAG, "Error Saving User: ", e)
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

    //endregion

    //region User Edit Functions
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
    suspend fun editDisplayName(userName: String, userId: String?) : Boolean {
        if(userId == null) {
            logger.e(TAG,"ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        if(userName.isBlank()) {
            logger.e(TAG,"User name is empty. Please add user name...")
            return false
        }
        try {
            docRef.update("displayName", userName)
            .await()
            updateTimestamp(userId)
            return true
        }
        catch (e: Exception) {
            logger.e(TAG, "Error Updating User: ", e)
            return false
        }
    }
    /**
     * A function for editing the Users "email" field in the firestore data.
     * This will take the new email and update the field in Firestore Cloud storage.
     * @param email A string containing the updated email.
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     * @author thefool309
     * @return Boolean
     * @see ILogger
     */
    suspend fun editEmail(email: String, userId: String?) : Boolean {
        if(userId == null) {
            logger.e(TAG,"ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        if(email.isBlank()) {
            logger.e(TAG,"User email is empty. Please add user email.")
            return false
        }
        try {
            docRef.update("email", email).await()
            updateTimestamp(userId)
            return true
        }
        catch (e: Exception) {
            logger.e(TAG, "Error Updating User: ", e)
            return false
        }

    }

    /**
     * A function for editing the value stored as the URL for the users photo they choose to represent themselves in the firestore data.
     * @param url A string representing the URL of the user's photo they choose to represent themselves
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     * @author thefool309
     * @return Boolean
     * @see ILogger
     */
    suspend fun editPhotoUrl(url: String, userId: String?) : Boolean {
        if(userId == null) {
            logger.e(TAG,"ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        if(url.isBlank()) {
            logger.e(TAG,"Photo url is empty. Please add Photo url.")
            return false
        }
        try {
            docRef.update("photoUrl", url).await()
            updateTimestamp(userId)
            return true
        }
        catch (e: Exception) {
            logger.e(TAG, "Error Updating User: ", e)
            return false
        }
    }


    /**
     * increment the streaks property of the users data class in the firestore data
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger
     * @return Boolean
     */
    // This can be done using editUserParameter, update the local value, pass it in as the new value to firebase
    suspend fun incrementStreaks(userId: String?) : Boolean {
        if (userId == null) {
            logger.e(TAG,"User ID is empty. Please make sure you're signed in.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)

        try{
            val data = docRef.get().await()
            var newStreaks = data["streaks"] as Long
            docRef.update("streaks", ++newStreaks).await()
            updateTimestamp(userId)
            return true
        }
        catch (e: Exception) {
            logger.e(TAG, "Error Updating User: ", e)
            return false
        }
    }

    /**
     * set the number coins to the Users firebase balance
     * @return Boolean
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger
     */

    suspend fun setStats(stats: Stats, userId: String?) : Boolean {
        if (userId == null) {
            logger.e(TAG,"ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        try{
            docRef.update("stats", stats).await()
            updateTimestamp(userId)
            return true
        }
        catch (e: Exception){
            logger.e(TAG, "Error Updating User: ", e)
            return false
        }

    }
    /**
     * set the currHealth value in the firestore data
     * @return Boolean
     * @param health  a long that contains the new balance
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger
     */
    suspend fun setCurrHealth(health: Long, userId: String?) : Boolean {
        if(userId == null) {
            logger.e(TAG,"ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        try {
            val data = docRef.get().await()
            docRef.update("currHealth", health).await()
            updateTimestamp(userId)
            return true
        }
        catch(e: Exception) {
            logger.e(TAG, "Error Updating User: ", e)
            return false
        }
    }
    /**
     * set the coins value in the Users firebase balance
     * @return Boolean
     * @param coins  a long that contains the new balance
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger
     */
    suspend fun setCoins(coins: Long, userId: String?) : Boolean {
        if(userId == null) {
            logger.e(TAG,"ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        try {
            docRef.update("coinsBalance", coins).await()
            updateTimestamp(userId)
            return true
        }
        catch(e: Exception) {
            logger.e(TAG, "Error Updating User: ", e)
            return false
        }
    }

    /**
     * add coins to the Users firebase balance
     * @return Boolean
     * @param coins  a long that contains the amount of coins to add
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger
     */
    suspend fun addCoins(coins: Long, userId: String?) : Boolean {
        if(userId == null) {
            logger.e(TAG,"ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        try {
            val data = docRef.get().await()
            var newCoinsBalance = data["coinsBalance"] as Long
            newCoinsBalance += coins
            docRef.update("coinsBalance", newCoinsBalance).await()
            updateTimestamp(userId)
            return true
        }
        catch(e: Exception) {
            logger.e(TAG, "Error Updating User: ", e)
            return false
        }
    }
    /**
     * a method to subtract coins from the users firebase balance
     * @return Boolean
     * @param coins  a long that contains the amount of coins to subtract
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     */
    suspend fun subtractCoins(coins: Long, userId: String?) : Boolean {
        if(userId == null) {
            logger.e(TAG,"ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        try {
            val data = docRef.get().await()
            var newCoinsBalance = data["coinsBalance"] as Long
            newCoinsBalance -= coins
            docRef.update("coinsBalance", newCoinsBalance).await()
            updateTimestamp(userId)
            return true
        }
        catch(e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
            return false
        }
    }

    /**
     * A toggler for setOnboardingComplete in the firestore database. should be called after the user has walked through a tutorial
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     * @returns Boolean
     * @author thefool309
     */

    suspend fun setOnboardingComplete(userId: String?) : Boolean {
        if(userId == null) {
            logger.e(TAG,"ID is null. Please login to firebase.")
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
            updateTimestamp(userId)
            true
        }
        catch (e: Exception) {
            logger.e(TAG, "Error Updating User: ", e)
            false
        }
    }

    /**
     * an overload to pass in a specific value to set for onboardingComplete
     * @return boolean
     * @param onboardingComplete the value that is passed in to change onboardingComplete to
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     */
    suspend fun setOnboardingComplete(onboardingComplete: Boolean, userId: String?) : Boolean {
        if(userId == null) {
            logger.e(TAG,"ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
        .document(userId)
        try {
            docRef.update("onboardingComplete", onboardingComplete).await()
            updateTimestamp(userId)
            return true
        }
        catch (e: Exception) {
            logger.e("FireStore", "Error Updating User: ", e)
            return false
        }
    }

    /**
     * Increments the players level by one in the firestore data
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     * @see ILogger
     * */
    suspend fun incrementLevel(userId: String?) : Boolean {
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
            updateTimestamp(userId)
            true
        }
        catch(e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
            return false
        }
    }

    /**
     * A function for adding Xp to the users firestore data
     * @param xp A double representing the amount of xp to be added
     * @param logger A double representing the amount of xp to be added
     * @see ILogger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     * @author thefool309, fdesouza1992
     */
    suspend fun addXp(xp: Double, userId: String?) : UsersData? {
        if(userId == null) {
            logger.e(TAG,"ID is null. Please login to firebase.")
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

            var user = getUser(userId) ?: run {
                logger.e(TAG, "Error Updating User: Please make sure you're logged in")
                return null
            }

            if (newXp >= user.xpToNextLevel.toDouble()) {
                if (!incrementLevel(userId)) {
                    logger.e(TAG, "Level increment failed")
                }
                user = getUser(userId) ?: return null
                user.calculateXpToNextLevel()
            }
            user
        } catch (e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
            null
        }
    }

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

        // Badges are stored as a nested map
        val completedBadges =
            (data["completedBadges"] as? Map<*, *>)?.mapNotNull { (key, value) ->
                val badgeId = key as? String ?: return@mapNotNull null
                val ts = value as? Timestamp ?: return@mapNotNull null
                badgeId to ts
            }?.toMap() ?: emptyMap()

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
            completedBadges    = completedBadges,
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

        // Load in badges
        val badges = try {
            badgeCol()
                .get().await().documents.mapNotNull { doc ->
                    doc.toObject(Badge::class.java)?.copy(badgeId = doc.id)
                }
        } catch (e: Exception) {
            logger.e("Firestore", "Error loading badge list", e)
            emptyList()
        }

        return UsersData(
            userBase = user,
            reminders = remindersList,
            streaks = streaksList,
            badges = badges,
        )
    }

    /**
     * Tries to fully delete the currently signed-in user and their data.
     *
     * Method Flow:
     * 1. Checks for the user's ID. If there is not a user file associated with it, it will log it and stop.
     * 2. Delete any existing subcollections and logs errors but keeps going
     * 3. Delete the user document from the 'users' collection in Firestore Database
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

    // Method to set life points mirroring the behavior of existing setCoins and addCoins methods
    suspend fun setLifePoints(lifePoints: Long, uid: String?): Boolean {
        if (uid == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return false
        }
        return try {
            db.collection("users").document(uid)
                .update("lifePoints", lifePoints)
                .await()
            updateTimestamp(uid)
            true
        } catch (e: Exception) {
            logger.e("Firestore", "Error updating lifePoints", e)
            false
        }
    }

    //endregion

    //region Badge Handling

    /**
     * A reference to the path of the badge collection
     * @author Elyseia
     */
    fun badgeCol() =
        db.collection("badges")

    /**
     * Adds the ID and time stamp of the completed badge to Firestore.
     */
    suspend fun saveCompletedBadge(
        uid: String,
        badgeId: String,
        completedAt: Timestamp,
    ) {
        try {
            val updateMap = mapOf(
                "completedBadges.$badgeId" to Timestamp.now()
            )

            db.collection("users")
                .document(uid)
                .update(updateMap)
                .await()

            logger.d("Firestore", "Badge $badgeId marked completed")
        } catch (e: Exception) {
            logger.e("Firestore", "Error saving completed badge $badgeId", e)
        }
    }

    //endregion

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


    //region Streak handling
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

    //endregion

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
     * A method to create an FcmToken for the FcmTokens field. This field is generated by firebase when the user does things like
     * reinstall the app, log in on a new device, or wipe the data on their device.
     * @param token the token for the users new firebaseToken
     * @param logger
     * @return Boolean
     */
    suspend fun setFirebaseToken(token: String?, uID: String?) : Boolean {
        if(uID == null) {
            logger.e(TAG,"ID is null. Please login to firebase.")
            return false
        }
        if(token == null) {
            logger.e(TAG, "Token is null. Please login to firebase.")
            return false
        }
        // document reference


        try {
            val docRef = db.collection("fcmTokens").document(uID)
            docRef.update(mapOf("token" to token,
                "uID" to uID,
                "lastUpdate" to Timestamp.now())
            ).await()
            return true
        }
        catch (e: Exception) {
            logger.e(TAG, "Error Updating User: ", e)
            return false
        }
    }

    /**
     * Awards XP, coins, and progression updates when a reminder is completed.
     *
     * This method is the single source of truth for applying gameplay rewards tied to reminder completion.
     * It performs two major responsibilities:
     * 1) Records the reminder completion for the given date.
     * 2) Atomically updates the user's progression inside a Firestore transaction:
     *    - XP gain
     *    - Level ups (if applicable)
     *    - Life points increase on level up
     *    - Coin rewards (including level-up bonus coins)
     *
     * Flow overview:
     * - Validates that the user is authenticated.
     * - Increments the reminder completion count for the selected reminder/date.
     * - Loads the current user snapshot inside a Firestore transaction.
     * - Calculates EXP and coins using RewardsCalculator.
     * - Applies level-up logic if XP crosses the next level threshold:
     *     - Carries over leftover XP.
     *     - Increments the user's level.
     *     - Grants +5 life points.
     *     - Awards additional bonus coins for leveling up.
     * - Persists all updated values atomically to prevent race conditions.
     *
     * @param reminderId Firestore ID of the completed reminder.
     * @param reminderTitle Human-readable title (used for completion tracking / logs).
     * @param date The date the reminder was completed for.
     * @param logger Logger used for diagnostics and error reporting.
     * @return true if the reward transaction succeeds; false otherwise.
     * @author fdesouza1992
     */
    suspend fun awardForReminderCompletion(
        reminderId: String,
        reminderTitle: String,
        date: LocalDate,
        logger: ILogger,
    ): Boolean {
        val uid = getUserId()
        if (uid.isNullOrBlank()) {
            logger.e(TAG, "awardForReminderCompletion: user not authenticated")
            return false
        }

        return try {
            val completionOk = reminderRepo.incrementReminderCompletionForDate(
                reminderId = reminderId,
                reminderTitle = reminderTitle,
                date = date,
                logger = logger
            )
            if (!completionOk) return false

            val userRef = db.collection("users").document(uid)

            db.runTransaction { tx: Transaction ->
                val snap = tx.get(userRef)
                val user = snap.toObject(Users::class.java)
                    ?: throw IllegalStateException("User doc missing")

                val expEarned = RewardsCalculator.calcExpForReminderCompletion(user)
                val coinsEarned = RewardsCalculator.calcCoinsForReminderCompletion(user)

                val currentLevel = user.level
                val currentXp = user.currentXp
                val nextXp = user.xpToNextLevel.toDouble() // derived from level

                var newLevel = currentLevel
                var newXp = currentXp + expEarned
                var newLifePoints = user.lifePoints
                var extraLevelCoins = 0L

                // Level up logic
                if (newXp >= nextXp) {
                    val leftover = newXp - nextXp
                    newLevel = currentLevel + 1
                    newXp = leftover
                    newLifePoints = user.lifePoints + 5

                    extraLevelCoins = RewardsCalculator.levelUpBonusCoins(
                        newLevel = newLevel,
                        completionCoins = coinsEarned
                    ) - coinsEarned
                }

                val totalCoinsToAdd = coinsEarned + extraLevelCoins

                // Update fields in Firestore
                tx.update(userRef, mapOf(
                    "level" to newLevel,
                    "currentXp" to newXp,
                    "lifePoints" to newLifePoints,
                    "coinsBalance" to (user.coinsBalance + totalCoinsToAdd),
                    "lastUpdate" to FieldValue.serverTimestamp(),
                ))

                null
            }.await()

            true
        } catch (e: Exception) {
            logger.e(TAG, "awardForReminderCompletion failed", e)
            false
        }
    }

    /**
     * Returns doc object on success or returns null on failure
     * @param db FireStore instance giving access to firestore
     * @param onComplete callback - receives terms if success or null if failure - used cause FB is async- have to wait for data to return - when returns
     *@author sgcfsu1993
     */
    fun termsFireBaseFetch(onComplete: (Terms?) -> Unit) {
        db.collection("legalDocuments").document("terms").get()
            .addOnSuccessListener {
                    doc -> onComplete(doc.toObject(Terms::class.java))
            }.addOnFailureListener { onComplete(null)}
    }

    /**
     * Returns doc object on success or returns null on failure
     * @param db FireStore instance giving access to firestore
     * @param onComplete callback - receives terms if success or null if failure - used cause FB is async- have to wait for data to return - when returns
     *@author sgcfsu1993
     */
    fun privacyFireBaseFetch(onComplete: (Privacy?) -> Unit) {
        db.collection("legalDocuments").document("privacy").get()
            .addOnSuccessListener {
                    doc -> onComplete(doc.toObject(Privacy::class.java))
            }.addOnFailureListener { onComplete(null)}
    }
}