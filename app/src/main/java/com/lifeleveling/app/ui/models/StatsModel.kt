package com.lifeleveling.app.ui.models

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