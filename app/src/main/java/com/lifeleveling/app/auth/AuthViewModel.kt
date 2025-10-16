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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val user: FirebaseUser? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _ui = MutableStateFlow(AuthUiState(user = auth.currentUser))
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    private val listener = FirebaseAuth.AuthStateListener { fb ->
        _ui.value = _ui.value.copy(user = fb.currentUser, isLoading = false, error = null)
    }

    init { auth.addAuthStateListener(listener) }
    override fun onCleared() { auth.removeAuthStateListener(listener) }

    fun googleClient(activity: Activity): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // .requestIdToken(activity.getString(com.lifeleveling.app.R.string.default_web_client_id))
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, gso)
    }

    fun beginGoogleSignIn() {
        _ui.value = _ui.value.copy(isLoading = true, error = null)
    }

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

    private fun firebaseAuthWithGoogle(idToken: String) {
        val cred = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(cred)
            .addOnSuccessListener { res ->
                val user = res.user
                Log.d("FB", "Google sign-in OK: uid=${user?.uid}, name=${user?.displayName}")

                // Writes healthcheck on database
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

                _ui.value = _ui.value.copy(isLoading = false, error = null)
            }
            .addOnFailureListener { e ->
                Log.e("FB", "Firebase signInWithCredential failed", e)
                _ui.value = _ui.value.copy(isLoading = false, error = "Firebase auth failed.")
            }
    }

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
