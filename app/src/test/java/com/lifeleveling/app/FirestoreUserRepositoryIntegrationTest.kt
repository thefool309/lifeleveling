package com.lifeleveling.app

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import kotlin.random.Random

class FirestoreUserRepositoryIntegrationTest {

    // Late-initialized properties for the Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // test data for user and document
    private val testUsername = "testuser_${Random.nextInt(10000)}"
    private val testPassword = "password123"
    private val testEmail = "$testUsername@example.com"

    //TODO: initialize testing environment
    @Before
    fun setup() {
        // Connect to the Firestore emulator
        val firestoreSettings = firestoreSettings {
            host = "10.0.2.2:8080"
            isSslEnabled = false
            isPersistenceEnabled = false
        }
        firestore = Firebase.firestore.apply {
            firestore.firestoreSettings = firestoreSettings
        }

        // Connect to the Auth emulator
        auth = Firebase.auth
        auth.useEmulator("10.0.2.2", 9099)
    }

    @After
    fun cleanup() {
        // Clean up Auth and Firestore data after each test
        runTest {
            // Un-authenticate the user
            auth.signOut()

            // Clear all data in the Firestore emulator
            firestore.clearPersistence()
        }
    }


    // TODO: Test createUser

    // TODO: Test editUser function


}