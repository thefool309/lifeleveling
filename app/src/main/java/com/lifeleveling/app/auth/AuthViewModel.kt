package com.lifeleveling.app.auth

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
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
import com.lifeleveling.app.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.lifeleveling.app.data.FirestoreRepository
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.tasks.await

/**
 * Simple container for what the auth screen needs to know:
 * - who the current user is (if any)
 * - whether we're busy doing an auth call
 * - any error message to show
 *
 * @author fdesouza1992
 * **/
data class AuthUiState(
    val user: FirebaseUser? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel that owns all of our Firebase/Google sign-in logic and exposes a simple UI state (AuthUiState) that screens can observe.
 *
 * @author fdesouza1992
 * **/
class AuthViewModel : ViewModel() {
    // Firebase auth and Firestore repository instance
    private val auth = Firebase.auth
    private val repo = FirestoreRepository()

    // Backing field for authentication UI state
    private val _ui = MutableStateFlow(AuthUiState(user = auth.currentUser))
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    /**
     * Clears any current auth error message from the UI state.
     *
     * Call this after the user dismisses an error dialog so we don't keep re-showing the same message.
     *
     * @author fdesouza1992
     */
    fun clearError() {
        _ui.value = _ui.value.copy(error = null)
    }


    // Listener to monitor Firebase authentication state changes
    private val listener = FirebaseAuth.AuthStateListener { fb ->
        _ui.value = _ui.value.copy(user = fb.currentUser, isLoading = false, error = null)
    }

    // Initialization/ Cleanup
    init { auth.addAuthStateListener(listener) }
    override fun onCleared() { auth.removeAuthStateListener(listener) }

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

    /**
     * Marks the UI as “loading” when the user starts the Google sign-in flow.
     *
     * This is usually called right before we launch the Google sign-in intent, so the UI can show a spinner or disable buttons.
     * @author fdesouza1992
     */
    fun beginGoogleSignIn() {
        _ui.value = _ui.value.copy(isLoading = true, error = null)
    }

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
    private fun postLoginBookkeeping(provider: String, logger: ILogger) {
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            try { repo.ensureUserCreated(user) } catch (e: Exception) {
                logger.w("FB", "ensureUserCreated failed: ${e.message}")
            }
        }
        Firebase.firestore.collection("authLogs")
            .add(
                mapOf(
                    "ts" to com.google.firebase.Timestamp.now(),
                    "source" to "emailPasswordLogin",
                    "provider" to provider,
                    "uid" to user.uid,
                    "email" to user.email,
                    "name" to (user.displayName ?: "")
                )
            )
            .addOnFailureListener { e -> logger.w("FB", "Auth Log write failed: ${e.message}") }

        _ui.value = _ui.value.copy(isLoading = false, error = null)
    }

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
        viewModelScope.launch {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken == null) {
                    _ui.value = _ui.value.copy(isLoading = false, error = "Missing ID token.")
                    return@launch
                }
                firebaseAuthWithGoogle(idToken)
            } catch (e: ApiException) {
                Log.e("FB", "Google sign-in failed", e)
                _ui.value = _ui.value.copy(isLoading = false, error = "Google sign-in failed.")
            }
        }
    }

    /**
     * Completes Firebase sign-in using a Google ID token.
     *
     * Flow:
     * 1. Exchange the Google ID token for Firebase credentials.
     * 2. On success, make sure the user has a Firestore document.
     * 3. Log an auth event to `authLogs` for monitoring.
     * 4. Clear loading/error state so the UI can move on.
     *
     * If anything fails, we log the exception and show a simple error message to the user.
     *
     * @param idToken The Google ID token returned from the Google sign-in flow.
     * @author fdesouza1992
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val cred = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(cred)
            .addOnSuccessListener { res ->
                val user = res.user
                Log.d("FB", "Google sign-in OK: uid=${user?.uid}, name=${user?.displayName}")

                // Ensure user document exists in Firestore
                if (user != null) {
                    viewModelScope.launch {
                        try {
                            repo.ensureUserCreated(user)
                        } catch (e: Exception) {
                            Log.e("FB", "ensureUserCreated failed", e)
                        }
                    }
                }

                // Writes healthcheck log to Firestore for monitoring
                Firebase.firestore.collection("authLogs")
                    .add(mapOf(
                        "ts" to Timestamp.now(),
                        "source" to "android",
                        "provider" to "google",
                        "uid" to user?.uid,
                        "email" to user?.email,
                        "name" to (user?.displayName ?: "")
                    ))
                    .addOnSuccessListener { doc -> Log.d("FB", "Auth Log doc: ${doc.id}") }
                    .addOnFailureListener { e -> Log.e("FB", "Auth Log write failed", e) }

                // Reset loading and error state
                _ui.value = _ui.value.copy(isLoading = false, error = null)
            }
            .addOnFailureListener { e ->
                Log.e("FB", "Firebase signInWithCredential failed", e)
                _ui.value = _ui.value.copy(isLoading = false, error = "Firebase auth failed.")
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
    suspend fun signInWithEmailPassword(email: String, password: String, logger: ILogger)
    {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, error = null)
            try {
                // If this email is Google-only, this call will fail with InvalidCredentials.
                auth.signInWithEmailAndPassword(email.trim(), password).await()
                postLoginBookkeeping(provider = "password", logger = logger)
                _ui.value = _ui.value.copy(isLoading = false, error = null)
            } catch (e: com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                // No account exists with this email
                logger.w("FB", "No user for ${email.trim()}")
                _ui.value = _ui.value.copy(isLoading = false, error = "No account found for this email.")
            } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                // Wrong password or email malformed, or Google-only account
                logger.e("FB", "Invalid credentials", e)
                // Tells user if the email is federated-only
                val methods = auth.fetchSignInMethodsForEmail(email.trim()).await().signInMethods.orEmpty()
                val msg = if ("google.com" in methods && "password" !in methods) {
                    "This email is registered with Google. Use 'Login using Google'."
                } else {
                    "Invalid email or password."
                }
                _ui.value = _ui.value.copy(isLoading = false, error = msg)
            } catch (e: com.google.firebase.auth.FirebaseAuthException) {
                logger.e("FB", "Auth exception", e)
                _ui.value = _ui.value.copy(isLoading = false, error = "Authentication error.")
            } catch (e: Exception) {
                logger.e("FB", "Unexpected sign-in error", e)
                _ui.value = _ui.value.copy(isLoading = false, error = "Sign-in failed.")
            }
        }
    }

    /**
     * Creates a new Firebase user with email and password, then signs them in.
     *
     * Flow:
     * 1. Mark the UI as loading.
     * 2. Call `createUserWithEmailAndPassword`.
     * 3. Immediately sign the user in with the same credentials.
     * 4. Run the shared post-login work (create user doc, log, etc.).
     * 5. Handle common error cases (email already used, bad format, etc.) with user-friendly messages.
     *
     * As with signIn, this is marked `suspend` but internally uses `viewModelScope.launch` so it won’t block the caller.
     *
     * @param email  The email for the new account.
     * @param password The password for the new account.
     * @param logger Used to log any issues during sign-up.
     * @author thefool309, fdesouza1992
     */
    suspend fun createUserWithEmailAndPassword(email: String, password: String, logger: ILogger)
    {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, error = null)
            try {
                auth.createUserWithEmailAndPassword(email.trim(), password).await()
                auth.signInWithEmailAndPassword(email.trim(), password).await()
                postLoginBookkeeping(provider = "password", logger = logger)
                _ui.value = _ui.value.copy(isLoading = false, error = null)
            } catch (e: com.google.firebase.auth.FirebaseAuthUserCollisionException) {

                // Email already in use.
                val methods = auth.fetchSignInMethodsForEmail(email.trim()).await().signInMethods.orEmpty()
                val msg = if ("google.com" in methods && "password" !in methods) {
                    "This email is registered with Google. Use 'Login using Google'."
                } else {
                    "Email already in use. Try signing in."
                }
                logger.w("FB", msg)
                _ui.value = _ui.value.copy(isLoading = false, error = msg)
            } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                logger.e("FB", "Invalid email/password", e)
                _ui.value = _ui.value.copy(isLoading = false, error = "Invalid email or password format.")
            } catch (e: com.google.firebase.auth.FirebaseAuthException) {
                logger.e("FB", "Auth exception", e)
                _ui.value = _ui.value.copy(isLoading = false, error = "Could not create account.")
            } catch (e: Exception) {
                logger.e("FB", "Unexpected sign-up error", e)
                _ui.value = _ui.value.copy(isLoading = false, error = "Sign-up failed.")
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
        _ui.value = _ui.value.copy(isLoading = true, error = null)
        auth.signOut()
        if (activity != null) {
            googleClient(activity).signOut().addOnCompleteListener {
                _ui.value = _ui.value.copy(isLoading = false)
            }
        } else {
            _ui.value = _ui.value.copy(isLoading = false)
        }
    }

    /**
     * A full account delete for the currently signed-in user.
     *
     * Flow:
     * 1. Mark the UI as loading and clear any old error.
     * 2. Call into the FirestoreRepository to delete the user and their data.
     * 3. If the repo call returns false or throws an error, a simple “try again” message is displayed.
     * 4. On success, the AuthStateListener will notice that the user is now null and the rest of the app can react to that.
     *
     * @param logger Used to log any errors that happen during the delete process.
     * @author fdesouza1992
     */
    fun deleteAccount(logger: ILogger) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val ok = repo.deleteUser(logger)
                if (!ok) {
                    _ui.value = _ui.value.copy(
                        isLoading = false,
                        error = "Failed to delete account. Please try again."
                    )
                } else {
                    // AuthStateListener will see user == null once delete succeeds
                    _ui.value = _ui.value.copy(
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                logger.e("Auth", "deleteAccount failed", e)
                _ui.value = _ui.value.copy(
                    isLoading = false,
                    error = "Failed to delete account. Please try again."
                )
            }
        }
    }
}
