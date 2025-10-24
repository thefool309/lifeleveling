package com.lifeleveling.app.data

import com.google.firebase.Timestamp
import kotlin.math.pow

data class Users(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val coinsBalance: Long = 0,
    val stats: Map<String, Long> = mapOf( //stats had to be explicitly typed for usage
        "agility" to 0,
        "defense" to 0,
        "intellect" to 0,
        "strength" to 0,
        "currentHealth" to 50,
        "maxHealth" to 50,
    ),
    val streaks: Long = 0,
    val onboardingComplete: Boolean = false,
    val createdAt: Timestamp? = null,
    val lastUpdate: Timestamp? = null,
    val level: Long = 1,
    val lifePoints: Long = 0,
    val currXp: Double = 0.0,

    ) {
    // for a derived property like this it is not necessary to include in firebase
    // since it's calculated everytime a user is instantiated
    // for this reason xpToNextLevel is not included in the primary constructor meaning it won't be serialized
    var xpToNextLevel: Double = 0.0

    init {
        calculateXpToNextLevel(level)
    }

    fun calculateXpToNextLevel(level: Long) {
        xpToNextLevel = (level / 100.0).pow(2)
    }
}