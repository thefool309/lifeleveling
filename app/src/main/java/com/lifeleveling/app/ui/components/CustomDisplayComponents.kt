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
import androidx.compose.runtime.collectAsState
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
import com.lifeleveling.app.data.LocalUserManager
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
 * @param showLevelTip The bool that controls showing the tooltip window
 * @param modifier Add in a weight or a size for how much of the screen the display takes up
 *
 * @author Elyseia, fdesouza1992
 */
@Composable
fun LevelAndProgress(
    modifier: Modifier = Modifier,// add a weight for how much of the page or a size
    showLevelTip: MutableState<Boolean>,
) {
    val userManager = LocalUserManager.current
    val userState by userManager.uiState.collectAsState()

    val level = userState.userBase?.level ?: 0
    val currentExp = userState.userBase?.currentXp ?: 0.0
    val formattedExp = String.format("%.2f", currentExp)
    val expToNextLevel = userState.xpToNextLevel

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
 * Display of the equipment boxes
 * @param modifier Recommended passed in variables are .align(Alignment.TopStart).zIndex(1f)
 *
 * @author Elyseia
 */
@Composable
fun EquipmentDisplay(
    modifier: Modifier = Modifier,
) {
    val userManager = LocalUserManager.current
    val userState by userManager.uiState.collectAsState()

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
 * A display of ther user's health in two forms: a number representation and a progress bar based on the percentage between current helath and max health.
 * @param showHealthTip A boolean tht controls if the health tooltip overlay will display
 * @author fdesouza1992, Elyseia
 */
@Composable
fun HealthDisplay(
    modifier: Modifier = Modifier,
    showHealthTip: MutableState<Boolean>,
) {
    val userManager = LocalUserManager.current
    val userState by userManager.uiState.collectAsState()

    val currentHealth = userState.userBase?.currHealth ?: 0
    val maxHealth = userState.maxHealth

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