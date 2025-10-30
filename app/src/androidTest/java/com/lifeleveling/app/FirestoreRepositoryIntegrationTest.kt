package com.lifeleveling.app

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.lifeleveling.app.data.FirestoreRepository
import com.lifeleveling.app.util.TestLogger
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.test.assertFailsWith

@RunWith(AndroidJUnit4::class)
class FirestoreRepositoryIntegrationTest {

    // Late-initialized properties for the Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // test data for user and document
    private val testUsername = "testuser_${Random.Default.nextInt(10000)}"
    private val testPassword = "password123"
    private val testEmail = "$testUsername@example.com"

    val firestoreSettings = firestoreSettings {
        // Android emulator uses 10.0.2.2 to connect to local loopback address
        // https://stackoverflow.com/questions/9808560/why-do-we-use-10-0-2-2-to-connect-to-local-web-server-instead-of-using-computer
        host = "10.0.2.2:8080"
        isSslEnabled = false
        isPersistenceEnabled = false
    }

    //TODO: initialize testing environment
    @Before
    fun setup() {
        val logger = TestLogger()
        logger.d("Test setup", "Test setup commencing...")
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Ensure FirebaseApp is only initialized once
        if (FirebaseApp.getApps(context).isEmpty()) {
            val app = FirebaseApp.initializeApp(context)
            checkNotNull(app) { "FirebaseApp initialization failed" }
        }

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        auth.useEmulator("10.0.2.2", 9099)

        // Connect to the Firestore emulator
        firestore.firestoreSettings = firestoreSettings
    }

    @After
    fun cleanup() {
        // Clean up Auth and Firestore data after each test
        val latch = CountDownLatch(2)

        auth.signOut()
        latch.countDown()
        firestore.clearPersistence().addOnSuccessListener { Log.d("FirestoreRepositoryIntegrationTest", "Firebase database cleanup success") }
            .addOnFailureListener { e -> Log.e("FirestoreRepositoryIntegrationTest", "Firebase database cleanup failed", e) }
            .addOnCompleteListener { latch.countDown() }

        latch.await(10, TimeUnit.SECONDS) // Wait for up to 10 seconds
    }

    // Test createUser
    @Test
    fun createUserPositiveTest() = runTest {
        firestore = Firebase.firestore.apply {
            firestore.firestoreSettings = firestoreSettings
        }
        auth.createUserWithEmailAndPassword(testEmail, testPassword)
        val logger: TestLogger = TestLogger()
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
    fun createUserNullPointerNegativeTest() = runTest {
        firestore = Firebase.firestore.apply {
            firestore.firestoreSettings = firestoreSettings
        }
        val logger: TestLogger = TestLogger()
        val repo = FirestoreRepository()
        val exception = assertFailsWith<NullPointerException> {
            val result = repo.createUser(
                mapOf(
                    "displayName" to testUsername,
                    "email" to testEmail,
                    "userId" to "broken user"
                ), logger
            )
        }
    }
    // TODO: Test editUser function
    @Test
    fun editUserPositiveTest() = runTest {
        firestore = Firebase.firestore.apply {
            firestore.firestoreSettings = firestoreSettings
        }
    }


}