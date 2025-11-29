package com.lifeleveling.app.ui.components

import android.icu.util.Calendar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.theme.enumColor

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
    var StatStrength by mutableStateOf(3)
    var StatDefense by mutableStateOf(2)
    var StatIntelligence by mutableStateOf(3)
    var StatAgility by mutableStateOf(3)
    var StatHealth by mutableStateOf(3)
    var LifePointsUsed by mutableStateOf(6)
    var UnusedLifePoints by mutableStateOf(12)

    // Data for badges
    var totalStreaksCompleted by mutableStateOf(2)
    var weekStreaksCompleted by mutableStateOf(2)
    var monthStreaksCompleted by mutableStateOf(0)
    var badgesEarned by mutableStateOf(3)
    var allExpEver by mutableStateOf(550)
    var allCoinsEarned by mutableStateOf(805)
    var coinsSpent by mutableStateOf(504)
    val profileCreatedDate = Calendar.getInstance().apply {
        set(2025, Calendar.OCTOBER, 18,12, 0,0)
        set(Calendar.MILLISECOND,0)
    }.timeInMillis
    var mostCompletedReminder = Pair<String,Long>("", 0)

    // Getting user's time since creation
    fun getTimeSinceUserCreated(): String{
        val now = Calendar.getInstance()
        val timeDiff = now.timeInMillis - profileCreatedDate

        val days = timeDiff / (1000 * 60 * 60 * 24)
        val years = days / 365
        val remainingDays = days % 365

        val yearsSection = if (years > 0) {
            "$years year" + if(years > 1) "s" else " "
        } else {
            ""
        }
        val daysSection = "$remainingDays day" + if(remainingDays > 1) "s" else ""
        return yearsSection + daysSection
    }

    // ================= Reminder  handling =================================
    // All reminders
    private val reminders = mutableListOf (
        Reminder(1, "Drink Water", R.drawable.water_drop, null, true, 3, 0, 20),
        Reminder(2, "Laundry", R.drawable.shirt, enumColor.BrandTwo, false, 0, 4, 2),
        Reminder(3, "Shower", R.drawable.shower_bath, null, false, 0, 15, 3),
        Reminder(4, "Read", R.drawable.person_reading, enumColor.SecondaryTwo, false, 0, 8, 1),
        Reminder(5, "Run", R.drawable.person_running_color, null, false, 0, 4, 1),
        Reminder(6, "Make my bed", R.drawable.bed_color, null, true, 1, 0, 6),
        Reminder(7, "Brush teeth", R.drawable.toothbrush, null, true, 2, 0, 12),
        Reminder(8, "Wash Hair", R.drawable.shower_bath, null, false, 0, 9, 5),
        Reminder(9, "Feed the dog", R.drawable.grass, null, true, 2, 0, 13),
        Reminder(10, "Take Medication", R.drawable.med_bottle, null, true, 2, 0, 9),
    )
    val calendarReminders by mutableStateOf(listOf(
        calReminder(1,"Go to store", false,false,true,true,R.drawable.shop_color, 1, 32, 1,5,2,3,1),
        calReminder(2,"shower", false,false,true,true,R.drawable.shower_bath, 1, 12, 0,0,0,0,0),
        calReminder(3,"Doctor", false,false,true,true,R.drawable.shirt_color, 1, 12, 0,0,0,0,0),
        calReminder(4,"TEST", false,false,true,true,R.drawable.med_bottle, 1, 12, 0,0,0,0,0),
        calReminder(5,"Run", false,false,true,true,R.drawable.person_running, 1, 12, 0,0,0,0,0),
        calReminder(6,"T3", false,false,true,true,R.drawable.bell, 1, 12, 0,0,0,0,0),
        calReminder(7,"T4", false,false,true,true,R.drawable.document, 1, 12, 0,0,0,0,0),
        calReminder(8,"T5", false,false,true,true,R.drawable.heart, 1, 12, 0,0,0,0,0),

        )
    )
    private var nextId = (reminders.maxOfOrNull { it.id } ?: 0) + 1

    // Weekly and monthly lists
    var weeklyReminders by mutableStateOf(reminders.filter { it.daily })
    var monthlyReminders by mutableStateOf(reminders.filter { !it.daily })

    // Look up reminders by ID
    private val reminderMap get() = reminders.associateBy { it.id }

    // Functions on reminders
    // Add a reminder
    fun addReminder(name: String, icon: Int, color: enumColor?, daily: Boolean, timesPerDay: Int, timesPerMonth: Int) {
        val newReminder = Reminder(nextId++, name, icon, color, daily, timesPerDay, timesPerMonth, 0)
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

    // Checking for most completed reminder
    fun updateTopReminder() {
        val top = reminders.maxByOrNull { it.completedTally } ?: return
        if (top.completedTally > mostCompletedReminder.second) {
            mostCompletedReminder = Pair(top.name, top.completedTally)
        }
    }

    // Streaks handling
    var weeklyStreaks by mutableStateOf(
        listOf(
            reminderToStreak(reminders.first { it.id == 1 }, numberCompleted = 5, repeat = true, repeatIndefinitely = true),
            reminderToStreak(reminders.first { it.id == 6 }, numberCompleted = 4, repeat = true, repeatNumber = 4, repeatInterval = "Weeks"),
        )
    )
    var monthlyStreaks by mutableStateOf(
        listOf(
            reminderToStreak(reminders.first { it.id == 2 }, numberCompleted = 2),
            reminderToStreak(reminders.first { it.id == 3 }, numberCompleted = 12, repeat = true, repeatIndefinitely = true),
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
    fun addStreak(
        reminder: Reminder,
        repeat: Boolean = false,
        repeatIndefinitely: Boolean = false,
        repeatNumber: Int = 0,
        repeatInterval: String = "",
    ) {
        val streak = reminderToStreak(
            reminder,
            repeat = repeat,
            repeatIndefinitely = repeatIndefinitely,
            repeatNumber = repeatNumber,
            repeatInterval = repeatInterval,
        )
        if (reminder.daily) {
            if(reminderMap.containsKey(reminder.id) && weeklyStreaks.none { it.reminder.id == reminder.id }) {
                weeklyStreaks = weeklyStreaks + streak
            }
        } else {
            if(reminderMap.containsKey(reminder.id) && monthlyStreaks.none { it.reminder.id == reminder.id }) {
                monthlyStreaks = monthlyStreaks + streak
            }
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
        numberCompleted: Int = 0,
        repeat: Boolean = false,
        repeatIndefinitely: Boolean = false,
        repeatNumber: Int = 0,
        repeatInterval: String = "",
    ): Streak {
        val total = if (reminder.daily) {
            reminder.timesPerDay * 7
        } else {
            reminder.timesPerMonth
        }
        return Streak(
            reminder = reminder,
            totalAmount = total,
            numberCompleted = numberCompleted,
            repeat = repeat,
            repeatIndefinitely = repeatIndefinitely,
            repeatNumber = repeatNumber,
            repeatInterval = repeatInterval,
        )
    }

    fun incrementStreak(streak: Streak) {
        streak.numberCompleted += 1
    }

    // =============== Badges ========================
    var allBadges by mutableStateOf(
        listOf(
            Badge(
                3,
                R.drawable.flame,
                enumColor.BrandOne,
                "On Fire!",
                "Complete your first week streak.",
                true,
                Calendar.getInstance().apply {
                    set(2025, Calendar.OCTOBER, 25,12, 0,0)
                    set(Calendar.MILLISECOND,0)
                }.timeInMillis,
            ),
            Badge(
                2,
                R.drawable.sun_glasses,
                enumColor.BrandOne,
                "Looking Good!",
                "Customize the look of your avatar.",
                true,
                Calendar.getInstance().apply {
                    set(2025, Calendar.OCTOBER, 19,12, 0,0)
                    set(Calendar.MILLISECOND,0)
                }.timeInMillis,
            ),
            Badge(
                1,
                R.drawable.one,
                enumColor.SecondaryTwo,
                "Everyone Starts at the Beginning",
                "You created your account and started your Life Leveling journey!",
                true,
                Calendar.getInstance().apply {
                    set(2025, Calendar.OCTOBER, 18,12, 0,0)
                    set(Calendar.MILLISECOND,0)
                }.timeInMillis,
            ),
            Badge(
                4,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            Badge(
                5,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            Badge(
                6,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            Badge(
                7,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            Badge(
                8,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            Badge(
                9,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            Badge(
                10,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            Badge(
                11,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            Badge(
                12,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            Badge(
                13,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            Badge(
                14,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            Badge(
                15,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            Badge(
                16,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            Badge(
                17,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            Badge(
                18,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            Badge(
                19,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            Badge(
                20,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            Badge(
                21,
                R.drawable.question_mark,
                enumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
        )
    )

    // Complete a badge by ID
    fun completeBadge(badgeId: Int) {
        // Find badge
        val badge = allBadges.find { it.id == badgeId } ?: return
        // Save badge and update to completed
        val completedBadge = badge.copy(
            completed = true,
            completedOn = System.currentTimeMillis(),
        )
        // Delete badge from list and add new completed version to the top
        allBadges = listOf(completedBadge) + allBadges.filter { it.id != badgeId }
    }
}

data class Reminder (
    val id: Int,
    val name: String,
    val icon: Int,
    val color: enumColor?,
    val daily: Boolean,
    val timesPerDay: Int,
    val timesPerMonth: Int,
    val completedTally: Long
)

data class Streak (
    val reminder: Reminder,
    val totalAmount: Int,
    var numberCompleted: Int = 0,
    var repeat: Boolean = false,
    var repeatIndefinitely: Boolean = false,
    var repeatNumber: Int = 0,
    var repeatInterval: String = "",
)

data class Badge (
    val id: Int,
    val icon: Int,
    val color: enumColor,
    val title: String,
    val description: String,
    val completed: Boolean = false,
    val completedOn: Long? = null,
)

data class calReminder(
    val id: Int,
    val name: String,
    val isDoNotRepeat: Boolean,
    val isDaily: Boolean,
    val isWeekDay: Boolean,
    val isIndefiniteRepeat: Boolean,
    val icon: Int,
    val selectedHours: Int,
    val selectedMinutes: Int,
    val amOrPm: Int,
    val reminderAmount: Int,
    val reminderAmountHourDayWeek: Int,
    val repeatAmount: Int,
    val selectRepeatAmount: Int,
)

