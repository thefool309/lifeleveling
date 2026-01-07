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
    val formattedExp = String.format("%.2f", currentExp)

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
            reminders = repo.getRemindersForDay(date, logger)
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
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                reminders.forEach { reminder ->
                    DailyReminderRow(reminder = reminder, logger = logger)
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
    // How many “slots” we should show for today (1, 4, etc.)
    val checkboxCount = calculateDailySlots(reminder)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Icon + title (left side)
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

            Text(
                text = reminder.title,
                style = AppTheme.textStyles.Default,
                color = AppTheme.colors.Gray
            )
        }

        // Checkboxes (right side)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            repeat(checkboxCount) { index ->
                var checked by remember(reminder.reminderId, index) {
                    mutableStateOf(false)
                }

                CustomCheckbox(
                    checked = checked,
                    onCheckedChange = { new ->
                        checked = new
                        // TODO: hook this up to repo.setReminderCompleted / per-slot tracking
                        logger.d(
                            "Reminders",
                            "Clicked checkbox $index for reminder ${reminder.reminderId}"
                        )
                    },
                    size = 18.dp,
                )
            }
        }
    }
}

/**
 * Decide how many checkboxes to show for a reminder on a given day.
 *
 * • Every N hours → 24 / N slots (so every 6 hours = 4 checkboxes)
 * • Every N days → N checkboxes (simple mapping for now)
 * • Every N weeks / months → N checkboxes (we can refine this to a bit more specific later)
 * • One-off / simple daily → 1 checkbox.
 */
private fun calculateDailySlots(reminder: Reminders): Int {
    val hours = reminder.timesPerHour
    val perDay = reminder.timesPerDay
    val perMonth = reminder.timesPerMonth

    return when {
        hours > 0 -> (24 / hours).coerceAtLeast(1)
        perDay > 0 -> perDay
        perMonth > 0 -> perMonth
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