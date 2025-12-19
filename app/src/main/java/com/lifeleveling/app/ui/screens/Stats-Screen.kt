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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.R
import com.lifeleveling.app.data.LocalUserManager
import com.lifeleveling.app.ui.theme.AppTextStyles
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.components.CustomButton
import com.lifeleveling.app.ui.components.HighlightCard
import com.lifeleveling.app.ui.components.ShadowedIcon
import com.lifeleveling.app.ui.components.StatsToolTip
import com.lifeleveling.app.ui.components.LevelAndProgress
import com.lifeleveling.app.ui.components.LifeExperienceToolTip

@Composable
fun StatsScreen() {
    val userManager = LocalUserManager.current
    val userState by userManager.uiState.collectAsState()

    // Small dialogs
    val showHelpDialog = remember { mutableStateOf(false) }
    val showStatsDialog = remember { mutableStateOf(false) }

    // ---- SESSION STATE ----
    // Start-of-session: user has 0 used points and a pool of uiState.unusedLifePoints to spend.

    var usedPoints by remember(userState.userBase?.lifePointsUsed) {
        mutableStateOf(userState.userBase?.lifePointsUsed ?: 0)
    }
    val totalPoints = userState.userBase?.lifePointsTotal ?: 0
    val remainingPoints = totalPoints - usedPoints
    var resetKey by remember { mutableStateOf(0) }

    // Per-stat values (one set only; re-seeded when resetSignal changes)
    val baseStats = userState.userBase?.stats
    val strength     = remember(baseStats?.strength, resetKey) { mutableStateOf(baseStats?.strength ?: 0) }
    val defense      = remember(baseStats?.defense, resetKey) { mutableStateOf(baseStats?.defense ?: 0) }
    val intelligence = remember(baseStats?.intelligence, resetKey) { mutableStateOf(baseStats?.intelligence ?: 0) }
    val agility      = remember(baseStats?.agility, resetKey) { mutableStateOf(baseStats?.agility ?: 0) }
    val health       = remember(baseStats?.health, resetKey) { mutableStateOf(baseStats?.health ?: 0) }

    // Minimum (floor) values from the snapshot, keyed by label resource id (avoid string key issues)
    val baseByLabel = mapOf(
        R.string.strength     to userState.userBase?.stats?.strength,
        R.string.defense      to userState.userBase?.stats?.defense,
        R.string.intelligence to userState.userBase?.stats?.intelligence,
        R.string.agility      to userState.userBase?.stats?.agility,
        R.string.health       to userState.userBase?.stats?.health,
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
                    showLevelTip = showHelpDialog,
                    level = userState.userBase?.level ?: 1,
                    currentExp = userState.userBase?.currentXp ?: 0.0,
                    expToNextLevel = userState.xpToNextLevel,
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
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f,
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
                        text = stringResource(R.string.LPUsed, usedPoints, userState.userBase?.lifePointsTotal ?: 0),
                        color = AppTheme.colors.Gray,
                        style = AppTheme.textStyles.Default.copy(
                            shadow = Shadow(
                                color = AppTheme.colors.DropShadow,
                                offset = Offset(2f, 2f),
                                blurRadius = 2f,
                            )
                        )
                    )

                    Text(
                        text = stringResource(R.string.LPRemaining, remainingPoints),
                        color = AppTheme.colors.Gray,
                        style = AppTheme.textStyles.Default.copy(
                            shadow = Shadow(
                                color = AppTheme.colors.DropShadow,
                                offset = Offset(2f, 2f),
                                blurRadius = 2f,
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
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        statItems.forEachIndexed { index, (iconRes, labelRes, color, statValue) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                //icons on the left and the text
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    ShadowedIcon(
                                        imageVector = ImageVector.vectorResource(iconRes),
                                        tint = color,
                                        modifier = Modifier.size(36.dp),
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(labelRes),
                                        color = AppTheme.colors.Gray,
                                        style = AppTheme.textStyles.HeadingSix
                                    )
                                }

                                // stat controls on the right
                                // Right: − [value] +
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // minus button
                                    Image(
                                        painter = painterResource(id = R.drawable.minus),
                                        contentDescription = "Decrease $labelRes",
                                        colorFilter = ColorFilter.tint(AppTheme.colors.FadedGray),
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clickable(
                                                indication = null,
                                                interactionSource = remember { MutableInteractionSource() },
                                                onClick = {
                                                    val floor = baseByLabel[labelRes] ?: 0
                                                    if (statValue.value > floor) {
                                                        statValue.value -= 1
                                                        usedPoints -= 1
                                                        // remainingPoints is derived; no manual change
                                                    }
                                                }
                                            )
                                    )

                                    // value
                                    Box(
                                        modifier = Modifier
                                            .weight(1f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = statValue.value.toString(),
                                            color = AppTheme.colors.SecondaryOne,
                                            style = AppTheme.textStyles.HeadingSix,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }

                                    // plus
                                    Image(
                                        painter = painterResource(id = R.drawable.plus),
                                        contentDescription = "Increase $labelRes",
                                        colorFilter = ColorFilter.tint(AppTheme.colors.SecondaryTwo),
                                        modifier = Modifier
                                            .size(28.dp)
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
                        onClick = {
                            resetKey++
                        },
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
                            userManager.updateStats(
                                strength = strength.value,
                                defense = defense.value,
                                intelligence = intelligence.value,
                                agility = agility.value,
                                health = health.value,
                                usedPoints = usedPoints,
                            )
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


data class StatItem(
    val icon: Int,
    val label: Int,
    val color: Color,
    val value: MutableState<Long>
)