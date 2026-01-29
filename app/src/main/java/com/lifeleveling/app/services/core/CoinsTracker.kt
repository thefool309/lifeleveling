package com.lifeleveling.app.services.core

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.lifeleveling.app.data.CoinsBalance
import com.lifeleveling.app.data.CoinsEvent
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.tasks.await


/**
 * # `CoinsBalanceException()`
 *
 * a simple Exception class made for the sake of having a default message, and having
 * a more specific Exception for more complex error catching when using the CoinsTracker in conjuction with other things
 */
class CoinsBalanceException(msg: String = "Coins balance not found!") : Exception(msg) {}
/**
 * # CoinsTracker
 * A class that will be placed into the users calculated data, and called inside the UserViewModel
 * This will not be saved into the database.
 * @see CoinsBalance
 * @sample CoinsViewModel
 * @author thefool309
 */
class CoinsTracker(
    var coinsBalance: CoinsBalance,
) {

    private val _coinsEvents = MutableSharedFlow<CoinsEvent>()
    val rewardEvents = _coinsEvents.asSharedFlow()

    companion object {
        val TAG: String = CoinsTracker::class.java.simpleName
    }

    /**
     * # `addCoins()`
     *
     * adds coins to the `coinsBalance.currCoins` and `coinsBalance.lifeTimeCoins` properties
     * @param coins the number of coins to add to the coinsBalance properties
     * @sample CoinsViewModel.handleCoinsEvent
     * @author thefool309
     */
    fun addCoins(coins: Long): Long {
        coinsBalance.currCoins += coins
        coinsBalance.lifetimeCoins += coins
        return coinsBalance.currCoins
    }
    /**
     * # `subtractCoins()`
     *
     * subtracts coins from the `coinsBalance.currCoins` property
     * @param coins the number of coins to add to the `coinsBalance.currCoins` property
     * @sample CoinsViewModel.handleCoinsEvent
     * @author thefool309
     */
    fun subtractCoins(coins: Long): Long {
        coinsBalance.currCoins -= coins
        return coinsBalance.currCoins
    }

    /**
     * # getCoins()
     * return the `coinsBalance.currCoins` property
     * @author thefool309
     */
    fun getCoins() : Long {
        return coinsBalance.currCoins
    }

    /**
     * # getLifetimeCoins()
     * return the `coinsBalance.lifetimeCoins` property.
     * @author thefool309
     */
    fun getLifetimeCoins(): Long {
        return coinsBalance.lifetimeCoins
    }
    /**
     * # `startCoinsEvent()`
     * A function for handling the reward timer. It waits for a defined number of seconds,
     * updates the coin balance, returns a RewardEvent and emits a RewardEvent for UI components
     * can be set to 0 seconds if instant.
     * @see CoinsViewModel
     * @sample CoinsViewModel.handleCoinsEvent
     * @param delay the number of seconds between the events defaults to 60
     * @param coins the number of coins to be awarded defaults to 10
     * @param source a TAG intended to identify where the event was spawned from
     * @param isReward defines whether it is a reward(add) or a purchase(subtract)
     * @param message the message for the CoinsEvent to be passed to the UI or logger
     * @author thefool309
     */
    suspend fun startCoinsEvent(delay: Long = 60L, coins: Long = 10L, source: String = "", isReward: Boolean = true, message: String = "You got coins!") : CoinsEvent {
        delay(delay * 1000L)
        val coinsEvent = CoinsEvent(coins,source, isReward, message)
        _coinsEvents.emit(coinsEvent)
        return CoinsEvent(coins, "rewardEvent")
    }

    /**
     * # `saveCoinsBalance()`
     * a function for saving the coins balance to firebase. I chose to rewrite this here, as I felt like this was a more appropriate place for it to live.
     * used to update the nested database table "coins" represented by the data class CoinsBalance
     * @see CoinsViewModel
     * @sample CoinsViewModel.serializeCoins
     * @param userId the userId of the currently logged in user, so we update the correct record
     * @param logger an interface for modifying the behavior of the logger
     * @author thefool309
     */
    suspend fun saveCoinsBalance(userId: String, logger: ILogger = AndroidLogger()) {
        val docRef = FirebaseFirestore.getInstance().collection("coins").document(userId)
        val data = coinsBalance
        try {
            docRef.set(data, SetOptions.merge()).await()
        } catch (exception: Exception) {
            logger.e(TAG, "saveCoins failed: ",exception)
        }
    }
    /**
     * # `retrieveCoinsBalance()`
     * a function for retrieving the coins balance from firestore. returns this data as a `CoinsBalance` object
     * @sample CoinsViewModel.loadFirestoreCoinsBalance
     * @param userId a string representing the current authenticated user
     * @param logger an interface for modifying the behavior of the logger. defaults to an AndroidLogger
     * @author thefool309
     * @return CoinsBalance?
     */
    suspend fun retrieveCoinsBalance(userId: String, logger: ILogger = AndroidLogger()) : CoinsBalance? {
        val docRef = FirebaseFirestore.getInstance().collection("coins").document(userId)
        try {
            val snap = docRef.get().await()
            coinsBalance = snap.toObject(CoinsBalance::class.java)!!
            return coinsBalance
        }
        catch (exception: Exception) {
            logger.e(TAG, "retrieveCoins failed: ",exception)
            return null
        }
    }
}
