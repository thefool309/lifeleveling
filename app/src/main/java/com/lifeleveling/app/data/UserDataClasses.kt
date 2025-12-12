package com.lifeleveling.app.data

import com.google.firebase.Timestamp
import com.lifeleveling.app.ui.theme.enumColor

data class Stats (
    val strength: Long = 0,
    val defense: Long = 0,
    val intelligence: Long = 0,
    val agility: Long = 0,
    val health: Long = 0,
)

// Nested Models
// A user's "reminder template" (the base CRUD)
data class Reminder(
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
    val colorToken: enumColor?,      // nullable like enumColor? in TestUser
    val iconName: Int = 0,           // store icon key (ex: "water_drop"), not R.drawable.id
    val completedTally: Long = 0,           // Used for calculating the most completed reminders for the user journey stats
    val enabled: Boolean = true,               // If the reminder is active or just saved
)

// One active streak the user is tracking
/* Figma concept:
   - Add a Week or Add a Month Streak
   - Choose an existing reminder
   - Track how many times they've completed it */
data class Streak(
    val streakId: String = "",                  // doc id inside streaks subcollection
    val reminderId: String = "",                // link to Reminders.reminderId
    val isWeekly: Boolean = false,              // "weekly" or "monthly"
    val totalRequired: Long = 0,                // totalAmount in TestUser.kt
    val numberCompleted: Long = 0,              // numberCompleted in TestUser.kt
    val repeat: Boolean = true,                 // Repeat flag
    val createdAt: Timestamp? = null,
    val lastUpdate: Timestamp? = null,
)

// Badge the user can earn
data class Badge(
    val badgeId: String = "",
    val badgeName: String = "",
    val badgeDescription: String = "",
    val iconName: String = "",           // Stores the name, not the R.drawable
    val colorToken: enumColor,
    val completed: Boolean = false,
    val unlockedAt: Timestamp? = null,   // When badge was earned
)