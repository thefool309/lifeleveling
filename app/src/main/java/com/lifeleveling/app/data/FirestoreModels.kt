package com.lifeleveling.app.data

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser

/**
 * The base of the user information. Everything inside of this are values that can be written into firestore
 */
data class UsersBase(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val coinsBalance: Long = 0,
    val allCoinsEarned: Long = 0,
    // Update from inline map to now use Stats data class
    val stats: Stats = Stats(),
    val onboardingComplete: Boolean = false,
    val createdAt: Timestamp? = null,
    val lastUpdate: Timestamp? = null,
    // variables that were missing during our first introduction of the Users collection
    var level: Long = 1,
    val lifePointsUsed: Long = 0,           // used lifePoints
    val lifePointsTotal: Long = 5,      // all lifePoints - saving the total in case we want to add to the total for badge completion
    val currentXp: Double = 0.0,        // Current Experience // Experience needed to level up
    val currHealth: Long = 60,          // Default 60 at start
    // Badges can be stored in arrays of Badge objects on user doc.
    val completedBadges: Map<String, Timestamp> = emptyMap(),       // greyed out badges/ secret badges
    val fightOrMeditate: Int = 0,
    // User Journey Stats to be saved
    val weekStreaksCompleted: Long = 0,
    val monthStreaksCompleted: Long = 0,
    var mostCompletedReminder: Pair<String, Long> = Pair("", 0L),
    // Settings to be saved
    val isDarkTheme: Boolean = true,

    )

/**
 * An extension of the UsersBase
 * Contains variables that are derived from the base class and the functions to calculate them.
 * Holds flags to be observed by the state.
 * @param error Used to save error messages to pass to loggers
 * @param levelUpCoins To hold an amount of coins to be added during level up logic
 * @param isLoading A local value that triggers a spinner wheel to display on the UI
 * @param isLoggedIn A flag the application listens for to navigate the user to authentication screens versus main screens
 * @param levelUpFlag A flag that triggers an overlay congratulations message to the user for their level up.
 */
