package com.lifeleveling.app.data

import com.google.firebase.Timestamp

data class Users(
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
    ) {
    // for a derived property like this it is not necessary to include in firebase
    // since it's calculated everytime a user is instantiated
    // for this reason xpToNextLevel is not included in the primary constructor meaning it won't be serialized
    var xpToNextLevel: Long = 0L
    var maxHealth: Long = 0L
    val baseHealth: Long = 60L
    var lifePointsNotUsed: Long = 0L
    //Lists
    var enabledReminders: List<Reminder> = emptyList()
    // User Journey Stats
    var totalStreaksCompleted: Long = 0L
    var badgesEarned: Long = 0L
    var allExpEver: Double = 0.0
    var coinsSpent: Long = 0L

    // For use in functions
    val isLoading: Boolean = false
    val isLoggedIn: Boolean = false

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
        xpToNextLevel = level * 100L
    }

    fun calculateMaxHealth() {
        val healthStat = stats.health
        maxHealth = baseHealth + (healthStat * 5)
    }

    fun calculateUnusedLifePoints() {
        lifePointsNotUsed = lifePointsTotal - lifePointsUsed
    }

    fun calculateEnabledReminders() {
        enabledReminders = reminders.filter { it.enabled }
    }

    fun calculateTotalStreaks() {
        totalStreaksCompleted = weekStreaksCompleted + monthStreaksCompleted
    }

    fun calculateBadgesEarned() {
        badgesEarned = (badges.filter { it.completed }).size.toLong()
    }

    fun calculateAllExp() {
        val exp = 100L * level * (level + 1) / 2
        allExpEver = currentXp + exp
    }

    fun calculateCoinsSpent() {
        coinsSpent = allCoinsEarned - coinsBalance
    }

    fun calculateMostCompletedReminder() {
        val highest = reminders.maxByOrNull { it.completedTally }
        if (highest != null) {
            if (mostCompletedReminder.second < highest.completedTally) {
                mostCompletedReminder = Pair(highest.title, highest.completedTally)
            }
        }
    }
}



