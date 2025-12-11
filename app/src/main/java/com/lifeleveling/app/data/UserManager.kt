package com.lifeleveling.app.data

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lifeleveling.app.auth.AuthModel
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.Int
import kotlin.collections.orEmpty

/**
 * Manages the local state of the user.
 * Calls outside functions to write to firebase.
 * Calls outside functions for authenticating the user.
 */
class UserManager(
    private val logger: ILogger = AndroidLogger(),  // Put the creation of the logger here so that any function can access it if it is desired.
    private val authModel: AuthModel = AuthModel(logger = logger),
    private val fireRepo: FirestoreRepository = FirestoreRepository(logger = logger),
) : ViewModel() {
    private val userData = MutableStateFlow(UsersData())
    val uiState: StateFlow<UsersData> = userData.asStateFlow()  // Makes everything react to changes

    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser

        if (user == null) {
            // User logged out so resets to defaults
            userData.value = UsersData()
            return@AuthStateListener
        }

        // User logged in
        viewModelScope.launch {
            userData.update { it.copy(isLoading = true, error = null) }

            try {
                // create user if first time
                fireRepo.ensureUserCreated(user)

                // Load profile
                val loaded = fireRepo.getUser(user.uid)

                // Update UI
                userData.value = loaded!!.copy(
                    isLoggedIn = true,
                    fbUser = user,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("FirestoreRepository", "Error creating user", e)
                userData.update { it.copy(isLoading = false, error = "Error creating user") }
            }
        }
    }

    // Initialization

    init {
        authModel.addAuthStateListener(listener)
    }

    override fun onCleared() { authModel.removeAuthStateListener(listener) }


    // ========================================== Functions =======================================================


    // ============ Functions for changing variables ===============================================
    /**
     * Handles adding experience to the user.
     * Will do level up logic if needed.
     * Leveling up rolls over extra exp, gives coins, and adds 5 life points to the user.
     */
    fun addExp(amount: Double) {
        val user = userData.value.userBase ?: return
        val next = userData.value.xpToNextLevel
        val newExp = user.currentXp + amount
        val updated: UsersBase
        var leveledUp = false
        var coins = 0L

        if (newExp >= next) {
            // Level up
            leveledUp = true
            val leftover = newExp - next
            coins = ((user.level + 1) * 10) + calcCoinsForReminderCompletion()

            updated = user.copy(
                level = user.level + 1,
                currentXp = leftover,
                lifePointsTotal = user.lifePointsTotal + 5,
                coinsBalance = user.coinsBalance + coins,
                allCoinsEarned = user.allCoinsEarned + coins
                )
        } else {
            updated = user.copy(currentXp = newExp)
        }
        // Updates the state and UI
        userData.update {
            it.copy(
                userBase = updated,
                levelUpFlag = leveledUp, // If leveled up the popup will trigger
                levelUpCoins = coins
            ).recalculateAll()
        }
    }

    fun updateTheme(isDark: Boolean) {
        val current = userData.value.userBase ?: return
        val updated = current.copy(isDarkTheme = isDark)
        userData.update { current ->
            current.copy(userBase = updated)
        }
    }

    fun clearLevelUpFlag() {
        userData.update {
            it.copy(
                levelUpFlag = false,
                levelUpCoins = 0L
            )
        }
    }

    // ============ Calculation Functions ===============================================
    /**
     * Calculates the number of coins to give the user for completing a reminder.
     * Uses 10 coins as the base value.
     * Defense gives an extra 2% per point
     * Intelligence gives an extra 5% per point
     * Agility gives an extra 3% per point
     */
    fun calcCoinsForReminderCompletion() : Long {
        val user = userData.value.userBase ?: return 0
        val coins = 10 +
                (10 *
                (
                        (user.stats.defense * .02) +
                        (user.stats.intelligence * .05) +
                        (user.stats.agility * .03)
                )
                )
        return coins.toLong()
    }

    // TODO: Adjust how many decimals for the double
    /**
     * Calculates the amount of exp to give the user for completing a reminder.
     * Uses 15 exp as the base value
     * Strength gives an extra 5% per point
     * Defense gives an extra 3% per point
     * Agility gives an extra 2% per point
     */
    fun calcExpForReminderCompletion() : Double {
        val user = userData.value.userBase ?: return 0.0
        val exp = 15 +
                (15 *
                (
                        (user.stats.strength * .05) +
                        (user.stats.defense * .03) +
                        (user.stats.agility * .02)
                )
                )
        return exp
    }

    // ================== Auth Functions ==========================================================

    /**
     * Handles the Google sign-in result Intent returned to the Activity.
     *
     * Flow:
     * 1. Try to pull the Google account and ID token out of the intent.
     * 2. If the token is there, pass it down to Firebase to finish sign-in.
     * 3. If anything fails, log it and update the UI with an error message.
     *
     * @param data The Intent returned from the Google sign-in Activity result.
     * @author fdesouza1992
     */
    fun handleGoogleResultIntent(data: android.content.Intent?) {
        userData.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val idToken = authModel.handleGoogleResultIntent(data)
                val user = authModel.firebaseAuthWithGoogle(idToken)
                    ?: throw Exception("Firebase user null")
                Log.d("FB", "Google sign-in success: uid=${user.uid}")
                postLoginBookkeeping("google")
                userData.update { it.copy(isLoggedIn = true, isLoading = false, error = null) }
            } catch (e: ApiException) {
                Log.e("FB", "Google sign-in failed", e)
                userData.update { it.copy(isLoading = false, error = "Google sign-in failed: ${e.message}") }
            } catch (e: Exception) {
                Log.e("FB", "Google sign-in failed", e)
                userData.update { it.copy(isLoading = false, error = "Firebase sign-in failed: ${e.message}") }
            }
        }
    }

    /**
     * Signs a user in using email and password and updates the UI state.
     *
     * Flow:
     * 1. Mark the UI as loading.
     * 2. Call Firebase `signInWithEmailAndPassword`.
     * 3. Run post-login work (create user doc, log event, etc.).
     * 4. On different error types, logger logs what happened and sets a friendly message in the UI (no account, wrong password, Google-only, etc.).
     *
     * Note: even though this function is marked `suspend`, it uses viewModelScope.launch` so it never blocks the caller.
     *
     * @param email  The user’s email address.
     * @param password The user’s password.
     * @param logger Used to log warnings and errors during sign-in.
     *
     * @author thefool309, fdesouza1992
     */
    fun signInWithEmailPassword(email: String, password: String)
    {
        userData.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // If this email is Google-only, this call will fail with InvalidCredentials.
                authModel.signInWithEmailPassword(email.trim(), password)
                postLoginBookkeeping(provider = "password")
                userData.update { it.copy(isLoggedIn = true, isLoading = false, error = null) }

            } catch (e: com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                // No account exists with this email
                logger.w("FB", "No user for ${email.trim()}")
                userData.update { it.copy(isLoading = false, error = "No account found for this email.") }

            } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                // Wrong password or email malformed, or Google-only account
                logger.e("FB", "Invalid credentials", e)
                // Tells user if the email is federated-only
                val methods = authModel.fetchSignInMethods(email)
                val msg = if ("google.com" in methods && "password" !in methods) {
                    "This email is registered with Google. Use 'Login using Google'."
                } else {
                    "Invalid email or password."
                }
                userData.update { it.copy(isLoading = false, error = msg) }

            } catch (e: com.google.firebase.auth.FirebaseAuthException) {
                logger.e("FB", "Auth exception", e)
                userData.update { it.copy(isLoading = false, error = "Authentication error.") }

            } catch (e: Exception) {
                logger.e("FB", "Unexpected sign-in error", e)
                userData.update { it.copy(isLoading = false, error = "Sign-in failed.") }
            }
        }
    }

    /**
     * Signs the current user out of Firebase and (optionally) Google.
     *
     * Flow:
     * 1. Mark the UI as loading.
     * 2. Call Firebase `signOut()`.
     * 3. If an Activity is passed in, also sign out of the Google client.
     * 4. Once the Google sign-out finishes, the loading flag clears so the UI can update.
     *
     * @param activity Used to sign out from the Google client *Optional*.
     * @author fdesouza1992
     */
    fun signOut(activity: Activity? = null) {
        userData.update { it.copy(isLoading = true, error = null) }
        authModel.signOut()
        if (activity != null) {
            authModel.googleClient(activity).signOut().addOnCompleteListener {
                userData.update { it.copy(isLoggedIn = false, isLoading = false) }
            }
        } else {
            userData.update { it.copy(isLoggedIn = false, isLoading = false) }
        }
    }

    // I think this will be rewritten after pulling in new branches
