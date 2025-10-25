package com.lifeleveling.app.data

import com.google.firebase.Timestamp

data class Users(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val coinsBalance: Long = 0,
    // Update from inline map to now use Stats data class
    val stats: Stats = Stats(),
    val streaks: Long = 0,
    val onboardingComplete: Boolean = false,
    val createdAt: Timestamp? = null,
    val lastUpdate: Timestamp? = null,
    // variables that were missing during our first introduction of the Users collection
    val level: Long = 1,
    val lifePoints: Long = 0,
    val currentXp: Double = 0.0,
    val xpToNextLevel: Double = 0.0,
)

// Nested Models
// A user's "reminder template" (the base CRUD)
data class Reminders(
    val reminderId: String = "",         // Firestore doc id (also stored in doc for convenience)
    val title: String = "",
    val notes: String = "",
    val dueAt: Timestamp? = null,        // when the reminder should trigger (nullable)
    val isCompleted: Boolean = false,
    val completedAt: Timestamp? = null,  // set when marked complete
    val createdAt: Timestamp? = null,    // serverTimestamp on create
    val lastUpdate: Timestamp? = null,    // serverTimestamp on any write
    val isDaily: Boolean = true,         // daily = weekly streaks source, false = monthly streak source
    val timesPerDay: Long = 0,           // How many times per day
    val timesPerMonth: Long = 0,         // How many times per month
    val colorToken: String? = null,      // nullable like enumColor? in TestUser
    val iconName: String = "",           // store icon key (ex: "water_drop"), not R.drawable.id
)

// Player stat block (Stats Screen)
data class Stats (
    val agility: Long = 0,
    val defense: Long = 0,
    val intellect: Long = 0,
    val strength: Long = 0,
    val currentHealth: Long = 0,
    val maxHealth: Long = 0,
)

// Badge the user can earn
data class Badge(
    val badgeId: String = "",
    val badgeName: String = "",
    val badgeDescription: String = "",
    val iconName: String = "",
    val colorToken: String = "",
    val completed: Boolean = false,
    val unlockedAt: Timestamp? = null,
)