package com.lifeleveling.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.firestore
import com.lifeleveling.app.data.FirestoreRepository
import com.lifeleveling.app.data.Users
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.TestLogger
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertThrows
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


    //TODO: initialize testing environment
    @Before
    fun setup() = runTest {

        val logger = TestLogger()
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
        // auth.createUserWithEmailAndPassword(testEmail, testPassword)
        auth.signInWithEmailAndPassword(testEmail, testPassword).await()
        val logger: AndroidLogger = AndroidLogger()
        val createdUser = auth.currentUser
        val repo = FirestoreRepository()
        val result = repo.createUser(
            mapOf(
                "displayName" to testUsername,
                "email" to testEmail,
            ), logger
        )
        assert(createdUser!!.uid == result!!.userId)
    }
    // create test expecting null pointer exception
    @Test
    fun createUserExpectNullNegativeTest() = runTest {
        if(auth.currentUser != null) {
            auth.signOut()
        }
        val logger: AndroidLogger = AndroidLogger()
        val repo = FirestoreRepository()

            val result = repo.createUser(
                mapOf(
                    "displayName" to testUsername,
                    "email" to testEmail,
                ), logger
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
        val logger: AndroidLogger = AndroidLogger()
        val repo = FirestoreRepository()
        val user = repo.createUser(mapOf(
            "displayName" to testUsername,
            "email" to testEmail,
        ), logger)
        val result = repo.editUser(mapOf(
            "displayName" to "sillyGoose420",
        ), logger)
        assert(result)
    }

    @Test
    fun getUserMalformedNegativeTest() = runTest {
        // Implementing negative test for editUser kind of became a negative test for get user.
        // editUser will take in any type and assign it to a value in the database
        // causing major problems when you try to read the user back in.
        //comment top line in if you need to create a user  in auth
        //auth.createUserWithEmailAndPassword(testEmail, testPassword).await()
        auth.signInWithEmailAndPassword(testEmail, testPassword).await()
        val logger = AndroidLogger()
        val repo = FirestoreRepository()
        val user = repo.createUser(mapOf(
            "displayName" to testUsername,
            "email" to testEmail,
        ), logger)
        val editUserResult = repo.editUser(mapOf("displayName" to 150), logger)
        // expect null pointer exception
        var result: Users? = null

        result = repo.getUser(auth.currentUser?.uid, logger)
        assert(result == null)
        assertFailsWith<NullPointerException> {
            result = repo.getUser(auth.currentUser?.uid, logger)!!
        }
    }

    @Test
    fun getUserPositiveTest() = runTest {
        // the test passes :3 and log output is correct
        //comment top line in if you need to create a user  in auth
        // auth.createUserWithEmailAndPassword(testEmail, testPassword).await()
        auth.signInWithEmailAndPassword(testEmail, testPassword).await()
        val logger: AndroidLogger = AndroidLogger()
        val repo = FirestoreRepository()
        val user = repo.createUser(mapOf(
            "displayName" to testUsername,
            "email" to testEmail,
        ), logger)
        val result = repo.getUser(auth.currentUser!!.uid, logger)
        logger.d("getUserPositiveTest", "result: $result")
        assert(result!!.userId == auth.currentUser!!.uid)
        assert(result.displayName == testUsername)
        assert(result.email == testEmail)
    }

    @Test
    fun getUserExpectNullNegativeTest() = runTest {
        val logger: AndroidLogger = AndroidLogger()
        val repo = FirestoreRepository()
        val result = repo.getUser("not a user id", logger)
        //if create user returns a null user something went wrong
        assert(result == null)
    }

    @Test
    fun getUserBlankIDNegativeTest() = runTest {
        val logger: AndroidLogger = AndroidLogger()
        val repo = FirestoreRepository()
        val result = repo.getUser("     ", logger)
        assert(result == null)
    }

    @Test
    fun getUserNullIDNegativeTest() = runTest {
        if (auth.currentUser != null) {
            auth.signOut()
        }
        val logger: AndroidLogger = AndroidLogger()
        val repo = FirestoreRepository()
        // if someone were to pass null in as the userID it also gracefully fails
        // if you use a non-null asserted operator (auth.currentUser!!.uid) here it will throw a nullpointer exception
        // when calling getUser it is safer to use a "Safe" operator as below
        val result = repo.getUser(auth.currentUser?.uid, logger)
        assert(result == null)
    }
}