//    fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
//        try {
//            authRepo.sendPasswordResetEmail(email)
//        } catch (e: Exception) {
//            userAllData.update { it.copy(errorMessage = e.localizedMessage) }
//        }
//    }

    /**
     * Runs the “after login” work once a user has successfully signed in.
     *
     * Flow:
     * 1. Grab the current Firebase user.
     * 2. In the background, make sure they have a user document in Firestore.
     * 3. Log an auth event to `authLogs` for basic monitoring.
     * 4. Clear out loading and error state in the UI.
     *
     * If the Firestore work fails, we log a warning but don’t block sign-in.
     *
     * @param provider The auth provider string (e.g., "password" or "google").
     * @param logger   For logging any issues while doing post-login work.
     * @author fdesouza1992
     */
    private fun postLoginBookkeeping(provider: String) = viewModelScope.launch {
        val user = authModel.currentUser ?: return@launch
        try {
            fireRepo.ensureUserCreated(user)
        } catch (e: Exception) {
            logger.w("FB", "ensureUserCreated failed: ${e.message}")
        }
        fireRepo.writeBookkeeping(provider, user)
    }

    // ================== Firestore managing functions ==============================================


    // Unsure if we want to make this function because it would require saving all the user data.
    // Can always be split into a smaller save for app closing saves
