package com.lifeleveling.app.auth

import android.app.Activity
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.lifeleveling.app.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import com.lifeleveling.app.data.UsersData
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.tasks.await

// These flags were moved into the UsersData object
///**
// * Simple container for what the auth screen needs to know:
// * - who the current user is (if any)
// * - whether we're busy doing an auth call
// * - any error message to show
// *
// * @author fdesouza1992
// * **/
//data class AuthUiState(
//    val user: FirebaseUser? = null,
//    val isLoading: Boolean = false,
//    val error: String? = null
//)

/**
 * Class that owns all of our Firebase/Google sign-in logic.
 * @property auth The FirebaseAuth object to call to
 * @property logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
 * @author fdesouza1992
 * **/
class AuthModel(
    // Firebase auth and Firestore repository instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val logger: ILogger
) {
//    private val repo = FirestoreRepository()

    // Backing field for authentication UI state
//    private val _ui = MutableStateFlow(AuthUiState(user = auth.currentUser))
//    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    /**
     * Velma wuz here :3
     */
    // Helper functions
    private fun getUserId() : String? {
        return auth.currentUser?.uid
    }
    val currentUser get() = auth.currentUser

    /**
     * Clears any current auth error message from the UI state.
     *
     * Call this after the user dismisses an error dialog so we don't keep re-showing the same message.
     *
     * @author fdesouza1992
     */
    fun clearError(user: UsersData) {
        user.error = null
//        _ui.value = _ui.value.copy(error = null)
    }


    // Listener to monitor Firebase authentication state changes
//    private val listener = FirebaseAuth.AuthStateListener { fb ->
//        _ui.value = _ui.value.copy(user = fb.currentUser, isLoading = false, error = null)
//    }
    /**
     * Add the listener to monitor the Firebase authentication state changes
     * @param listener The listener to register
     * @author fdesouza1992
     */
    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.addAuthStateListener(listener)
    }

    // Initialization/ Cleanup
//    init { auth.addAuthStateListener(listener) }
//    override fun onCleared() { auth.removeAuthStateListener(listener) }
    /**
     *  Removes the listener that is observing the Firebase authentication state
     *  @param listener The listener to remove
     *  @author fdesouza1992
     */
    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.removeAuthStateListener(listener)
    }

    /**
     * Builds and returns a GoogleSignInClient configured for our app.
     *
     * This is used by the Activity to launch the Google account picker flow.
     *
     * @param activity The Activity needed to grab resources and build the client.
     * @return A configured GoogleSignInClient ready to start sign-in.
     * @author fdesouza1992
     */
    fun googleClient(activity: Activity): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // .requestIdToken(activity.getString(com.lifeleveling.app.R.string.default_web_client_id))
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, gso)
    }

    // This will be taken care of in UserManager
//    /**
//     * Marks the UI as “loading” when the user starts the Google sign-in flow.
//     *
//     * This is usually called right before we launch the Google sign-in intent, so the UI can show a spinner or disable buttons.
//     * @author fdesouza1992
//     */
//    fun beginGoogleSignIn() {
//        _ui.value = _ui.value.copy(isLoading = true, error = null)
//    }

    // Moved to UserManager and FirestoreRepository
