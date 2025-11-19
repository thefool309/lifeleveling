package com.lifeleveling.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.components.AddStreak
import com.lifeleveling.app.ui.components.TestUser
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.components.AllBadgesDisplay
import com.lifeleveling.app.ui.components.BadgesToolTip
import com.lifeleveling.app.ui.components.HighlightCard
import com.lifeleveling.app.ui.components.LazyColumnFadeEdges
import com.lifeleveling.app.ui.components.LevelAndProgress
import com.lifeleveling.app.ui.components.LifeExperienceToolTip
import com.lifeleveling.app.ui.components.ProgressBar
import com.lifeleveling.app.ui.components.SeparatorLine
import com.lifeleveling.app.ui.components.ShadowedIcon
import com.lifeleveling.app.ui.components.ShowStreak
import com.lifeleveling.app.ui.components.SingleBadgeDisplay
import com.lifeleveling.app.ui.components.StreaksToolTip
import com.lifeleveling.app.ui.theme.resolveEnumColor

@Preview
@Composable
fun StreaksScreen(
    navController: NavController? = null,
) {
    // Pop up tips
    val showLevelTip = remember { mutableStateOf(false) }
    val showStreaksTip = remember { mutableStateOf(false) }
    val showBadgesTip = remember { mutableStateOf(false) }
    val showBadge = remember { mutableStateOf(false) }
    val showStreakInfo = remember { mutableStateOf(false) }
    val badgeToDisplay = remember { mutableStateOf(TestUser.allBadges[1])}
    val streakToShow = remember { mutableStateOf(TestUser.weeklyStreaks[0])}
    val addWeekStreak = remember { mutableStateOf(false) }
    val addMonthStreak = remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()

    // Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.colors.Background)
            .padding(16.dp)
    ) {
        // Inside with scrolling if needed
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Level and exp
            LevelAndProgress(
                showLevelTip = showLevelTip
            )
            // Streaks title
            Row(
                modifier = Modifier
                    .align(Alignment.Start),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.streaks),
                    color = AppTheme.colors.SecondaryOne,
                    style = AppTheme.textStyles.HeadingThree.copy(
                        shadow = Shadow(
                            color = AppTheme.colors.DropShadow,
                            offset = Offset(2f, 2f),
                            blurRadius = 2f,
                        )
                    ),
                )
                ShadowedIcon(
                    imageVector = ImageVector.vectorResource(R.drawable.info),
                    tint = AppTheme.colors.FadedGray,
                    modifier = Modifier
                        .size(20.dp)
                        .offset(y = 9.74.dp)
                        .clickable {
                            showStreaksTip.value = !showStreaksTip.value
                        }
                )
            }

            // Weekly Goals
            HighlightCard(
                modifier = Modifier,
                outerPadding = 0.dp,
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ){
                    // Title
                    Text(
                        text = stringResource(R.string.this_week),
                        color = AppTheme.colors.SecondaryThree,
                        style = AppTheme.textStyles.HeadingFive.copy(
                            shadow = Shadow(
                                color = AppTheme.colors.DropShadow,
                                offset = Offset(2f, 2f),
                                blurRadius = 2f,
                            )
                        ),
                    )
                    // Separator
                    SeparatorLine(color = AppTheme.colors.SecondaryTwo)

                    // Display of streaks
                    TestUser.weeklyStreaks.forEach { streak ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clickable {
                                    streakToShow.value = streak
                                    showStreakInfo.value = true
                                },
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ){
                                    ShadowedIcon(
                                        imageVector = ImageVector.vectorResource(streak.reminder.icon),
                                        tint = if (streak.reminder.color == null) Color.Unspecified
                                                else resolveEnumColor(streak.reminder.color),
                                        modifier = Modifier
                                            .size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = streak.reminder.name,
                                        style = AppTheme.textStyles.HeadingSix,
                                        color = AppTheme.colors.Gray
                                    )
                                }
                                Spacer(Modifier.weight(1f))
                                Text(
                                    text = "${streak.numberCompleted}/${streak.totalAmount}",
                                    style = AppTheme.textStyles.HeadingSix,
                                    color = AppTheme.colors.Gray,
                                )
                            }
                            val percentageCompleted = streak.numberCompleted.toFloat() / streak.totalAmount
                            ProgressBar(
                                progress = percentageCompleted,
                            )
                        }
                    }

                    // Add goal
                    Row(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .clickable { addWeekStreak.value = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(R.drawable.plus),
                            tint = AppTheme.colors.SecondaryThree,
                            modifier = Modifier
                                .size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.add_goal),
                            color = AppTheme.colors.SecondaryThree,
                            style = AppTheme.textStyles.DefaultUnderlined.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f,
                                )
                            ),
                        )
                    }
                }
            }

            // Month Goals
            HighlightCard(
                modifier = Modifier,
                outerPadding = 0.dp,
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ){
                    // Title
                    Text(
                        text = stringResource(R.string.this_month),
                        color = AppTheme.colors.SecondaryThree,
                        style = AppTheme.textStyles.HeadingFive.copy(
                            shadow = Shadow(
                                color = AppTheme.colors.DropShadow,
                                offset = Offset(2f, 2f),
                                blurRadius = 2f,
                            )
                        ),
                    )
                    // Separator
                    SeparatorLine(color = AppTheme.colors.SecondaryTwo)

                    // Display of streaks
                    TestUser.monthlyStreaks.forEach { streak ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clickable {
                                    streakToShow.value = streak
                                    showStreakInfo.value = true
                                },
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ){
                                    ShadowedIcon(
                                        imageVector = ImageVector.vectorResource(streak.reminder.icon),
                                        tint = if (streak.reminder.color == null) Color.Unspecified
                                        else resolveEnumColor(streak.reminder.color),
                                        modifier = Modifier
                                            .size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = streak.reminder.name,
                                        style = AppTheme.textStyles.HeadingSix,
                                        color = AppTheme.colors.Gray
                                    )
                                }
                                Spacer(Modifier.weight(1f))
                                Text(
                                    text = "${streak.numberCompleted}/${streak.totalAmount}",
                                    style = AppTheme.textStyles.HeadingSix,
                                    color = AppTheme.colors.Gray,
                                )
                            }
                            val percentageCompleted = streak.numberCompleted.toFloat() / streak.totalAmount
                            ProgressBar(
                                progress = percentageCompleted,
                            )
                        }
                    }

                    // Add goal
                    Row(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .clickable { addMonthStreak.value = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(R.drawable.plus),
                            tint = AppTheme.colors.SecondaryThree,
                            modifier = Modifier
                                .size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.add_goal),
                            color = AppTheme.colors.SecondaryThree,
                            style = AppTheme.textStyles.DefaultUnderlined.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f,
                                )
                            ),
                        )
                    }
                }
            }

            // Extra Space between elements
            Spacer(modifier = Modifier.height(16.dp))

            // Badges Title
            Row(
                modifier = Modifier
                    .align(Alignment.Start),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.badges),
                    color = AppTheme.colors.SecondaryOne,
                    style = AppTheme.textStyles.HeadingThree.copy(
                        shadow = Shadow(
                            color = AppTheme.colors.DropShadow,
                            offset = Offset(2f, 2f),
                            blurRadius = 2f,
                        )
                    ),
                )
                ShadowedIcon(
                    imageVector = ImageVector.vectorResource(R.drawable.info),
                    tint = AppTheme.colors.FadedGray,
                    modifier = Modifier
                        .size(20.dp)
                        .offset(y = 9.74.dp)
                        .clickable {
                            showBadgesTip.value = !showBadgesTip.value
                        }
                )
                Spacer(Modifier.weight(1f))
                // Navigation to user journey screen
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { navController?.navigate("journey_stats") },
                    text = stringResource(R.string.my_journey_stats),
                    color = AppTheme.colors.SecondaryThree,
                    style = AppTheme.textStyles.DefaultUnderlined,
                )
            }

            // Badges Display
            HighlightCard(
                modifier = Modifier,
                outerPadding = 0.dp,
                height = 200.dp
            ) {
                AllBadgesDisplay(
                    toShow = showBadge,
                    showBadge = badgeToDisplay,
                    scrollState = gridState
                )

                LazyColumnFadeEdges(
                    gridState = gridState
                )
            }
        }
    }
    // Badge Popup
    if (showBadge.value) {
        SingleBadgeDisplay(
            badge = badgeToDisplay.value,
            toShow = showBadge,
        )
    }

    // Add Streak screen
    if (addWeekStreak.value) {
        AddStreak(
            toShow = addWeekStreak,
            daily = true
        )
    }
    if (addMonthStreak.value) {
        AddStreak(
            toShow = addMonthStreak,
            daily = false
        )
    }

    // Show Tooltip Popups
    if (showLevelTip.value) {
        LifeExperienceToolTip(showLevelTip)
    }
    if (showStreaksTip.value) {
        StreaksToolTip(showStreaksTip)
    }
    if (showBadgesTip.value) {
        BadgesToolTip(showBadgesTip)
    }

    // Streak info handling
    if (showStreakInfo.value) {
        ShowStreak(
            toShow = showStreakInfo,
            passedStreak = streakToShow
        )
    }
}