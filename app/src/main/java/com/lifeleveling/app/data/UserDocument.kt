package com.lifeleveling.app.data

import com.google.firebase.Timestamp

/**
 * This is the Users data class. It inherits from UserBase
 */
data class UserDocument(
    /**
     * Information for the user that WILL be written into firebase
     */
    override val userId: String = "",
    override val displayName: String = "",
    override val email: String = "",
    override var level: Long = 1,
    override val photoUrl: String = "",
    override val stats: Stats = Stats(),
    override val currentExp: Long = 0,
    override val coins: Long = 0,
    override val currentHealth: Int = 60,
    override val lifePointsUsed: Long = 0,
    override val lifePointsTotal: Long = 3,
    override val coinsBalance: Long = 0,
    override val onboardingComplete: Boolean = false,
    override val lifePoints: Long = 0,
    override val currentXp: Double = 0.0,
    override val currHealth: Long = 0,
    override val badgesLocked: List<Badge> = emptyList(),
    override val badgesUnlocked: List<Badge> = emptyList(),
    override val fightOrMeditate: Int = 0,
    override val reminders: List<Reminder> = listOf(),
    override val streaks: List<Streak> = listOf(),
    override val weekStreaksCompleted: Long = 0,
    override val monthStreaksCompleted: Long = 0,
    override val allCoinsEarned: Long = 0,
    override val lastUpdate: Timestamp? = Timestamp.Companion.now(),
    override val createdAt: Timestamp? = Timestamp.Companion.now(),

    val isDarkTheme: Boolean = true,
) : UserBase {}

class UserState(
    override val userId: String = "",
    override val displayName: String = "",
    override val email: String = "",
    override var level: Long = 1,
    override val photoUrl: String = "",
    override val stats: Stats = Stats(),
    override val currentExp: Long = 0,
    override val coins: Long = 0,
    override val currentHealth: Int = 60,
    override val lifePointsUsed: Long = 0,
    override val lifePointsTotal: Long = 3,
    override val coinsBalance: Long = 0,
    override val onboardingComplete: Boolean = false,
    override val lifePoints: Long = 0,
    override val currentXp: Double = 0.0,
    override val currHealth: Long = 0,
    override val badgesLocked: List<Badge> = emptyList(),
    override val badgesUnlocked: List<Badge> = emptyList(),
    override val fightOrMeditate: Int = 0,
    override val reminders: List<Reminder> = listOf(),
    override val streaks: List<Streak> = listOf(),
    override val weekStreaksCompleted: Long = 0,
    override val monthStreaksCompleted: Long = 0,
    override val allCoinsEarned: Long = 0,
    override val lastUpdate: Timestamp? = Timestamp.Companion.now(),
    override val createdAt: Timestamp? = Timestamp.Companion.now(),
    /**
     * Information for the user that WILL NOT be written into firebase
     */
    var expToNextLevel: Long = 0,
    var maxHealth: Long = 60,
    var lifePointsNotUsed: Long = 0,

    var enabledReminders: List<Reminder> = listOf(),

    var totalStreaksCompleted: Long = 0,
    var badgesEarned: Long = 0,
    var allExpEver: Long = 0,
    var coinsSpend: Long = 0,
    var mostCompletedRemind: Pair<String, Long> = Pair("", 0L),

    var isLoading: Boolean = false,
    var errorMessage: String? = null,

    var isLoggedIn: Boolean = false,
    val baseHealth: Long = 60L
) : UserBase {}