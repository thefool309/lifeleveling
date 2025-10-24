package com.lifeleveling.app.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
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

    private fun getUserId() : String? {
        return auth.currentUser?.uid
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
        }
        catch (e: Exception) {
            logger.e("Auth", "Error Updating User: ", e)
            result = false
        }
        return result
    }

    suspend fun editDisplayName(userName: String, logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")
        .document(userId)
        if(userName.isEmpty()) {
            logger.e("Invalid Parameter","User name is empty. Please add user name...")
            return false
        }
        try {
            docRef.update("displayName", userName)
            .await()
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
        if(email.isEmpty()) {
            logger.e("Invalid Parameter","User email is empty. Please add user email.")
            return false
        }
        try {
            docRef.update("email", email).await()
            return true
        }
        catch (e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
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
            return true
        }
        catch (e: Exception) {
            logger.e("Firestore", "Error Updating User: ", e)
            return false
        }
    }

    suspend fun incrementStrength(logger: ILogger) : Boolean {
        val userId: String? = getUserId()
        if(userId == null) {
            logger.e("Auth","ID is null. Please login to firebase.")
            return false
        }
        val docRef = db.collection("users")  // and waste a ton of time
        .document(userId)
        try {
            val data = docRef.get().await()
            val newStats = (data["stats"] as Map<*, *>).toMutableMap()
            var newStrength = newStats["strength"] as Long
            newStats["strength"] = ++newStrength
            docRef.update("stats", newStats).await()
            return true
        }
        catch (e: Exception) {
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
            return true
        }
        catch (e: Exception) {
            logger.e("FireStore", "Error Updating User: ", e)
            return false
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
            val stats = data["stats"] as Map<*, *> // warning here caused me to change type to Map<*, *> in Users data class to avoid casting issues
            val streaks = data["streaks"] as Long
            val onboardingComplete = data["onboardingComplete"] as Boolean
            val createdAt = data["createdAt"] as Timestamp
            val lastUpdate = data["lastUpdate"] as Timestamp
            val level = data["level"] as Int
            val xpToNextLevel = data["xpToNextLevel"] as Float
            val currentXP = data["currentXP"] as Float
            result = Users(userId, displayName, email, photoUrl, coinsBalance, stats, streaks, onboardingComplete, createdAt, lastUpdate, level, currentXP, xpToNextLevel)
            // return the data as a Users object.
        }
        catch (e: Exception) {
            logger.e("Firestore", "Error Getting User Data: ", e)
            return null
        }

        return result
    }
}
