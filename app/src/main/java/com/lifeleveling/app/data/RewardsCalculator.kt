package com.lifeleveling.app.data

import kotlin.math.roundToLong

/**
 * Centralized reward math for the Life Leveling game system.
 *
 * This object contains all calculations related to:
 * - XP gained from completing a reminder
 * - Coins earned from reminder completion
 * - Bonus coins awarded when leveling up
 *
 * IMPORTANT:
 * - If progression ever feels too slow or too easy, adjust the BASE values or stat multipliers here instead of hunting through the app.
 * @author fdesouza1992
 */
object RewardsCalculator {

    private const val BASE_EXP = 15.0
    private const val BASE_COINS = 10.0

    /**
     * Calculates how much EXP a user earns when completing a reminder.
     *
     * The formula starts with a base EXP value and applies bonus scaling based on the user's current stats:
     * - Strength → +5% EXP per point
     * - Defense  → +3% EXP per point
     * - Agility  → +2% EXP per point
     *
     * Example:
     * If BASE_EXP = 15 and the combined stat bonus multiplier is 0.20, the final EXP awarded will be: 15 + (15 * 0.20) = 18 EXP
     *
     * @param user The current user whose stats are used for the calculation.
     * @return Final EXP value to award for this reminder completion.
     * @author fdesouza1992
     */
    fun calcExpForReminderCompletion(user: UsersBase): Double {
        val s = user.stats

        // Strength 5% exp, Defense 3% exp, Agility 2% exp
        val bonusMultiplier =
            (s.strength * 0.05) +
                    (s.defense * 0.03) +
                    (s.agility * 0.02)

        return BASE_EXP + (BASE_EXP * bonusMultiplier)
    }

    /**
     * Calculates how many coins a user earns when completing a reminder.
     *
     * The calculation uses a base coin value and applies bonus scaling from the user's stats:
     * - Defense       → +2% coins per point
     * - Intelligence  → +5% coins per point
     * - Agility       → +3% coins per point
     *
     * The final value is rounded instead of truncated so rewards feel fair and predictable to the player.
     *
     * @param user The current user whose stats are used for the calculation.
     * @return Final coin amount to award for this reminder completion.
     * @author fdesouza1992
     */
    fun calcCoinsForReminderCompletion(user: UsersBase): Long {
        val s = user.stats

        // Defense 2% coins, Intelligence 5% coins, Agility 3% coins
        val bonusMultiplier =
            (s.defense * 0.02) +
                    (s.intelligence * 0.05) +
                    (s.agility * 0.03)

        val coins = BASE_COINS + (BASE_COINS * bonusMultiplier)

        // rounding feels better than truncating
        return coins.roundToLong()
    }

    /**
     * Calculates bonus coins awarded when the user levels up.
     *
     * Formula: bonusCoins = (newLevel * 10) + completionCoins
     *
     * This stacks a small level-based bonus on top of the normal completion reward so leveling up always feels meaningful and noticeable to the player.
     * This method is intentionally simple for now and can later be expanded to include multipliers, perks, achievements, or difficulty scaling.
     *
     * @param newLevel The user's new level after leveling up.
     * @param completionCoins The coins earned from the triggering completion.
     * @return Total bonus coins to award.
     * @author fdesouza1992
     */
    fun levelUpBonusCoins(newLevel: Long, completionCoins: Long): Long {
        return (newLevel * 10L) + completionCoins
    }
}
