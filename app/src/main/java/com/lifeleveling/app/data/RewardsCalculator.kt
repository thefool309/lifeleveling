package com.lifeleveling.app.data

import kotlin.math.roundToLong

object RewardsCalculator {

    private const val BASE_EXP = 15.0
    private const val BASE_COINS = 10.0

    fun calcExpForReminderCompletion(user: Users): Double {
        val s = user.stats

        // Strength 5% exp, Defense 3% exp, Agility 2% exp
        val bonusMultiplier =
            (s.strength * 0.05) +
                    (s.defense * 0.03) +
                    (s.agility * 0.02)

        return BASE_EXP + (BASE_EXP * bonusMultiplier)
    }

    fun calcCoinsForReminderCompletion(user: Users): Long {
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
     * Optional "level up bonus coins"
     * coins = (newLevel * 10) + coinsForCompletion
     */
    fun levelUpBonusCoins(newLevel: Long, completionCoins: Long): Long {
        return (newLevel * 10L) + completionCoins
    }
}
