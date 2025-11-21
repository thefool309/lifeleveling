package com.lifeleveling.app.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Information for the user that WILL be written into firebase
 */
data class UserData(
    val level: Int = 1,
    val currentExp: Int = 0
)

/**
 * Information that will NOT be written in firebase
 */
data class UserLocals(
    val userData: UserData? = null,
    val expToNextLevel: Int = 0,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val isLoggedIn: Boolean = false,
)

/**
 * Manages the local state of the user, writing to firebase, pulling from firebase, and more
 */
class UserManager(
    private val authRepo: AuthRepository = AuthRepository(),
    private val userRepo: UserRepository = UserRepository()
) : ViewModel() {
    private val userLocals = MutableStateFlow(UserLocals())
    val uiState: StateFlow<UserLocals> = userLocals.asStateFlow()

    // Initialization
    init {
        // Listens for login/logout
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                userLocals.update {
                    it.copy(
                        isLoading = false,
                        userData = null,
                        expToNextLevel = 0,
                        )
                }
            } else {
                viewModelScope.launch { loadUser() }
            }
        }
        authRepo.addAuthStateListener(listener)
    }


    // ================== Functions =======================================================

    // ================== Firestore managing functions ==========
    // Load user from firestore
    suspend fun loadUser() {
        val uid = authRepo.currentUser?.uid ?: return
        userLocals.update { it.copy(isLoading = true, errorMessage = null) }

        try {
            val data = userRepo.loadUser(uid)
            if (data != null) {
                updateLocalVariables(data)
                userLocals.update { it.copy(isLoading = false, isLoggedIn = true) }
            } else {
                createNewUser()
            }
        } catch (e: Exception) {
            userLocals.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
        }
    }

    // Write user into firestore
    suspend fun saveUser() {
        val user = userLocals.value.userData ?: return
        val uid = authRepo.currentUser?.uid ?: return

        userLocals.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            userRepo.saveUser(uid, user)
            userLocals.update { it.copy(isLoading = false) }
        } catch (e: Exception) {
            userLocals.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
        }
    }

    // Create a new User
    suspend fun createNewUser() {
        val user = UserData()
        val uid = authRepo.currentUser?.uid ?: return

        updateLocalVariables(user)
        userLocals.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            userRepo.createNewUser(uid, user)
            userLocals.update { it.copy(isLoading = false, isLoggedIn = true) }
        } catch (e: Exception) {
            userLocals.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
        }
    }

    // ========= Calculating Local Logic Variable Functions ==========
    // Broad function for any smaller ones so they all get loaded at once
    // Used after reading from firebase
    private fun updateLocalVariables(userData: UserData) {
        userLocals.update { current ->
            current.copy(
                userData = userData,
                expToNextLevel = calcExpToNextLevel(userData.level)
            )
        }
    }

    private fun calcExpToNextLevel(level: Int) = 100 * level

    // ============ Functions for changing variables =================
    fun addExp(amount: Int) {
        val user = userLocals.value.userData ?: return
        val next = userLocals.value.expToNextLevel
        val newExp = user.currentExp + amount

        val updated = if (newExp >= next) {
            val leftover = newExp - next
            user.copy(level = user.level + 1, currentExp = leftover)
        } else {
            user.copy(currentExp = newExp)
        }
        updateLocalVariables(updated)
    }

    // ================== Auth Functions =========================
    fun login(email: String, password: String) = viewModelScope.launch {
        try {
            userLocals.update { it.copy(isLoading = true, errorMessage = null) }
            authRepo.login(email, password)
            loadUser()
        } catch (e: Exception) {
            userLocals.update { it.copy(errorMessage = e.localizedMessage) }
        } finally {
            userLocals.update { it.copy(isLoading = false) }
        }
    }

    fun register(email: String, password: String) = viewModelScope.launch {
        try {
            userLocals.update { it.copy(isLoading = true, errorMessage = null) }
            authRepo.register(email, password)
            createNewUser()
        } catch (e: Exception) {
            userLocals.update { it.copy(errorMessage = e.localizedMessage) }
        } finally {
            userLocals.update { it.copy(isLoading = false) }
        }
    }

    fun logout() = authRepo.logout()

    fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        try {
            authRepo.sendPasswordResetEmail(email)
        } catch (e: Exception) {
            userLocals.update { it.copy(errorMessage = e.localizedMessage) }
        }
    }
}