data class UsersData (
    var userBase: UsersBase = UsersBase(),

    // Run time collections that loading from firestore directly could affect
    val reminders: List<Reminder> = emptyList(),
    val streaks: List<Streak> = emptyList(),
    val badges: List<Badge> = emptyList(),

    //Lists
    var enabledReminders: List<Reminder> = emptyList(),
    var weeklyStreaks: List<Streak> = emptyList(),
    var monthlyStreaks: List<Streak> = emptyList(),
    var badgeDisplay: List<BadgeDisplay> = emptyList(),
    val reminderCompletions: Map<String, Int> = emptyMap(),

    // for a derived property like this it is not necessary to include in firebase
    // since it's calculated everytime a user is instantiated
    // for this reason xpToNextLevel is not included in the primary constructor meaning it won't be serialized
    var xpToNextLevel: Long = 0L,
    var maxHealth: Long = 0L,
    val baseHealth: Long = 60L,

    // User Journey Stats
    var totalStreaksCompleted: Long = 0L,
    var badgesEarned: Long = 0L,
    var allExpEver: Double = 0.0,
    var coinsSpent: Long = 0L,
    val profileCreatedDate: String = "Unknown",
    val timeSinceCreated: String = "",
    val totalRemindersCompleted: Long = 0,
    val mostCompletedReminderDisplay: String = "",

    // Flags
    var error: String? = null,
    val fbUser: FirebaseUser? = null,
    val levelUpCoins: Long = 0L,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val levelUpFlag: Boolean = false,
    val isCalendarLoading: Boolean = false,
) {

//    init {
//        recalculateAll()
//    }

    /**
     * To set the recalculate function to go off.
     * Doing an init sets off a loop
     */
    fun withBase(newBase: UsersBase): UsersData {
        return this.copy(userBase = newBase).recalculateAll()
    }

    /**
     * Calculates all the derived values
     */
    fun recalculateAll() : UsersData {
        return this.copy(
            userBase = userBase.copy(
                mostCompletedReminder = calculateMostCompletedReminder(),
            ),
            xpToNextLevel = calculateXpToNextLevel(),
            maxHealth = calculateMaxHealth(),
            enabledReminders = calculateEnabledReminders(),
            totalStreaksCompleted = calculateTotalStreaks(),
            badgesEarned = calculateBadgesEarned(),
            allExpEver = calculateAllExp(),
            coinsSpent = calculateCoinsSpent(),
            weeklyStreaks = calcWeeklyStreaks(),
            monthlyStreaks = calcMonthlyStreaks(),
            badgeDisplay = getBadgeDisplayList()
        )
    }

    /**
     * Recalculates the derived values that are used in the UserJourney screen
     * @return A UsersData object for updating the state
     * @author Elyseia
     */
    fun recalculatingUserJourney() : UsersData {
        return this.copy(
            userBase = userBase.copy(
                mostCompletedReminder = calculateMostCompletedReminder(),
            ),
            totalStreaksCompleted = calculateTotalStreaks(),
            badgesEarned = calculateBadgesEarned(),
            allExpEver = calculateAllExp(),
            coinsSpent = calculateCoinsSpent(),
        )
    }

    /**
     * Recalculates the derived values effected by the level up change in UsersBase
     * @return A whole UsersData object for updating the state
     * @author Elyseia
     */
    fun recalculateAfterLevelUp() : UsersData {
        return this.copy(
            xpToNextLevel = calculateXpToNextLevel(),
            coinsSpent = calculateCoinsSpent(),
            allExpEver = calculateAllExp(),
        )
    }

    /**
     * Calls the function that sets up a badge display list based on completed badges.
     * @author Elyseia
     */
    fun recalculateAfterBadgeCompletion() : UsersData {
        return this.copy(
            badgeDisplay = getBadgeDisplayList()
        )
    }


    /**
     * Recalculates the max health after the stats item has been updated
     * @return A UsersData object for updating the state
     * @author Elyseia
     */
    fun recalculateStatDependencies() : UsersData {
        return this.copy(
            maxHealth = calculateMaxHealth(),
        )
    }

    /**
     * Separates the streaks pulled from firestore into two lists of weekly and monthly.
     * @return A UsersData object for updating the state
     * @author Elyseia
     */
    fun separateStreaks(): UsersData {
        return this.copy(
            weeklyStreaks = calcWeeklyStreaks(),
            monthlyStreaks = calcMonthlyStreaks(),
        )
    }

    /**
     * Recalculates which reminders are enabled
     * @return a UsersData object for updating the state
     * @author Elyseia
     */
    fun updateReminderDependencies(): UsersData {
        return this.copy(
            enabledReminders = calculateEnabledReminders()
        )
    }

    /**
     * Calculated the experience needed to reach the next level
     * @return A long value to pass into the UsersData
     * @author Elyseia
     */
    fun calculateXpToNextLevel() : Long {
        return (userBase.level) * 100L
    }

    /**
     * Calculated the max health from the health stat.
     * @return A long value to pass into the UsersData
     * @author Elyseia
     */
    fun calculateMaxHealth() : Long {
        val healthStat = userBase.stats.health
        return baseHealth + ((healthStat) * 5)
    }

    /**
     * Calculates a list of enabled reminders out of all the saved reminders the user has
     * @return A list of reminders to pass into UsersData
     * @author Elyseia
     */
    fun calculateEnabledReminders() : List<Reminder> {
        return reminders.filter { it.enabled }
    }

    /**
     * Calculates the total number of streaks completed.
     * @return A long to pass into UsersData
     * @author Elyseia
     */
    fun calculateTotalStreaks() : Long {
        return (userBase.weekStreaksCompleted) + (userBase.monthStreaksCompleted)
    }

    /**
     * Calculates how many badges have been earned out of the badge list.
     * @return A long to pass into UsersData
     * @author Elyseia
     */
    fun calculateBadgesEarned() : Long {
        return (userBase.completedBadges.size).toLong()
    }

    /**
     * Calculates all the experience a user has received up to this point.
     * @return A double to pass into UsersData
     * @author Elyseia
     */
    fun calculateAllExp() : Double {
        val level = userBase.level
        val exp = 100L * (level - 1) * level / 2
        return userBase.currentXp + exp
    }

    /**
     * Calculates the amount of coins that have been spent.
     * @return A long to pass into UsersData
     * @author Elyseia
     */
    fun calculateCoinsSpent() : Long {
        return userBase.allCoinsEarned - userBase.coinsBalance
    }

    /**
     * Calculates which reminder has been completed the most.
     * @return A Pair that has the name of the reminder and the number of times it was completed.
     * @author Elyseia
     */
    fun calculateMostCompletedReminder() : Pair<String, Long> {
        val highest = reminders.maxByOrNull { it.completedTally }
        if (highest != null) {
            if (userBase.mostCompletedReminder.second < highest.completedTally) {
                return Pair(highest.title, highest.completedTally)
            }
        }
        return userBase.mostCompletedReminder
    }

    // TODO: Separate Streak list into week and month
    /**
     * Separates the list of streaks into a list of the weekly streaks.
     * @return A list of streaks that run for a week.
     * @author Elyseia
     */
    fun calcWeeklyStreaks() : List<Streak> {
        return streaks.filter { it.weekly }
    }

    /**
     * Separates the list of streaks into a list of monthly streaks.
     * @return A list of streaks that run for a month.
     * @author Elyseia
     */
    fun calcMonthlyStreaks() : List<Streak> {
        return streaks.filter { it.weekly }
    }

    /**
     * Creates a list of badges from the badge list and user's completed badges.
     * Sorts them by newest completed first
     * Then the rest of the badges alphabetically
     * @author Elyseia
     */
    fun getBadgeDisplayList(): List<BadgeDisplay> {
        return badges.map { badge ->
            val ts = userBase.completedBadges[badge.badgeId]
            BadgeDisplay(
                badge = badge,
                completedAt = ts,
                isCompleted = ts != null,
            )
        }
            .sortedWith(compareByDescending<BadgeDisplay> { it. completedAt != null }
                .thenByDescending { it.completedAt }
                .thenBy { it.badge.badgename.lowercase() })
    }
}

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
