package com.lifeleveling.app.data

import com.google.firebase.Timestamp

data class Users(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val coinsBalance: Long = 0,
    val stats: Map<String, Long> = mapOf(
        "agility" to 0,
        "defense" to 0,
        "healthPoints" to 0,
        "strength" to 0
    ),
    val streaks: Long = 0,
    val onboardingComplete: Boolean = false,
    val createdAt: Timestamp? = null,
    val lastUpdate: Timestamp? = null
)

data class Reminders(
    val reminderId: String = "",         // Firestore doc id (also stored in doc for convenience)
    val title: String = "",
    val notes: String = "",
    val dueAt: Timestamp? = null,        // when the reminder should trigger (nullable)
    val isCompleted: Boolean = false,
    val completedAt: Timestamp? = null,  // set when marked complete
    val createdAt: Timestamp? = null,    // serverTimestamp on create
    val lastUpdate: Timestamp? = null    // serverTimestamp on any write
)