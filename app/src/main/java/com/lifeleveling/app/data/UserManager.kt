package com.lifeleveling.app.data

import androidx.lifecycle.ViewModel
import com.lifeleveling.app.auth.AuthViewModel
import com.lifeleveling.app.util.GlobalConst
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel to use with the User object, to handle reading/writing to firebase,
 * calculating derived values, and the overall state of the user that is logged in.
 * @param user - the user to be entered.
 * @param authViewModel - a ViewModel for authorization. Capable of running coroutines
 * @param firestoreRepository A repository full of suspend functions that must be run inside of a coroutine context.
 */
class UserManager(val user: UserDoc = UserDoc(),
                  val authViewModel: AuthViewModel = AuthViewModel(),
                  val firestoreRepository: FirestoreRepository = FirestoreRepository())
    : ViewModel() {
    private val mutableUserState = MutableStateFlow<UserState>(UserState(user = user,))
    private val userState: StateFlow<UserState> = mutableUserState.asStateFlow()

    init {

    }

    fun getUserState(): UserState {
        return mutableUserState.value
    }

    fun getUserObject() : UserDoc {
        return user
    }

    fun initializeUserState() {
        mutableUserState.value = UserState(user = user,)
    }

    private fun updateLocalVariables(user: UserDoc) {
        mutableUserState.update { currentState ->
            currentState.copy(
                user = user,
                xpToNextLevel = calcLevelExp(user.level),
                maxHealth = calcMaxHealth(user.stats.health),
                baseHealth = GlobalConst.BASE_HEALTH,
                lifePointsNotUsed = calcNotUsedLifePoints(user.lifePointsTotal, user.lifePointsUsed),
//                enabledReminders = calcEnabledReminders(user.)
            )

        }
    }


    private fun calcLevelExp(level: Long): Long = (100L * level)
    private fun calcMaxHealth(healthStat: Long): Long = 60 + ( 5 * healthStat)
    private fun calcNotUsedLifePoints(total: Long, used: Long) : Long = total - used
    private fun calcEnabledReminders(reminders: List<Reminders>) : List<Reminders> = reminders.filter { it.enabled }
    private fun calcTotalStreaks(dailyStreaks: Long, monthlyStreaks: Long) = dailyStreaks + monthlyStreaks
    private fun calcBadgesEarned(badges: List<Badge>): Long = (badges.filter { it.completed }).size.toLong()
    private fun calcAllExp(currentExp: Long, level: Int): Long {
        val exp = 100L * level * (level + 1) / 2
        return currentExp + exp
    }
    private fun calcCoinsSpent(current: Long, total: Long) : Long = total - current
    private fun calcMostCompletedReminder(reminders: List<Reminders>): Pair<String, Long> {
        val highest = reminders.maxByOrNull { it.completedTally }
        var result = Pair("", 0L)
        if (highest != null) {
            result = Pair(highest.name, highest.completedTally)
        }
        return result
    }



}