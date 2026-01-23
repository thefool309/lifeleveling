package com.lifeleveling.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.R
import com.lifeleveling.app.data.Reminder
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.theme.iconResForName
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * Level and Experience Display
 * Takes in parameters from the user state instead of recollecting it.
 * This will allow only the pieces that change to be updated instead of recollecting all over again with every change.
 *
 * @param showLevelTip The bool that controls showing the tooltip window
 * @param modifier Add in a weight or a size for how much of the screen the display takes up
 * @param level Pass in the state handling of level from the user information
 * @param currentExp Pass in the state handling of the currentXp from the user information
 * @param expToNextLevel Pass in the state handling of the expToNextLevel from the user information
 *
 * @author Elyseia, fdesouza1992
 */
@Composable
fun LevelAndProgress(
    modifier: Modifier = Modifier,// add a weight for how much of the page or a size
    showLevelTip: MutableState<Boolean>,
    level: Long,
    currentExp: Double,
    expToNextLevel: Long,
) {
    val formattedExp = String.format(Locale.getDefault(), "%.2f", currentExp)

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Level and info icon
        Row(
            modifier = Modifier
                .align(Alignment.Start),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Level Display
            Text(
                text = stringResource(R.string.level, level),
                color = AppTheme.colors.SecondaryOne,
                style = AppTheme.textStyles.HeadingThree.copy(
                    shadow = Shadow(
                        color = AppTheme.colors.DropShadow,
                        offset = Offset(3f, 4f),
                        blurRadius = 6f,
                    )
                ),
            )
            // Info Icon
            ShadowedIcon(
                imageVector = ImageVector.vectorResource(R.drawable.info),
                tint = AppTheme.colors.FadedGray,
                modifier = Modifier
                    .size(20.dp)
                    .offset(y = 9.74.dp)
                    .clickable {
                        showLevelTip.value = !showLevelTip.value
                    }
            )
        }

        // Progress Bar
        ProgressBar(
            progress = currentExp.toFloat() / expToNextLevel.toFloat(),
        )

        // Experience Display
        Text(
            text = stringResource(R.string.exp_display, formattedExp, expToNextLevel),
            color = AppTheme.colors.Gray,
            style = AppTheme.textStyles.Default,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

/**
 * A display of the user's health in two forms:
 * a number representation and a progress bar based on the percentage between current health and max health.
 * Takes in parameters from the user state instead of recollecting it.
 * This will allow only the pieces that change to be updated instead of recollecting all over again with every change.
 * @param showHealthTip A boolean tht controls if the health tooltip overlay will display
 * @param currentHealth Pass in the state handling of the currentHealth value from the user state
 * @param maxHealth Pass in the state handling of the maxHealth value from the user state
 * @author fdesouza1992, Elyseia
 */
@Composable
fun HealthDisplay(
    modifier: Modifier = Modifier,
    showHealthTip: MutableState<Boolean>,
    currentHealth: Long,
    maxHealth: Long,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // This line of health display
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Heart
            ShadowedIcon(
                modifier = Modifier
                    .size(20.dp),
                imageVector = ImageVector.vectorResource(R.drawable.heart),
                tint = AppTheme.colors.SecondaryThree,
                shadowOffset = Offset(4f, 4f)
            )
            // Health Text
            Text(
                text = stringResource(R.string.health_display, currentHealth, maxHealth),
                style = AppTheme.textStyles.Default,
                color = AppTheme.colors.Gray
            )
            // Info Pop-up Button
            ShadowedIcon(
                imageVector = ImageVector.vectorResource(R.drawable.info),
                tint = AppTheme.colors.FadedGray,
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        showHealthTip.value = !showHealthTip.value
                    }
            )
        }

        // Progress bar
        ProgressBar(
            progress = currentHealth.toFloat() / maxHealth,
            progressColor = AppTheme.colors.SecondaryThree
        )
    }
}


/**
 * Display of the equipment boxes
 * @param modifier Recommended passed in variables are .align(Alignment.TopStart).zIndex(1f)
 *
 * @author Elyseia
 */
