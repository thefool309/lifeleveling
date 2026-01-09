package com.lifeleveling.app.data
import com.lifeleveling.app.services.core.CoinsTracker
import com.lifeleveling.app.services.core.TimerViewModel
/**
 * # CoinsBalance
 * A data class that will be nested inside the user data and used for keeping track of the user balance
 *
 * This will be saved to the database
 * @see CoinsTracker
 * @see TimerViewModel
 * @author thefool309
 */
data class CoinsBalance(
    val userId: Int,
    var currCoins: Long,
    var lifeTimeCoins: Long,
) {}

/**
 * # CoinsEvent
 * the data associated with a specific coins event. For use with communicating with the UI, and other listening parts of the application.
 *
 * Can be a reward or a subtraction (purchase)
 * @see CoinsTracker
 * @author thefool309
 */
data class CoinsEvent(
    val coinsEarned: Long,
    val source: String,  // a name for where it came from
    val isReward: Boolean = true,
    val message: String = "You earned coins!",
)