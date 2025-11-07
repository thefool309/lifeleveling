package com.lifeleveling.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.theme.AppTextStyles
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.components.CustomButton
import com.lifeleveling.app.ui.components.HighlightCard
import com.lifeleveling.app.ui.components.ShadowedIcon
import com.lifeleveling.app.ui.components.StatsToolTip
import com.lifeleveling.app.ui.components.LevelAndProgress
import com.lifeleveling.app.ui.components.LifeExperienceToolTip
import com.lifeleveling.app.ui.models.StatsUi
import com.lifeleveling.app.ui.models.EditedStats
import kotlinx.coroutines.launch

@Composable
fun StatsScreen(
    uiState: StatsUi,
    onCancel: () -> Unit = { println("Cancel pressed") },
    onConfirm: () -> Unit = { println("Confirm pressed") },
    onCommit: (EditedStats) -> Unit = {},
    resetSignal: Int = 0,   // bump this from the Route to re-seed local state
) {
    val progress = (uiState.currentXp.toFloat() / uiState.xpToNextLevel.toFloat()).coerceIn(0f, 1f)

    // Small dialogs
    val showHelpDialog = remember { mutableStateOf(false) }
    val showStatsDialog = remember { mutableStateOf(false) }

    // ---- SESSION STATE ----
    // Start-of-session: user has 0 used points and a pool of uiState.unusedLifePoints to spend.

    var usedPoints by remember(resetSignal) { mutableStateOf(0) }
    val remainingPoints = (uiState.unusedLifePoints - usedPoints).coerceAtLeast(0)

    // Per-stat values (one set only; re-seeded when resetSignal changes)
    val strength     = remember(resetSignal) { mutableStateOf(uiState.strength) }
    val defense      = remember(resetSignal) { mutableStateOf(uiState.defense) }
    val intelligence = remember(resetSignal) { mutableStateOf(uiState.intelligence) }
    val agility      = remember(resetSignal) { mutableStateOf(uiState.agility) }
    val health       = remember(resetSignal) { mutableStateOf(uiState.health) }

    // Minimum (floor) values from the snapshot, keyed by label resource id (avoid string key issues)
    val baseByLabel = mapOf(
        R.string.strength     to uiState.strength,
        R.string.defense      to uiState.defense,
        R.string.intelligence to uiState.intelligence,
        R.string.agility      to uiState.agility,
        R.string.health       to uiState.health,
    )

    // Pack items for rendering
    val statItems = listOf(
        StatItem(R.drawable.sword,          R.string.strength,     AppTheme.colors.BrandOne,      strength),
        StatItem(R.drawable.shield,         R.string.defense,      AppTheme.colors.BrandTwo,      defense),
        StatItem(R.drawable.brain,          R.string.intelligence, AppTheme.colors.SecondaryOne,  intelligence),
        StatItem(R.drawable.person_running, R.string.agility,      AppTheme.colors.SecondaryTwo,  agility),
        StatItem(R.drawable.heart,          R.string.health,       AppTheme.colors.SecondaryThree,health)
    )
    // ---- END SESSION STATE ----

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppTheme.colors.Background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {

                LevelAndProgress(
                    modifier = Modifier,
                    showLevelTip = showHelpDialog,
                )

                // Header + counters
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            stringResource(R.string.stats),
                            color = AppTheme.colors.SecondaryOne,
                            style = AppTheme.textStyles.HeadingThree.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
                                )
                            )
                        )
                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(R.drawable.info),
                            tint = AppTheme.colors.FadedGray,
                            modifier = Modifier
                                .size(20.dp)
                                .offset(y = 9.74.dp)
                                .clickable { showStatsDialog.value = !showStatsDialog.value }
                        )
                    }

                    Text(
                        text = stringResource(R.string.LPUsed, usedPoints, uiState.unusedLifePoints),
                        color = AppTheme.colors.Gray,
                        style = AppTheme.textStyles.Default.copy(
                            shadow = Shadow(
                                color = AppTheme.colors.DropShadow,
                                offset = Offset(3f, 4f),
                                blurRadius = 6f,
                            )
                        )
                    )

                    Text(
                        text = stringResource(R.string.LPRemaining, remainingPoints),
                        color = AppTheme.colors.Gray,
                        style = AppTheme.textStyles.Default.copy(
                            shadow = Shadow(
                                color = AppTheme.colors.DropShadow,
                                offset = Offset(3f, 4f),
                                blurRadius = 6f,
                            )
                        )
                    )
                }

                // Stats list
                HighlightCard(
                    modifier = Modifier.fillMaxWidth(),
                    outerPadding = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        statItems.forEachIndexed { index, (iconRes, labelRes, color, statValue) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Left: icon + label
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    ShadowedIcon(
                                        imageVector = ImageVector.vectorResource(iconRes),
                                        tint = color,
                                        modifier = Modifier.size(38.dp),
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(labelRes),
                                        color = AppTheme.colors.Gray,
                                        style = AppTheme.textStyles.HeadingSix
                                    )
                                }

                                // Right: − [value] +
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // minus
                                    Image(
                                        painter = painterResource(id = R.drawable.minus),
                                        contentDescription = "Decrease $labelRes",
                                        colorFilter = ColorFilter.tint(AppTheme.colors.FadedGray),
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clickable(
                                                indication = null,
                                                interactionSource = remember { MutableInteractionSource() },
                                                onClick = {
                                                    val floor = baseByLabel[labelRes] ?: 0
                                                    if (statValue.value > floor) {
                                                        statValue.value -= 1
                                                        usedPoints = (usedPoints - 1).coerceAtLeast(0)
                                                        // remainingPoints is derived; no manual change
                                                    }
                                                }
                                            )
                                            .padding(horizontal = 8.dp)
                                    )

                                    // value
                                    Box(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = statValue.value.toString(),
                                            color = AppTheme.colors.SecondaryOne,
                                            style = AppTheme.textStyles.HeadingFour,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }

                                    // plus
                                    Image(
                                        painter = painterResource(id = R.drawable.plus),
                                        contentDescription = "Increase $labelRes",
                                        colorFilter = ColorFilter.tint(AppTheme.colors.SecondaryTwo),
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clickable(
                                                indication = null,
                                                interactionSource = remember { MutableInteractionSource() },
                                                onClick = {
                                                    if (remainingPoints > 0) {
                                                        statValue.value += 1
                                                        usedPoints += 1
                                                        // remainingPoints is derived; no manual change
                                                    }
                                                }
                                            )
                                            .padding(horizontal = 8.dp)
                                    )
                                }
                            }

                            // divider
                            if (index < statItems.lastIndex) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.separator_line),
                                    tint = AppTheme.colors.FadedGray,
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CustomButton(
                        width = 122.dp,
                        content = {
                            Text(
                                stringResource(R.string.Cancel),
                                style = AppTextStyles.HeadingSix,
                                color = AppTheme.colors.Background
                            )
                        },
                        onClick = { onCancel() },
                        backgroundColor = AppTheme.colors.Error,
                    )

                    Spacer(modifier = Modifier.width(32.dp))

                    CustomButton(
                        width = 122.dp,
                        content = {
                            Text(
                                stringResource(R.string.Confrim),
                                style = AppTextStyles.HeadingSix,
                                color = AppTheme.colors.Background
                            )
                        },
                        onClick = {
                            onCommit(
                                EditedStats(
                                    strength = strength.value,
                                    defense = defense.value,
                                    intelligence = intelligence.value,
                                    agility = agility.value,
                                    health = health.value,
                                    usedPoints = usedPoints,
                                    remainingPoints = remainingPoints
                                )
                            )
                            onConfirm()
                        },
                        backgroundColor = AppTheme.colors.SecondaryTwo,
                    )
                }
            }
        }

        if (showHelpDialog.value) {
            LifeExperienceToolTip(showHelpDialog)
        }
        if (showStatsDialog.value) {
            StatsToolTip(showStatsDialog)
        }
    }
}

