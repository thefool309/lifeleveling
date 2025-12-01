package com.lifeleveling.app.data

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.lifeleveling.app.util.GlobalConst

data class UserDoc(
    override val userId: String = "",
    override val displayName: String = "",
    override val email: String = "",
    override val photoUrl: String = "",
    override val coinsBalance: Long = 0,
    override val coinsSpent: Long = 0,
    // Update from inline map to now use Stats data class
    override val stats: Stats = Stats(),
    override val streaks: List<Streak> = emptyList(),
    override val onboardingComplete: Boolean = false,

    override val createdAt: Timestamp? = null,
    // Automatically populates the timestamp when the document is created/updated
    @get:ServerTimestamp
    @set:ServerTimestamp
    override var lastUpdate: Timestamp? = null,
    // variables that were missing during our first introduction of the Users collection


    // set different names in the database for properties with the below macros
    //@set:PropertyName("level")
    //@get:PropertyName("level")
    override var level: Long = 1,
    override val lifePointsUsed: Long = 0,           // unused lifePoints
    override val lifePointsTotal: Long = 0,
    override val currentXp: Long = 0L,        // Current Experience // Experience needed to level up
    override val currHealth: Long = 0,
    // Badges can be stored in arrays of Badge objects on user doc.
    override val badgesLocked: List<Badge> = emptyList(),       // greyed out badges/ secret badges
    override val badgesUnlocked: List<Badge> = emptyList(),     // completed badges
    override val allReminders: List<Reminders> = emptyList(),
    ) : UserBase {

}


data class UserState(
    val user: UserDoc,
    var xpToNextLevel: Long = 0L,
    var maxHealth: Long = 0L,
    val baseHealth: Long = GlobalConst.BASE_HEALTH,
    val lifePointsNotUsed: Long = 0,
    val enabledReminders: List<Reminders> = listOf(),

    val totalStreaksCompleted: Long = 0,
    val badgesEarned: Long = 0,
    val allExpEver: Long = 0,
    val coinsSpend: Long = 0,
    val mostCompletedRemind: Pair<String, Long> = Pair("", 0L),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val isLoggedIn: Boolean = false,
    ) : ViewModel() {

    init { }


}


// Nested Models
// A user's "reminder template" (the base CRUD)
data class Reminders(
    val reminderId: String = "",         // Firestore doc id (also stored in doc for convenience)
    val title: String = "",
    val notes: String = "",
    val dueAt: Timestamp? = null,        // when the reminder should trigger (nullable)
    val isCompleted: Boolean = false,
    val completedAt: Timestamp? = null,  // set when marked complete
    val createdAt: Timestamp? = null,    // serverTimestamp on create
    val lastUpdate: Timestamp? = null,    // serverTimestamp on any write
    val isDaily: Boolean = true,         // daily = weekly streaks source, false = monthly streak source
    val timesPerHour: Int = 0,          // How many hour(s)
    val timesPerDay: Int = 0,           // How many day(s)
    val timesPerMonth: Int = 0,         // How many month(s)
    val colorToken: String? = null,      // nullable like enumColor? in TestUser
    val iconName: String = "",              // store icon key (ex: "water_drop"), not R.drawable.id
    val name: String = "",
    val enabled: Boolean = false,
    val completedTally: Long = 0L, // store icon key (ex: "water_drop"), not R.drawable.id
    val repeatForever: Boolean = false,  // true if "Repeats indefinitely" checked
    val repeatCount: Int = 0,            // how many units
    val repeatInterval: String? = null,  // days, weeks, months or years
)

// Player stat block (Stats Screen)
data class Stats (
    val agility: Long = 0,
    val defense: Long = 0,
    val intelligence: Long = 0,
    val strength: Long = 0,
    val health: Long = 0,
)

// Badge the user can earn
data class Badge(
    val badgeId: String = "",
    val badgeName: String = "",
    val badgeDescription: String = "",
    val iconName: String = "",           // Stores the name, not the R.drawable
    val colorToken: String = "",
    val completed: Boolean = false,
    val unlockedAt: Timestamp? = null,   // When badge was earned
)

// One active streak the user is tracking
/* Figma concept:
   - Add a Week or Add a Month Streak
   - Choose an existing reminder
   - Track how many times they've completed it */
data class Streak(
    val streakId: String = "",                  // doc id inside streaks subcollection
    val reminderId: String = "",                // link to Reminders.reminderId
    val periodType: String = "weekly",          // "weekly" or "monthly"
    val totalRequired: Long = 0,                // totalAmount in TestUser.kt
    val numberCompleted: Long = 0,              // numberCompleted in TestUser.kt
    val repeatIndefinitely: Boolean = false,
    val repeatEveryAmount: Long? = null,        // future: "every 2", "every 3", etc
    val repeatEveryUnit: String? = null,        // "days", "weeks", "months", "years"
    val createdAt: Timestamp? = null,
    val lastUpdate: Timestamp? = null,
)

data class FcmTokens(
    val uID: String = "",
    val token: String = "",
    val lastUpdate: Timestamp? = null
)