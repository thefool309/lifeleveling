package com.lifeleveling.app.data

import android.util.Log
import com.lifeleveling.app.BuildConfig
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.tasks.await
import kotlin.Long

/**
 * A library of CRUD functions for our Firestore Cloud Database.
 * This is instantiated as an object, then the functions are called from the object.
 * This is for access to private member variables(properties),
 * to simplify the use of `Firebase.firestore` and `Firebase.auth`.
 * @author fdesouza1992
 * @author thefool309
 * @property db a shortened alias for `Firebase.firestore`
 * @property auth a shortened alias for `Firebase.auth`
 * @property logTag a tag added by thefool309 for debugging purposes. I chose a centralized tag so we could quickly identify what file any log is coming from
 */
class FirestoreRepository {
    private val auth = Firebase.auth

    private val db = Firebase.firestore

    private val logTag = "FirestoreRepository"

    // Helper functions
    fun getUserId() : String? {
        return auth.currentUser?.uid
    }

    /**
     * a function for updating the "lastUpdate" property of the users data class in the firestore data
     */
    private fun updateTimestamp(userId: String, logger: ILogger) {
        try {
            db.collection("users")
                .document(userId)
                .update("lastUpdate", Timestamp.now())
        }
        catch (e: Exception) {
            logger.e(logTag, "Error Updating Timestamp", e)
        }
    }