//    /**
//     * Runs the “after login” work once a user has successfully signed in.
//     *
//     * Flow:
//     * 1. Grab the current Firebase user.
//     * 2. In the background, make sure they have a user document in Firestore.
//     * 3. Log an auth event to `authLogs` for basic monitoring.
//     * 4. Clear out loading and error state in the UI.
//     *
//     * If the Firestore work fails, we log a warning but don’t block sign-in.
//     *
//     * @param provider The auth provider string (e.g., "password" or "google").
//     * @param logger   For logging any issues while doing post-login work.
//     * @author fdesouza1992
//     */
//    private fun postLoginBookkeeping(provider: String, logger: ILogger) {
//        val user = auth.currentUser ?: return
//        viewModelScope.launch {
//            try { repo.ensureUserCreated(user) } catch (e: Exception) {
//                logger.w("FB", "ensureUserCreated failed: ${e.message}")
//            }
//        }
//        Firebase.firestore.collection("authLogs")
//            .add(
//                mapOf(
//                    "ts" to com.google.firebase.Timestamp.now(),
//                    "source" to "emailPasswordLogin",
//                    "provider" to provider,
//                    "uid" to user.uid,
//                    "email" to user.email,
//                    "name" to (user.displayName ?: "")
//                )
//            )
//            .addOnFailureListener { e -> logger.w("FB", "Auth Log write failed: ${e.message}") }
//
//        _ui.value = _ui.value.copy(isLoading = false, error = null)
//    }

    /**
     * Handles the Google sign-in result Intent returned to the Activity.
     *
     * Flow:
     * 1. Try to pull the Google account and ID token out of the intent.
     * 2. If the token is there, pass it down to Firebase to finish sign-in.
     *
     * @param data The Intent returned from the Google sign-in Activity result.
     * @return Returns the Google ID of the logged-in user
     * @author fdesouza1992
     */
    suspend fun handleGoogleResultIntent(data: android.content.Intent?) : String {
        val account = GoogleSignIn.getSignedInAccountFromIntent(data).await()
//            .getResult(ApiException::class.java)
        return account.idToken ?: throw Exception("Missing ID token")
    }

    /**
     * Completes Firebase sign-in using a Google ID token.
     *
     * Flow:
     * 1. Exchange the Google ID token for Firebase credentials.
     * 2. Log an auth event to `authLogs` for monitoring.
     *
     * @param idToken The Google ID token returned from the Google sign-in flow.
     * @return Returns the FirebaseUser created from the Google token
     * @author fdesouza1992
     */
    suspend fun firebaseAuthWithGoogle(idToken: String) : FirebaseUser? {
        val cred = GoogleAuthProvider.getCredential(idToken, null)
        return auth.signInWithCredential(cred).await().user
    }

    /**
     * Signs a user in using email and password and updates the UI state.
     * Calls Firebase `signInWithEmailAndPassword`.
     *
     * @param email  The user’s email address.
     * @param password The user’s password.
     *
     * @author thefool309, fdesouza1992
     */
    suspend fun signInWithEmailPassword(email: String, password: String)
    {
        // If this email is Google-only, this call will fail with InvalidCredentials.
        auth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    suspend fun fetchSignInMethods(email: String): List<String> {
        return auth.fetchSignInMethodsForEmail(email.trim()).await().signInMethods.orEmpty()
    }

    /**
     * Creates a new Firebase user with email and password, then signs them in.
     *
     * Flow:
     * 1. Call `createUserWithEmailAndPassword`.
     * 2. Immediately sign the user in with the same credentials.
     *
     * @param email  The email for the new account.
     * @param password The password for the new account.
     * @author thefool309, fdesouza1992
     */
    suspend fun createUserWithEmailAndPassword(email: String, password: String)
    {
        auth.createUserWithEmailAndPassword(email.trim(), password).await()
        auth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    suspend fun checkIfEmailInUse(email: String) : String {
        val methods = auth.fetchSignInMethodsForEmail(email.trim()).await().signInMethods.orEmpty()
        val msg = if ("google.com" in methods && "password" !in methods) {
            "This email is registered with Google. Use 'Login using Google'."
        } else {
            "Email already in use. Try signing in."
        }
        return msg
    }

    /**
     * Signs the current user out of Firebase and (optionally) Google.
     *
     * Flow:
     * 2. Call Firebase `signOut()`.
     * 3. If an Activity is passed in, also sign out of the Google client.
     * 4. Once the Google sign-out finishes, the loading flag clears so the UI can update.
     *
     * @param activity Used to sign out from the Google client *Optional*.
     * @author fdesouza1992
     */
    fun signOut(activity: Activity? = null) {
        auth.signOut()
        if (activity != null) {
            googleClient(activity).signOut()
        }
    }

    // Moved to UserManager and broken apart. See deleteUser below and in FirestoreRepository
//    /**
//     * A full account delete for the currently signed-in user.
//     *
//     * Flow:
//     * 1. Mark the UI as loading and clear any old error.
//     * 2. Call into the FirestoreRepository to delete the user and their data.
//     * 3. If the repo call returns false or throws an error, a simple “try again” message is displayed.
//     * 4. On success, the AuthStateListener will notice that the user is now null and the rest of the app can react to that.
//     *
//     * @param logger Used to log any errors that happen during the delete process.
//     * @author fdesouza1992
//     */
//    fun deleteAccount(logger: ILogger) {
//        viewModelScope.launch {
//            _ui.value = _ui.value.copy(
//                isLoading = true,
//                error = null
//            )
//            try {
//                val ok = repo.deleteUser(logger)
//                if (!ok) {
//                    _ui.value = _ui.value.copy(
//                        isLoading = false,
//                        error = "Failed to delete account. Please try again."
//                    )
//                } else {
//                    // AuthStateListener will see user == null once delete succeeds
//                    _ui.value = _ui.value.copy(
//                        isLoading = false,
//                        error = null
//                    )
//                }
//            } catch (e: Exception) {
//                logger.e("Auth", "deleteAccount failed", e)
//                _ui.value = _ui.value.copy(
//                    isLoading = false,
//                    error = "Failed to delete account. Please try again."
//                )
//            }
//        }
//    }

    /**
     * Deletes the user from firebase auth
     * @param uid The ID of the user to delete
     * @return Returns a boolean for a success check
     * @author fdesouza1992
     */
    suspend fun deleteUser(uid: String?) : Boolean {
        if (uid == null) {
            logger.e("Auth", "User ID is null. Please login to firebase.")
            return false
        }

        return try {
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
}
