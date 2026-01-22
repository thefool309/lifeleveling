package com.lifeleveling.app.data

import com.google.firebase.Timestamp
import com.lifeleveling.app.ui.theme.EnumColor
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
    val timesPerHour: Int,
    val timesPerDay: Int,
    val timesPerMonth: Int,
    val repeatForever: Boolean,
    val repeatCount: Long,
    val repeatInterval: String?,
    val colorToken: EnumColor?,
    val iconName: String, // TODO: Update this
)

/**
 * Builds the reminder draft from the information on the UI form
 * @param selectedAmOrPm 0 = AM, 1 = PM
 * @param selectedReminderAmountHourDayWeek 0 = hours, 1 = days, 2 = weeks
 * @param selectedRepeatAmount 0 = days, 1, = weeks, 2 = months, 3 = years
 * @author fdesouza1992
 */
fun buildReminderDraft(
    title: String,
    selectedHour: Int,
    selectedMinute: Int,
    selectedAmOrPm: Int, // 0 = AM, 1 = PM
    selectedReminderIndex: Int,
    iconNameOptions: List<String>,
    asDaily: Boolean,
    asWeekDay: Boolean,
    reminderAmountNumber: String,
    selectedReminderAmountHourDayWeek: Int, // 0 = hours, 1 = days, 2 = weeks
    doNotRepeat: Boolean,
    indefinitelyRepeat: Boolean,
    repeatAmount: String,
    selectedRepeatAmount: Int, // 0 = days, 1 = weeks, 2 = months, 3 = years
): ReminderDraft {
    // Time calculations
    val hourStr = selectedHour.toString()
    val minuteStr = selectedMinute.toString()
    val rawHour = hourStr.toIntOrNull() ?: 0
    val minute = minuteStr.toIntOrNull() ?: 0
    // This block converts the chosen AM/PM hour into a proper 24-hour format,
    // handling the special cases for 12 AM and 12 PM.
    val hour24 = if (selectedAmOrPm == 1) {
        // PM
        if (rawHour % 12 == 0) 12 else (rawHour % 12 + 12)
    } else {
        // AM
        rawHour % 12
    }
    // --- Starting time: move to tomorrow if time already passed today ---
    val now = Calendar.getInstance()
    val cal = Calendar.getInstance().apply{
        set(Calendar.HOUR_OF_DAY, hour24)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    // If time is earlier than "now", we will move to the next day
    if (cal.before(now)){
        cal.add(Calendar.DAY_OF_YEAR,1)
    }
    val dueAt = Timestamp(cal.time)
    val iconName = iconNameOptions.getOrNull(selectedReminderIndex) ?: ""

    // --- "Remind me every" section ---
    val isDaily = asDaily || asWeekDay
    var timesPerHour = 0
    var timesPerDay = 0
    var timesPerMonth = 0

    val everyCount = reminderAmountNumber.toIntOrNull() ?: 0
    if (everyCount > 0) {
        when (selectedReminderAmountHourDayWeek) {
            0 -> {
                // Example: "Remind me every 8 Hours"
                // We store "8" in timesPerHour.
                timesPerHour = everyCount
            }
            1 -> {
                // Example: "Remind me every 3 Days"
                // For now we store the number 3 in timesPerDay.
                timesPerDay = everyCount
            }
            2 -> {
                // Example: "Remind me every 2 Weeks"
                // For now we store the number 2 in timesPerMonth
                timesPerMonth = everyCount
            }
        }
    }
    // --- "Repeat for" [ amount + (Days/Weeks/Months/Years) ]
    val repeatForever = indefinitelyRepeat
    var repeatCount: Long = 0
    var repeatInterval: String? = null

    if (!doNotRepeat && !repeatForever) {
        val count = repeatAmount.toLongOrNull() ?: 0L
        if (count > 0) {
            repeatCount = count
            repeatInterval = when (selectedRepeatAmount) {
                0 -> "days"
                1 -> "weeks"
                2 -> "months"
                3 -> "years"
                else -> null
            }
        }
    }
    // Build the draft
    return ReminderDraft(
        title = title.trim(),
        dueAt = dueAt,
        daily = isDaily,
        timesPerHour = timesPerHour,
        timesPerDay = timesPerDay,
        timesPerMonth = timesPerMonth,
        repeatForever = repeatForever,
        repeatCount = repeatCount,
        repeatInterval = repeatInterval,
        colorToken = null,
        iconName = iconName           // fallback to empty if somehow null
    )
}