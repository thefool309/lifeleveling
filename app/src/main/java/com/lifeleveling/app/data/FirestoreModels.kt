package com.lifeleveling.app.data

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser

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

class UsersData (
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
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    var error: String? = null,
    fbUser: FirebaseUser? = null,
) {

    init {
        calculateXpToNextLevel()
        calculateMaxHealth()
        calculateUnusedLifePoints()
        calculateEnabledReminders()
        calculateTotalStreaks()
        calculateBadgesEarned()
        calculateAllExp()
        calculateCoinsSpent()
        calculateMostCompletedReminder()
    }

    fun recalculatingUserJourney() {
        calculateTotalStreaks()
        calculateBadgesEarned()
        calculateAllExp()
        calculateCoinsSpent()
        calculateMostCompletedReminder()
    }

    fun calculateXpToNextLevel() {
        xpToNextLevel = (userBase?.level ?: 1) * 100L
    }

    fun calculateMaxHealth() {
        val healthStat = userBase?.stats?.health
        maxHealth = baseHealth + ((healthStat ?: 0) * 5)
    }

    fun calculateUnusedLifePoints() {
        lifePointsNotUsed = (userBase?.lifePointsTotal ?: 0) - (userBase?.lifePointsUsed ?: 0)
    }

    fun calculateEnabledReminders() {
        enabledReminders = userBase?.reminders?.filter { it.enabled } ?: emptyList()
    }

    fun calculateTotalStreaks() {
        totalStreaksCompleted = (userBase?.weekStreaksCompleted ?: 0) + (userBase?.monthStreaksCompleted ?: 0)
    }

    fun calculateBadgesEarned() {
        badgesEarned = (userBase?.badges?.filter { it.completed })?.size?.toLong() ?: 0
    }

    fun calculateAllExp() {
        val level = userBase?.level ?: 1
        val exp = 100L * (level - 1) * level / 2
        allExpEver = (userBase?.currentXp ?: 0.0) + exp
    }

    fun calculateCoinsSpent() {
        coinsSpent = (userBase?.allCoinsEarned ?: 0) - (userBase?.coinsBalance ?: 0)
    }

    fun calculateMostCompletedReminder() {
        val highest = userBase?.reminders?.maxByOrNull { it.completedTally }
        if (highest != null) {
            if (userBase!!.mostCompletedReminder.second < highest.completedTally) {
                userBase!!.mostCompletedReminder = Pair(highest.title, highest.completedTally)
            }
        }
    }
}



