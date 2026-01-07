package com.lifeleveling.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.firestore
import com.lifeleveling.app.data.FirestoreRepository
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertFailsWith

@RunWith(AndroidJUnit4::class)
class FirestoreRepositoryIntegrationTest {

    // Late-initialized properties for the Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // test data for user and document
    private val testUsername = "testuser_1"
    private val testPassword = "testPassword1"
    private val testEmail = "$testUsername@example.com"
    val _firestoreSettings = FirebaseFirestoreSettings.Builder().setHost("10.0.2.2:8080").setSslEnabled(false).build()
//        firestoreSettings {
//        // Android emulator uses 10.0.2.2 to connect to local loopback address
//        // https://stackoverflow.com/questions/9808560/why-do-we-use-10-0-2-2-to-connect-to-local-web-server-instead-of-using-computer
//        host = "10.0.2.2:8080"
//        isSslEnabled = false
//    }


    private suspend fun authorizeFirebaseUser(logger: ILogger): Boolean {
        // a fancy way to check if the user exists without having their ID
        try {
            auth.signInWithEmailAndPassword(testEmail, testPassword).await()
            return true
        } catch (e: FirebaseAuthException) {
            logger.e("Test Auth Failure", "Failure signing in, attempting to create user...", e)
            try {
                // if a FirebaseAuthException is thrown try creating the user first
                auth.createUserWithEmailAndPassword(testEmail, testPassword).await()
                auth.signInWithEmailAndPassword(testEmail, testPassword).await()
                return true
            }
            catch (e: FirebaseAuthException) {
                // if both of these fail it's something else so check the logs
                logger.e("Test Auth Failure", "createUserWithEmailAndPassword Failed: ", e)
                return false
            }
        }
    }
    // initialize testing environment
    @Before
    fun setup() = runTest {

        val logger = AndroidLogger()
        logger.d("Test setup", "Test setup commencing...")
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Ensure FirebaseApp is only initialized once
        if (FirebaseApp.getApps(context).isEmpty()) {
            val app = FirebaseApp.initializeApp(context)
            checkNotNull(app) { "FirebaseApp initialization failed" }
        }

        //correct order call use emulator before setting the instance.
        Firebase.firestore.useEmulator("10.0.2.2", 8080)
        Firebase.auth.useEmulator("10.0.2.2", 9099)

        firestore = Firebase.firestore
        firestore.setFirestoreSettings(_firestoreSettings)

        auth = Firebase.auth
    }

    @After
    fun cleanup()  = runTest {
        // Clean up Auth and Firestore data after each test
        if (auth.currentUser != null) {
            auth.signOut()
        }
        firestore.terminate().await()
        firestore.clearPersistence().await()
    }

    // Test createUser
    @Test
    fun createUserPositiveTest() = runTest {
        //comment top line in if you need to create a user in auth
        val logger = AndroidLogger()
        authorizeFirebaseUser(logger)
        val createdUser = auth.currentUser
        val repo = FirestoreRepository(logger = logger)
        val result = repo.createUser(
            mapOf(
                "displayName" to testUsername,
                "email" to testEmail,
            ), createdUser
        )
        assert(createdUser!!.uid == result!!.userBase!!.userId)
    }
    // create test expecting null pointer exception
    @Test
    fun createUserExpectNullNegativeTest() = runTest {
        if(auth.currentUser != null) {
            auth.signOut()
        }
        val logger = AndroidLogger()
        val repo = FirestoreRepository(logger = logger)

            val result = repo.createUser(
                mapOf(
                    "displayName" to testUsername,
                    "email" to testEmail,
                ), auth.currentUser
            )
        //if create user returns a null user something went wrong

        assert(result == null)
    }
    // Test editUser function
    @Test
    fun editUserPositiveTest() = runTest {
        //comment top line in if you need to create a user  in auth
        // auth.createUserWithEmailAndPassword(testEmail, testPassword).await()
        auth.signInWithEmailAndPassword(testEmail, testPassword).await()
        val logger = AndroidLogger()
        val repo = FirestoreRepository(logger = logger)
        repo.createUser(mapOf(
            "displayName" to testUsername,
            "email" to testEmail,
        ), auth.currentUser)
        val result = repo.editUser(userData = mapOf(
            "displayName" to "sillyGoose420",
        ), userId = auth.currentUser!!.uid)
        assert(result)
    }

