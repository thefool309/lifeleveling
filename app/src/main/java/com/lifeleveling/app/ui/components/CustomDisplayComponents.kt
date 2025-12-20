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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.lifeleveling.app.data.FirestoreRepository
import com.lifeleveling.app.ui.models.StatsUi
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.ILogger
import com.lifeleveling.app.data.Reminders
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/*
Reusable components that will appear on multiple screens
fun found in this file
* LevelAndProgress
* EquipmentDisplay
 */

/**
 * Level and Experience Display
 *
 * It will load the current user from Firestore, build a [StatsUi], and then show the level / XP bar using that snapshot.
 * While it is loading, a small progress indicator is shown.
 *
 * @param showLevelTip The bool that controls showing the tooltip window
 * @param statsUi Optional stats model so when null, stats are fetched from Firestore
 * @param repo Used to load the current user when [statsUi] is null.
 * @param logger Used for reporting any errors while loading stats from Firestore.
 *
 * @author Elyseia, fdesouza1992
 *
 */
@Composable
fun LevelAndProgress(
    modifier: Modifier = Modifier,// add a weight for how much of the page or a size
    showLevelTip: MutableState<Boolean>,
    statsUi: StatsUi? = null,
    repo: FirestoreRepository = FirestoreRepository(),
    logger: ILogger = AndroidLogger(),
) {

    var isLoading by remember { mutableStateOf(statsUi == null) }
    var uiState by remember { mutableStateOf(statsUi) }

    // Decide: use provided statsUi, or fetch from Firestore if null
    LaunchedEffect(statsUi) {
        if (statsUi != null) {
            uiState = statsUi
            isLoading = false
        } else {
            // No stats provided → fetch current user from Firestore.
            isLoading = true
            val user = repo.getCurrentUser(logger)
            uiState = user?.let { u ->
                val baseStats = u.stats
                StatsUi(
                    level            = u.level.toInt(),
                    currentXp        = u.currentXp.toInt(),
                    xpToNextLevel    = u.xpToNextLevel.toInt(),
                    usedLifePoints   = 0,
                    unusedLifePoints = u.lifePoints.toInt(),                // total life point pool
                    strength         = baseStats.strength.toInt(),
                    defense          = baseStats.defense.toInt(),
                    intelligence     = baseStats.intelligence.toInt(),
                    agility          = baseStats.agility.toInt(),
                    health           = baseStats.health.toInt()
                )
            }
            isLoading = false
        }
    }

    when {
        isLoading -> {
            // Small inline loader
            CircularProgressIndicator(
                color = AppTheme.colors.SecondaryTwo
            )
        }

        uiState != null -> {
            val data = uiState!!

            val progress = if (data.xpToNextLevel > 0) {
                (data.currentXp.toFloat() / data.xpToNextLevel.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }

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
                        text = stringResource(R.string.level, data.level),
                        color = AppTheme.colors.SecondaryOne,
                        style = AppTheme.textStyles.HeadingThree.copy(
                            shadow = Shadow(
                                color = AppTheme.colors.DropShadow,
                                offset = Offset(2f, 2f),
                                blurRadius = 2f,
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
                    progress = data.currentXp.toFloat() / data.xpToNextLevel
                )

                // Experience Display
                Text(
                    text = stringResource(R.string.exp_display, data.currentXp, data.xpToNextLevel),
                    color = AppTheme.colors.Gray,
                    style = AppTheme.textStyles.Default,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        } else -> {}
    }
}

@Composable
fun HealthDisplay(
    //modifier: Modifier = Modifier,
    showHealthTip: MutableState<Boolean>,
    fightMeditateSwitch: MutableState<Int>,
    repo: FirestoreRepository = FirestoreRepository(),
    logger: ILogger = AndroidLogger(),
) {
    var currentHealth by remember { mutableStateOf(0) }
    var maxHealth by remember { mutableStateOf(1) }   // avoid divide-by-zero

    // Load current user once, like LevelAndProgress
    LaunchedEffect(Unit) {
        val user = repo.getCurrentUser(logger)
        if (user != null) {
            currentHealth = user.currHealth.toInt()
            maxHealth = user.maxHealth.toInt()
        } else {
            logger.e("Home", "HealthDisplay: could not load current user.")
        }
    }

    val safeMax = maxHealth.coerceAtLeast(1)
    val progress = (currentHealth.toFloat() / safeMax.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            //.weight(.2f)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {// This line of health display
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Heart
            ShadowedIcon(
                modifier = Modifier.size(20.dp),
                imageVector = ImageVector.vectorResource(R.drawable.heart),
                tint = AppTheme.colors.SecondaryThree,
                shadowOffset = Offset(4f, 4f)
            )

            // Health text
            Text(
                text = stringResource(R.string.health_display, currentHealth, maxHealth),
                style = AppTheme.textStyles.Default,
                color = AppTheme.colors.Gray
            )

            // Info pop-up button
            ShadowedIcon(
                imageVector = ImageVector.vectorResource(R.drawable.info),
                tint = AppTheme.colors.FadedGray,
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        //showHealthTip.value = !showHealthTip.value
                        if(!showHealthTip.value) {showHealthTip.value = true} else {showHealthTip.value = false}
                    }
            )
        }

        // Health progress bar
        ProgressBar(
            progress = progress,
            progressColor = AppTheme.colors.SecondaryThree
        )

        // Fight / Meditate switch
        SlidingSwitch(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            options = listOf(
                stringResource(R.string.fight),
                stringResource(R.string.meditate)
            ),
            selectedIndex = fightMeditateSwitch.value,
            onOptionSelected = { fightMeditateSwitch.value = it },
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

@Composable
fun DailyRemindersList(
    date: LocalDate,
    repo: FirestoreRepository = FirestoreRepository(),
    logger: ILogger = AndroidLogger(),
) {
    var isLoading by remember { mutableStateOf(true) }
    var reminders by remember { mutableStateOf<List<Reminders>>(emptyList()) }

    LaunchedEffect(date) {
        isLoading = true
        try {
            reminders = repo.getRemindersForDate(date, logger)
        } catch (e: Exception) {
            logger.e("Reminders", "DailyRemindersList: failed to load for $date", e)
            reminders = emptyList()
        } finally {
            isLoading = false
        }
    }

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
                    DailyReminderRow(reminder = reminder, logger = logger)

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

@Composable
private fun DailyReminderRow(
    reminder: Reminders,
    logger: ILogger,
) {
    val checkboxCount = calculateDailySlots(reminder)

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

                val dueText = reminder.startingAt?.toDate()?.let { date ->
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
                        var checked by remember(reminder.reminderId, index) { mutableStateOf(false) }

                        CustomCheckbox(
                            checked = checked,
                            onCheckedChange = { new ->
                                checked = new
                                logger.d("Reminders", "Clicked checkbox $index for reminder ${reminder.reminderId}")
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
private fun calculateDailySlots(reminder: Reminders): Int {
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

/**
 * Map stored iconName → drawable id. Falls back to the bell icon if we don’t recognize it (can be updated to the correct error icon).
 */
private fun iconResForName(iconName: String?): Int? =
    when (iconName) {
        "water_drop"     -> R.drawable.water_drop
        "bed_color"      -> R.drawable.bed_color
        "shirt_color"    -> R.drawable.shirt_color
        "med_bottle"     -> R.drawable.med_bottle
        "shower_bath"    -> R.drawable.shower_bath
        "shop_color"     -> R.drawable.shop_color
        "person_running" -> R.drawable.person_running
        "heart"          -> R.drawable.heart
        "bell"           -> R.drawable.bell
        "brain"          -> R.drawable.brain
        "document"       -> R.drawable.document
        "doctor"         -> R.drawable.doctor
        else             -> R.drawable.bell
    }