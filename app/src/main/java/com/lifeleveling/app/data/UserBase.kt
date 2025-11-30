package com.lifeleveling.app.data

import com.google.firebase.Timestamp
import com.lifeleveling.app.ui.components.Reminder

/**
 * The abstract base class for all User sub objects. This mostly serves as a rules guideline for what NEEDS to be included
 *
 */
interface UserBase {
    val userId: String
    val displayName: String
    val email: String
    val photoUrl: String
    var level: Long
    val lifePoints: Long            // unused lifePoints
       // Current Experience
    val currHealth: Long
    val coinsBalance: Long
    // Update from inline map to now use Stats data class
    val stats: Stats
    val currentXp: Long
    val coins: Long
    val currentHealth: Int
    val lifePointsUsed: Long
    val lifePointsTotal: Long
    val fightOrMeditate: Int
    val badgesLocked: List<Badge>        // greyed out badges/ secret badges
    val badgesUnlocked: List<Badge>
    val reminders: List<Reminders>

    val allCoinsEarned: Long
    val streaks: List<Streak>
    val weekStreaksCompleted: Long
    val monthStreaksCompleted: Long
    val onboardingComplete: Boolean
    val createdAt: Timestamp?
    val lastUpdate: Timestamp?
    // variables that were missing during our first introduction of the Users collection


    // Badges can be stored in arrays of Badge objects on user doc.

}