    @Test
    fun getUserPositiveTest() = runTest {
        // the test passes :3 and log output is correct
        val logger = AndroidLogger()
        val authSuccess: Boolean = authorizeFirebaseUser(logger)
        if (!authSuccess) {
            throw Exception("Check Logs for Auth")
        }
        val repo = FirestoreRepository(logger = logger)

        repo.createUser(mapOf(
            "displayName" to testUsername,
            "email" to testEmail,
        ), auth.currentUser)
        val result = repo.getUser(auth.currentUser!!.uid)
        logger.d("getUserPositiveTest", "result: $result")
        assert(result!!.userBase!!.userId == auth.currentUser!!.uid)
        assert(result.userBase!!.displayName == testUsername)
        assert(result.userBase!!.email == testEmail)
    }

    @Test
    fun getUserExpectNullNegativeTest() = runTest {
        val logger = AndroidLogger()
        val repo = FirestoreRepository(logger = logger)
        val result = repo.getUser("not a user id")
        //if create user returns a null user something went wrong
        assert(result == null)
    }

    @Test
    fun getUserBlankIDNegativeTest() = runTest {
        val logger = AndroidLogger()
        val repo = FirestoreRepository(logger = logger)
        val result = repo.getUser("     ")
        assert(result == null)
    }

    @Test
    fun getUserNullIDNegativeTest() = runTest {
        if (auth.currentUser != null) {
            auth.signOut()
        }
        val logger = AndroidLogger()
        val repo = FirestoreRepository(logger = logger)
        // if someone were to pass null in as the userID it also gracefully fails
        // if you use a non-null asserted operator (auth.currentUser!!.uid) here it will throw a nullpointer exception
        // when calling getUser it is safer to use a "Safe" operator as below
        val result = repo.getUser(auth.currentUser?.uid)
        assert(result == null)
    }

    private suspend fun resetUser(logger: ILogger, repo: FirestoreRepository) {
        // a helper function to reset the User back to test environment defaults
        repo.createUser(
            mapOf(
                "displayName" to testUsername,
                "email" to testEmail,
            ), auth.currentUser
        )
    }

    @Test
    fun editDisplayNamePositiveTest() = runTest {
        val logger = AndroidLogger()
        authorizeFirebaseUser(logger)
        val repo = FirestoreRepository(logger = logger)
        val result = repo.editDisplayName("Tingle", auth.currentUser!!.uid)
        assert(result)
    }

    @Test
    fun editDisplayNameIsEmptyNegativeTest() = runTest {
        val logger = AndroidLogger()
        authorizeFirebaseUser(logger)
        val repo = FirestoreRepository(logger = logger)
        val result = repo.editDisplayName("", auth.currentUser!!.uid)
        assert(!result)
    }

    @Test
    fun editDisplayNameIsBlankNegativeTest() = runTest {
        val logger = AndroidLogger()
        authorizeFirebaseUser(logger)
        val repo = FirestoreRepository(logger = logger)
        val result = repo.editDisplayName("    ", auth.currentUser!!.uid)
        assert(!result)
    }

    @Test
    fun editEmailPositiveTest() = runTest {
        val logger = AndroidLogger()
        authorizeFirebaseUser(logger)
        val repo = FirestoreRepository(logger = logger)
        val result = repo.editEmail("tingle@annoyingasscharacters.com", auth.currentUser!!.uid)
        assert(result)
    }

    @Test
    fun editEmailIsEmptyNegativeTest() = runTest {
        val logger = AndroidLogger()
        authorizeFirebaseUser(logger)
        val repo = FirestoreRepository(logger = logger)
        val result = repo.editEmail("", auth.currentUser!!.uid)
        assert(!result)
    }

    @Test
    fun editEmailIsBlankNegativeTest() = runTest {
        val logger = AndroidLogger()
        authorizeFirebaseUser(logger)
        val repo = FirestoreRepository(logger = logger)
        val result = repo.editEmail("    ", auth.currentUser!!.uid)
        assert(!result)
    }

