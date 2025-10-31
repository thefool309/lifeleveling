package com.lifeleveling.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.theme.AllBadgesDisplay
import com.lifeleveling.app.ui.theme.BadgesToolTip
import com.lifeleveling.app.ui.theme.CustomButton
import com.lifeleveling.app.ui.theme.CustomDialog
import com.lifeleveling.app.ui.theme.HighlightCard
import com.lifeleveling.app.ui.theme.LevelAndProgress
import com.lifeleveling.app.ui.theme.LifeExperienceToolTip
import com.lifeleveling.app.ui.theme.ProgressBar
import com.lifeleveling.app.ui.theme.ShadowedIcon
import com.lifeleveling.app.ui.theme.SingleBadgeDisplay
import com.lifeleveling.app.ui.theme.StreaksToolTip

@Preview
@Composable
fun StreaksScreen() {
    // Pop up tips
    val showLevelTip = remember { mutableStateOf(false) }
    val showStreaksTip = remember { mutableStateOf(false) }
    val showBadgesTip = remember { mutableStateOf(false) }
    val showBadge = remember { mutableStateOf(false) }
    val showStreakInfo = remember { mutableStateOf(false) }
    val badgeToDisplay = remember { mutableStateOf(TestUser.allBadges[1])}
    val streakToShow = remember { mutableStateOf(TestUser.weeklyStreaks[0])}

    // Background with scrolling if needed
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.colors.Background)
            .padding(16.dp)
    ) {
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
                            offset = Offset(3f, 4f),
                            blurRadius = 6f,
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
                                offset = Offset(3f, 4f),
                                blurRadius = 6f,
                            )
                        ),
                    )
                    // Separator
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.separator_line),
                        tint = AppTheme.colors.SecondaryTwo,
                        contentDescription = null,
                    )

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
                            .clickable {},
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
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
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
                                offset = Offset(3f, 4f),
                                blurRadius = 6f,
                            )
                        ),
                    )
                    // Separator
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.separator_line),
                        tint = AppTheme.colors.SecondaryTwo,
                        contentDescription = null,
                    )

                    // Display of streaks
                    TestUser.monthlyStreaks.forEach { streak ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
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
                            .clickable {},
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
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
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
                            offset = Offset(3f, 4f),
                            blurRadius = 6f,
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
            }

            // Badges Display
            HighlightCard(
                modifier = Modifier,
                outerPadding = 0.dp,
                height = 200.dp
            ) {
                AllBadgesDisplay(
                    toShow = showBadge,
                    showBadge = badgeToDisplay
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

@Composable
fun ShowStreak(
    toShow: MutableState<Boolean>,
    passedStreak: MutableState<Streak>,
) {
    val streak = passedStreak.value
    CustomDialog(
        toShow = toShow,
        dismissOnInsideClick = false,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Display icon and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ShadowedIcon(
                    modifier = Modifier.size(20.dp),
                    imageVector = ImageVector.vectorResource(streak.reminder.icon),
                    tint = if (streak.reminder.color == null) Color.Unspecified
                    else resolveEnumColor(streak.reminder.color),
                )
                Text(
                    text = streak.reminder.name,
                    style = AppTheme.textStyles.HeadingFour,
                    color = AppTheme.colors.SecondaryThree
                )
            }
            // Progress bar display
            val percentageCompleted = streak.numberCompleted.toFloat() / streak.totalAmount
            ProgressBar(
                progress = percentageCompleted,
            )
            // Extra details
            Text(
                text = stringResource(R.string.streak_completed, streak.numberCompleted),
                style = AppTheme.textStyles.Default,
                color = AppTheme.colors.Gray
            )
            Text(
                text = stringResource(R.string.streak_to_complete, streak.totalAmount),
                style = AppTheme.textStyles.Default,
                color = AppTheme.colors.Gray
            )
            if (streak.reminder.daily) {
                Text(
                    text = stringResource(R.string.streak_daily_count, streak.reminder.timesPerDay),
                    style = AppTheme.textStyles.Default,
                    color = AppTheme.colors.Gray
                )
            }
            // Buttons for deleting or closing window
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CustomButton(
                    onClick = { },
                    backgroundColor = AppTheme.colors.Error75,
                    width = 120.dp
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        style = AppTheme.textStyles.HeadingSix,
                        color = AppTheme.colors.Background
                    )
                }
                Spacer(Modifier.width(16.dp))
                CustomButton(
                    onClick = { toShow.value = false },
                    backgroundColor = AppTheme.colors.Success75,
                    width = 120.dp
                ) {
                    Text(
                        text = stringResource(R.string.close),
                        style = AppTheme.textStyles.HeadingSix,
                        color = AppTheme.colors.Background
                    )
                }
            }
        }
    }
}