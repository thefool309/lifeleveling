package com.lifeleveling.app.auth

import android.app.Activity
import com.lifeleveling.app.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.tasks.await

/**
 * Class that owns all of our Firebase/Google sign-in logic.
 * @property auth The FirebaseAuth object to call to
 * @property logger A parameter that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
 * @author fdesouza1992
 * **/
class AuthModel(
    // Firebase auth instance and passed in logger logic
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val logger: ILogger
) {
    val currentUser get() = auth.currentUser


    /**
     * Velma wuz here :3
     */
    // Helper functions
    private fun getUserId() : String? {
        return auth.currentUser?.uid
    }

    // Listener to monitor Firebase authentication state changes
    /**
     * Add the listener to monitor the Firebase authentication state changes
     * @param listener The listener to register
     * @author fdesouza1992
     */
    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.addAuthStateListener(listener)
    }

    // Initialization/ Cleanup
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

    /**
     * Calls the Firebase auth function of sending a password reset email to the user.
     * @param email The email the message needs to be sent to.
     * @author fdesouze1992
     */
    suspend fun sendPasswordResetEmail(email: String) {
        val trimmed = email.trim()
        auth.sendPasswordResetEmail(trimmed).await()
    }
}
