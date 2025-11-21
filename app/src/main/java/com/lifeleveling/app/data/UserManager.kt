package com.lifeleveling.app.data

import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Information for the user that WILL be written into firebase
 */
data class UserData(
    val level: Int = 1,
    val currentExp: Int = 0
)

/**
 * Manages the local state of the user, writing to firebase, pulling from firebase, and more
 */
object UserManager {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Firestore reference
    private val userDoc: DocumentReference?
        get() = auth.currentUser?.let { user ->
            firestore.collection("users").document(user.uid)
        }


    // ================== User Values =====================================================
    // User State for UI to pull
    var user by mutableStateOf<UserData?>(null)
    private set

    // Other exposed variable for any file to pull that will be calculated locally
    // Do not forget to make a function to do the calculations and/or add it into updateLocalVariables
    var expToNextLevel by mutableStateOf(100)


    // ================== Functions =======================================================

    // ================== Firestore managing functions ==========
    // Load user from firestore
    fun loadUser(
        onResult: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        val doc = userDoc ?: run {
            onResult(false, "No user logged in.")
            return
        }

        doc.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val data = snapshot.toObject(UserData::class.java)
                    if (data != null) {
                        // updates local save of user data
                        user = data
                        // updates local variables based on that data
                        updateLocalVariables(data)
                        onResult(true, null)
                    } else {
                        onResult(false, "Corrupt user data")
                    }
                } else {
                    // First time user so create a new profile
                    createNewUser(onResult)
                }
            }
            .addOnFailureListener { exception ->
                onResult(false, exception.localizedMessage)
            }
    }

    // Write user into firestore
    fun saveUser(onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
        val doc = userDoc ?: run {
            onResult(false, "No user logged in.")
            return
        }

        val current = user ?: run {
            onResult(false, "User data not loaded")
            return
        }

        doc.set(current)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { exception ->
                onResult(false, exception.localizedMessage)
            }
    }

    // Create a new User
    private fun createNewUser(
        onResult: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        val startingData = UserData()
        user = startingData
        updateLocalVariables(startingData)

        userDoc?.set(startingData)
            ?.addOnSuccessListener { onResult(true, null) }
            ?.addOnFailureListener { exception -> onResult(false, exception.localizedMessage) }
    }

    // ========= Calculating Local Logic Variable Functions ==========
    // Broad function for any smaller ones so they all get loaded at once
    // Used after reading from firebase
    private fun updateLocalVariables(userData: UserData) {
        expToNextLevel = calcExpToNextLevel(userData.level)
    }

    private fun calcExpToNextLevel(level: Int): Int {
        return 100 * level
    }

    // ============ Functions for changing variables =================
    fun addExp(amount: Int) {
        val current = user ?: return

        val newExp = current.currentExp + amount
        val nextLevelReq = expToNextLevel

        if (newExp > nextLevelReq) {
            // Level up
            user = current.copy(
                level = current.level + 1,
                currentExp = newExp - nextLevelReq
            )
            expToNextLevel = calcExpToNextLevel(current.level + 1)
        } else {
            user = current.copy(currentExp = newExp)
        }
    }
}