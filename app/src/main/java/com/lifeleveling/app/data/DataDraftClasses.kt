package com.lifeleveling.app.data

import com.google.firebase.Timestamp
import java.util.Calendar

/**
 * A small data class for the UI to fill out and pass to create a Streak object.
 * @author Elyseia
 */
data class StreakDraft(
    val reminderId: String,
    val weekly: Boolean,
    val repeat: Boolean,
)

/**
 * A small data class for the UI to fill out and pass to create a Reminder object.
 * @author Elyseia
 */
data class ReminderDraft(
    val title: String,
    val dueAt: Timestamp,
    val daily: Boolean,
    val timesPerMinute: Int,
    val timesPerHour: Int,
    val timesPerDay: Int,
    val timesPerMonth: Int,
    val repeatForever: Boolean,
    val repeatCount: Int,
    val repeatInterval: String?,
    val dotColor: String?,
    val iconName: String,
)

/**
 * Turns raw data from create reminders screen into an usable timestamp for dueAt
 * @param selectedAmOrPm 0 = AM, 1 = PM
 * @author fdesouza1992
 */
fun buildTimestamp(
    year: Int,
    month: Int,
    day: Int,
    hourStr: String,
    minuteStr: String,
    selectedAmOrPm: Int, // 0 = AM, 1 = PM
): Timestamp {
    // 1. Resolve date and time into a Timestamp
    val rawHour = hourStr.toIntOrNull() ?: 0
    val minute = minuteStr.toIntOrNull() ?: 0

    // Convert to a 24-hr clock
    val hour24 = if (selectedAmOrPm == 1) {
        // PM
        if (rawHour % 12 == 0) 12 else (rawHour % 12 + 12)
    } else {
        // AM
        rawHour % 12
    }

    // Built a java.util.Date for the chosen year/month/day/time
    val now = Calendar.getInstance()
    val cal = now.apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1) // Calendar months are 0-based
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, hour24)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    return Timestamp(cal.time)
}

/**
 * Builds the reminder draft from the information on the UI form
 * @param selectedReminderAmountHourDayWeek 0 = minutes, 1 = hours
 * @param selectedRepeatAmount 0 = days, 1, = weeks, 2 = months, 3 = years
 * @author fdesouza1992
 */
fun buildReminderDraft(
    title: String,
    dueAt: Timestamp,
    asDaily: Boolean,
    reminderAmountNumber: String,
    selectedReminderAmountHourDayWeek: Int, // 0 = minutes, 1 = hours
    repeatReminder: Boolean,
    repeatAmount: String,
    selectedRepeatAmount: Int, // 0 = days, 1 = weeks, 2 = months, 3 = years
    dotColor: String?,
    iconName: String,
): ReminderDraft {

    // 1. "Set as daily" + "Remind me every:"
    val isDaily = asDaily
    var timesPerMinute = 0
    var timesPerHour = 0
    val timesPerDay = 0
    val everyCount = reminderAmountNumber.toIntOrNull() ?: 0

    if (isDaily && everyCount > 0) {
        when (selectedReminderAmountHourDayWeek) {
            0 -> {
                // "Reminder me every X Mins"
                timesPerMinute = everyCount
            }
            1 -> {
                // "Remind me every X Hours"
                timesPerHour = everyCount
            }
        }
    }

    // timesPerMonth is unused for now
    val timesPerMonth = 0

    // 2. "Repeat this reminder" (duration)
    var repeatForever = false
    var repeatCount = 0
    var repeatInterval: String? = null

    if (repeatReminder) {
        val count = repeatAmount.toIntOrNull() ?: 0
        if (count > 0) {
            repeatCount = count
            repeatInterval = when (selectedRepeatAmount) {
                0 -> "days"
                1 -> "weeks"
                2 -> "months"
                3 -> "years"
                else -> null
            }
        } else {
            // User checked the box but didn't give a number is being treated as "repeat forever" for now.
            repeatForever = true
        }
    }

    // 3. Build the draft
    return ReminderDraft(
        title = title.trim(),
        dueAt = dueAt,
        daily = isDaily,
        timesPerMinute = timesPerMinute,
        timesPerHour = timesPerHour,
        timesPerDay = timesPerDay,
        timesPerMonth = timesPerMonth,
        repeatForever = repeatForever,
        repeatCount = repeatCount,
        repeatInterval = repeatInterval,
        dotColor = dotColor,
        iconName = iconName           // fallback to empty if somehow null
    )
}