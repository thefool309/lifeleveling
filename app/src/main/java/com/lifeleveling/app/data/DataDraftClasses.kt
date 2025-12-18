package com.lifeleveling.app.data

/**
 * A small data class for the UI to fill out and pass to create a Streak object.
 * @author Elyseia
 */
data class StreakDraft(
    val reminderId: String,
    val weekly: Boolean,
    val repeat: Boolean,
)