package com.lifeleveling.app.data

/**
 * A data class that will be nested inside the user data and used for keeping track of the user balance
 */
data class CoinBalance(
    val userId: Int,
    var currCoins: Long,
    var lifeTimeCoins: Long,
) {}

data class RewardEvent(
    val coinsEarned: Long,
    val source: String,  // a name for where it came from
    val isBonus: Boolean = false,
    val message: String = "You earned coins!",
)