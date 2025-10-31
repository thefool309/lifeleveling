package com.lifeleveling.app.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.tasks.await
import kotlin.Long


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
                intellect = 0L,
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
            data["createdAt"] = FieldValue.serverTimestamp()
        }

        // merge = idempotent; won’t blow away future fields
        docRef.set(data, SetOptions.merge()).await()

        Log.d("FB", "users/$uid created=$firstTime")
        return firstTime
    }
    /**
     * :3c Velma wuz here >^.^<
     */
    // Function to create user and store in firebase
    // returns null on failure. We use a suspend function because
    // FirebaseFirestore is async
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
        try {
            val data = docRef.get().await()
            val onboarding = data["onboarding"] as Boolean
            if(onboarding) {
                docRef.update("onboardingComplete", false).await()
            }
            else {
                docRef.update("onboardingComplete", true).await()
            }
            updateTimestamp(userId, logger)
            return true
        }
        catch (e: Exception) {
            logger.e("FireStore", "Error Updating User: ", e)
            return false
        }
    }
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
        try {
            val data = docRef.get().await()
            var newLevel = data["level"] as Int
            newLevel = ++newLevel
            docRef.update("level", newLevel).await()
            updateTimestamp(userId, logger)
            return true
        }
        catch(e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
            return false
        }
    }

    suspend fun addXp(xp: Double, logger: ILogger) : Users? /*returns the updated User or null if it fails*/ {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return null
        }
        val docRef = db.collection("users")
            .document(userId)
        try {
            val data = docRef.get().await()
            var newXp = data["currXp"] as Double
            newXp += xp
            docRef.update("xp", newXp).await()
            var user = getUser(userId, logger)
            if(user == null) {
                logger.e("Auth", "Error Updating User: Please make sure you're logged in")
                return null
            }
            if(newXp > user.xpToNextLevel) {
                incrementLevel(logger)
                user = getUser(userId, logger)
                if(user == null) {
                    return null
                }
                user.calculateXpToNextLevel()
            }
            return user
        }
        catch(e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
            return null
        }
    }

    suspend fun getUser(uID: String?, logger: ILogger): Users? {
        // get the document snapshot
        var result: Users?
        if (uID == null) {
            logger.e("Auth", "User ID null when trying to retrieve user please ensure you're signed in.")
            return null
        }
        if (uID.isEmpty() || uID.isBlank()) {
            logger.e("Auth", "User ID is empty or blank")
            return null
        }
        var docRef: DocumentReference?
        try {
            docRef = db.collection("users").document(uID)
        }
        catch(e: Exception) {
            logger.e("Auth", "Error Getting User: ", e)
            return null
        }
        val docSnapshot = docRef.get().await()
        // extract the data from the snapshot
        var data: Map<String, *>?

        try {
            data = docSnapshot.data
            val userId = data!!["userId"] as String
            val displayName = data["displayName"] as String
            val email = data["email"] as String
            val photoUrl = data["photoUrl"] as String
            val coinsBalance = data["coinsBalance"] as Long
            val stats = data["stats"] as Stats // Changes to stats object made this type checking safer
            val streaks = data["streaks"] as Long
            val onboardingComplete = data["onboardingComplete"] as Boolean
            val createdAt = data["createdAt"] as Timestamp
            val lastUpdate = data["lastUpdate"] as Timestamp
            val lifePoints = data["lifePoints"] as Long
            val level = data["level"] as Long
            val currXp = data["currXp"] as Double
            val currHealth = data["currHealth"] as Long
            val badgesLocked = data["badgesLocked"] as List<Badge>
            val badgesUnlocked = data["badgesUnlocked"] as List<Badge>
            result = Users(userId, displayName, email, photoUrl, coinsBalance, stats, streaks, onboardingComplete, createdAt, lastUpdate, level, lifePoints, currXp,currHealth, badgesLocked, badgesUnlocked)
            // return the data as a Users object.
        }
        catch (e: Exception) {
            logger.e("Firestore", "Error Getting User Data: ", e)
            return null
        }

        return result
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

    // TODO: add deleteUser() to the users crud

    suspend fun deleteUser(logger: ILogger): Boolean  {
        val uid: String? = getUserId()
        if (uid == null) {
            logger.e("Auth", "User ID is null. Please login to firebase.")
            return false
        }
        TODO("Not Implemented yet")
    }

    /**
     * >^w^<
     */

    private fun remindersCol(uid: String) =
        //Firebase.firestore.collection("users").document(uid).collection("reminders")
        db.collection("users").document(uid).collection("reminders")

    // Creates a new reminder for the current user.
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
}