    @Test
    fun editPhotoUrlPositiveTest() = runTest {
        val logger = AndroidLogger()
        authorizeFirebaseUser(logger)
        val repo = FirestoreRepository(logger = logger)
        val result = repo.editPhotoUrl("www.reallycoolpicture.com/123456789.png", auth.currentUser!!.uid)
        assert(result)
    }

    @Test
    fun editPhotoUrlIsEmptyNegativeTest() = runTest {
        val logger = AndroidLogger()
        authorizeFirebaseUser(logger)
        val repo = FirestoreRepository(logger = logger)
        val result = repo.editPhotoUrl("", auth.currentUser!!.uid)
        assert(!result)
    }

    @Test
    fun editPhotoUrlIsBlankNegativeTest() = runTest {
        val logger = AndroidLogger()
        authorizeFirebaseUser(logger)
        val repo = FirestoreRepository(logger = logger)
        val result = repo.editPhotoUrl("    ", auth.currentUser!!.uid)
        assert(!result)
    }



    // TODO: Update this to check for increment streaks of weeks and months so two functions
//    @Test
//    fun incrementStreaksPositiveTest() = runTest {
//        val logger = AndroidLogger()
//        authorizeFirebaseUser(logger)
//        val repo = FirestoreRepository()
//        resetUser(logger, repo)
//        var user = repo.getUser(auth.currentUser!!.uid, logger)
//        val oldStreaks = user!!.streaks
//        val result = repo.incrementStreaks(logger)
//        user = repo.getUser(auth.currentUser!!.uid, logger)
//        val newStreaks = user!!.streaks
//        assert(newStreaks == (oldStreaks + 1))
//        assert(result)
//    }


    @Test
    fun setCurrHealthPositiveTest() = runTest {
        val logger = AndroidLogger()
        authorizeFirebaseUser(logger)
        val repo = FirestoreRepository(logger = logger)
        resetUser(logger, repo)
        var user = repo.getUser(auth.currentUser!!.uid)
        var health = user!!.userBase!!.currHealth
        repo.setCurrHealth(52L, auth.currentUser!!.uid)
        user = repo.getUser(auth.currentUser!!.uid)
        health = user!!.userBase!!.currHealth
        assert(health == 52L)
    }
    @Test
    fun setCoinsPositiveTest() = runTest {
        val logger = AndroidLogger()
        authorizeFirebaseUser(logger)
        val repo = FirestoreRepository(logger = logger)
        resetUser(logger, repo)
        var user = repo.getUser(auth.currentUser!!.uid)
        var coins = user!!.userBase!!.coinsBalance
        repo.setCoins(52L, auth.currentUser!!.uid)
        user = repo.getUser(auth.currentUser!!.uid)
        coins = user!!.userBase!!.coinsBalance
        assert(coins == 52L)
    }
    @Test
    fun addCoinsPositiveTest() = runTest {
        val logger = AndroidLogger()
        authorizeFirebaseUser(logger)
        val repo = FirestoreRepository(logger= logger)
        resetUser(logger, repo)
        var user = repo.getUser(auth.currentUser!!.uid)
        val oldCoins = user!!.userBase!!.coinsBalance
        repo.addCoins(52L, auth.currentUser!!.uid)
        user = repo.getUser(auth.currentUser!!.uid)
        val newCoins = user!!.userBase!!.coinsBalance
        assert(newCoins == (oldCoins + 52L))
    }
    @Test
    fun subtractCoinsPositiveTest() = runTest {
        val logger = AndroidLogger()
        authorizeFirebaseUser(logger)
        val repo = FirestoreRepository(logger = logger)
        resetUser(logger, repo)
        var user = repo.getUser(auth.currentUser!!.uid)
        val oldCoins = user!!.userBase!!.coinsBalance
        repo.subtractCoins(52L, auth.currentUser!!.uid)
        user = repo.getUser(auth.currentUser!!.uid)
        val newCoins = user!!.userBase!!.coinsBalance
        assert(newCoins == (oldCoins - 52L))
    }
}