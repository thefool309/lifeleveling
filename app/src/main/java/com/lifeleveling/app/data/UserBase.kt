package com.lifeleveling.app.data

import com.google.firebase.Timestamp

interface UserBase {
    val userId: String
    val displayName: String
    val email: String
    val photoUrl: String
    val coinsBalance: Long
    // Update from inline map to now use Stats data class
    val stats: Stats
    val streaks: Long
    val onboardingComplete: Boolean
    val createdAt: Timestamp?
    val lastUpdate: Timestamp?
    // variables that were missing during our first introduction of the Users collection
    var level: Long
    val lifePoints: Long           // unused lifePoints
    val currentXp: Double       // Current Experience // Experience needed to level up
    val currHealth: Long
    // Badges can be stored in arrays of Badge objects on user doc.
    val badgesLocked: List<Badge>       // greyed out badges/ secret badges
    val badgesUnlocked: List<Badge>
}