package com.lifeleveling.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.theme.AppTheme

/*
* This is just a singleton test user for us to hardcode values into for UI testing
* Later this can be used to build the logic needed for each user account
* as well as the local save of user logic.
*/

object TestUser {
    var name by mutableStateOf("John Doe")
    var level by mutableStateOf(3)
    var currentExp by mutableStateOf(250)
    var expToLevel by mutableStateOf(300)
    var coins by mutableStateOf(301)
    var currentHealth by mutableStateOf(34)
    var maxHealth by mutableStateOf(85)

    // ================= Reminder  handling =================================
    // All reminders
    private val reminders = mutableListOf (
        Reminder(1, "Drink Water", R.drawable.water_drop, null, true, 3, 0),
        Reminder(2, "Laundry", R.drawable.shirt, ReminderColor.BrandTwo, false, 0, 4),
        Reminder(3, "Shower", R.drawable.shower_bath, null, false, 0, 15),
        Reminder(4, "Read", R.drawable.person_reading, ReminderColor.SecondaryTwo, false, 0, 8),
        Reminder(5, "Run", R.drawable.person_running_color, null, false, 0, 4),
        Reminder(6, "Make my bed", R.drawable.bed_color, null, true, 1, 0),
        Reminder(7, "Brush teeth", R.drawable.toothbrush, null, true, 2, 0),
        Reminder(8, "Wash Hair", R.drawable.shower_bath, null, false, 0, 9),
        Reminder(9, "Feed the dog", R.drawable.grass, null, true, 2, 0),
        Reminder(10, "Take Medication", R.drawable.med_bottle, null, true, 2, 0),
    )

    private var nextId = (reminders.maxOfOrNull { it.id } ?: 0) + 1

    // Weekly and monthly lists
    var weeklyReminders by mutableStateOf(reminders.filter { it.daily })
    var monthlyReminders by mutableStateOf(reminders.filter { !it.daily })

    // Look up reminders by ID
    private val reminderMap get() = reminders.associateBy { it.id }

    // Functions on reminders
    // Add a reminder
    fun addReminder(name: String, icon: Int, color: ReminderColor?, daily: Boolean, timesPerDay: Int, timesPerMonth: Int) {
        val newReminder = Reminder(nextId++, name, icon, color, daily, timesPerDay, timesPerMonth)
        reminders.add(newReminder)
        updateLists()
    }

    // Delete reminder by ID
    fun deleteReminder(id: Int) {
        reminders.removeAll  { it.id == id }
        weeklyStreaks = weeklyStreaks.filter { it.reminder.id != id }
        monthlyStreaks = monthlyStreaks.filter { it.reminder.id != id }
        updateLists()
    }

    // Retrieve a reminder
    fun getReminderById(id: Int): Reminder? = reminderMap[id]

    // Put new reminders in weekly or monthly
    private fun updateLists() {
        weeklyReminders = reminders.filter { it.daily }
        monthlyReminders = reminders.filter { !it.daily }
    }

    // Streaks handling
    var weeklyStreaks by mutableStateOf(
        listOf(
            reminderToStreak(reminders.first { it.id == 1 }, numberCompleted = 5),
            reminderToStreak(reminders.first { it.id == 6 }, numberCompleted = 4),
        )
    )
    var monthlyStreaks by mutableStateOf(
        listOf(
            reminderToStreak(reminders.first { it.id == 2 }, numberCompleted = 2),
            reminderToStreak(reminders.first { it.id == 3 }, numberCompleted = 12),
        )
    )

    // Streak Functions
    // Add to Streaks
    fun addToWeeklyStreak(reminder: Reminder) {
        if (reminderMap.containsKey(reminder.id) && weeklyStreaks.none { it.reminder.id == reminder.id }) {
            weeklyStreaks = weeklyStreaks + reminderToStreak(reminder)
        }
    }
    fun addToMonthlyStreak(reminder: Reminder) {
        if (reminderMap.containsKey(reminder.id) && monthlyStreaks.none { it.reminder.id == reminder.id }) {
            monthlyStreaks = monthlyStreaks + reminderToStreak(reminder)
        }
    }

    // Remove a streak
    fun removeFromWeeklyStreak(reminder: Reminder) {
        weeklyStreaks = weeklyStreaks.filter { it.reminder.id != reminder.id }
    }
    fun removeFromMonthlyStreak(reminder: Reminder) {
        monthlyStreaks = monthlyStreaks.filter { it.reminder.id != reminder.id }
    }

    private fun removeFromStreaks(id: Int) {
        val reminder = reminderMap[id] ?: return
        weeklyStreaks = weeklyStreaks.filter { it.reminder.id == reminder.id }
        monthlyStreaks = monthlyStreaks.filter { it.reminder.id == reminder.id }
    }

    private fun reminderToStreak(
        reminder: Reminder,
        numberCompleted: Int = 0
    ): Streak {
        val total = if (reminder.daily) {
            reminder.timesPerDay * 7
        } else {
            reminder.timesPerMonth
        }
        return Streak(reminder = reminder, totalAmount = total, numberCompleted = numberCompleted)
    }

    fun incrementStreak(streak: Streak) {
        streak.numberCompleted += 1
    }
}

data class Reminder (
    val id: Int,
    val name: String,
    val icon: Int,
    val color: ReminderColor?,
    val daily: Boolean,
    val timesPerDay: Int,
    val timesPerMonth: Int,
)

data class Streak (
    val reminder: Reminder,
    val totalAmount: Int,
    var numberCompleted: Int = 0
)

enum class ReminderColor {
    BrandOne,
    BrandTwo,
    SecondaryOne,
    SecondaryTwo,
    SecondaryThree,
    Background,
    DarkerBackground,
    PopUpBackground,
    DropShadow,
    LightShadow,
    Gray,
    FadedGray,
    Success,
    Success75,
    Error,
    Error75,
    Warning,
}

@Composable
fun resolveReminderColor(color: ReminderColor): Color = when (color) {
    ReminderColor.BrandOne -> AppTheme.colors.BrandOne
    ReminderColor.BrandTwo -> AppTheme.colors.BrandTwo
    ReminderColor.SecondaryOne -> AppTheme.colors.SecondaryOne
    ReminderColor.SecondaryTwo -> AppTheme.colors.SecondaryTwo
    ReminderColor.SecondaryThree -> AppTheme.colors.SecondaryThree
    ReminderColor.Background -> AppTheme.colors.Background
    ReminderColor.DarkerBackground -> AppTheme.colors.DarkerBackground
    ReminderColor.PopUpBackground -> AppTheme.colors.PopUpBackground
    ReminderColor.DropShadow -> AppTheme.colors.DropShadow
    ReminderColor.LightShadow -> AppTheme.colors.LightShadow
    ReminderColor.Gray -> AppTheme.colors.Gray
    ReminderColor.FadedGray -> AppTheme.colors.FadedGray
    ReminderColor.Success -> AppTheme.colors.Success
    ReminderColor.Success75 -> AppTheme.colors.Success75
    ReminderColor.Error -> AppTheme.colors.Error
    ReminderColor.Error75 -> AppTheme.colors.Error75
    ReminderColor.Warning -> AppTheme.colors.Warning
}