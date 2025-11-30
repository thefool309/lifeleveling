package com.lifeleveling.app.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository (
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun userDoc(uid: String) = firestore.collection("users").document(uid)

    suspend fun loadUser(uid: String): Users? {
        val snapshot = userDoc(uid).get().await()
        return snapshot.toObject(Users::class.java)
    }

    suspend fun saveUser(uid: String, users: Users) {
        userDoc(uid).set(users).await()
    }

    suspend fun createNewUser(uid: String, users: Users) {
        userDoc(uid).set(users).await()
    }
}