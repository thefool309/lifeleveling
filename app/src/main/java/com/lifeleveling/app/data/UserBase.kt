package com.lifeleveling.app.data


import com.google.firebase.Timestamp
import com.lifeleveling.app.ui.components.Reminder

/*
* An interface defined for use as a template for the UserDoc data class
* this defines a strict set of rules of what must be implemented in the UserDoc class
* and can be added to if necessary
 */
interface UserBase {
    val userId: String
    val displayName: String
    val email: String
    val photoUrl: String
    val coinsBalance: Long
    // Update from inline map to now use Stats data class
    val coinsSpent: Long
    val stats: Stats
    val streaks: List<Streak>
    val onboardingComplete: Boolean
    val createdAt: Timestamp?
    val lastUpdate: Timestamp?
    // variables that were missing during our first introduction of the Users collection
    var level: Long
    val lifePointsUsed: Long // unused lifePoints
    val lifePointsTotal: Long // total lifePoints
    val currentXp: Long       // Current Experience // Experience needed to level up
    val currHealth: Long
    // Badges can be stored in arrays of Badge objects on user doc.
    val badgesLocked: List<Badge>       // greyed out badges/ secret badges
    val badgesUnlocked: List<Badge>

    val allReminders: List<Reminders>
}