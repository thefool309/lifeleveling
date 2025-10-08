package com.lifeleveling.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.theme.LifelevelingTheme

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

class MainActivity : ComponentActivity() {

    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    // observable state for Compose
    private val userState = mutableStateOf<FirebaseUser?>(null)
    private val isLoadingState = mutableStateOf(false)

    private val authListener = FirebaseAuth.AuthStateListener { fbAuth ->
        // any auth change triggers recomposition
        userState.value = fbAuth.currentUser

        // when auth changes, you’re definitely not “loading” anymore
        isLoadingState.value = false
    }

    // Initializes edge-to-edge UI, auth listeners, Google Sign-In, and the sign-in launcher
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // seed init current state
        userState.value = auth.currentUser

        // observe auth changes
        auth.addAuthStateListener(authListener)

        // Configure Google Sign-In to request ID token + email (web client id from google-services.json)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // launcher for Google sign-in intent
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account.idToken
                    if (idToken != null) {
                        firebaseAuthWithGoogle(idToken)
                    } else {
                        Log.e("FB", "No ID token from Google account")
                        isLoadingState.value = false
                    }
                } catch (e: ApiException) {
                    Log.e("FB", "Google sign-in failed", e)
                    isLoadingState.value = false
                }
            } else {
                Log.e("FB", "Google sign-in canceled")
                isLoadingState.value = false
            }
        }

        setContent {
            LifelevelingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { inner ->
                    AuthScreen(
                        user = userState.value,
                        isLoading = isLoadingState.value,

                        // Launch Google sign-in flow
                        onGoogleSignIn = {
                            isLoadingState.value = true
                            launcher.launch(googleSignInClient.signInIntent)
                        },

                        // Sign out from Firebase and Google
                        onSignOut = {
                            isLoadingState.value = true
                            auth.signOut()

                            // Also sign out the Google client (async)
                            googleSignInClient.signOut().addOnCompleteListener {

                                // AuthListener will clear loading when auth changes; this is a safety net in case nothing changes.
                                isLoadingState.value = false
                            }
                        },
                        modifier = Modifier.padding(inner)
                    )
                }
            }
        }
    }

    // Cleans up the Firebase auth listener to avoid leaks when the Activity is destroyed.
    override fun onDestroy() {
        super.onDestroy()
        auth.removeAuthStateListener(authListener)
    }

    // Exchanges a Google ID token for a Firebase credential and signs the user in.
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user
                Log.d("FB", "Google sign-in OK: uid=${user?.uid}, name=${user?.displayName}")

                // visual confirmation: write a healthcheck doc
                Firebase.firestore.collection("healthchecks")
                    .add(
                        mapOf(
                            "ts" to Timestamp.now(),
                            "source" to "android",
                            "provider" to "google",
                            "uid" to user?.uid,
                            "email" to user?.email,
                            "name" to (user?.displayName ?: "")
                        )
                    )
                    .addOnSuccessListener { doc -> Log.d("FB", "Healthcheck doc: ${doc.id}") }
                    .addOnFailureListener { e -> Log.e("FB", "Healthcheck write failed", e) }

                // isLoading ends when authListener fires; keep a safety net here too
                isLoadingState.value = false
            }
            .addOnFailureListener { e ->
                Log.e("FB", "Firebase signInWithCredential failed", e)
                isLoadingState.value = false
            }
    }
}

@Composable
private fun AuthScreen(
    user: FirebaseUser?,
    isLoading: Boolean,
    onGoogleSignIn: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(24.dp)) {

        if (isLoading) {
            // simple visual feedback while auth is in-flight
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Please wait…",
                color = AppTheme.colors.BrandOne,
                style = AppTheme.textStyles.HeadingThree
            )
            return@Column
        }

        if (user == null) {
            Text(
                text = "Sign in to continue",
                color = AppTheme.colors.BrandOne,
                style = AppTheme.textStyles.HeadingThree
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = onGoogleSignIn) {
                Text("Sign in with Google")
            }
        } else {
            Text(
                text = "Signed in as ${user.displayName ?: user.email ?: user.uid}",
                color = AppTheme.colors.BrandOne,
                style = AppTheme.textStyles.HeadingThree
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = onSignOut) {
                Text("Sign out")
            }
        }
    }
}

