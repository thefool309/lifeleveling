package com.lifeleveling.app.data

import android.content.Intent
import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.lifeleveling.app.auth.AuthViewModel
import com.lifeleveling.app.ui.components.TestUser.profileCreatedDate
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.Int

/**
 * Information for the user that WILL be written into firebase
 */
data class UserData(
    val username: String = "",
    val email: String = "",

    val level: Int = 1,
    val currentExp: Long = 0,
    val coins: Long = 0,
    val currentHealth: Int = 60,
    val lifePointsUsed: Long = 0,
    val lifePointsTotal: Long = 3,
    val profileCreatedOn: Long = 0,
    val lastUpdate: Long = 0,
    val fightOrMeditate: Int = 0,

    val stats: Stats = Stats(),
    val reminders: List<Reminder> = listOf(),
    val streaks: List<Streak> = listOf(),
    val badges: List<Badge> = listOf(),

    val weekStreaksCompleted: Long = 0,
    val monthStreaksCompleted: Long = 0,
    val allCoinsEarned: Long = 0,

    val isDarkTheme: Boolean = true,
)

/**
 * Information that will NOT be written in firebase
 */
data class UserCalculated(
    val userData: UserData? = null,
    val expToNextLevel: Long = 0,
    val maxHealth: Long = 60,
    val lifePointsNotUsed: Long = 0,

    val enabledReminders: List<Reminder> = listOf(),

    val totalStreaksCompleted: Long = 0,
    val badgesEarned: Long = 0,
    val allExpEver: Long = 0,
    val coinsSpend: Long = 0,
    val mostCompletedRemind: Pair<String, Long> = Pair("", 0L),

    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val isLoggedIn: Boolean = false,
)

/**
 * Manages the local state of the user, writing to firebase, pulling from firebase, and more
 */
