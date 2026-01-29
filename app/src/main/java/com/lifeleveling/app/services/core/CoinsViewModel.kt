package com.lifeleveling.app.services.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.lifeleveling.app.data.CoinsBalance
import com.lifeleveling.app.data.CoinsEvent
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * # CoinsViewModel
 * A ViewModel I created for the sake of an example of the code I'm adding into services. This is an example of how to use the CoinsTracker class
 * 
 * @see CoinsTracker
 * @param coinsBalance the CoinBalance instance that is associated with the current user.
 * @author thefool309
 */


class CoinsViewModel(val coinsBalance: CoinsBalance, val logger: ILogger = AndroidLogger()) : ViewModel() {
    val coinsTracker: CoinsTracker = CoinsTracker(coinsBalance)
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
         val TAG: String = this::class.java.simpleName
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // I did this with a try catch statement but one could easily do this with a boolean flag of "newUser" or something of the like
            try { coinsTracker.retrieveCoinsBalance(auth.currentUser!!.uid) }
            catch(e: Exception) {
                logger.d(TAG, "The users coins balance was not retrieved! Must be a new user...")
                coinsTracker.coinsBalance = CoinsBalance(auth.currentUser!!.uid, 0, 0)
                coinsTracker.saveCoinsBalance(auth.currentUser!!.uid)
            }
        }
    }
    /**
     * # `handleCoinsEvent`
     * an example of how to handle a `CoinsEvent` in a view model
     *
     * award or subtract coins from the user and update the tracker.
     *
     * can be placed on a timer or set to 0 if instant.
     *
     * @see CoinsTracker.addCoins
     * @see CoinsTracker.subtractCoins
     * @see CoinsTracker.startCoinsEvent
     * @param delay the number of seconds to delay this coroutine before the award is awarded
     * @param reward the number of coins to be awarded
     * @param source a TAG intended to identify where the event was spawned from
     * @param isReward defines whether it is a reward(add) or a purchase(subtract)
     * @param message the message for the CoinsEvent to be passed to the UI or logger
     * @author thefool309
     */
    fun handleCoinsEvent(delay: Long = 60L, reward: Long = 10L, source: String = "", isReward: Boolean = true, message: String = "You got coins!") {
        viewModelScope.launch(Dispatchers.IO) {
            val coinsEvent: CoinsEvent = coinsTracker.startCoinsEvent(delay, reward, source, isReward, message)
            if(coinsEvent.isReward) {
                coinsTracker.addCoins(coinsEvent.coins)
            }
            else {
                coinsTracker.subtractCoins(coinsEvent.coins)
            }
        }
    }

    /**
     * an example of using the coinsTracker's `retrieveCoinsBalance()` function
     *
     * the retrieveCoinsBalance function will load the coinsBalance into the coinsTracker and return null on failure. throwing a CoinsBalanceException in this case.
     * @see CoinsTracker.retrieveCoinsBalance
     * @author thefool309
     */
    fun loadFirestoreCoinsBalance() {
        viewModelScope.launch(Dispatchers.IO) {
            coinsTracker.retrieveCoinsBalance(auth.currentUser!!.uid) ?: throw CoinsBalanceException()
        }
    }

    /**
     * # `reduceBalance()`
     * An example of how to use the CoinsEvent and CoinTracker to carry out a specific operation like subtracting coins from the users balance
     * @see CoinsTracker.subtractCoins
     * @see CoinsTracker.startCoinsEvent
     * @param delay the number of seconds to delay this coroutine before the award is awarded
     * @param amount the number of coins to be subtracted
     * @author thefool309
     */
    fun reduceBalance(delay:Long = 0L, amount: Long = 1L, source: String = "Purchase") {
        viewModelScope.launch(Dispatchers.IO) {
            val coinsEvent: CoinsEvent = coinsTracker.startCoinsEvent(delay, amount, source, isReward = false, "You just spent some coins! at $source")
            coinsTracker.subtractCoins(coinsEvent.coins)
        }
    }
    /**
     * # `serializeCoins()`
     * for controlling when we serialize a CoinsBalance model to the database.
     * @see CoinsTracker.saveCoinsBalance
     * @author thefool309
     */
    fun serializeCoins() {
        viewModelScope.launch(Dispatchers.IO) {
            coinsTracker.saveCoinsBalance(auth.currentUser!!.uid)
        }
    }
}