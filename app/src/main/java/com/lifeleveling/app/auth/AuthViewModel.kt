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
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.lifeleveling.app.data.FirestoreRepository
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.tasks.await

// UI State
data class AuthUiState(
    val user: FirebaseUser? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

// Handles Firebase and Google Sign-in authentication logic
class AuthViewModel : ViewModel() {
    // Firebase auth and Firestore repository instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val repo = FirestoreRepository()

    // Backing field for authentication UI state
    private val _ui = MutableStateFlow(AuthUiState(user = auth.currentUser))
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    // Listener to monitor Firebase authentication state changes
    private val listener = FirebaseAuth.AuthStateListener { fb ->
        _ui.value = _ui.value.copy(user = fb.currentUser, isLoading = false, error = null)
    }

    // Initialization/ Cleanup
    init { auth.addAuthStateListener(listener) }
    override fun onCleared() { auth.removeAuthStateListener(listener) }

    // Google Sign-In Client Config
    fun googleClient(activity: Activity): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // .requestIdToken(activity.getString(com.lifeleveling.app.R.string.default_web_client_id))
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, gso)
    }

    // Sets loading state while user selects account
    fun beginGoogleSignIn() {
        _ui.value = _ui.value.copy(isLoading = true, error = null)
    }

    // Handles Google Sign-In. Called from MainActivity.kt once Google returns result intent
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

    // Firebase Sign-In with Google Credential ensures user exists in Firestore and logs event
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
                Firebase.firestore.collection("healthchecks")
                    .add(mapOf(
                        "ts" to Timestamp.now(),
                        "source" to "android",
                        "provider" to "google",
                        "uid" to user?.uid,
                        "email" to user?.email,
                        "name" to (user?.displayName ?: "")
                    ))
                    .addOnSuccessListener { doc -> Log.d("FB", "Healthcheck doc: ${doc.id}") }
                    .addOnFailureListener { e -> Log.e("FB", "Healthcheck write failed", e) }

                // Reset loading and error state
                _ui.value = _ui.value.copy(isLoading = false, error = null)
            }
            .addOnFailureListener { e ->
                Log.e("FB", "Firebase signInWithCredential failed", e)
                _ui.value = _ui.value.copy(isLoading = false, error = "Firebase auth failed.")
            }
    }

    fun signInWithEmailPassword(email: String, password: String, logger: ILogger) : Boolean {
        try {
            auth.signInWithEmailAndPassword(email, password)
            return true
        }
        catch (e: FirebaseAuthInvalidCredentialsException) {
            logger.e("FB", "signInWithEmailPassword failed due to Invalid Credentials: ", e)
            return false
        }
        catch (e: FirebaseAuthException) {
            logger.e("FB", "signInWithEmailPassword failed due to FirebaseAuthException: ", e)
            try {
                auth.createUserWithEmailAndPassword(email, password)
                auth.signInWithEmailAndPassword(email, password)
                return true
            }
            catch (e: Exception) {
                logger.e("FB", "signInWithEmailPassword failed due to unspecified Exception: ", e)
                return false
            }
        }
    }

    // Signs out from Firebase and Google client (if used)
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


}
