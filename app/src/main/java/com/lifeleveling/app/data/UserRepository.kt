package com.lifeleveling.app.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository (
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun userDoc(uid: String) = firestore.collection("users").document(uid)

    suspend fun loadUser(uid: String): UserData? {
        val snapshot = userDoc(uid).get().await()
        return snapshot.toObject(UserData::class.java)
    }

    suspend fun saveUser(uid: String, userData: UserData) {
        userDoc(uid).set(userData).await()
    }

    suspend fun createNewUser(uid: String, userData: UserData) {
        userDoc(uid).set(userData).await()
    }
}