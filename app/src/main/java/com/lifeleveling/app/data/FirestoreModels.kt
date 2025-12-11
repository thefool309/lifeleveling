package com.lifeleveling.app.data

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.lifeleveling.app.ui.components.TestUser.mostCompletedReminder

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
    val badges: List<Badge> = emptyList(),       // greyed out badges/ secret badges
    val reminders: List<Reminder> = emptyList(),
    val fightOrMeditate: Int = 0,
    // User Journey Stats to be saved
    val weekStreaksCompleted: Long = 0,
    val monthStreaksCompleted: Long = 0,
    var mostCompletedReminder: Pair<String, Long> = Pair("", 0L),
    // Settings to be saved
    val isDarkTheme: Boolean = true,
    )

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
        )
    }

    /**
     * Recalculates the derived values that are used in the UserJourney screen
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

    fun recalculateAfterLevelUp() : UsersData {
        return this.copy(
            xpToNextLevel = calculateXpToNextLevel(),
            lifePointsNotUsed = calculateUnusedLifePoints(),
        )
    }

    fun calculateXpToNextLevel() : Long {
        return (userBase?.level ?: 1) * 100L
    }

    fun calculateMaxHealth() : Long {
        val healthStat = userBase?.stats?.health
        return baseHealth + ((healthStat ?: 0) * 5)
    }

    fun calculateUnusedLifePoints() : Long{
        return (userBase?.lifePointsTotal ?: 0) - (userBase?.lifePointsUsed ?: 0)
    }

    fun calculateEnabledReminders() : List<Reminder> {
        return userBase?.reminders?.filter { it.enabled } ?: emptyList()
    }

    fun calculateTotalStreaks() : Long {
        return (userBase?.weekStreaksCompleted ?: 0) + (userBase?.monthStreaksCompleted ?: 0)
    }

    fun calculateBadgesEarned() : Long {
        return (userBase?.badges?.filter { it.completed })?.size?.toLong() ?: 0
    }

    fun calculateAllExp() : Double {
        val level = userBase?.level ?: 1
        val exp = 100L * (level - 1) * level / 2
        return (userBase?.currentXp ?: 0.0) + exp
    }

    fun calculateCoinsSpent() : Long {
        return (userBase?.allCoinsEarned ?: 0) - (userBase?.coinsBalance ?: 0)
    }

    fun calculateMostCompletedReminder() : Pair<String, Long> {
        val highest = userBase?.reminders?.maxByOrNull { it.completedTally }
        if (highest != null) {
            if (userBase!!.mostCompletedReminder.second < highest.completedTally) {
                return Pair(highest.title, highest.completedTally)
            }
        }
        return userBase!!.mostCompletedReminder
    }
}



