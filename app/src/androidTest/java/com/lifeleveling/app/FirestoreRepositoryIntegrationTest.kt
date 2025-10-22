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
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.TestLogger
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

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
        auth.signOut()
        firestore.terminate().await()
        firestore.clearPersistence().await()
    }

    // Test createUser
    @Test
    fun createUserPositiveTest() = runTest {

        auth.signInWithEmailAndPassword(testEmail, testPassword).await()
        val logger: AndroidLogger = AndroidLogger()
        val createdUser = auth.currentUser
        val repo = FirestoreRepository()
        val result = repo.createUser(
            mapOf(
                "displayName" to testUsername,
                "email" to testEmail,
                "userId" to createdUser!!.uid
            ), logger
        )
        assert(createdUser.uid == result!!.userId)
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
                    "userId" to "broken user"
                ), logger
            )
        //if create user returns a null user something went wrong

        assert(result == null)
    }
    // TODO: Test editUser function
    @Test
    fun editUserPositiveTest() = runTest {
        // TODO("Implement editUserPositiveTest")
        auth.signInWithEmailAndPassword(testEmail, testPassword).await()
        val logger: AndroidLogger = AndroidLogger()
        val repo = FirestoreRepository()
        val user = repo.createUser(mapOf(
            "displayName" to testUsername,
            "email" to testEmail,
            "userId" to "broken user"
        ), logger)
        val result = repo.editUser(mapOf(
            "displayName" to "sillyGoose420",
        ), logger)
        assert(result)
    }


}