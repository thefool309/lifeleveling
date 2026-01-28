package com.lifeleveling.app.data

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lifeleveling.app.auth.AuthModel
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.lifeleveling.app.R
import kotlin.Int
import java.time.LocalDate

/**
 * Manages the local state of the user.
 * Is a viewModel to be able to be called from the UI
 * @param fireRepo Creates outside class to write to firebase.
 * @param authModel Creates outside class for authenticating the user.
 * @param logger Creates an instance of a logger that can inherit from any class based on the interface ILogger. Used to modify behavior of the logger.
 * @author Elyseia
 */
class UserManager(
    val logger: ILogger = AndroidLogger(),  // Put the creation of the logger here so that any function can access it if it is desired.
    private val db: FirebaseFirestore = Firebase.firestore,
    private val authModel: AuthModel = AuthModel(logger = logger),
    private val fireRepo: FirestoreRepository = FirestoreRepository(logger = logger, db = db),
    private val reminderRepo: ReminderRepository = ReminderRepository(logger, db),
) : ViewModel() {
    //region SetUp
    private val userData = MutableStateFlow(UsersData())
    val uiState: StateFlow<UsersData> = userData.asStateFlow()  // Makes everything react to changes

    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser

        if (user == null) {
            // User logged out so resets to defaults
            userData.value = UsersData()
            return@AuthStateListener
        }

        // User logged in
        viewModelScope.launch {
            userData.update { it.copy(isLoading = true, error = null) }

            try {
                // create user if first time
                fireRepo.ensureUserCreated(user)

                // Load profile
                val loaded = fireRepo.getUser(user.uid) ?: throw Exception("Failed to load user data")

                // Update UI
                userData.value = loaded.copy(
                    isLoggedIn = true,
                    fbUser = user,
                    isLoading = false
                ).recalculateAll()
            } catch (e: Exception) {
                Log.e("FirestoreRepository", "Error creating user", e)
                userData.update { it.copy(isLoading = false, error = "Error creating user") }
            }
        }
    }

    // Initialization

    init {
        authModel.addAuthStateListener(listener)
    }

    override fun onCleared() { authModel.removeAuthStateListener(listener) }

    //endregion

    //region User Functions


    //region Functions for changing variables
    /**
     * Handles adding experience to the user.
     * Will do level up logic if needed.
     * Leveling up rolls over extra exp, gives coins, and adds 5 life points to the user.
     * After a level up the derived values will also be recalculated
     * @param amount The double for the amount of experience to be added to the user
     * @author Elyseia
     */
    fun addExp(amount: Double) {
        val user = userData.value.userBase
        val next = userData.value.xpToNextLevel
        val newExp = user.currentXp + amount
        val updated: UsersBase
        var leveledUp = false
        var coins = 0L

        if (newExp >= next) {
            // Level up
            leveledUp = true
            val leftover = newExp - next
            coins = ((user.level + 1) * 10) + calcCoinsForReminderCompletion()

            updated = user.copy(
                level = user.level + 1,
                currentXp = leftover,
                lifePointsTotal = user.lifePointsTotal + 5,
                coinsBalance = user.coinsBalance + coins,
                allCoinsEarned = user.allCoinsEarned + coins
                )
        } else {
            updated = user.copy(currentXp = newExp)
        }
        // Updates the state and UI
        userData.update {
            it.copy(
                userBase = updated,
                levelUpFlag = leveledUp, // If leveled up the popup will trigger
                levelUpCoins = coins
            ).recalculateAfterLevelUp()
        }
    }

    /**
     * Is a call to update the theme saved for the user in the user state
     * Light mode or dark mode
     * Sends the updated preference to firestore
     * @param isDark boolean that controls if it is in dark mode (true) or light mode (false)
     * @author Elyseia
     */
    fun updateTheme(isDark: Boolean) {
        val current = userData.value.userBase
        val updated = current.copy(isDarkTheme = isDark)
        userData.update { current ->
            current.copy(
                userBase = updated,
                isLoading = true,
                error = null
            )
        }
        viewModelScope.launch {
            try {
                fireRepo.editUser(
                    current.userId,
                    mapOf("isDark" to isDark),
                )
                userData.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                logger.e("FB", "Error updating theme preference to firestore", e)
                userData.update { it.copy(isLoading = false, error = "Error writing theme preference to firestore") }
            }
        }
    }

    /**
     * Clears the level up flag so the overlay will stop showing
     * @author Elyseia
     */
    fun clearLevelUpFlag() {
        userData.update {
            it.copy(
                levelUpFlag = false,
                levelUpCoins = 0L
            )
        }
    }

    /**
     * Switches the value controlling if the user is in Fight or Meditate mode
     * @param value 0 for Fight and 1 for Meditate
     * @author Elyseia
     */
    fun setFightOrMeditate(value: Int) {
        val user = userData.value.userBase
        val updatedUser = user.copy(fightOrMeditate = value)
        userData.update { it.copy(userBase = updatedUser) }
    }

    /**
     * This will take in a new set of stats and save them to the user's information
     * Reset life points by setting all parameters to 0.
     *
     * Flow:
     * 1. Pull the user's information for their stats and life points.
     * 2. Set the stats and life points to the new values.
     * 3. Update values to the user's local state.
     * 4. Sends the updated stats and life point count to firestore
     * 5. If the firestore write fails, the updates to the state rollback
     *
     * @author fdesouza1992, Elyseia
     */
    fun updateStats(
        strength: Long,
        defense: Long,
        intelligence: Long,
        agility: Long,
        health: Long,
        usedPoints: Long
    ) {
        val user = userData.value.userBase
        val newStats = user.stats.copy(
            strength = strength,
            defense = defense,
            intelligence = intelligence,
            agility = agility,
            health = health,
        )
        val updated = user.copy(
            stats = newStats,
            lifePointsUsed = usedPoints,
        )
        userData.update { current ->
            current.copy(
                userBase = updated,
                isLoading = true,
                error = null
            ).recalculateStatDependencies()
        }
        viewModelScope.launch {
            try {
                fireRepo.editUser(
                    user.userId,
                    mapOf(
                        "stats" to newStats,
                        "lifePointsUsed" to usedPoints,
                    ),
                )
                userData.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                logger.e("FB", "Error updating stats", e)
                // Will roll back if write to firestore fails
                userData.update { it.copy(userBase = user, isLoading = false, error = "Error updating stats").recalculateStatDependencies() }
            }
        }
    }

    /**
     * Updates the UI State to include the new completed badge.
     * Saves the completed badge to Firestore
     * @param badgeId The ID of the badge that is completed
     * @author Elyseia
     */
    fun completeBadge(badgeId: String) {
        viewModelScope.launch {
            userData.update { it.copy(isLoading = true, error = null) }

            val now = Timestamp.now()

            userData.update { current ->
                val base = current.userBase
                run {
                    val updatedUserBase = base.copy(
                        completedBadges = base.completedBadges + (badgeId to now)
                    )
                    current.copy(userBase = updatedUserBase).recalculateAfterBadgeCompletion()
                }
            }
            val uid = authModel.currentUser?.uid
            if (uid == null) {
                userData.update { it.copy(isLoading = false, error = "User ID missing") }
                return@launch
            }
            try {
                fireRepo.saveCompletedBadge(uid, badgeId, now)
            } catch (e: Exception) {
                userData.update { it.copy(error = "Error saving completed badge $badgeId")}
                logger.e("UserManager", "Error saving completed badge $badgeId", e)
            } finally {
                userData.update { it.copy(isLoading = false) }
            }
        }
    }
    //endregion

    //region Calculation Functions
    /**
     * Calculates the number of coins to give the user for completing a reminder.
     * Uses 10 coins as the base value.
     * Defense gives an extra 2% per point
     * Intelligence gives an extra 5% per point
     * Agility gives an extra 3% per point
     * @return Long
     * @author Elyseia
     */
    fun calcCoinsForReminderCompletion() : Long {
        val user = userData.value.userBase
        val coins = 10 +
                (10 *
                (
                        (user.stats.defense * .02) +
                        (user.stats.intelligence * .05) +
                        (user.stats.agility * .03)
                )
                )
        return coins.toLong()
    }

    /**
     * Calculates the amount of exp to give the user for completing a reminder.
     * Uses 15 exp as the base value
     * Strength gives an extra 5% per point
     * Defense gives an extra 3% per point
     * Agility gives an extra 2% per point
     * @return Double
     * @author Elyseia
     */
    fun calcExpForReminderCompletion() : Double {
        val user = userData.value.userBase
        val exp = 15 +
                (15 *
                (
                        (user.stats.strength * .05) +
                        (user.stats.defense * .03) +
                        (user.stats.agility * .02)
                )
                )
        return exp
    }

    /**
     * Recalculates some data for most accurate representation in UserJourney screen
     * @author Elyseia
     */
    fun userJourneyCalculations() {
        userData.update { it.copy().recalculatingUserJourney() }
    }

    /**
     * Calculates how long it has been since the user created their profile.
     * Takes their createdAt time and subtracts if from the current time to get the difference
     * Uses the result of that to calculate a string that will display the number of years and days since creation
     * @return Returns a string already formatted with years(if years since created) and days
     * @author Elyseia
     */
    fun calcTimeSinceCreatedDate(): String {
        val createdAtTimestamp = userData.value.userBase.createdAt ?: return "Unknown"
        val createdAtDate = createdAtTimestamp.toDate()
        val now = System.currentTimeMillis()
        val timeDifference = now - createdAtDate.time

        // Converting to days
        val days = timeDifference / (1000 * 60 * 60 * 24)
        val years = days / 365
        val remainingDays = days % 365

        // Building string for display
        val yearsSection = if (years > 0) "$years year${if (years > 1) "s " else " "}" else ""
        val daysSection = "$remainingDays day${if (remainingDays != 1L) "s" else ""}"

        return (yearsSection + daysSection).trim()
    }
    //endregion

    //region Reminder Functions
    /**
     * Retrieves a reminder's information out of the reminder list based on the id
     * @param id ID of the reminder
     * @return A reminder object if it exists, null if nothing was found
     * @author Elyseia
     */
    fun retrieveReminder(id: String) : Reminder? {
        return userData.value.reminders.find { it.reminderId == id }
    }

    /**
     * Takes in a ReminderDraft object from the UI and turns it into a full reminder.
     * Passes that reminder to the repo to be written to firestore.
     * Updates the firestore id returned from adding it.
     * Adds the full reminder to the user state and updates the dependencies
     * @author fdesouza1992, Elyseia
     */
    fun addReminder(draft: ReminderDraft) {
        viewModelScope.launch {
            userData.update { it.copy(isLoading = true, error = null) }

            try {
                val uid = authModel.currentUser?.uid ?: error("User not logged in")

                // Build Reminder from draft
                val reminder = Reminder(
                    reminderId = "",
                    title = draft.title,
                    notes = "",
                    dueAt = draft.dueAt,
                    completed = false,
                    completedAt = null,
                    lastUpdate = null,
                    daily = draft.daily,
                    timesPerMinute = draft.timesPerMinute,
                    timesPerHour = draft.timesPerHour,
                    timesPerDay = draft.timesPerDay,
                    timesPerMonth = draft.timesPerMonth,
                    iconName = draft.iconName,
                    repeatForever = draft.repeatForever,
                    repeatCount = draft.repeatCount,
                    repeatInterval = draft.repeatInterval,
                    dotColor = draft.dotColor,
                )

                // Write to firestore
                val reminderId = reminderRepo.createReminder(reminder, uid)
                    ?: error("Failed to create reminder")

                val reminderWithId = reminder.copy(reminderId = reminderId)

                // Update UI ONLY after success
                userData.update { current ->
                    current.copy(
                        reminders = current.reminders + reminderWithId,
                    ).updateReminderDependencies()
                }
            } catch (e: Exception) {
                logger.e("FB", "Error adding reminder", e)
                userData.update { it.copy(error = "Error adding reminder") }
            } finally {
                userData.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Retrieves a list of reminders for the user on the selected date and total completed reminders
     * 1. Puts up the loading overlay
     * 2. Pulls the reminder list for the day from reminderRepo
     * 3. Pulls the completion of reminders for that day from reminderRepo
     * 4. Gives empty lists if failed
     * 5. Clears the loading overlay and returns the list and map
     * @param date The date to look up in the user's reminder collection
     * @return A pair that contains a list of reminders for the given date and a map of how many are completed
     * @author fdesouza1992
     */
    fun getRemindersForDate(date: LocalDate): Pair<List<Reminder>, Map<String, Int>> {
        var reminders: List<Reminder> = emptyList()
        var completionsByReminderId: Map<String, Int> = emptyMap()

        viewModelScope.launch {
            userData.update { it.copy(isLoading = true, error = null) }

            try {
                val uid = authModel.currentUser?.uid ?: error("User not logged in")

                reminders = reminderRepo.getRemindersForDate(date, uid)
                completionsByReminderId = reminderRepo.getReminderCompletionsForDate(date, uid)
            } catch (e: Exception) {
                logger.e("Reminders", "DailyRemindersList: failed to load for $date", e)
                userData.update { it.copy(error = "DailyRemindersList: failed to load for $date") }
                reminders = emptyList()
                completionsByReminderId = emptyMap()
            } finally {
                userData.update { it.copy(isLoading = false) }
            }
        }

        return Pair(reminders, completionsByReminderId)
    }

    /**
     * Increases the "completed" count for a reminder on a specific day.
     *
     * This is what we call when the user checks off a reminder in the Day View.
     * Only counts one tap at a time, so we call this every time a checkbox goes from not done → done.
     *
     * @param reminderId The reminder we’re counting for.
     * @param reminderTitle Title saved for future reference (nice for UI stats/history)
     * @param date The day this completion happened.
     * @author fdesouza1992
     */
    fun incrementReminderCompletionForDate(
        reminderId: String,
        reminderTitle: String,
        date: LocalDate
    ) {
        viewModelScope.launch {
            userData.update { it.copy(isLoading = true, error = null) }

            try {
                val uid = authModel.currentUser?.uid ?: error("User not logged in")

                val ok = reminderRepo.incrementReminderCompletionForDate(reminderId, reminderTitle, date, uid)
                if (!ok) {
                    logger.e("Reminders", "Failed to increment completion for $reminderId on $date")
                    userData.update { it.copy(error = "Failed to increment completion for $reminderId on $date") }
                }
            } catch (e: Exception) {
                logger.e("Reminders", "Failed to increment completion for $reminderId on $date", e)
                userData.update { it.copy(error = "Failed to increment completion for $reminderId on $date") }
            } finally {
                userData.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Decreases the completion count for a reminder on a specific day.
     *
     * This is basically the opposite of `incrementReminderCompletionForDate`.
     * We call this when the user unchecks a reminder that was previously marked complete.
     *
     * @param reminderId The reminder we’re counting for.
     * @param reminderTitle Title saved for future reference (nice for UI stats/history)
     * @param date The day this completion happened.
     * @author fdesouza1992
     */
    fun decrementReminderCompletionForDate(
        reminderId: String,
        reminderTitle: String,
        date: LocalDate
    ) {
        viewModelScope.launch {
            userData.update { it.copy(isLoading = true, error = null) }

            try {
                val uid = authModel.currentUser?.uid ?: error("User not logged in")

                val ok = reminderRepo.decrementReminderCompletionForDate(reminderId, reminderTitle, date, uid)
                if (!ok) {
                    logger.e("Reminders", "Failed to increment completion for $reminderId on $date")
                    userData.update { it.copy(error = "Failed to increment completion for $reminderId on $date") }
                }
            } catch (e: Exception) {
                logger.e("Reminders", "Failed to increment completion for $reminderId on $date", e)
                userData.update { it.copy(error = "Failed to increment completion for $reminderId on $date") }
            } finally {
                userData.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Updates a reminder by its document ID.
     *
     * We pass in a map of fields we want to change (title, notes, color, etc.) and Firestore will only update those fields instead of overwriting everything.
     * Also sneaks in a `lastUpdate` timestamp automatically so we always know when this reminder was last touched.
     * @param reminderId The Firestore document ID for the reminder we want to update.
     * @param updates A map of fields we want to modify. Only these fields get changed.
     * @param uid The ID of the user to write to
     * @author fdesouza1992
     */
    fun updateReminder(
        reminderId: String,
        updates: Map<String, Any?>,
    ) {
        viewModelScope.launch {
            userData.update { it.copy(isLoading = true, error = null) }

            try {
                val uid = authModel.currentUser?.uid ?: error("User not logged in")

                val ok = reminderRepo.updateReminder(reminderId, updates, uid)
                if (!ok) {
                    logger.e("Reminders", "MyReminders: failed to update enabled for $reminderId")
                    userData.update { it.copy(error = "MyReminders: failed to update enabled for $reminderId") }
                }
            } catch (e: Exception) {
                logger.e("Reminders", "MyReminders: failed to update enabled for $reminderId", e)
                userData.update { it.copy(error = "MyReminders: failed to update enabled for $reminderId") }
            } finally {
                userData.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteReminder(
        reminderId: String,
    ): Boolean {
        var result = false
        viewModelScope.launch {
            userData.update { it.copy(isLoading = true, error = null) }

            try {
                val uid = authModel.currentUser?.uid ?: error("User not logged in")

                result = reminderRepo.deleteReminder(reminderId, uid)
            } catch (e: Exception) {
                logger.e("Reminders", "MyReminders: delete failed for $reminderId", e)
                userData.update { it.copy(error = "MyReminders: delete failed for $reminderId") }
            } finally {
                userData.update { it.copy(isLoading = false) }
            }
        }
        return result
    }

    /**
     * Returns total number of reminder completions across all time.
     *
     * We read every document inside `reminderCompletions` and sum the `count` values. Great for showing progress in "My Journey" or achievement screens.
     * If something goes wrong, we return 0 instead of crashing the app.
     *
     * @return The total count of completions across all reminders.
     * @author fdesouza1992
     */
    fun getTotalReminderCompletion(): Long {
        var result: Long = 0
        viewModelScope.launch {
            userData.update { it.copy(isLoading = true, error = null) }

            try {
                val uid = authModel.currentUser?.uid ?: error("User not logged in")

                result = reminderRepo.getTotalReminderCompletions(uid)
            } catch (e: Exception) {
                logger.e("Reminders", "Retrieval of Total Reminder Completions failed.", e)
                userData.update { it.copy(error = "Retrieval of Total Reminder Completions failed.") }
            } finally {
                userData.update { it.copy(isLoading = false) }
            }
        }
        return result
    }
    //endregion

    //region Streak Functions
    /**
     * This function takes the UI supplied draft and a reminder information to create a full streak object.
     * @param streakId The new ID for the streak.
     * @param reminder The reminder the streak is based on
     * @param draft The draft of information the user gave from the UI
     * @see StreakDraft
     * @return A full Streak object.
     * @author Elyseia
     */
    private fun buildStreak(
        streakId: String,
        draft: StreakDraft,
        reminder: Reminder,
    ) : Streak {
        val now = Timestamp.now()
        val total = if (reminder.daily) {
            reminder.timesPerDay * 7
        } else {
            reminder.timesPerMonth
        }
        return Streak(
            streakId = streakId,
            reminderId = draft.reminderId,
            weekly = draft.weekly,
            totalRequired = total,
            numberCompleted = 0,
            repeat = draft.repeat,
            createdAt = now,
            endsAt = null, /* TODO: Update this */
            lastUpdate = now
        )
    }

    /**
     * Creates a new streak and adds it into the user's data and firestore collection
     * Flow:
     * 1. Pulls information needed for user and reminder
     * 2. Sends the information to firestore and returns a full streak object with the uid firestore assigned
     * 3. Updates the user's streak collection in the local state information
     * @param draft A draft object for a streak the user provides from the UI
     * @see StreakDraft
     * @author Elyseia
     */
    fun addStreak(draft: StreakDraft) {
        viewModelScope.launch {
            userData.update { it.copy(isLoading = true, error = null) }

            try {
                val uid = authModel.currentUser?.uid ?: error("User not logged in")
                val reminder = retrieveReminder(draft.reminderId) ?: error("Reminder not found")

                val streak = fireRepo.createStreak(uid) { streakId ->
                    buildStreak(
                        streakId,
                        draft,
                        reminder,
                    )
                }

                userData.update {current ->
                    current.copy(
                        streaks = current.streaks + streak,
                    ).separateStreaks()
                }
            } catch (e: Exception) {
                logger.e("FB", "Error adding streak", e)
                userData.update { it.copy(error = "Error adding streak") }
            } finally {
                userData.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Removes a streak from the streak list
     * Reupdates the weekly and month lists after the streak was removed
     * FLow:
     * 1. Deletes the streak from firestore
     * 2. Deletes the streak from local storage of all streaks
     * 3. Updates separated lists to no longer have the streak
     * @param streakId ID of the streak to be removed
     * @author Elyseia
     */
    fun removeStreak(streakId: String) {
        viewModelScope.launch {
            userData.update { it.copy(isLoading = true, error = null) }

            try {
                val uid = authModel.currentUser?.uid ?: error("User not logged in")

                if (userData.value.streaks.none { it.streakId == streakId }) {
                    error("Streak not found")
                }

                fireRepo.deleteStreak(uid, streakId)

                userData.update {current ->
                    current.copy(
                        streaks = userData.value.streaks.filterNot { it.streakId == streakId }
                    ).separateStreaks()
                }
            } catch (e: Exception) {
                logger.e("FB", "Error removing streak", e)
                userData.update { it.copy(error = "Error removing streak") }
            } finally {
                userData.update { it.copy(isLoading = false) }
            }
        }
    }
    //endregion

    //endregion

    //region Auth Functions

    /**
     * Handles the Google sign-in result Intent returned to the Activity.
     *
     * Flow:
     * 1. Try to pull the Google account and ID token out of the intent. (handled in AuthModel)
     * 2. If the token is there, pass it down to Firebase to finish sign-in. (handled in AuthModel)
     * 3. Writes a bookkeeping instance to firestore (handled in FirestoreRepository)
     * 4. If anything fails, log it and update the UI with an error message.
     *
     * @param data The Intent returned from the Google sign-in Activity result.
     * @author fdesouza1992
     */
    fun handleGoogleResultIntent(data: android.content.Intent?) {
        userData.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val idToken = authModel.handleGoogleResultIntent(data)
                val user = authModel.firebaseAuthWithGoogle(idToken)
                    ?: throw Exception("Firebase user null")
                Log.d("FB", "Google sign-in success: uid=${user.uid}")
                postLoginBookkeeping("google").join()
                userData.update { it.copy(isLoggedIn = true, isLoading = false, error = null) }
            } catch (e: ApiException) {
                Log.e("FB", "Google sign-in failed", e)
                userData.update { it.copy(isLoading = false, error = "Google sign-in failed: ${e.message}") }
            } catch (e: Exception) {
                Log.e("FB", "Google sign-in failed", e)
                userData.update { it.copy(isLoading = false, error = "Firebase sign-in failed: ${e.message}") }
            }
        }
    }

    /**
     * Signs a user in using email and password and updates the UI state.
     *
     * Flow:
     * 1. Mark the UI as loading.
     * 2. Call Firebase `signInWithEmailAndPassword`.
     * 3. Run post-login work (create user doc, log event, etc.).
     * 4. On different error types, logger logs what happened and sets a friendly message in the UI (no account, wrong password, Google-only, etc.).
     *
     * Note: even though this function is marked `suspend`, it uses viewModelScope.launch` so it never blocks the caller.
     *
     * @param email  The user’s email address.
     * @param password The user’s password.
     *
     * @author thefool309, fdesouza1992
     */
    fun signInWithEmailPassword(email: String, password: String)
    {
        userData.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // If this email is Google-only, this call will fail with InvalidCredentials.
                authModel.signInWithEmailPassword(email.trim(), password)
                postLoginBookkeeping(provider = "password").join()
                userData.update { it.copy(isLoggedIn = true, isLoading = false, error = null) }

            } catch (e: com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                // No account exists with this email
                logger.e("FB", "No user for ${email.trim()}", e)
                userData.update { it.copy(isLoading = false, error = "No account found for this email.") }

            } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                // Wrong password or email malformed, or Google-only account
                logger.e("FB", "Invalid credentials", e)
                // Tells user if the email is federated-only
                val methods = authModel.fetchSignInMethods(email)
                val msg = if ("google.com" in methods && "password" !in methods) {
                    "This email is registered with Google. Use 'Login using Google'."
                } else {
                    "Invalid email or password."
                }
                userData.update { it.copy(isLoading = false, error = msg) }

            } catch (e: com.google.firebase.auth.FirebaseAuthException) {
                logger.e("FB", "Auth exception", e)
                userData.update { it.copy(isLoading = false, error = "Authentication error.") }

            } catch (e: Exception) {
                logger.e("FB", "Unexpected sign-in error", e)
                userData.update { it.copy(isLoading = false, error = "Sign-in failed.") }
            }
        }
    }

    /**
     * Signs the current user out of Firebase and (optionally) Google.
     *
     * Flow:
     * 1. Mark the UI as loading.
     * 2. Call Firebase `signOut()`.
     * 3. If an Activity is passed in, also sign out of the Google client.
     * 4. Once the Google sign-out finishes, the loading flag clears so the UI can update.
     *
     * @param activity Used to sign out from the Google client *Optional*.
     * @author fdesouza1992
     */
    fun signOut(activity: Activity? = null) {
        userData.update { it.copy(isLoading = true, error = null) }
        authModel.signOut(activity)
        if (activity != null) {
            authModel.googleClient(activity).signOut().addOnCompleteListener {
                userData.update { it.copy(isLoggedIn = false, isLoading = false) }
            }
        } else {
            userData.update { it.copy(isLoggedIn = false, isLoading = false) }
        }
    }

    // I think this will be rewritten after pulling in new branches
//    fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
//        try {
//            authRepo.sendPasswordResetEmail(email)
//        } catch (e: Exception) {
//            userAllData.update { it.copy(errorMessage = e.localizedMessage) }
//        }
//    }

    /**
     * Runs the “after login” work once a user has successfully signed in.
     *
     * Flow:
     * 1. Grab the current Firebase user.
     * 2. In the background, make sure they have a user document in Firestore.
     * 3. Log an auth event to `authLogs` for basic monitoring.
     *
     * If the Firestore work fails, we log a warning but don’t block sign-in.
     *
     * @param provider The auth provider string (e.g., "password" or "google").
     * @author fdesouza1992
     */
    private fun postLoginBookkeeping(provider: String) = viewModelScope.launch {
        val user = authModel.currentUser ?: return@launch
        try {
            val base = fireRepo.ensureUserCreated(user)
            userData.update { it.withBase(base) }
            fireRepo.writeBookkeeping(provider, user)
        } catch (e: Exception) {
            logger.w("FB", "postLoginBookkeeping failed: ${e.message}")
        }
    }

    /**
     * Makes sure the flag is set to logged out.
     * Used at the start when the app loads if a user is not saved
     * @author Elyseia
     */
    fun setLoggedOut() {
        userData.update { it.copy(isLoggedIn = false, userBase = UsersBase()) }
    }

    /**
     * Send a Firebase password reset email to the user's registered email address
     *
     * Flow:
     * 1. Try to send the reset email using FirebaseAuth.
     * 2. If it succeeds, call onResult with true and a friendly message.
     * 3. If it fails, log and call onResult with false and a brief message.
     * Note: This does NOT change AuthUiState
     *
     * @param email The email address to send the reset link to
     * @author fdesouza1992
     */
    fun sendPasswordResetEmail(
        email: String,
    ): Pair<Boolean, Int> {
        var result = Pair<Boolean, Int> (false, 0)
        userData.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val trimmed = email.trim()
                authModel.sendPasswordResetEmail(trimmed)
                result = Pair(true, R.string.resetPasswordEmailSent)
            } catch (e: com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                logger.e("FB", "Password reset: invalid email format", e)
                result = Pair(false, (R.string.resetPasswordEmailError))
            } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                logger.e("FB", "Password reset: invalid email format", e)
                result = Pair(false, (R.string.resetPasswordEmailError))

            } catch (e: com.google.firebase.auth.FirebaseAuthException) {
                logger.e("FB", "Password reset: FirebaseAuthException", e)
                result = Pair(false, (R.string.resetPasswordFirebaseAuthError))

            } catch (e: Exception) {
                logger.e("FB", "Password reset: unexpected error", e)
                result = Pair(false, (R.string.resetPasswordFirebaseAuthError))
            } finally {
                userData.update { it.copy(isLoading = false) }
            }
        }
        return result
    }

    //endregion

    //region Firestore managing functions


    // TODO: Unsure if we want to make this function because it would require saving all the user data.
    // Can always be split into a smaller save for app closing saves
//    // Write user into firestore
//    suspend fun saveUser() {
//        val user = userData.value.userBase ?: return
//        val uid = authModel.currentUser?.uid ?: return
//        userData.update { it.copy(isLoading = true, error = null) }
//
//        try {
//            fireRepo.saveUser(uid, user)
//        } catch (e: Exception) {
//            userData.update { it.copy(isLoading = false, error = e.localizedMessage) }
//        }
//    }

    // TODO: This function would be passed to the application layer to save user data when the app is paused or closed.
    // Can call above function or any new ones made
//    fun saveOnPause() {
//        viewModelScope.launch { saveUser() }
//    }

    // Create a new User
    /**
     * Creates a new Firebase user with email and password, then signs them in.
     *
     * Flow:
     * 1. Mark the UI as loading
     * 2. Call 'createUserWithEmailAndPassword'
     * 3. Immediately sign the user in with the same credentials
     * 4. Run the shared post-login work (create user doc, log, etc.)
     * 5. Handle common error cases (email already used, bad format, etc.) with user-friendly messages.
     *
     * @param email The email for the new account
     * @param password The password for the new account
     * @author thefool309, fdesouza1992
     */
    fun createNewUserWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            userData.update { it.copy(isLoading = true, error = null) }

            try {
                authModel.createUserWithEmailAndPassword(email.trim(), password)
                postLoginBookkeeping(provider = "password").join()
                userData.update { it.copy(isLoggedIn = true, isLoading = false, error = null) }

            } catch (e: com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                // Email already in use.
                val msg = authModel.checkIfEmailInUse(email)
                logger.e("FB", msg, e)
                userData.update { it.copy(isLoading = false, error = msg) }

            } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                logger.e("FB", "Invalid email/password", e)
                userData.update { it.copy(isLoading = false, error = "Invalid email or password format.") }

            } catch (e: com.google.firebase.auth.FirebaseAuthException) {
                logger.e("FB", "Auth exception", e)
                userData.update { it.copy(isLoading = false, error = "Could not create account.") }

            } catch (e: Exception) {
                logger.e("FB", "Unexpected sign-up error", e)
                userData.update { it.copy(isLoading = false, error = "Sign-up failed.") }
            }
        }
    }

    /**
     * A full account delete for the currently signed-in user.
     *
     * Flow:
     * 1. Mark the UI as loading and clear any old error.
     * 2. Call into the Firestore Repository to delete the user and their data.
     * 3. If the repo call returns false or throws an error, a simple "try again" message is displayed.
     * 4. On success, the auth listener will notice that the user is now null and the rest of the app can react to that.
     *
     * @author fdesouza1992
     */
    fun deleteAccount() {
        userData.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val uid = authModel.currentUser?.uid

            try {
                // Delete subcollections (Just reminders for now)
                reminderRepo.deleteAllRemindersForUser(uid!!)

                // Delete Firestore user data
                val ok = fireRepo.deleteUser(uid)
                if (!ok) {
                    userData.update { it.copy(isLoading = false, error = "Failed to delete account data. Please try again.")}
                    return@launch
                }

                // Delete Firebase Auth user
                val authOk = authModel.deleteUser(uid)
                if (!authOk) {
                    userData.update { it.copy(isLoading = false, error = "Failed to remove authentication. Please try again.")}
                    return@launch
                }

                // Delete successful
                userData.update { it.copy(isLoggedIn = false, isLoading = false, error = null) }

            } catch (e: Exception) {
                logger.e("Auth", "deleteAccount failed", e)
               userData.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to delete account. Please try again."
                    )
                }
            }
        }
    }

    /**
     * Writes the information from a level up into the user's firestore data
     * @author Elyseia
     */
    fun writeLevelUp() {
        val user = userData.value.userBase
        userData.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                fireRepo.editUser(
                    userId = user.userId,
                    userData = mapOf(
                        "level" to user.level,
                        "currentXp" to user.currentXp,
                        "lifePointsTotal" to user.lifePointsTotal,
                        "coinsBalance" to user.coinsBalance,
                        "allCoinsEarned" to user.allCoinsEarned,
                    )
                )
            } catch (e: Exception) {
                logger.e("FB", "Unable to update level up", e)
                userData.update { it.copy(error = "Failed to update level up.") }
            } finally {
                userData.update { it.copy(isLoading = false) }
            }
        }
    }

    fun termsFireBaseFetch(onComplete: (Terms?) -> Unit) {
        userData.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                fireRepo.termsFireBaseFetch(onComplete)
            } catch (e: Exception) {
                logger.e("FB", "Terms fetch failed.", e)
                userData.update { it.copy(error = "Terms fetch failed.") }
            } finally {
                userData.update { it.copy(isLoading = false) }
            }
        }
    }

    fun privacyFireBaseFetch(onComplete: (Privacy?) -> Unit) {
        userData.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                fireRepo.privacyFireBaseFetch(onComplete)
            } catch (e: Exception) {
                logger.e("FB", "Privacy fetch failed.", e)
                userData.update { it.copy(error = "Privacy fetch failed.") }
            } finally {
                userData.update { it.copy(isLoading = false) }
            }
        }
    }

    //endregion
}