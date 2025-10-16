package com.lifeleveling.app.services

import android.util.Log
import androidx.annotation.Nullable
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lifeleveling.app.data.Users
import kotlinx.coroutines.tasks.await

class DBServices {
    private val db = FirebaseFirestore.getInstance()
    // Function to create user and store in firebase
    // returns null on failure. We use a suspend function because
    // FirebaseFirestore is async
    suspend fun CreateUser(userData: Map<String, Any>): Users? {

        val currentUser = FirebaseAuth.getInstance().currentUser

        return if (currentUser != null) {
            val uid = currentUser.uid

            val result = Users(
                userId = uid,
                displayName = userData["displayName"].toString(),
                email = userData["email"].toString(),
                photoUrl = userData["photoUrl"].toString(),
                createdAt = Timestamp.now(),
                lastUpdate = Timestamp.now()
            )
            try {
                FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .document(uid)
                    .set(result)
                    .await()

                result;
            }
            catch (e: Exception) {
                // unknown error saving user to Firebase
                Log.e("Firestore", "Error Saving User: ", e)
                null;
            }

        } else {
            // No user is signed in
            Log.e("Auth", "UID is null. Please authenticate user before calling CreateUser...")
            null;
        }

    }
    // TODO: function to edit user in firebase
    fun EditUser(user: Users) {

    }
    // TODO: function to retrieve user information from firebase
    fun GetUser(uID: String): Users {
        val result = Users();
        return result;
    }
}