package com.lifeleveling.app.data

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.lifeleveling.app.MainActivity.Companion.TAG
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

/**
 * Repository that encapsulates all Firestore reminder operations.
 *
 * This class is used internally by [FirestoreRepository] so that the rest of the app can keep calling FirestoreRepository.createReminder(), getRemindersForDate(), deleteReminder(), etc. without any changes.
 *
 * @author fdesouza1992
 */
class ReminderRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    /**
     * Convenience helper for getting the currently signed-in user's UID.
     *
     * This simply reads `auth.currentUser?.uid` and returns it, or `null` if there is no authenticated Firebase user at the moment
     * @return The current Firebase Auth user's UID, or `null` if no user is signed in.
     * @author fdesouza1992
     */
    private fun getUserId(): String? = auth.currentUser?.uid

    /**
     * Helper to get this user's 'reminders' collection in Firestore.
     *
     * We use this to keep the path logic in one place 'users/{uid}/reminders'.
     *
     * @param uid The user's unique Firestore/Firebase Auth ID.
     * @return A reference to that user's 'reminders' collection.
     * @author fdesouza1992
     * **/
    private fun remindersCol(uid: String) =
        //Firebase.firestore.collection("users").document(uid).collection("reminders")
        db.collection("users").document(uid).collection("reminders")

    /**
     * Creates or updates a reminder for the currently signed-in user.
     *
     * Current Flow:  (May need to be updated as we progress)
     * 1. Check that we have a logged-in user; if not, log it and return null.
     * 2. Builds the reminder payload, letting Firestore handle server timestamps.
     * 3. If `reminders.reminderId` is blank, a new doc with an auto ID is created, otherwise writes to that specific document.
     * 4. Ensures the `reminderId` field inside the document matches the doc ID to facilitate with mapping later.
     *
     * On success, we log the created reminder and return its document ID.
     * On failure, we log the error and return null so the caller can handle it.
     *
     * @param reminders The reminder data we want to store.
     * @param logger Used to log success or failures during write.
     * @return The Firestore document ID for this reminder, or `null` if something went wrong.
     * @author fdesouza1992
     * **/
    suspend fun createReminder(
        reminders: Reminders,
        logger: ILogger
    ): String? {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            logger.e("Reminders", "createReminder: user not authenticated.")
            return null
        }

        // Build the payload; let Firestore set timestamps
        val payload = hashMapOf(
            "reminderId" to (reminders.reminderId.ifBlank { null }),
            "title" to reminders.title,
            "notes" to reminders.notes,
            "startingAt" to reminders.startingAt,
            "completed" to reminders.completed,
            "completedAt" to reminders.completedAt,
            "createdAt" to FieldValue.serverTimestamp(),
            "lastUpdate" to FieldValue.serverTimestamp(),
            "daily" to reminders.daily,
            "timesPerMinute" to reminders.timesPerMinute,
            "timesPerHour" to reminders.timesPerHour,
            "timesPerDay" to reminders.timesPerDay,
            "timesPerMonth" to reminders.timesPerMonth,
            "colorToken" to reminders.colorToken,
            "iconName" to reminders.iconName,
            "repeatForever" to reminders.repeatForever,
            "repeatCount" to reminders.repeatCount,
            "repeatInterval" to reminders.repeatInterval,
            "enabled" to reminders.enabled,
        ).filterValues { it != null } // don't write null reminderId if empty

        return try {
            val docRef = if (reminders.reminderId.isBlank()) {
                remindersCol(uid).document() // auto id
            } else {
                remindersCol(uid).document(reminders.reminderId)
            }

            // Persist reminderId inside the doc for simple mapping
            val finalPayload = payload.toMutableMap().apply {
                put("reminderId", docRef.id)
            }

            docRef.set(finalPayload, SetOptions.merge()).await()
            logger.d("Reminders", "createReminder: created ${docRef.id}")
            docRef.id
        } catch (e: Exception) {
            logger.e("Reminders", "createReminder failed", e)
            null
        }
        // TODO: implement notifTimestamp calculation
    }

    /**
     * Updates a reminder by its document ID.
     *
     * We pass in a map of fields we want to change (title, notes, color, etc.) and Firestore will only update those fields instead of overwriting everything.
     * Also sneaks in a `lastUpdate` timestamp automatically so we always know when this reminder was last touched.
     * Returns true if everything worked, or false if something failed.
     *
     * @param reminderId The Firestore document ID for the reminder we want to update.
     * @param updates A map of fields we want to modify. Only these fields get changed.
     * @param logger For debug/error messages.
     * @author fdesouza1992
     */
    suspend fun updateReminder(
        reminderId: String,
        updates: Map<String, Any?>,
        logger: ILogger
    ): Boolean {
        val uid = getUserId()
        if (uid == null) {
            logger.e("Reminders", "updateReminder: user not authenticated.")
            return false
        }
        return try {
            val payload = updates.toMutableMap().apply {
                this["lastUpdate"] = FieldValue.serverTimestamp()
            }
            remindersCol(uid).document(reminderId).update(payload).await()
            logger.d("Reminders", "updateReminder: $reminderId")
            true
        } catch (e: Exception) {
            logger.e("Reminders", "updateReminder failed", e)
            false
        }
        //TODO: implement notifTimestamp recalculation
    }

    /**
    * Marks a reminder as complete or incomplete.
    *
    * If completed = true → we also set `completedAt` with a server timestamp.
    * If false → we clear `completedAt` (because it's not done anymore).
    * Returns true on success, false if something broke.
    *
    * @param reminderId The ID of the reminder to update.
    * @param completed Whether the reminder is done or not.
    * @param logger For logging success/failure messages.
     * @author fdesouza1992
    */
    suspend fun setReminderCompleted(
        reminderId: String,
        completed: Boolean,
        logger: ILogger
    ): Boolean {
        val uid = getUserId()
        if (uid == null) {
            logger.e("Reminders", "setReminderCompleted: user not authenticated.")
            return false
        }
        return try {
            val payload = hashMapOf<String, Any?>(
                "completed" to completed,
                "completedAt" to if (completed) FieldValue.serverTimestamp() else null,
                "lastUpdate" to FieldValue.serverTimestamp()
            )
            remindersCol(uid).document(reminderId).update(payload).await()
            logger.d("Reminders", "setReminderCompleted: $reminderId -> $completed")
            true
        } catch (e: Exception) {
            logger.e("Reminders", "setReminderCompleted failed", e)
            false
        }
    }

    /**
     * Deletes a single reminder from Firestore.
     *
     * Just give me the reminderId and boom — it's gone. (Assuming the user is signed in and Firestore doesn't freak out.)
     * Returns true if delete worked, false if something failed.
     *
     * @param reminderId The ID of the reminder we want to remove.
     * @param logger Used to report errors if Firestore doesn't cooperate.
     * @author fdesouzq1992
     */
    suspend fun deleteReminder(reminderId: String, logger: ILogger): Boolean {
        val uid = getUserId()
        if (uid == null) {
            logger.e("Reminders", "deleteReminder: user not authenticated.")
            return false
        }
        return try {
            remindersCol(uid).document(reminderId).delete().await()
            logger.d("Reminders", "deleteReminder: $reminderId")
            true
        } catch (e: Exception) {
            logger.e("Reminders", "deleteReminder failed", e)
            false
        }
    }

    /**
     * Returns all reminders that should show up on the selected [date] for the currently signed-in user.
     *
     * 1) Gets the current user's uid. If we don't have one, we log it and return an empty list.
     * 2) Builds an "end of day" timestamp (exclusive) which is **the start of the next day**.
     *    Example: if date is 2025-12-11, endOfDay is 2025-12-12 00:00 (local time).
     * 3) Queries Firestore for reminders where `startingAt < endOfDay`.
     *    - This gives us a *candidate list* of reminders that start before the day ends.
     * 4) Converts docs into `Reminders` objects and copies `doc.id` into `reminderId`.
     * 5) Filters the list using `occursOn(date, zone)` so we only keep reminders that actually apply to that calendar day (one-time, daily, and repeat rules).
     * 6) Sorts the results by `startingAt` so the day view shows them in a nice order.
     *
     * Edge cases:
     * - If user is not signed in -> logs + returns emptyList()
     * - If Firestore read fails -> logs + returns emptyList()
     *
     * @param date The day the calendar is showing.
     * @param logger Logger used for debug/error messages.
     * @return List of reminders that should appear on [date], sorted by due time.
     * @author fdesouza1992
     */
    suspend fun getRemindersForDate(
        date: LocalDate,
        logger: ILogger
    ): List<Reminders> {
        val uid = getUserId()
        if (uid.isNullOrBlank()) {
            logger.e("Reminders", "getRemindersForDate: user id is null/blank; sign in first.")
            return emptyList()
        }

        val zone = ZoneId.systemDefault()
        val endOfDay = date.plusDays(1).atStartOfDay(zone)
        val endTs = Timestamp(Date.from(endOfDay.toInstant()))

        return try {
            // Fetch candidates with dueAt <= endOfSelectedDay.
            val snap = db.collection("users")
                .document(uid)
                .collection("reminders")
                .whereLessThan("startingAt", endTs)
                .get()
                .await()

            val all = snap.documents.mapNotNull { doc ->
                doc.toObject(Reminders::class.java)?.copy(reminderId = doc.id)
            }

            all
                .filter { it.occursOn(date, zone) }
                .sortedBy { it.startingAt?.toDate() } // keeps a nice ordering
        } catch (e: Exception) {
            logger.e("Reminders", "getRemindersForDate failed for $date", e)
            emptyList()
        }
    }

    /**
     * Checks if this reminder should be shown on a specific day.
     *
     * This is mainly used by the Day View to figure out which reminders belong on the selected date.
     *
     * It takes into account:
     * - When the reminder starts
     * - Whether it is daily
     * - Whether it repeats (and for how long)
     *
     * @param date The calendar day being evaluated.
     * @param zone The device time zone used to safely convert timestamps to dates.
     * @return true if the reminder applies to the given date, false if it does not.
     * @author fdesouza1992
     */

    private fun Reminders.occursOn(date: LocalDate, zone: ZoneId): Boolean {
        val start = this.startingAt?.toDate() ?: return false
        val startDate = start.toInstant().atZone(zone).toLocalDate()

        if (date.isBefore(startDate)) return false

        // If it’s a one-off, only show on its start date.
        val hasRepeatRule = repeatForever || (repeatCount > 0 && !repeatInterval.isNullOrBlank())
        if (!daily && !hasRepeatRule) {
            return date == startDate
        }

        // If it’s daily with no duration rule, show every day from start onward.
        if (daily && !hasRepeatRule) return true

        // If it repeats forever, allow it as long as date >= start.
        if (repeatForever) return true

        // Otherwise it repeats with a finite duration rule.
        val interval = repeatInterval ?: return false
        val count = repeatCount

        val endDate = when (interval) {
            "days" -> startDate.plusDays(count.toLong())
            "weeks" -> startDate.plusWeeks(count.toLong())
            "months" -> startDate.plusMonths(count.toLong())
            "years" -> startDate.plusYears(count.toLong())
            else -> return false
        }

        if (date.isAfter(endDate)) return false

        return true
    }

    /**
     * Returns **all reminders for the currently signed-in user.
     *
     * 1. Retrieves the currently authenticated user's uid.
     * 2. If the user is not signed in, logs the issue and returns an empty list.
     * 3. Fetches all documents from `users/{uid}/reminders`.
     * 4. Maps each Firestore document into a [Reminders] object and injects the document id into `reminderId` for easy reference in updates and deletes.
     * 5. Sorts the results by `startingAt` so reminders appear in chronological order.
     *
     * Edge cases:
     * - If the user is not authenticated → logs + returns `emptyList()`.
     * - If the Firestore read fails → logs the exception + returns `emptyList()`.
     *
     * @param logger Logger used for debug/error messaging.
     * @return A chronologically sorted list of all reminders belonging to the signed-in user.
     * @author fdesouza1992
     */
    suspend fun getAllReminders(logger: ILogger): List<Reminders> {
        val uid = getUserId()
        if (uid.isNullOrBlank()) {
            logger.e("Reminders", "getAllReminders: user id is null/blank; sign in first.")
            return emptyList()
        }

        return try {
            val snap = remindersCol(uid).get().await()

            snap.documents.mapNotNull { doc ->
                doc.toObject(Reminders::class.java)?.copy(reminderId = doc.id)
            }.sortedBy { it.startingAt?.toDate() }
        } catch (e: Exception) {
            logger.e("Reminders", "getAllReminders failed", e)
            emptyList()
        }
    }

    /**
     * Deletes all reminders for a specific user. Mainly used when fully removing a user account.
     * We look inside `users/{uid}/reminders`, grab every document, batch-delete them, and commit it in one go.
     *
     * If something goes wrong, we log the error but don't throw — this keeps the delete user flow moving instead of crashing out.
     *
     * @param uid The user ID whose reminders we want to clear.
     * @param logger For reporting any issues that happen during deletion.
     * @author fdesouza1992
     */
    suspend fun deleteAllRemindersForUser(uid: String, logger: ILogger) {
        try {
            val remindersSnap = remindersCol(uid).get().await()
            if (!remindersSnap.isEmpty) {
                val batch = db.batch()
                for (doc in remindersSnap.documents) {
                    batch.delete(doc.reference)
                }
                batch.commit().await()
            }
        } catch (e: Exception) {
            logger.e("Firestore", "Failed to delete reminders for user $uid", e)
        }
    }

    /**
     * Increases the "completed" count for a reminder on a specific day.
     *
     * This is what we call when the user checks off a reminder in the Day View.
     * Only counts one tap at a time, so we call this every time a checkbox goes from not done → done.
     *
     * We store these inside:
     * `users/{uid}/reminderCompletions/{reminderId_yyyy-MM-dd}`
     *
     * Example:
     * - Reminder ID = "water"
     * - Date = 2025-01-03
     * - Document = `"water_2025-01-03"`
     *
     * If the doc exists, we just bump count by +1.
     * If not, Firestore creates it and starts from 1.
     *
     * @param reminderId The reminder we’re counting for.
     * @param date The day this completion happened.
     * @param logger For logging success/fail messages.
     * @return `true` if increment works, `false` if something failed.
     * @author fdesouza1992
     */
    suspend fun incrementReminderCompletionForDate(
        reminderId: String,
        date: LocalDate,
        logger: ILogger
    ): Boolean = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            logger.e(TAG, "incrementReminderCompletionForDate: no logged in user.")
            return@withContext false
        }

        val dateKey = date.toString() // ISO "yyyy-MM-dd"
        val docId = "${reminderId}_$dateKey"

        try {
            val userRef = db.collection("users").document(uid)
            val completionsCol = userRef.collection("reminderCompletions")
            val docRef = completionsCol.document(docId)

            val atMidnight = date
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .let { Date.from(it) }

            val data = mapOf(
                "reminderId" to reminderId,
                "dateKey" to dateKey,
                "date" to atMidnight,
                "count" to FieldValue.increment(1L)
            )

            docRef.set(data, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            logger.e(TAG, "incrementReminderCompletionForDate failed for $reminderId on $dateKey", e)
            false
        }
    }

    /**
     * Gets how many times each reminder was completed on a given day.
     *
     * Useful for Day View stats like:
     *  - “How many times did I drink water today?”
     *  - “Did I complete workout 3 times?”
     *
     * @param date The day we want completion stats for.
     * @param logger Logs errors if something breaks.
     * @return A map of reminderId to completion count for that date.
     * @author fdesouza1992
     */
    suspend fun getReminderCompletionsForDate(
        date: LocalDate,
        logger: ILogger
    ): Map<String, Int> = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            logger.e(TAG, "getReminderCompletionsForDate: no logged in user.")
            return@withContext emptyMap()
        }

        val dateKey = date.toString()
        return@withContext try {
            val userRef = db.collection("users").document(uid)
            val completionsCol = userRef.collection("reminderCompletions")

            val snapshot = completionsCol
                .whereEqualTo("dateKey", dateKey)
                .get()
                .await()

            val result = mutableMapOf<String, Int>()
            for (doc in snapshot.documents) {
                val reminderId = doc.getString("reminderId") ?: continue
                val count = (doc.getLong("count") ?: 0L).toInt()
                result[reminderId] = count
            }
            result
        } catch (e: Exception) {
            logger.e(TAG, "getReminderCompletionsForDate failed for $dateKey", e)
            emptyMap()
        }
    }

    /**
     * Returns total number of reminder completions across all time.
     *
     * We read every document inside `reminderCompletions` and sum the `count` values. Great for showing progress in "My Journey" or achievement screens.
     * If something goes wrong, we return 0 instead of crashing the app.
     *
     * @param logger For logging issues if Firestore read fails.
     * @return The total count of completions across all reminders.
     * @author fdesouza1992
     */
    suspend fun getTotalReminderCompletions(
        logger: ILogger
    ): Long = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            logger.e(TAG, "getTotalReminderCompletions: no logged in user.")
            return@withContext 0L
        }

        return@withContext try {
            val userRef = db.collection("users").document(uid)
            val completionsCol = userRef.collection("reminderCompletions")
            val snapshot = completionsCol.get().await()

            snapshot.documents.fold(0L) { acc, doc ->
                acc + (doc.getLong("count") ?: 0L)
            }
        } catch (e: Exception) {
            logger.e(TAG, "getTotalReminderCompletions failed", e)
            0L
        }
    }
}