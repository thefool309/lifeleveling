package com.lifeleveling.app.services.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifeleveling.app.data.CoinBalance
import com.lifeleveling.app.data.RewardEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * # TimerViewModel
 * A ViewModel I created for the sake of an example of the code I'm adding into services. This is an example of how to use the CoinsTracker class
 * 
 * @see CoinsTracker
 * @param coinBalance the CoinBalance instance that is associated with the current user.
 */
class TimerViewModel(val coinBalance: CoinBalance) : ViewModel() {
    val coinsTracker: CoinsTracker = CoinsTracker(coinBalance)


    fun awardCoins(seconds: Long = 60L, reward: Long = 10L) {
        viewModelScope.launch(Dispatchers.IO) {
            val rewardEvent: RewardEvent = coinsTracker.startRewardTimer(seconds, reward)
            coinsTracker.addCoins(rewardEvent.coinsEarned)
        }
    }
}