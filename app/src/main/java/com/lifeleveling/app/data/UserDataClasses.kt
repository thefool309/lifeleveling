package com.lifeleveling.app.data

import com.google.firebase.Timestamp
import com.lifeleveling.app.ui.theme.enumColor

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
 * @param startingAt When the reminder will trigger
 * @param completed
 * @param completedAt
 * @param createdAt The timestamp of when the reminder was created
 * @param lastUpdate The last time the reminder was updated
 * @param daily If this is a daily occurring reminder or a reminder with intervals between instances.
 * @param timesPerDay If it is a daily, the number of times it will go off in a day
 * @param timesPerMonth If it is not a daily, the number of times it will go off in a month
 * @param colorToken The color associated with the reminder
 * @param iconName The icon chosen for the reminder
 * @param completedTally The number of times that this reminder has been completed (For UserJourney information)
 * @param enabled If the reminder is actively on the calendar or not. Also controls if the reminder will be available to make a streak from.
 */
data class Reminder(
    val reminderId: String = "",         // Firestore doc id (also stored in doc for convenience)
    val title: String = "",
    val notes: String = "",
    val startingAt: Timestamp? = null,        // when the reminder should trigger (nullable)
    val completed: Boolean = false,
    val completedAt: Timestamp? = null,  // set when marked complete
    val createdAt: Timestamp? = null,    // serverTimestamp on create
    val lastUpdate: Timestamp? = null,    // serverTimestamp on any write
    val daily: Boolean = true,         // daily = weekly streaks source, false = monthly streak source
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
    val totalRequired: Long = 0,                // totalAmount in TestUser.kt
    val numberCompleted: Long = 0,              // numberCompleted in TestUser.kt
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
 * @param completed A boolean value to signal if the badge has been achieved or not
 * @param unlockedAt If the badge has been received, this is the time stamp of when it was achieved
 */
data class Badge(
    val badgeId: String = "",
    val badgeName: String = "",
    val badgeDescription: String = "",
    val iconName: String = "",           // Stores the name, not the R.drawable
    val colorToken: enumColor,
    val completed: Boolean = false,
    val unlockedAt: Timestamp? = null,   // When badge was earned
)