class UserManager(
    private val authRepo: AuthViewModel = AuthViewModel(),
    private val userRepo: FirestoreRepository = FirestoreRepository(),
) : ViewModel() {
    private val userAllData = MutableStateFlow(UserCalculated())
    val uiState: StateFlow<UserCalculated> = userAllData.asStateFlow()

    var logger: ILogger = AndroidLogger()

    constructor(authRepo: AuthViewModel, userRepo: FirestoreRepository, logger: ILogger) : this(authRepo = authRepo, userRepo = userRepo) {
        this.logger = logger
    }

    // Initialization
    init {

    }


    // ================== Functions =======================================================

    // ================== Firestore managing functions ==========
    // Load user from firestore
    suspend fun loadUser() {
        val uid = authRepo.ui.value.user?.uid ?: return
        userAllData.update { it.copy(isLoading = true, errorMessage = null) }

        try {
            val data = userRepo.getUser(uid, logger)
            if (data != null) {
                updateLocalVariables(data)
                userAllData.update { it.copy(isLoading = false, isLoggedIn = true) }
            } else {
                createNewUser()
            }
        } catch (e: Exception) {
            userAllData.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
        }
    }

    // Write user into firestore
    suspend fun saveUser() {
        val user = userAllData.value.userData ?: return
        val uid = authRepo.ui.value.user?.uid ?: return

        try {
            userRepo.saveUser(uid, user)
        } catch (e: Exception) {
            userAllData.update { it.copy(errorMessage = e.localizedMessage) }
        }
    }
    val db = Firebase.firestore
    /**
     * Firestore is a NoSQL database, so it is best practice to save pieces of data individually instead of saving the whole user all at once.
     *
     * there is a lot of reasons why but the breakdown is essentially
     *
     * - Efficiency. Saving the whole user on pause can cause the app to hang when reopening, and the operation to write the whole user is extremely inneficient.
     *
     * - Concurrency and Data Integrity. Using "save whole user" operations can run the risk of overwriting changes made elsewhere.
     *
     * - The security rules are easier to manage when they focus on validating individual fields, rather than the entire shape of a large complex object during every write operation
     *
     * However firestore charges on the number of read and writes, whether it's 20 things written or 1.
     * There is probably a careful balance that can optimize not only the users experience but the number of reads and writes we are performing.
     *  @see FirestoreRepository
     */
    fun saveOnPause() {
        viewModelScope.launch { saveUser() }
    }

    // Create a new User
    suspend fun createNewUser() {
        val user = UserData()
        val uid = authRepo.currentUser?.uid ?: return

        updateLocalVariables(user)
        userAllData.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            userRepo.createNewUser(uid, user)
            userAllData.update { it.copy(isLoading = false, isLoggedIn = true) }
        } catch (e: Exception) {
            userAllData.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
        }
    }

    // ========= Calculating Local Logic Variable Functions ==========
    // Broad function for any smaller ones so they all get loaded at once
    // Used after reading from firebase
    private fun updateLocalVariables(userData: UserData) {
        userAllData.update { current ->
            current.copy(
                userData = userData,
                expToNextLevel = calcLevelExp(userData.level),
                maxHealth = calcMaxHealth(userData.stats.health),
                lifePointsNotUsed = calcNotUsedLifePoints(userData.lifePointsTotal, userData.lifePointsUsed),

                enabledReminders = calcEnabledReminders(userData.reminders),

                totalStreaksCompleted = calcTotalStreaks(userData.weekStreaksCompleted, userData.monthStreaksCompleted),
                badgesEarned = calcBadgesEarned(userData.badges),
                allExpEver = calcAllExp(userData.currentExp, userData.level),
                coinsSpend = calcCoinsSpent(userData.coins, userData.allCoinsEarned),
                mostCompletedRemind = calcMostCompletedReminder(userData.reminders),
            )
        }
    }

    private fun calcLevelExp(level: Int) = (100 * level).toLong()
    private fun calcMaxHealth(healthPoints: Long) = 60 + ( 5 * healthPoints)
    private fun calcNotUsedLifePoints(total: Long, used: Long) = total - used
    private fun calcEnabledReminders(reminders: List<Reminder>) = reminders.filter { it.enabled }
    private fun calcTotalStreaks(dailyStreaks: Long, monthlyStreaks: Long) = dailyStreaks + monthlyStreaks
    private fun calcBadgesEarned(badges: List<Badge>) = (badges.filter { it.completed }).size.toLong()
    private fun calcAllExp(currentExp: Long, level: Int): Long {
        val exp = 100L * level * (level + 1) / 2
        return currentExp + exp
    }
    private fun calcCoinsSpent(current: Long, total: Long) = total - current
    private fun calcMostCompletedReminder(reminders: List<Reminder>): Pair<String, Long> {
        val highest = reminders.maxByOrNull { it.completedTally }
        var result = Pair("", 0L)
        if (highest != null) {
            result = Pair(highest.name, highest.completedTally)
        }
        return result
    }


    // ============ Functions for changing variables =================
    fun addExp(amount: Int) {
        val user = userAllData.value.userData ?: return
        val next = userAllData.value.expToNextLevel
        val newExp = user.currentExp + amount

        val updated = if (newExp >= next) {
            // Level up
            val leftover = newExp - next
            val coins = user.level * 25
            user.copy(
                level = user.level + 1,
                currentExp = leftover,
                lifePointsUsed = user.lifePointsUsed + 3,
                coins = user.coins + coins,
                allCoinsEarned = user.allCoinsEarned + coins
                )
        } else {
            user.copy(currentExp = newExp)
        }
        updateLocalVariables(updated)
    }

    fun updateTheme(isDark: Boolean) = viewModelScope.launch {
        val current = userAllData.value.userData ?: return@launch
        val updated = current.copy(isDarkTheme = isDark)
        userAllData.update { current ->
            current.copy(userData = updated)
        }
    }

    // ================== Auth Functions =========================
    fun login(email: String, password: String) = viewModelScope.launch {
        userAllData.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            authRepo.login(email, password)
            loadUser()
        } catch (e: Exception) {
            userAllData.update { it.copy(errorMessage = e.localizedMessage) }
        } finally {
            userAllData.update { it.copy(isLoading = false) }
        }
    }

    fun register(email: String, password: String) = viewModelScope.launch {
        userAllData.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            authRepo.register(email, password)
            createNewUser()
        } catch (e: Exception) {
            userAllData.update { it.copy(errorMessage = e.localizedMessage) }
        } finally {
            userAllData.update { it.copy(isLoading = false) }
        }
    }

    fun signInWithGoogleIntent(intent: Intent?) = viewModelScope.launch {
        userAllData.update { it.copy(isLoading = true, errorMessage = null) }

        try {
            authRepo.handleGoogleResultIntent(intent)
            loadUser()
        } catch (e: Exception) {
            userAllData.update { it.copy(errorMessage = e.localizedMessage) }
        } finally {
            userAllData.update { it.copy(isLoading = false) }
        }
    }

    fun logout() = authRepo.logout()

    fun setLoggedOut() {
        userAllData.update { it.copy(isLoggedIn = false, userData = null) }
    }

    fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        try {
            authRepo.sendPasswordResetEmail(email)
        } catch (e: Exception) {
            userAllData.update { it.copy(errorMessage = e.localizedMessage) }
        }
    }
}