@Composable
fun EquipmentDisplay(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Helmet
        Column(
            modifier = Modifier
                .clickable { },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.helmet),
                style = AppTheme.textStyles.Small,
                color = AppTheme.colors.Gray
            )
            HighlightCard(
                modifier = Modifier
                    .wrapContentWidth(),
                wrapContent = true,
                outerPadding = 0.dp,
                innerPadding = 4.dp,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.helmet),
                    contentDescription = null,
                    tint = AppTheme.colors.Background,
                    modifier = Modifier.size(50.dp),
                )
            }
        }
        // Armor
        Column(
            modifier = Modifier
                .clickable { },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.armor),
                style = AppTheme.textStyles.Small,
                color = AppTheme.colors.Gray
            )
            HighlightCard(
                modifier = Modifier
                    .wrapContentWidth(),
                wrapContent = true,
                outerPadding = 0.dp,
                innerPadding = 4.dp,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.armor),
                    contentDescription = null,
                    tint = AppTheme.colors.Background,
                    modifier = Modifier.size(50.dp),
                )
            }
        }
        // Weapon
        Column(
            modifier = Modifier
                .clickable { },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.weapon),
                style = AppTheme.textStyles.Small,
                color = AppTheme.colors.Gray
            )
            HighlightCard(
                modifier = Modifier
                    .wrapContentWidth(),
                wrapContent = true,
                outerPadding = 0.dp,
                innerPadding = 4.dp,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.sword),
                    contentDescription = null,
                    tint = AppTheme.colors.Background,
                    modifier = Modifier.size(50.dp),
                )
            }
        }
        // Accessory 1
        Column(
            modifier = Modifier
                .clickable { },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.accessory),
                style = AppTheme.textStyles.Small,
                color = AppTheme.colors.Gray
            )
            HighlightCard(
                modifier = Modifier
                    .wrapContentWidth(),
                wrapContent = true,
                outerPadding = 0.dp,
                innerPadding = 4.dp,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.accessory),
                    contentDescription = null,
                    tint = AppTheme.colors.Background,
                    modifier = Modifier.size(50.dp),
                )
            }
        }
        // Accessory 2
        Column(
            modifier = Modifier
                .clickable { },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.accessory),
                style = AppTheme.textStyles.Small,
                color = AppTheme.colors.Gray
            )
            HighlightCard(
                modifier = Modifier
                    .wrapContentWidth(),
                wrapContent = true,
                outerPadding = 0.dp,
                innerPadding = 4.dp,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.accessory),
                    contentDescription = null,
                    tint = AppTheme.colors.Background,
                    modifier = Modifier.size(50.dp),
                )
            }
        }
    }
}

/**
 * Shows all reminders for a specific day in the Day View screen.
 *
 * What this composable does:
 * - Loads reminders for the given `date` from Firestore
 * - Also loads how many times each reminder was completed that day
 * - Shows a loading spinner while fetching
 * - Shows an empty message if there are no reminders
 * - Otherwise displays a scrollable list of `DailyReminderRow`s
 *
 * Basically: You give it a date, it builds the UI for that day’s reminders.
 * Each reminder in the list will have its own row with checkboxes.
 *
 * @param date The day we want to display reminders for.
 * @param repo FirestoreRepository used to load reminders + completion counts.
 * @param logger For logging errors instead of crashing the UI.
 * @author fdesouza1992
 */
