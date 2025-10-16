package com.lifeleveling.app.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

    // TODO: Test createUser
    // Function to create user and store in firebase
    // returns null on failure. We use a suspend function because
    // FirebaseFirestore is async
    suspend fun createUser(userData: Map<String, Any>): Users? {

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
                Log.e("Firestore", "Error Saving User: ", e)
                null
            }

        } else {
            // No user is signed in
            Log.e("Auth", "UID is null. Please authenticate user before calling CreateUser...")
            null
        }

    }
    // TODO: Test editUser function
    // function to edit user in firebase
    suspend fun editUser(userData: Map<String, Any>) : Boolean {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        var result: Boolean
        try {
            db.collection("users")
                .document(userId)
                .update(userData)
                .await()
            result = true
        }
        catch (e: Exception) {
            Log.e("Auth", "Error Updating User: ", e)
            result = false
        }
        return result
    }
    // TODO: function to retrieve user information from firebase
    fun getUser(uID: String): Users {
        val result = Users()
        return result
    }
}
