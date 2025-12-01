package com.lifeleveling.app.data

import androidx.lifecycle.ViewModel
import com.lifeleveling.app.auth.AuthViewModel
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.GlobalConst
import com.lifeleveling.app.util.ILogger
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

    val logger: ILogger = AndroidLogger()
    init {

    }

    /**
     * constructor can take logger in case you wanted to override the loggers behavior
     * @param logger a logger that's behavior can be modified by deriving a class from the interface.
     * @param user the UserDoc object to be turned into a UserState
     * @param authViewModel A view model for running suspend functions
     *                   related to firebase Auth
     * @param firestoreRepository A Repository of suspend functions for accessing the firestore database.
     *
     */
    constructor(logger: ILogger,
                user: UserDoc = UserDoc(),
                authViewModel: AuthViewModel = AuthViewModel(),
                firestoreRepository: FirestoreRepository = FirestoreRepository()
    ) : this(user, authViewModel, firestoreRepository) {

    }

    /**
     * constructor can take UserState object, in case you wanted to instantiate from a UserState instead
     * @param userState an object representing the users state in the View Model
     * @param user the UserDoc object to be turned into a UserState
     * @param authViewModel A view model for running suspend functions
     *                      related to firebase Auth
     * @param firestoreRepository A Repository of suspend functions for accessing the firestore database.
     *
     */
    constructor(userState: UserState,
                userDoc: UserDoc = UserDoc(),
                authViewModel: AuthViewModel = AuthViewModel(),
                firestoreRepository: FirestoreRepository = FirestoreRepository()) : this(userDoc, authViewModel, firestoreRepository) {
                    this.mutableUserState.update {
                        it.copy(user = userDoc,
                                xpToNextLevel = userState.xpToNextLevel,
                                maxHealth = userState.maxHealth,
                                baseHealth = userState.baseHealth,
                                lifePointsNotUsed = userState.lifePointsNotUsed,
                                enabledReminders = userState.enabledReminders,

                                totalStreaksCompleted = userState.totalStreaksCompleted,
                                badgesEarned = userState.badgesEarned,
                                allExpEver = userState.allExpEver,
                                coinsSpend = userState.coinsSpend,
                                mostCompletedRemind = userState.mostCompletedRemind,
                                isLoading = userState.isLoading,
                                errorMessage = userState.errorMessage,
                                isLoggedIn = userState.isLoggedIn,
                            )

                    }

                }

    /**
     * function returns `UserState` object from user manager
     * @return UserState
     */
    fun getUserState(): UserState {
        return userState.value
    }

    /**
     * returns `UserDoc` object from user manager
     * @return UserDoc
     */
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
                enabledReminders = calcEnabledReminders(user.allReminders) ,
                totalStreaksCompleted = user.streaks.count().toLong(),
                badgesEarned = user.badgesUnlocked.count().toLong(),
                allExpEver = calcAllExp(user.currentXp, user.level)


            )

        }
    }


    private fun calcLevelExp(level: Long): Long = (100L * level)
    private fun calcMaxHealth(healthStat: Long): Long = 60 + ( 5 * healthStat)
    private fun calcNotUsedLifePoints(total: Long, used: Long) : Long = total - used
    private fun calcEnabledReminders(reminders: List<Reminders>) : List<Reminders> = reminders.filter { it.enabled }

    //    private fun calcTotalStreaks(dailyStreaks: Long, monthlyStreaks: Long) = dailyStreaks + monthlyStreaks // not needed in current implementation
    //    private fun calcBadgesEarned(badges: List<Badge>): Long = (badges.filter { it.completed }).size.toLong()
    private fun calcAllExp(currentXp: Long, level: Long): Long {
        val exp = 100L * level * (level + 1) / 2
        return currentXp + exp
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