@Composable
fun DailyRemindersList(
    date: LocalDate,
    reminders: List<Reminder>,
    completionsByReminderId: Map<String, Int>,
    isLoading: Boolean,
    onChecked: (reminderId: String, reminderTitle: String, increment: Boolean) -> Unit
) {
    when {
        isLoading -> {
            // Small inline loader so the user sees *something*
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = AppTheme.colors.SecondaryTwo
                )
            }
        }

        reminders.isEmpty() -> {
            // Empty-state text – matches Figma “blank day” vibe
            Text(
                text = stringResource(R.string.no_reminders_for_day),
                // add this string in strings.xml: "No reminders for this day yet."
                style = AppTheme.textStyles.Default,
                color = AppTheme.colors.FadedGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                reminders.forEachIndexed { index, reminder ->
                    val initialCompletedSlots = completionsByReminderId[reminder.reminderId] ?: 0

                    DailyReminderRow(
                        reminder = reminder,
                        date = date,
                        initialCompletedSlots = initialCompletedSlots,
                        onChecked = onChecked
                        )

                    // Separator line
                    if (index != reminders.lastIndex) {
                        SeparatorLine(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * A single reminder row inside the daily list — includes the icon, title, starting date/time, and the checkboxes that track completion.
 *
 * Each checkbox represents one "repeat slot" for that reminder.
 * Example:
 *  - Drink water every 2 hours → multiple checkmarks in a day
 *  - Take medication once → probably just one checkbox
 *
 * User interactions:
 * - Checking a box calls `incrementReminderCompletionForDate`
 * - Unchecking a box calls `decrementReminderCompletionForDate`
 * - Completion count is remembered so UI shows past progress
 *
 * @param reminder The reminder being displayed in this row.
 * @param date The day we're marking completions for.
 * @param initialCompletedSlots How many boxes are already checked for that day.
 * @param repo FirestoreRepository used to increment/decrement counts.
 * @param logger For debugging if Firestore calls fail.
 * @author fdesouza1992
 */
@Composable
private fun DailyReminderRow(
    reminder: Reminder,
    date: LocalDate,
    initialCompletedSlots: Int,
    onChecked: (reminderId: String, reminderTitle: String, increment: Boolean) -> Unit,
) {
    val checkboxCount = calculateDailySlots(reminder)
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // LEFT: icon + (title + starting line)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val iconRes = iconResForName(reminder.iconName)
            if (iconRes != null) {
                ShadowedIcon(
                    imageVector = ImageVector.vectorResource(iconRes),
                    tint = Color.Unspecified,
                    modifier = Modifier.size(30.dp)
                )
            }

            Column {
                Text(
                    text = reminder.title,
                    style = AppTheme.textStyles.Default,
                    color = AppTheme.colors.Gray
                )

                val dueText = reminder.dueAt?.toDate()?.let { date ->
                    // Use whatever formatting style you prefer
                    val local = date.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime()

                    val month = local.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    val day = local.dayOfMonth
                    val year = local.year

                    val hour12 = ((local.hour + 11) % 12) + 1
                    val ampm = if (local.hour >= 12) "PM" else "AM"
                    val minute = local.minute.toString().padStart(2, '0')

                    "Starting: $month $day, $year • $hour12:$minute $ampm"
                } ?: "Starting: —"

                Text(
                    text = dueText,
                    style = AppTheme.textStyles.Small,
                    color = AppTheme.colors.FadedGray
                )
            }
        }

        // RIGHT: checkboxes
        val rows = (0 until checkboxCount).toList().chunked(4)

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            rows.forEach { rowIndices ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    rowIndices.forEach { index ->
                        // A given slot is "already completed" if its index is < initialCompletedSlots.
                        var checked by remember(reminder.reminderId, date, index) {
                            mutableStateOf(index < initialCompletedSlots)
                        }

                        CustomCheckbox(
                            checked = checked,
                            onCheckedChange = { new ->
                                val increment = new && !checked
                                val decrement = !new && !checked

                                if (increment || decrement) {
                                    checked = new
                                    onChecked(reminder.reminderId, reminder.title, increment)
                                }
                            },
                            size = 18.dp,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Calculates how many "time slots" (checkboxes) should be shown for a reminder on a single day in the calendar view.
 *
 * The count is based on how often the reminder repeats:
 *
 * - Times per minute:
 *   → (24 * 60) / minutes
 *   → Example: every 30 minutes = 48 slots
 *   → Hard-capped at 24 to avoid creating an insane amount of checkboxes.
 *
 * - Times per hour:
 *   → 24 / hours
 *   → Example: every 6 hours = 4 slots.
 *
 * - Times per day:
 *   → Uses the value directly (minimum of 1).
 *   → Example: 3 times per day = 3 slots.
 *
 * - Times per month:
 *   → Uses the value directly (minimum of 1).
 *
 * - Fallback:
 *   → If no repeat values are set, default to 1 slot.
 *
 * @param reminder The reminder containing repeat frequency values.
 * @return The number of daily slots (checkboxes) to render.
 * @author fdesouza1992
 */
private fun calculateDailySlots(reminder: Reminder): Int {
    val mins = reminder.timesPerMinute
    val hours = reminder.timesPerHour
    val perDay = reminder.timesPerDay
    val perMonth = reminder.timesPerMonth

    return when {
        mins > 0 -> ((24*60)/mins).coerceAtLeast(1).coerceAtMost(24)        // adding a cap to avoid the creation of 144 checkboxes
        hours > 0 -> (24 / hours).coerceAtLeast(1)
        perDay > 0 -> perDay.coerceAtLeast(1)
        perMonth > 0 -> perMonth.coerceAtLeast(1)
        else -> 1
    }
}