package com.lifeleveling.app.ui.components

import android.icu.util.Calendar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.theme.EnumColor

/*
* This is just a singleton test user for us to hardcode values into for UI testing
* Later this can be used to build the logic needed for each user account
* as well as the local save of user logic.
*/

object _TestingUser {
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
        _TestReminder(1, "Drink Water", R.drawable.water_drop, null, true, 3, 0, 20),
        _TestReminder(2, "Laundry", R.drawable.shirt, EnumColor.BrandTwo, false, 0, 4, 2),
        _TestReminder(3, "Shower", R.drawable.shower_bath, null, false, 0, 15, 3),
        _TestReminder(4, "Read", R.drawable.person_reading, EnumColor.SecondaryTwo, false, 0, 8, 1),
        _TestReminder(5, "Run", R.drawable.person_running_color, null, false, 0, 4, 1),
        _TestReminder(6, "Make my bed", R.drawable.bed_color, null, true, 1, 0, 6),
        _TestReminder(7, "Brush teeth", R.drawable.toothbrush, null, true, 2, 0, 12),
        _TestReminder(8, "Wash Hair", R.drawable.shower_bath, null, false, 0, 9, 5),
        _TestReminder(9, "Feed the dog", R.drawable.grass, null, true, 2, 0, 13),
        _TestReminder(10, "Take Medication", R.drawable.med_bottle, null, true, 2, 0, 9),
    )
    // ================================= My Reminders Test stuff =================================
    val calendarReminders = mutableStateOf(listOf(
        _calReminder(isEnabled = true, id = 1, day = 12,month = 12, year = 0,"Go to store", false,false,R.drawable.shop_color, 1, 32, 1,5,2,3,1,0),
        _calReminder(isEnabled = true, id =2, day = 12,month = 12, year = 0,"shower", false,true,R.drawable.shower_bath, 7, 3, 0,0,0,0,0,1),
        _calReminder(isEnabled = true, id =3, day = 12,month = 12, year = 0,"Doctor", false,false,R.drawable.shirt_color, 2, 12, 0,0,0,0,0,2),
        _calReminder(isEnabled = true, id =4, day = 12,month = 12, year = 0,"TEST", true,false,R.drawable.med_bottle, 6, 12, 0,0,0,0,0,3),
        _calReminder(isEnabled = true, id =5, day = 12,month = 12, year = 0,"Run", false,false,R.drawable.person_running, 1, 12, 0,0,0,0,0,1),
        _calReminder(isEnabled = true, id =6, day = 12,month = 12, year = 0,"T3", false,true,R.drawable.bell, 5, 22, 0,0,0,0,0,0),
        _calReminder(isEnabled = true, id =7, day = 2,month = 1, year = 1,"T4", false,false,R.drawable.document, 1, 12, 0,0,0,0,0,3),
        _calReminder(isEnabled = true, id =8, day = 22,month =1, year = 1,"T5", false,false,R.drawable.heart, 1, 12, 0,0,0,0,0,4),
        _calReminder(isEnabled = true, id =9, day = 21,month = 2, year = 1,"T5", false,false,R.drawable.heart, 1, 12, 0,0,0,0,0,4),
        _calReminder(isEnabled = true, id =10, day = 9,month = 3, year = 4,"T5", true,false,R.drawable.heart, 1, 12, 0,0,0,2,1,6),
        _calReminder(isEnabled = true, id =11, day = 8,month = 4, year = 4,"T5", false,false,R.drawable.heart, 1, 12, 0,0,0,0,0,3),
        _calReminder(isEnabled = true, id =12, day = 3,month = 5, year = 4,"T5", false,false,R.drawable.heart, 1, 12, 0,0,0,0,0,2),

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
    fun addReminder(name: String, icon: Int, color: EnumColor?, daily: Boolean, timesPerDay: Int, timesPerMonth: Int) {
        val newReminder = _TestReminder(nextId++, name, icon, color, daily, timesPerDay, timesPerMonth, 0)
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
    fun getReminderById(id: Int): _TestReminder? = reminderMap[id]

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
    fun addToWeeklyStreak(reminder: _TestReminder) {
        if (reminderMap.containsKey(reminder.id) && weeklyStreaks.none { it.reminder.id == reminder.id }) {
            weeklyStreaks = weeklyStreaks + reminderToStreak(reminder)
        }
    }
    fun addToMonthlyStreak(reminder: _TestReminder) {
        if (reminderMap.containsKey(reminder.id) && monthlyStreaks.none { it.reminder.id == reminder.id }) {
            monthlyStreaks = monthlyStreaks + reminderToStreak(reminder)
        }
    }
    fun addStreak(
        reminder: _TestReminder,
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
    fun removeFromWeeklyStreak(reminder: _TestReminder) {
        weeklyStreaks = weeklyStreaks.filter { it.reminder.id != reminder.id }
    }
    fun removeFromMonthlyStreak(reminder: _TestReminder) {
        monthlyStreaks = monthlyStreaks.filter { it.reminder.id != reminder.id }
    }

    private fun removeFromStreaks(id: Int) {
        val reminder = reminderMap[id] ?: return
        weeklyStreaks = weeklyStreaks.filter { it.reminder.id == reminder.id }
        monthlyStreaks = monthlyStreaks.filter { it.reminder.id == reminder.id }
    }

    private fun reminderToStreak(
        reminder: _TestReminder,
        numberCompleted: Int = 0,
        repeat: Boolean = false,
        repeatIndefinitely: Boolean = false,
        repeatNumber: Int = 0,
        repeatInterval: String = "",
    ): _TestStreak {
        val total = if (reminder.daily) {
            reminder.timesPerDay * 7
        } else {
            reminder.timesPerMonth
        }
        return _TestStreak(
            reminder = reminder,
            totalAmount = total,
            numberCompleted = numberCompleted,
            repeat = repeat,
            repeatIndefinitely = repeatIndefinitely,
            repeatNumber = repeatNumber,
            repeatInterval = repeatInterval,
        )
    }

    fun incrementStreak(streak: _TestStreak) {
        streak.numberCompleted += 1
    }

    // =============== Badges ========================
    var allBadges by mutableStateOf(
        listOf(
            _TestBadge(
                3,
                R.drawable.flame,
                EnumColor.BrandOne,
                "On Fire!",
                "Complete your first week streak.",
                true,
                Calendar.getInstance().apply {
                    set(2025, Calendar.OCTOBER, 25,12, 0,0)
                    set(Calendar.MILLISECOND,0)
                }.timeInMillis,
            ),
            _TestBadge(
                2,
                R.drawable.sun_glasses,
                EnumColor.BrandOne,
                "Looking Good!",
                "Customize the look of your avatar.",
                true,
                Calendar.getInstance().apply {
                    set(2025, Calendar.OCTOBER, 19,12, 0,0)
                    set(Calendar.MILLISECOND,0)
                }.timeInMillis,
            ),
            _TestBadge(
                1,
                R.drawable.one,
                EnumColor.SecondaryTwo,
                "Everyone Starts at the Beginning",
                "You created your account and started your Life Leveling journey!",
                true,
                Calendar.getInstance().apply {
                    set(2025, Calendar.OCTOBER, 18,12, 0,0)
                    set(Calendar.MILLISECOND,0)
                }.timeInMillis,
            ),
            _TestBadge(
                4,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            _TestBadge(
                5,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            _TestBadge(
                6,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            _TestBadge(
                7,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            _TestBadge(
                8,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            _TestBadge(
                9,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            _TestBadge(
                10,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            _TestBadge(
                11,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            _TestBadge(
                12,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            _TestBadge(
                13,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            _TestBadge(
                14,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            _TestBadge(
                15,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            _TestBadge(
                16,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            _TestBadge(
                17,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            _TestBadge(
                18,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            _TestBadge(
                19,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            _TestBadge(
                20,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
                "TBA",
                "Other badges to be filled out.",
                false
            ),
            _TestBadge(
                21,
                R.drawable.question_mark,
                EnumColor.BrandTwo,
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

data class _TestReminder (
    val id: Int,
    val name: String,
    val icon: Int,
    val color: EnumColor?,
    val daily: Boolean,
    val timesPerDay: Int,
    val timesPerMonth: Int,
    val completedTally: Long
)

data class _TestStreak (
    val reminder: _TestReminder,
    val totalAmount: Int,
    var numberCompleted: Int = 0,
    var repeat: Boolean = false,
    var repeatIndefinitely: Boolean = false,
    var repeatNumber: Int = 0,
    var repeatInterval: String = "",
)

data class _TestBadge (
    val id: Int,
    val icon: Int,
    val color: EnumColor,
    val title: String,
    val description: String,
    val completed: Boolean = false,
    val completedOn: Long? = null,
)

data class _calReminder(
    val isEnabled: Boolean,
    val id: Int,
    val day: Int,
    val month: Int,
    val year: Int,
    val name: String,
    val repeatReminder: Boolean,
    val isDaily: Boolean,
    //val isWeekDay: Boolean,
    //val isIndefiniteRepeat: Boolean,
    val icon: Int,
    val selectedHours: Int,
    val selectedMinutes: Int,
    val amOrPm: Int,
    val reminderAmount: Int,
    val reminderAmountHourDayWeek: Int,
    val repeatAmount: Int,
    val selectRepeatAmount: Int,
    val color: Int
)

