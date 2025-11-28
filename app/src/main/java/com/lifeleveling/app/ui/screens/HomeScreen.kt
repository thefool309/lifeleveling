package com.lifeleveling.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.R
import com.lifeleveling.app.data.LocalUserManager
import com.lifeleveling.app.ui.components.TestUser
import com.lifeleveling.app.ui.components.CircleButton
import com.lifeleveling.app.ui.components.EquipmentDisplay
import com.lifeleveling.app.ui.components.HealthDisplay
import com.lifeleveling.app.ui.components.HealthToolTip
import com.lifeleveling.app.ui.components.HighlightCard
import com.lifeleveling.app.ui.components.LevelAndProgress
import com.lifeleveling.app.ui.components.LifeExperienceToolTip
import com.lifeleveling.app.ui.components.ProgressBar
import com.lifeleveling.app.ui.components.ShadowedIcon
import com.lifeleveling.app.ui.components.SlidingSwitch
import com.lifeleveling.app.ui.models.StatsUi

//@Preview
@Composable
fun HomeScreen() {
    val userManager = LocalUserManager.current
    val userState by userManager.uiState.collectAsState()

    val showLevelTip = remember { mutableStateOf(false) }
    val showHealthTip = remember { mutableStateOf(false) }
    var fightMeditateSwitch = userState.userData?.fightOrMeditate ?: 0

    // Main screen pulling everything in 16.dp from edge
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.colors.Background)
            .padding(16.dp),
    ) {
        // Central Column for all elements to be stacked into
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Box of Level and Exp
            LevelAndProgress(
                showLevelTip = showLevelTip
            )

            // Middle section
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // =====================Avatar display here============================
                Box(
                    modifier = Modifier
                        .matchParentSize(),
                ) {
                    // Background Image
                    if (fightMeditateSwitch == 0){
                        Image(
                            painter = painterResource(R.drawable.dungeon_door),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .offset(x = 70.dp, y = (-10).dp)
                                .fillMaxWidth(1f)
                                .aspectRatio(1f)
                                .alpha(0.9f),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.meditation_arch),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(x = 0.dp, y = 40.dp)
                                .fillMaxWidth(1f)
                                .aspectRatio(1f)
                                .alpha(0.9f),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Text(
                        text = "Avatar",
                        color = AppTheme.colors.Gray,
                        style = AppTheme.textStyles.HeadingThree,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Equipment
                EquipmentDisplay(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .zIndex(1f),
                )

                // Circle Buttons
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .zIndex(1f),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Coins display
                    Box(
                        modifier = Modifier
                    ){
                        HighlightCard(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .wrapContentWidth(Alignment.End),
                            wrapContent = true,
                            outerPadding = 0.dp,
                            innerPadding = 8.dp,
                        ) {
                            Row(
                                modifier = Modifier
                                    .wrapContentWidth(Alignment.End),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(R.string.coins, TestUser.coins),
                                    style = AppTheme.textStyles.Default,
                                    color = AppTheme.colors.Gray
                                )

                                Image(
                                    painter = painterResource(R.drawable.coin),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    // Avatar Editing
                    CircleButton(
                        imageVector = ImageVector.vectorResource(R.drawable.head),
                        onClick = {},
                        size = 50.dp
                    )
                    // Store
                    CircleButton(
                        imageVector = ImageVector.vectorResource(R.drawable.store_basket),
                        onClick = {},
                        size = 50.dp
                    )
                    // Avatar positions
                    CircleButton(
                        imageVector = ImageVector.vectorResource(R.drawable.hand),
                        onClick = {},
                        size = 50.dp
                    )
                    // Inventory
                    CircleButton(
                        imageVector = ImageVector.vectorResource(R.drawable.backpack),
                        onClick = {},
                        size = 50.dp
                    )
                }
            }

            // Bottom health and switch
            Column(
                modifier = Modifier
//                    .weight(.2f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HealthDisplay( showHealthTip = showHealthTip )

                // Fight to Meditate Switch
                SlidingSwitch(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    options = listOf(stringResource(R.string.fight), stringResource(R.string.meditate)),
                    selectedIndex = fightMeditateSwitch,
                    onOptionSelected = { fightMeditateSwitch = it },
                )
            }
        }
    }

    // Show Tooltip Popups
    if (showLevelTip.value) {
        LifeExperienceToolTip(showLevelTip)
    }
    if (showHealthTip.value) {
        HealthToolTip(showHealthTip)
    }
}