//    // Write user into firestore
//    suspend fun saveUser() {
//        val user = userData.value.userBase ?: return
//        val uid = authModel.currentUser?.uid ?: return
//        userData.update { it.copy(isLoading = true, error = null) }
//
//        try {
//            fireRepo.saveUser(uid, user)
//        } catch (e: Exception) {
//            userData.update { it.copy(isLoading = false, error = e.localizedMessage) }
//        }
//    }

    // This function would be passed to the application layer to save user data when the app is paused or closed.
    // Can call above function or any new ones made
//    fun saveOnPause() {
//        viewModelScope.launch { saveUser() }
//    }

    // Create a new User
    fun createNewUserWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            userData.update { it.copy(isLoading = true, error = null) }

            try {
                authModel.createUserWithEmailAndPassword(email.trim(), password)
                postLoginBookkeeping(provider = "password")
                userData.update { it.copy(isLoading = false, error = null) }

            } catch (e: com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                // Email already in use.
                val msg = authModel.checkIfEmailInUse(email)
                logger.w("FB", msg)
                userData.update { it.copy(isLoading = false, error = msg) }

            } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                logger.e("FB", "Invalid email/password", e)
                userData.update { it.copy(isLoading = false, error = "Invalid email or password format.") }

            } catch (e: com.google.firebase.auth.FirebaseAuthException) {
                logger.e("FB", "Auth exception", e)
                userData.update { it.copy(isLoading = false, error = "Could not create account.") }

            } catch (e: Exception) {
                logger.e("FB", "Unexpected sign-up error", e)
                userData.update { it.copy(isLoading = false, error = "Sign-up failed.") }
            }
        }
    }

    fun deleteAccount() {
        userData.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val uid = authModel.currentUser?.uid

            try {
                // Delete Firestore user data
                val ok = fireRepo.deleteUser(uid)
                if (!ok) {
                    userData.update { it.copy(isLoading = false, error = "Failed to delete account data. Please try again.")}
                    return@launch
                }

                // Delete Firebase Auth user
                val authOk = authModel.deleteUser(uid)
                if (!authOk) {
                    userData.update { it.copy(isLoading = false, error = "Failed to remove authentication. Please try again.")}
                    return@launch
                }

                // Delete successful
                userData.update { it.copy(isLoggedIn = false, isLoading = false, error = null) }

            } catch (e: Exception) {
                logger.e("Auth", "deleteAccount failed", e)
               userData.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to delete account. Please try again."
                    )
                }
            }
        }
    }

    fun writeLevelUp() {
        val user = userData.value.userBase ?: return
        userData.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                fireRepo.updateMultipleParameters(
                    uid = user.userId,
                    params = mapOf(
                        "level" to user.level,
                        "currentXp" to user.currentXp,
                        "lifePointsTotal" to user.lifePointsTotal,
                        "coinsBalance" to user.coinsBalance,
                        "allCoinsEarned" to user.allCoinsEarned,
                    )
                )
            } catch (e: Exception) {
                logger.e("FB", "Unable to update level up", e)
                userData.update { it.copy(error = "Failed to update level up.") }
            } finally {
                userData.update { it.copy(isLoading = false) }
            }
        }
    }
}