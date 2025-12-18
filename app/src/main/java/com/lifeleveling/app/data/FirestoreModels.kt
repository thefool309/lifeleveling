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
    val streaks: List<Streak> = emptyList(),
    val onboardingComplete: Boolean = false,
    val createdAt: Timestamp? = null,
    val lastUpdate: Timestamp? = null,
    // variables that were missing during our first introduction of the Users collection
    var level: Long = 1,
    val lifePointsUsed: Long = 0,           // used lifePoints
    val lifePointsTotal: Long = 0,      // all lifePoints - saving the total in case we want to add to the total for badge completion
    val currentXp: Double = 0.0,        // Current Experience // Experience needed to level up
    val currHealth: Long = 60,          // Default 60 at start
    // Badges can be stored in arrays of Badge objects on user doc.
    val badges: List<Badge> = emptyList(),       // greyed out badges/ secret badges TODO: Write new badges in Firebase and put completed and IDs here
    val reminders: List<Reminder> = emptyList(),
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
    var userBase: UsersBase? = null,
    // for a derived property like this it is not necessary to include in firebase
    // since it's calculated everytime a user is instantiated
    // for this reason xpToNextLevel is not included in the primary constructor meaning it won't be serialized
    var xpToNextLevel: Long = 0L,
    var maxHealth: Long = 0L,
    val baseHealth: Long = 60L,
    var lifePointsNotUsed: Long = 0L,
    //Lists
    var enabledReminders: List<Reminder> = emptyList(),
    var weeklyStreaks: List<Streak> = emptyList(),
    var monthlyStreaks: List<Streak> = emptyList(),
    // User Journey Stats
    var totalStreaksCompleted: Long = 0L,
    var badgesEarned: Long = 0L,
    var allExpEver: Double = 0.0,
    var coinsSpent: Long = 0L,

    // For use in functions
    var error: String? = null,
    val fbUser: FirebaseUser? = null,
    val levelUpCoins: Long = 0L,

    // Flags
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val levelUpFlag: Boolean = false,
) {

    init {
        recalculateAll()
    }

    /**
     * Calculates all the derived values
     */
    fun recalculateAll() : UsersData {
        return this.copy(
            userBase = userBase?.copy(
                mostCompletedReminder = calculateMostCompletedReminder(),
            ),
            xpToNextLevel = calculateXpToNextLevel(),
            maxHealth = calculateMaxHealth(),
            lifePointsNotUsed = calculateUnusedLifePoints(),
            enabledReminders = calculateEnabledReminders(),
            totalStreaksCompleted = calculateTotalStreaks(),
            badgesEarned = calculateBadgesEarned(),
            allExpEver = calculateAllExp(),
            coinsSpent = calculateCoinsSpent(),
            weeklyStreaks = calcWeeklyStreaks(),
            monthlyStreaks = calcMonthlyStreaks(),
        )
    }

    /**
     * Recalculates the derived values that are used in the UserJourney screen
     * @return A UsersData object for updating the state
     * @author Elyseia
     */
    fun recalculatingUserJourney() : UsersData {
        return this.copy(
            userBase = userBase?.copy(
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
            lifePointsNotUsed = calculateUnusedLifePoints(),
            coinsSpent = calculateCoinsSpent(),
            allExpEver = calculateAllExp(),
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
     * Calculated the experience needed to reach the next level
     * @return A long value to pass into the UsersData
     * @author Elyseia
     */
    fun calculateXpToNextLevel() : Long {
        return (userBase?.level ?: 1) * 100L
    }

    /**
     * Calculated the max health from the health stat.
     * @return A long value to pass into the UsersData
     * @author Elyseia
     */
    fun calculateMaxHealth() : Long {
        val healthStat = userBase?.stats?.health
        return baseHealth + ((healthStat ?: 0) * 5)
    }

    /**
     * Calculates the amount of life points that have not been assigned yet.
     * @return A long to pass into UsersData
     * @author Elyseia
     */
    fun calculateUnusedLifePoints() : Long{
        return (userBase?.lifePointsTotal ?: 0) - (userBase?.lifePointsUsed ?: 0)
    }

    /**
     * Calculates a list of enabled reminders out of all the saved reminders the user has
     * @return A list of reminders to pass into UsersData
     * @author Elyseia
     */
    fun calculateEnabledReminders() : List<Reminder> {
        return userBase?.reminders?.filter { it.enabled } ?: emptyList()
    }

    /**
     * Calculates the total number of streaks completed.
     * @return A long to pass into UsersData
     * @author Elyseia
     */
    fun calculateTotalStreaks() : Long {
        return (userBase?.weekStreaksCompleted ?: 0) + (userBase?.monthStreaksCompleted ?: 0)
    }

    /**
     * Calculates how many badges have been earned out of the badge list.
     * @return A long to pass into UsersData
     * @author Elyseia
     */
    fun calculateBadgesEarned() : Long {
        return (userBase?.badges?.filter { it.completed })?.size?.toLong() ?: 0
    }

    /**
     * Calculates all the experience a user has received up to this point.
     * @return A double to pass into UsersData
     * @author Elyseia
     */
    fun calculateAllExp() : Double {
        val level = userBase?.level ?: 1
        val exp = 100L * (level - 1) * level / 2
        return (userBase?.currentXp ?: 0.0) + exp
    }

    /**
     * Calculates the amount of coins that have been spent.
     * @return A long to pass into UsersData
     * @author Elyseia
     */
    fun calculateCoinsSpent() : Long {
        return (userBase?.allCoinsEarned ?: 0) - (userBase?.coinsBalance ?: 0)
    }

    /**
     * Calculates which reminder has been completed the most.
     * @return A Pair that has the name of the reminder and the number of times it was completed.
     * @author Elyseia
     */
    fun calculateMostCompletedReminder() : Pair<String, Long> {
        val highest = userBase?.reminders?.maxByOrNull { it.completedTally }
        if (highest != null) {
            if (userBase!!.mostCompletedReminder.second < highest.completedTally) {
                return Pair(highest.title, highest.completedTally)
            }
        }
        return userBase!!.mostCompletedReminder
    }

    // TODO: Separate Streak list into week and month
    /**
     * Separates the list of streaks into a list of the weekly streaks.
     * @return A list of streaks that run for a week.
     * @author Elyseia
     */
    fun calcWeeklyStreaks() : List<Streak> {
        return userBase?.streaks?.filter { it.weekly } ?: emptyList()
    }

    /**
     * Separates the list of streaks into a list of monthly streaks.
     * @return A list of streaks that run for a month.
     * @author Elyseia
     */
    fun calcMonthlyStreaks() : List<Streak> {
        return userBase?.streaks?.filter { !it.weekly } ?: emptyList()
    }
}



