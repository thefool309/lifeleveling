package com.lifeleveling.app.services.core

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.lifeleveling.app.data.CoinsBalance
import com.lifeleveling.app.data.RewardEvent
import com.lifeleveling.app.util.AndroidLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.tasks.await

/**
 * # CoinsTracker
 * A class that will be placed into the users calculated data, and called inside the UserViewModel
 * This will not be saved into the database.
 * @see CoinsBalance
 * @sample TimerViewModel
 */

class CoinsTracker(
    val coinsBalance: CoinsBalance,
) {

    private val _rewardEvents = MutableSharedFlow<RewardEvent>()
    val rewardEvents = _rewardEvents.asSharedFlow()

    companion object {
        val TAG: String = CoinsTracker::class.java.simpleName
    }

    fun addCoins(coins: Long): Long {
        coinsBalance.currCoins += coins
        coinsBalance.lifeTimeCoins += coins
        return coinsBalance.currCoins
    }

    fun subtractCoins(coins: Long): Long {
        coinsBalance.currCoins -= coins
        return coinsBalance.currCoins
    }

    fun getCoins() : Long {
        return coinsBalance.currCoins
    }

    fun getLifeTimeCoins() : Long {
        return coinsBalance.lifeTimeCoins
    }
    /**
     * # startRewardTimer
     * A function for handling the reward timer. It waits 60 seconds, updates the coin balance, returns a RewardEvent and emits a RewardEvent for UI components
     * @see TimerViewModel
     * @sample TimerViewModel.awardCoins
     * @param seconds the number of seconds between the events defaults to 60
     * @param reward the number of coins to be awarded defaults to 10
     */
    suspend fun startRewardTimer(seconds: Long = 60L, reward: Long = 10L) : RewardEvent {
        delay(seconds * 1000L)
        val rewardEvent = RewardEvent(reward,"rewardEvent")
        _rewardEvents.emit(rewardEvent)
        return RewardEvent(reward, "rewardEvent")
    }

    /**
     * # saveCoinsBalance
     * a function for saving the coins balance to firebase. I chose to rewrite this here, as I felt like this was a more appropriate place for it to live.
     */
    suspend fun saveCoinsBalance(userId: String, logger: AndroidLogger = AndroidLogger()) {
        val docRef = FirebaseFirestore.getInstance().collection("coins").document(userId)
        val data = coinsBalance

        try {
            docRef.set(data, SetOptions.merge()).await()
        } catch (exception: Exception) {
            logger.e(TAG, "saveCoins failed: ",exception)
        }
    }
}
