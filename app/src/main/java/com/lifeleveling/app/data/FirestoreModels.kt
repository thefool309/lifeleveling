package com.lifeleveling.app.data

import com.google.firebase.Timestamp

data class Users(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val coinsBalance: Long = 0,
    val stats: Map<*, *> = mapOf(
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