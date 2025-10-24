package com.lifeleveling.app.ui.screens

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

/*
* This is just a singleton test user for us to hardcode values into for UI testing
* Later this can be used to build the logic needed for each user account
* as well as the local save of user logic.
*/

object TestUser {
    var name by mutableStateOf("John Doe")
    var level by mutableStateOf(3)
    var currentExp by mutableStateOf(250)
    var expToLevel by mutableStateOf(300)
    var coins by mutableStateOf(301)
    var currentHealth by mutableStateOf(34)
    var maxHealth by mutableStateOf(85)
    var StatStrength by mutableStateOf(3)
    var StatDefense by mutableStateOf(2)
    var StatIntelligence by mutableStateOf(3)
    var StatAgility by mutableStateOf(3)
    var StatHealth by mutableStateOf(3)
    var LifePointsUsed by mutableStateOf(6)
    var UnusedLifePoints by mutableStateOf(12)
}