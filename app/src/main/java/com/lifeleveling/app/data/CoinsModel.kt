package com.lifeleveling.app.data

data class CoinBalance(
    val id: Int,
    val userId: Int,
    val currCoins: Long,
    val lifeTimeCoins: Long,
) {

}