@Composable
fun StatsScreenRoute(
    repo: com.lifeleveling.app.data.FirestoreRepository = com.lifeleveling.app.data.FirestoreRepository(),
    logger: com.lifeleveling.app.util.ILogger = com.lifeleveling.app.util.ILogger.DEFAULT
) {
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var user by remember { mutableStateOf<com.lifeleveling.app.data.Users?>(null) }

    // NEW: a knob we can twist to force the child screen to reset its remembered fields
    var resetSignal by remember { mutableStateOf(0) }

    // Initial load
    androidx.compose.runtime.LaunchedEffect(Unit) {
        error = null
        isLoading = true
        user = repo.getCurrentUser(logger)
        isLoading = false
        if (user == null) error = "Could not load user profile."
    }

    // If still no user (very first frame), show a centered spinner
    if (user == null) {
        androidx.compose.material3.CircularProgressIndicator()
        return
    }

    val u = user!!

    // Build UI model from the current snapshot
    val baseStats = u.stats
    val baseUsed  = (baseStats.strength + baseStats.defense + baseStats.intelligence + baseStats.agility + baseStats.health).toInt()
    val lifePool  = u.lifePoints.toInt()

    val uiState = StatsUi(
        level           = u.level.toInt(),
        currentXp       = u.currentXp.toInt(),
        xpToNextLevel   = u.xpToNextLevel.toInt(),
        usedLifePoints  = 0,                                    // Starts the session with used = 0
        unusedLifePoints= lifePool,                             // total spendable life points for the session
        strength        = baseStats.strength.toInt(),
        defense         = baseStats.defense.toInt(),
        intelligence    = baseStats.intelligence.toInt(),
        agility         = baseStats.agility.toInt(),
        health          = baseStats.health.toInt()
    )

    // Keep content mounted; overlay loading/error
    Box(modifier = Modifier.fillMaxSize()) {
        StatsScreen(
            uiState = uiState,
            resetSignal = resetSignal,                 // <-- drives child state reset
            onCancel = {
                // No network call → just reset child state to the current snapshot
                resetSignal++                          // forces StatsScreen to re-seed from uiState
            },
            onConfirm = { /* optional toast/snackbar */ },
            onCommit = { edited ->
                val newStats = com.lifeleveling.app.data.Stats(
                    strength     = edited.strength.toLong(),
                    defense      = edited.defense.toLong(),
                    intelligence = edited.intelligence.toLong(),
                    agility      = edited.agility.toLong(),
                    health       = edited.health.toLong()
                )
                val newLifePoints = edited.remainingPoints.toLong()

                // Save then soft-refresh snapshot (overlay loader, no unmount)
                scope.launch {
                    isLoading = true
                    val okStats = repo.setStats(newStats, logger)
                    val okLP    = repo.setLifePoints(newLifePoints, logger)
                    if (okStats && okLP) {
                        user = repo.getCurrentUser(logger)   // refresh snapshot
                        resetSignal++                         // and reset child UI to the new snapshot
                        error = null
                    } else {
                        error = "Failed to save stats."
                    }
                    isLoading = false
                }
            }
        )

        if (isLoading) {
            // Simple inline overlay
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator()
            }
        }

        if (error != null) {
            // Non-blocking error text which can be swapped for a snackbar if preferred
            androidx.compose.material3.Text(
                text = error!!,
                color = AppTheme.colors.Error
            )
        }
    }
}


data class StatItem(
    val icon: Int,
    val label: Int,
    val color: androidx.compose.ui.graphics.Color,
    val value: MutableState<Int>
)