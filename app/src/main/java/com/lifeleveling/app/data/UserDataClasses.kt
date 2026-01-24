package com.lifeleveling.app.data

import com.google.firebase.Timestamp
import com.lifeleveling.app.R
import java.time.LocalDate
import java.time.ZoneId

/**
 * Stats object to store the stat values
 * @author fdesouza1992
 */
data class Stats (
    val strength: Long = 0,
    val defense: Long = 0,
    val intelligence: Long = 0,
    val agility: Long = 0,
    val health: Long = 0,
)

// Nested Models
// A user's "reminder template" (the base CRUD)
/**
 * Reminder object for all reminder information
 * @param reminderId Unique ID of the reminder
 * @param title Name of the reminder
 * @param notes
 * @param dueAt When the reminder will trigger
 * @param completed
 * @param completedAt
 * @param createdAt The timestamp of when the reminder was created
 * @param lastUpdate The last time the reminder was updated
 * @param daily If this is a daily occurring reminder or a reminder with intervals between instances.
 * @param timesPerHour How many hours(s)
 * @param timesPerDay If it is a daily, the number of times it will go off in a day
 * @param timesPerMonth If it is not a daily, the number of times it will go off in a month
 * @param colorToken The color associated with the reminder
 * @param iconName The icon chosen for the reminder
 * @param completedTally The number of times that this reminder has been completed (For UserJourney information)
 * @param enabled If the reminder is actively on the calendar or not. Also controls if the reminder will be available to make a streak from.
 * @param repeatForever
 * @param repeatCount
 * @param repeatInterval
 */
data class Reminder(
    val reminderId: String = "",         // Firestore doc id (also stored in doc for convenience)
    val title: String = "",
    val notes: String = "",
    val dueAt: Timestamp? = null,        // when the reminder should trigger (nullable)
    val completed: Boolean = false,
    val completedAt: Timestamp? = null,  // set when marked complete
    val createdAt: Timestamp? = null,    // serverTimestamp on create
    val lastUpdate: Timestamp? = null,    // serverTimestamp on any write
    val daily: Boolean = true,         // daily = weekly streaks source, false = monthly streak source
    val timesPerMinute: Int = 0,        // How many minutes(s)
    val timesPerHour: Int = 0,          // How many hour(s)
    val timesPerDay: Int = 0,           // How many times per day
    val timesPerMonth: Int = 0,         // How many times per month
    val iconName: String,           // store icon key (ex: "water_drop"), not R.drawable.id TODO: Ask Felipe if he made a way to change a string to the icon to save this as string
    val repeatForever: Boolean = false,
    val repeatCount: Int = 0,
    val repeatInterval: String? = null,
    val dotColor: String?,                 // Used for dot
    val enabled: Boolean = true,               // If the reminder is active or just saved
    val completedTally: Long = 0,           // Used for calculating the most completed reminders for the user journey stats

    val colorToken: String? = null,      // nullable like enumColor? in TestUser used for icon color
//    val selectedMinutes: Int = 0,
//    val amOrPm: Int = 0,
//    val selectedHours: Int = 0,
)

// One active streak the user is tracking
/* Figma concept:
   - Add a Week or Add a Month Streak
   - Choose an existing reminder
   - Track how many times they've completed it */
/**
 * Streak object to craft streak data
 * @param streakId Unique ID of the streak
 * @param reminderId ID of the reminder it is built from
 * @param weekly A boolean to distinguish if it is a weekly reminder (true) or monthly (false)
 * @param totalRequired How many times the reminder needs to be completed to finish the streak
 * @param numberCompleted How many reminders have been finished so far
 * @param repeat A boolean for if this reminder should be recreated at the end of its lifecycle
 * @param createdAt The timestamp for when the streak was created
 * @param endsAt The end of the period of time the user has to finish the streak
 * @param lastUpdate The last time this streak was updated with a completed reminder instance
 */
data class Streak(
    val streakId: String = "",                  // doc id inside streaks subcollection
    val reminderId: String = "",                // link to Reminders.reminderId
    val weekly: Boolean = false,              // "weekly" or "monthly"
    val totalRequired: Int = 0,                // totalAmount in TestUser.kt
    val numberCompleted: Int = 0,              // numberCompleted in TestUser.kt
    val repeat: Boolean = true,                 // Repeat flag
    val createdAt: Timestamp? = null,
    val endsAt: Timestamp? = null,
    val lastUpdate: Timestamp? = null,
)

// Badge the user can earn
/**
 * Badge object to craft badges for the list
 * @param badgeId Unique ID for each badge
 * @param badgeName Name of the badge
 * @param badgeDescription Description of the badge, usually how it is achieved
 * @param iconName The name of the picture that will be shown when the badge is completed
 * @param colorToken The color the badge will be when it is completed
// * @param completed A boolean value to signal if the badge has been achieved or not
// * @param unlockedAt If the badge has been received, this is the time stamp of when it was achieved
 * @author Elyseia
 */
data class Badge(
    val badgeId: String = "",
    val badgeName: String = "",
    val badgeDescription: String = "",
    val iconName: Int = R.drawable.question_mark,           // Stores the name, not the R.drawable
    val colorToken: String? = null,
//    val completed: Boolean = false,
//    val unlockedAt: Timestamp? = null,   // When badge was earned
)

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

fun Reminder.occursOn(date: LocalDate, zone: ZoneId): Boolean {
    val start = this.dueAt?.toDate() ?: return false
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

data class Terms(
    val content: String = "",
    val version: String = "",
    val lastUpdate: Timestamp? = null,
    val title: String = ""
)

data class Privacy(
    val content: String = "",
    val version: String = "",
    val lastUpdate: Timestamp? = null,
    val title: String = ""
)