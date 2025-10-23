package com.lifeleveling.app.ui.screens

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.unit.IntRect
import androidx.lifecycle.ViewModel

/*
* This is just a singleton test user for us to hardcode values into for UI testing
* Later this can be used to build the logic needed for each user account
* as well as the local save of user logic.
*/

class TestUser : ViewModel() {
    var name by mutableStateOf("John Doe")
    var level by mutableStateOf(3)
    var currentExp by mutableStateOf(250)
    var expToLevel by mutableStateOf(300)
    var coins by mutableStateOf(301)
    var currentHealth by mutableStateOf(34)
    var maxHealth by mutableStateOf(85)

    // All reminders
    private var _reminders = mutableStateListOf<Reminder>()
    val reminders: List<Reminder> get() = _reminders

    // Weekly vs Monthly
    val weeklyStreaks: SnapshotStateList<Reminder> = mutableStateListOf()
    val monthlyStreaks: SnapshotStateList<Reminder> = mutableStateListOf()

    // Available ID for reminder
    private var nextId = 1

    init {
        Reminder(1, "Drink Water", true, 3, 0)
        Reminder(2, "Laundry", false, 0, 4)
        Reminder(3, "Shower", false, 0, 15)
        Reminder(4, "Read", false, 0, 8)
        Reminder(5, "Run", false, 0, 4)
        Reminder(6, "Make my bed", true, 1, 0)
        Reminder(7, "Brush teeth", true, 2, 0)
        Reminder(8, "Wash Hair", false, 0, 9)
        Reminder(9, "Feed the dog", true, 2, 0)
        Reminder(10, "Take Medication", true, 2, 0)
    }

    // Add a reminder
    fun addReminder(name: String, daily: Boolean, timesPerDay: Int, timesPerMonth: Int) {
        val newReminder = Reminder(nextId++, name, daily, timesPerDay, timesPerMonth)
        _reminders.add(newReminder)
        categorizeReminder(newReminder)
    }

    // Delete reminder by ID
    fun deleteReminder(id: Int) {
        val toRemove = _reminders.find { it.id == id } ?: return
        _reminders.remove(toRemove)
        weeklyStreaks.remove(toRemove)
        monthlyStreaks.remove(toRemove)
    }

    // Put new reminders in weekly or monthly
    private fun categorizeReminder(reminder: Reminder) {
        if (reminder.daily) weeklyStreaks.add(reminder)
        else monthlyStreaks.add(reminder)
    }
}

data class Reminder (
    val id: Int,
    val name: String,
    val daily: Boolean,
    val timesPerDay: Int,
    val timesPerMonth: Int,
)