    suspend fun ensureUserCreated(user: FirebaseUser): Boolean {
        val uid = user.uid
        val docRef = db.collection("users").document(uid)

        val snap = docRef.get().await()
        val firstTime = !snap.exists()

        // Compose the write payload using your Users model defaults
        val model = UserDocument()

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
     * @author thefool309, fdesouza1992
     * @return Users?
     * @see ILogger
     */
    suspend fun createUser(userData: Map<String, Any>, logger: ILogger): UserDocument? {
        val currentUser = auth.currentUser

        return if (currentUser != null) {
            val uid = currentUser.uid
            val docRef = db.collection("users")
                            .document(uid)

            val result = UserDocument(
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
                logger.e(logTag, "Error Saving User: ", e)
                null
            }

        } else {
            // No user is signed in
            logger.e(logTag, "UID is null. Please authenticate user before calling CreateUser...")
            null
        }

    }

    /**
     * This function is designed for specifically updating the users displayName.
     * The displayName field is synonomous with a "username."
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
            logger.e(logTag,"ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        if(userName.isBlank()) {
            logger.e(logTag,"User name is empty. Please add user name...")
            return false
        }
        try {
            docRef.update("displayName", userName)
            .await()
            updateTimestamp(userId, logger)
            return true
        }
        catch (e: Exception) {
            logger.e(logTag, "Error Updating User: ", e)
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
    suspend fun editEmail(email: String, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e(logTag,"ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        if(email.isBlank()) {
            logger.e(logTag,"User email is empty. Please add user email.")
            return false
        }
        try {
            docRef.update("email", email).await()
            updateTimestamp(userId, logger)
            return true
        }
        catch (e: Exception) {
            logger.e(logTag, "Error Updating User: ", e)
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
    suspend fun editPhotoUrl(url: String, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e(logTag,"ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        if(url.isBlank()) {
            logger.e(logTag,"Photo url is empty. Please add Photo url.")
            return false
        }
        try {
            docRef.update("photoUrl", url).await()
            updateTimestamp(userId, logger)
            return true
        }
        catch (e: Exception) {
            logger.e(logTag, "Error Updating User: ", e)
            return false
        }
    }


    /**
     * increment the streaks property of the users data class in the firestore data
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger
     * @return Boolean
     */
    suspend fun incrementStreaks( logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if (userId == null) {
            logger.e(logTag,"User ID is empty. Please make sure you're signed in.")
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
            logger.e(logTag, "Error Updating User: ", e)
            return false
        }
    }

    /**
     * set the number coins to the Users firebase balance
     * @return Boolean
     * @param stats  a stats() object that represents the nested table in the UserDocument
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger
     */

    suspend fun setStats(stats: Stats, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if (userId == null) {
            logger.e(logTag,"ID is null. Please login to firebase.")
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
            logger.e(logTag, "Error Updating User: ", e)
            return false
        }

    }
    /**
     * set the currHealth value in the firestore data
     * @return Boolean
     * @param health  a long that contains the new balance
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger
     */
    suspend fun setCurrHealth(health: Long, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e(logTag,"ID is null. Please login to firebase.")
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
            logger.e(logTag, "Error Updating User: ", e)
            return false
        }
    }
    /**
     * set the coins value in the Users firebase balance
     * @return Boolean
     * @param coins  a long that contains the new balance
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger
     */
    suspend fun setCoins(coins: Long, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e(logTag,"ID is null. Please login to firebase.")
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
            logger.e(logTag, "Error Updating User: ", e)
            return false
        }
    }

    /**
     * add coins to the Users firebase balance
     * @return Boolean
     * @param coins  a long that contains the amount of coins to add
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger
     */
    suspend fun addCoins(coins: Long, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e(logTag,"ID is null. Please login to firebase.")
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
            logger.e(logTag, "Error Updating User: ", e)
            return false
        }
    }
    /**
     * a method to subtract coins from the users firebase balance
     * @return Boolean
     * @param coins  a long that contains the amount of coins to subtract
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     */
    suspend fun subtractCoins(coins: Long, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e(logTag,"ID is null. Please login to firebase.")
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

    /**
     * save whole user. DO NOT USE THIS WITHOUT PURPOSE.
     * This is going to end up being a long operation with a lot of validation to the input into it,
     * so using this willy nilly will not only complicate the logic, but cause the apps performance to dip
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     * @returns Boolean
     * @author thefool309
     */
    suspend fun saveUser(user: UserDocument, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if (userId == null) {
            if(BuildConfig.DEBUG){ logger.e(logTag,"ID is null. Please login to firebase.") }
            return false
        }
        val docRef = db.collection("users")
            .document(userId)
        try {
            docRef.set(user).await()
            if(BuildConfig.DEBUG){ logger.e(logTag,"User Successfully updated.") }
            return true
        }catch(e: Exception) {
            logger.e(logTag, "Error Updating User: ", e)
            return false
        }
    }
    /**
     * A toggler for setOnboardingComplete in the firestore database. should be called after the user has walked through a tutorial
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     * @returns Boolean
     * @author thefool309
     */

    suspend fun setOnboardingComplete(logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e(logTag,"ID is null. Please login to firebase.")
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
            logger.e(logTag, "Error Updating User: ", e)
            false
        }
    }

    /**
     * an overload to pass in a specific value to set for onboardingComplete
     * @return boolean
     * @param onboardingComplete the value that is passed in to change onboardingComplete to
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     * @see
     */
    suspend fun setOnboardingComplete(onboardingComplete: Boolean, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e(logTag,"ID is null. Please login to firebase.")
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

    /**
     * Increments the players level by one in the firestore data
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     * @see ILogger
     * @author thefool309
     * */
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

    /**
    * A function for adding Xp to the users firestore data
    * @param xp A double representing the amount of xp to be added
     * @param logger A double representing the amount of xp to be added
     * @see ILogger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     * @author thefool309, fdesouza1992
     */
    suspend fun addXp(xp: Long, logger: ILogger) : Boolean? {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e(logTag,"ID is null. Please login to firebase.")
            return null
        }
        val docRef = db.collection("users")
            .document(userId)
        return try {
            val data = docRef.awaitAndMapObject<UserState>()
            var newXp: Long = 0
            // read either "currentXp" (new) or "currXp" (legacy)
            try {
                val current = data?.userDoc?.currentXp
                 newXp = current!! + xp
            }
            catch(ne: NullPointerException) {
                logger.e(logTag, "Error Updating User: ", ne)
                false
            }
            // write back to "currentXp" (canonical)
            docRef.update("currentXp", newXp).await()
            var user: UserState? = null;
            user =  UserState(getUser(userId, logger)) // right side operands of these statements became unreachable

            if (newXp >= user.xpToNextLevel.toDouble()) {
                if (!incrementLevel(logger)) {
                    logger.e(logTag, "Level increment failed")
                }
                user = UserState(getUser(userId, logger)) // right side operands of these statements became unreachable

            }
            true
        } catch (e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
            false
        }
    }

    /**
     * A method to create an FcmToken for the FcmTokens field. This field is generated by firebase when the user does things like
     * reinstall the app, log in on a new device, or wipe the data on their device.
     * @param token the token for the users new firebaseToken
     * @param logger
     * @return Boolean
     */
    suspend fun setFirebaseToken(token: String?, logger: ILogger) : Boolean {

        // get user ID
        val uID: String? = getUserId()
        if(uID == null) {
            logger.e(logTag,"ID is null. Please login to firebase.")
            return false
        }
        if(token == null) {
            logger.e(logTag, "Token is null. Please login to firebase.")
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
            logger.e(logTag, "Error Updating User: ", e)
            return false
        }
    }
    /**
     * Extension function to asynchronously fetch and map a DocumentReference to a specific type T.
     * In standard Kotlin generics, type information is erased at runtime. You usually can't know the exact class T is referring to when the function runs.
     * Firebase's snapshot.toObject<T>() method needs the actual Kotlin Class<T> instance at runtime so it can instantiate the correct object using reflection.
     * The reified keyword solves this by telling the compiler to keep the type information for T available at runtime.
     * When a function is marked inline, the Kotlin compiler essentially copies the body of the function directly into the place where it is called, instead of creating a standard function call (which involves creating a new stack frame, etc.).
     * For a small, generic utility function like this one, inlining prevents the minor overhead associated with function calls, making the final bytecode slightly more efficient.
     * @see getUser
     * @param
     */
    suspend inline fun <reified T : Any> DocumentReference.awaitAndMapObject(): T? {
        // Call get() on the reference and use .await() to suspend the coroutine
        val documentSnapshot = this.get().await()

        // Use .toObject<T>() to automatically map the data
        return documentSnapshot.toObject<T>()
    }
    /**
     * A function for retrieving the full `Users` object from the firebase. Returns null on failure
     * @param uID the userId you're looking for
     * @param logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
     * @returns Users?
     * @see UserDocument
     * @see ILogger
     * @see com.lifeleveling.app.util.AndroidLogger
     */
    suspend fun getUser(uID: String?, logger: ILogger): UserDocument? {
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
            agility       = (statsMap["agility"] as? Number)?.toLong() ?: 0L,
            defense       = (statsMap["defense"] as? Number)?.toLong() ?: 0L,
            intelligence  = (statsMap["intelligence"] as? Number)?.toLong() ?: 0L,
            strength      = (statsMap["strength"] as? Number)?.toLong() ?: 0L,
            health        = (statsMap["health"] as? Number)?.toLong() ?: 0L,
        )


        val user = UserDocument(
            userId             = data["userId"] as? String ?: uID,
            displayName        = data["displayName"] as? String ?: "",
            email              = data["email"] as? String ?: "",
            photoUrl           = data["photoUrl"] as? String ?: "",
            coinsBalance       = num("coinsBalance"),
            stats              = stats,
            streaks            = data["streaks"] as List<Streak>,
            onboardingComplete = data["onboardingComplete"] as? Boolean ?: false,
            createdAt          = ts("createdAt"),
            lastUpdate         = ts("lastUpdate"),
            level              = (data["level"] as? Number)?.toLong() ?: 1L,
            lifePoints         = num("lifePoints"),
            // support either "currentXp" (new) or "currXp" (legacy)
            currentXp          = if (data.containsKey("currentXp")) num("currentXp") else num("currXp"),
            currHealth         = num("currHealth"),
            badgesLocked       = emptyList(),   // map arrays if/when needed
            badgesUnlocked     = emptyList(),
        )
        // new method of retrieving user
        val newUser = docRef.awaitAndMapObject<UserDocument>()

        return newUser
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
            "reminderId" to (if (reminders.reminderId.isNotBlank()) reminders.reminderId else null),
            "title" to reminders.title,
            "notes" to reminders.notes,
            "dueAt" to reminders.dueAt,
            "isCompleted" to reminders.isCompleted,
            "completedAt" to reminders.lastCompletedAt,
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
    suspend fun getCurrentUser(logger: ILogger): UserDocument? {
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

    suspend fun setExp(currentXp: Long, logger: ILogger): Boolean {
        val uid = getUserId()
        if (uid == null) {
            logger.e("Auth", "No user found with uid $uid; Please sign in.")
            return false
        }
        return try {
            db.collection("users").document(uid).update("currentXp", currentXp)
            true
        }
        catch(e: Exception) {
            false
        }
    }
}
