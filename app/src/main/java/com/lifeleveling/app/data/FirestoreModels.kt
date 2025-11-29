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
    var level: Long = 1,
    val lifePoints: Long = 0,           // unused lifePoints
    val currentXp: Double = 0.0,        // Current Experience // Experience needed to level up
    val currHealth: Long = 0,
    // Badges can be stored in arrays of Badge objects on user doc.
    val badgesLocked: List<Badge> = emptyList(),       // greyed out badges/ secret badges
    val badgesUnlocked: List<Badge> = emptyList(),     // completed badges
    ) {
    // for a derived property like this it is not necessary to include in firebase
    // since it's calculated everytime a user is instantiated
    // for this reason xpToNextLevel is not included in the primary constructor meaning it won't be serialized
    var xpToNextLevel: Long = 0L
    var maxHealth: Long = 0L
    val baseHealth: Long = 60L
    init {
        calculateXpToNextLevel()
        calculateMaxHealth()
    }

    fun calculateXpToNextLevel() {
        xpToNextLevel = level * 100L
    }

    fun calculateMaxHealth() {
        val healthStat = stats.health
        maxHealth = baseHealth + (healthStat * 5)
    }
}



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
    val timesPerHour: Int = 0,          // How many hour(s)
    val timesPerDay: Int = 0,           // How many day(s)
    val timesPerMonth: Int = 0,         // How many month(s)
    val colorToken: String? = null,      // nullable like enumColor? in TestUser
    val iconName: String = "",           // store icon key (ex: "water_drop"), not R.drawable.id
    val repeatForever: Boolean = false,  // true if "Repeats indefinitely" checked
    val repeatCount: Int = 0,            // how many units
    val repeatInterval: String? = null,  // days, weeks, months or years
)

// Player stat block (Stats Screen)
data class Stats (
    val agility: Long = 0,
    val defense: Long = 0,
    val intelligence: Long = 0,
    val strength: Long = 0,
    val health: Long = 0,
)

// Badge the user can earn
data class Badge(
    val badgeId: String = "",
    val badgeName: String = "",
    val badgeDescription: String = "",
    val iconName: String = "",           // Stores the name, not the R.drawable
    val colorToken: String = "",
    val completed: Boolean = false,
    val unlockedAt: Timestamp? = null,   // When badge was earned
)

// One active streak the user is tracking
/* Figma concept:
   - Add a Week or Add a Month Streak
   - Choose an existing reminder
   - Track how many times they've completed it */
data class Streak(
    val streakId: String = "",                  // doc id inside streaks subcollection
    val reminderId: String = "",                // link to Reminders.reminderId
    val periodType: String = "weekly",          // "weekly" or "monthly"
    val totalRequired: Long = 0,                // totalAmount in TestUser.kt
    val numberCompleted: Long = 0,              // numberCompleted in TestUser.kt
    val repeatIndefinitely: Boolean = false,
    val repeatEveryAmount: Long? = null,        // future: "every 2", "every 3", etc
    val repeatEveryUnit: String? = null,        // "days", "weeks", "months", "years"
    val createdAt: Timestamp? = null,
    val lastUpdate: Timestamp? = null,
)