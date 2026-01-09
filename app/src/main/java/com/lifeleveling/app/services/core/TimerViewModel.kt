package com.lifeleveling.app.services.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.lifeleveling.app.data.CoinsBalance
import com.lifeleveling.app.data.CoinsEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * # TimerViewModel
 * A ViewModel I created for the sake of an example of the code I'm adding into services. This is an example of how to use the CoinsTracker class
 * 
 * @see CoinsTracker
 * @param coinsBalance the CoinBalance instance that is associated with the current user.
 */
class TimerViewModel(val coinsBalance: CoinsBalance) : ViewModel() {
    val coinsTracker: CoinsTracker = CoinsTracker(coinsBalance)
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * # `awardCoins()`
     * award coins to the user and update the tracker.
     *
     * can be placed on a timer or set to 0 if instant.
     *
     * @see CoinsTracker.addCoins
     * @see CoinsTracker.startCoinEvent
     * @param seconds the number of seconds to delay this coroutine before the award is awarded
     * @param reward the number of coins to be awarded
     */
    fun awardCoins(seconds: Long = 60L, reward: Long = 10L) {
        viewModelScope.launch(Dispatchers.IO) {
            val coinsEvent: CoinsEvent = coinsTracker.startCoinEvent(seconds, reward)
            coinsTracker.addCoins(coinsEvent.coins)
        }
    }

    fun reduceBalance(amount: Long = 1L) {
        viewModelScope.launch(Dispatchers.IO) {
            val coinsEvent: CoinsEvent = coinsTracker.startCoinEvent(0L, amount)
            coinsTracker.subtractCoins(coinsEvent.coins)
        }
    }
    /**
     * # `serializeCoins()`
     * for controlling when we serialize a CoinsBalance model to the database.
     * @see CoinsTracker.saveCoinsBalance
     */
    fun serializeCoins() {
        viewModelScope.launch(Dispatchers.IO) {
            coinsTracker.saveCoinsBalance(auth.currentUser!!.uid)
        }
    }
}