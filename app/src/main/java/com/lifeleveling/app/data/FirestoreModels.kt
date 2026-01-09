package com.lifeleveling.app.data

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId

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
    val startingAt: Timestamp? = null,        // when the reminder should trigger (nullable)
    val completed: Boolean = false,
    val completedAt: Timestamp? = null,  // set when marked complete
    val createdAt: Timestamp? = null,    // serverTimestamp on create
    val lastUpdate: Timestamp? = null,    // serverTimestamp on any write
    val daily: Boolean = true,         // daily = weekly streaks source, false = monthly streak source
    val timesPerMinute: Int = 0,        // How many minutes(s)
    val timesPerHour: Int = 0,          // How many hour(s)
    val timesPerDay: Int = 0,           // How many day(s)
    val timesPerMonth: Int = 0,         // How many month(s)
    val colorToken: String? = null,      // nullable like enumColor? in TestUser
    val iconName: String = "",           // store icon key (ex: "water_drop"), not R.drawable.id
    val repeatForever: Boolean = false,  // true if "Repeats indefinitely" checked
    val repeatCount: Int = 0,            // how many units
    val repeatInterval: String? = null,  // days, weeks, months or years
    val enabled: Boolean = true,
    val selectedMinutes: Int = 0,
    val amOrPm: Int = 0,
    val selectedHours: Int = 0
) {

}

/**
 * Checks if this reminder should be shown on a specific day.
 *
 * This is mainly used by the Day View to figure out which reminders belong on the selected date.
 *
 * It takes into account:
 * - When the reminder starts
 * - Whether it is daily
 * - Whether it repeats (and for how long)
 *
 * @param date The calendar day being evaluated.
 * @param zone The device time zone used to safely convert timestamps to dates.
 * @return true if the reminder applies to the given date, false if it does not.
 * @author fdesouza1992
 */

private fun Reminders.occursOn(date: LocalDate, zone: ZoneId): Boolean {
    val start = this.startingAt?.toDate() ?: return false
    val startDate = start.toInstant().atZone(zone).toLocalDate()

    if (date.isBefore(startDate)) return false

    // If it’s a one-off, only show on its start date.
    val hasRepeatRule = repeatForever || (repeatCount > 0 && !repeatInterval.isNullOrBlank())
    if (!daily && !hasRepeatRule) {
        return date == startDate
    }

    // If it’s daily with no duration rule, show every day from start onward.
    if (daily && !hasRepeatRule) return true

    // If it repeats forever, allow it as long as date >= start.
    if (repeatForever) return true

    // Otherwise it repeats with a finite duration rule.
    val interval = repeatInterval ?: return false
    val count = repeatCount

    val endDate = when (interval) {
        "days" -> startDate.plusDays(count.toLong())
        "weeks" -> startDate.plusWeeks(count.toLong())
        "months" -> startDate.plusMonths(count.toLong())
        "years" -> startDate.plusYears(count.toLong())
        else -> return false
    }

    if (date.isAfter(endDate)) return false

    return true
}

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

data class FcmTokens(
    val uID: String = "",
    val token: String = "",
    val lastUpdate: Timestamp? = null
)

data class ReminderCompletionLog(
    val reminderId: String = "",
    val dateKey: String = "",
    val count: Long = 0L,
    val reminderTitle: String = "",
)
