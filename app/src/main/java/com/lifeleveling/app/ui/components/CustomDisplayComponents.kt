package com.lifeleveling.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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