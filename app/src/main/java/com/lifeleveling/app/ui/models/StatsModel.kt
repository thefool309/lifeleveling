package com.lifeleveling.app.ui.models

// TODO: Needs To use a Long Data type instead of int.
//  firebase and the data classes are setup to use 64 bit signed integers
//  A.K.A Long this being used in the ui could result in loss of data
//  https://firebase.google.com/docs/firestore/manage-data/data-types
data class StatsUi(

    val level: Int,
    val currentXp: Int,
    val xpToNextLevel: Int,
    val usedLifePoints: Int,                // already-allocated points (sum of stats)
    val unusedLifePoints: Int,              // total pool shown on the right of "used/total"
    val strength: Int,
    val defense: Int,
    val intelligence: Int,
    val agility: Int,
    val health: Int
)

data class EditedStats(
    val strength: Int,
    val defense: Int,
    val intelligence: Int,
    val agility: Int,
    val health: Int,
    val usedPoints: Int,
    val remainingPoints: Int                 // this should be the new lifePoints to persist
)