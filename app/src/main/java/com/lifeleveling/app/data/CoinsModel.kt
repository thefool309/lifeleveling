package com.lifeleveling.app.data
import com.lifeleveling.app.services.core.CoinsTracker
import com.lifeleveling.app.services.core.TimerViewModel
/**
 * # `CoinsBalance`
 * A data class that will be nested inside the user data and used for keeping track of the user balance
 *
 * This will be saved to the database
 * @see CoinsTracker
 * @see TimerViewModel
 * @author thefool309
 * @param userId the ID of the user this balance is related with.
 * @param currCoins the number of coins currently in the Users balance
 * @param lifetimeCoins the number of coins earned over the lifetime of the account
 */
data class CoinsBalance(
    val userId: Int,
    var currCoins: Long,
    var lifetimeCoins: Long,
) {}

/**
 * # `CoinsEvent`
 * the data associated with a specific coins event. For use with communicating with the UI, and other listening parts of the application.
 *
 * Can be a reward or a subtraction (purchase)
 * @see CoinsTracker
 * @author thefool309
 * @param coins the number of coins to be awarded or removed
 * @param source a string to represent where the award or purchase came from
 * @param isReward a boolean to represent whether to add or subtract the coins
 * @param message a message for any UI events. in prod it should be passed through a string resource
 */
data class CoinsEvent(
    val coins: Long,
    val source: String,  // a name for where it came from
    val isReward: Boolean = true,
    val message: String = "You earned coins!",
)