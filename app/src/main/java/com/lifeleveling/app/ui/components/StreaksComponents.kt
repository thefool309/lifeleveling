package com.lifeleveling.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.theme.resolveEnumColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Shows the information of a streak and option to delete it.
 * @param toShow A boolean controlling if the window shows or not.
 * @param passedStreak The streak that is being clicked on.
 */
@Composable
fun ShowStreak(
    toShow: MutableState<Boolean>,
    passedStreak: MutableState<Streak>,
) {
    val streak = passedStreak.value
    var delete by remember { mutableStateOf(false) }

    CustomDialog(
        toShow = toShow,
        dismissOnInsideClick = false,
    ) {
        if (!delete) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Display icon and title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    ShadowedIcon(
                        modifier = Modifier.size(30.dp),
                        imageVector = ImageVector.vectorResource(streak.reminder.icon),
                        tint = if (streak.reminder.color == null) Color.Unspecified
                        else resolveEnumColor(streak.reminder.color),
                    )
                    Spacer(Modifier.width(8.dp))
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CustomButton(
                        width = 120.dp,
                        onClick = { delete = true },
                        backgroundColor = AppTheme.colors.Error75,
                    ) {
                        Text(
                            text = stringResource(R.string.delete),
                            style = AppTheme.textStyles.HeadingSix,
                            color = AppTheme.colors.Background
                        )
                    }
                    Spacer(Modifier.width(20.dp))
                    CustomButton(
                        width = 120.dp,
                        onClick = { toShow.value = false },
                        backgroundColor = AppTheme.colors.Success75,
                    ) {
                        Text(
                            text = stringResource(R.string.close),
                            style = AppTheme.textStyles.HeadingSix,
                            color = AppTheme.colors.Background
                        )
                    }
                }
            }
        } else {
            // Screen for confirming delete
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center,
                    text = (
                        buildAnnotatedString {
                            withStyle(style = AppTheme.textStyles.HeadingSix.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                                append(stringResource(R.string.streak_delete))
                            }
                            withStyle(style = AppTheme.textStyles.HeadingSix.toSpanStyle().copy(color = AppTheme.colors.SecondaryThree, textDecoration = TextDecoration.Underline)) {
                                append(streak.reminder.name)
                            }
                            withStyle(style = AppTheme.textStyles.HeadingSix.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                                append(stringResource(R.string.streak_delete_two))
                            }
                        }
                    ),
                    style = AppTheme.textStyles.HeadingSix,
                    color = AppTheme.colors.Gray
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CustomButton(
                        width = 120.dp,
                        onClick = { delete = false },
                        backgroundColor = AppTheme.colors.Success75,
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            style = AppTheme.textStyles.HeadingSix,
                            color = AppTheme.colors.Background
                        )
                    }
                    Spacer(Modifier.width(20.dp))
                    CustomButton(
                        width = 120.dp,
                        onClick = {
                            if (streak.reminder.daily) {
                                TestUser.removeFromWeeklyStreak(streak.reminder)
                            } else {
                                TestUser.removeFromMonthlyStreak(streak.reminder)
                            }
                            toShow.value = false
                        },
                        backgroundColor = AppTheme.colors.Error75,
                    ) {
                        Text(
                            text = stringResource(R.string.delete),
                            style = AppTheme.textStyles.HeadingSix,
                            color = AppTheme.colors.Background
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddStreak(
    toShow: MutableState<Boolean>,
    daily: Boolean = true
) {
    var selectedReminderIndex by remember { mutableStateOf(0) }
    val reminders = (if(daily) TestUser.weeklyReminders else TestUser.monthlyReminders)
    var selectedRepeatIndex by remember { mutableStateOf(0) }

    CustomDialog(
        toShow = toShow,
        dismissOnInsideClick = false,
        dismissOnOutsideClick = false,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Title
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(if (daily) R.string.add_week_streak else R.string.add_month_streak),
                    style = AppTheme.textStyles.HeadingFour,
                    color = AppTheme.colors.SecondaryOne
                )
                // Separator
                SeparatorLine(color = AppTheme.colors.SecondaryTwo)
            }

            // Choosing a reminder
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ){
                Text(
                    text = stringResource(R.string.choose_reminder),
                    style = AppTheme.textStyles.HeadingFive,
                    color = AppTheme.colors.SecondaryThree
                )
                DropDownReminderMenu(
                    options = reminders,
                    selectedIndex = selectedReminderIndex,
                    onOptionSelected = { selectedReminderIndex = it },
                    menuWidth = 300.dp,
                    textStyle = AppTheme.textStyles.HeadingSix,
                    iconSize = 25.dp,
                    arrowSize = 25.dp,
                    menuOffset = IntOffset(50, 0)
                )
                Text(
                    text = stringResource(R.string.need_a_new_daily),
                    style = AppTheme.textStyles.DefaultUnderlined,
                    color = AppTheme.colors.Gray
                )
            }

            // Repeat options
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ){
                // Repeat
                Text(
                    text = stringResource(R.string.repeat_options),
                    style = AppTheme.textStyles.HeadingFive,
                    color = AppTheme.colors.SecondaryThree
                )
                DropDownTextMenu(
                    options = listOf(
                        stringResource(R.string.weeks),
                        stringResource(R.string.months),
                        stringResource(R.string.years),
                    ),
                    selectedIndex = selectedRepeatIndex,
                    onOptionSelected = { selectedRepeatIndex = it },
                    textStyle = AppTheme.textStyles.HeadingSix,
                    arrowSize = 25.dp,
                )
            }

            // Button controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CustomButton(
                    width = 120.dp,
                    onClick = { toShow.value = false },
                    backgroundColor = AppTheme.colors.Error75,
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = AppTheme.textStyles.HeadingSix,
                        color = AppTheme.colors.Background
                    )
                }
                Spacer(Modifier.width(20.dp))
                CustomButton(
                    width = 120.dp,
                    onClick = {
                        if(daily) TestUser.addToWeeklyStreak(reminders[selectedReminderIndex])
                        else TestUser.addToMonthlyStreak(reminders[selectedReminderIndex])
                        toShow.value = false
                    },
                    backgroundColor = AppTheme.colors.Success75,
                ) {
                    Text(
                        text = stringResource(R.string.create),
                        style = AppTheme.textStyles.HeadingSix,
                        color = AppTheme.colors.Background
                    )
                }
            }
        }
    }
}

/**
 * Displays the players badges.
 * @param columns Number of columns in the grid.
 * @param toShow A boolean for showing a window with badge information inside it.
 * @param showBadge A variable to store a badge into when it is clicked, will be used for displaying the information.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllBadgesDisplay(
    modifier: Modifier = Modifier,
    columns: Int = 5,
    toShow: MutableState<Boolean>,
    showBadge: MutableState<Badge>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(TestUser.allBadges) { badge ->
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                CircleButton(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = ImageVector.vectorResource(badge.icon),
                    onClick = {
                        showBadge.value = badge
                        toShow.value = true
                    },
                    backgroundColor = if (!badge.completed) AppTheme.colors.FadedGray
                    else resolveEnumColor(badge.color),
                    elevation = 12.dp
                )
            }
        }
    }
}

/**
 * Shows the information of a single badge
 * @param toShow A boolean to determine if the window shows
 * @param badge The badge item to be displayed
 */
@Composable
fun SingleBadgeDisplay(
    modifier: Modifier = Modifier,
    badge: Badge,
    toShow: MutableState<Boolean>,
) {
    CustomDialog(
        toShow = toShow,
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                CircleButton(
                    imageVector = ImageVector.vectorResource(badge.icon),
                    onClick = { toShow.value = false },
                    backgroundColor = if (!badge.completed) AppTheme.colors.FadedGray
                    else resolveEnumColor(badge.color),
                    elevation = 12.dp,
                    size = 50.dp
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = badge.title,
                    color = AppTheme.colors.SecondaryThree,
                    textAlign = TextAlign.Center,
                    style = AppTheme.textStyles.HeadingSix.copy(
                        shadow = Shadow(
                            color = AppTheme.colors.DropShadow,
                            offset = Offset(3f, 4f),
                            blurRadius = 6f,
                        )
                    )
                )
            }

            Text(
                text = badge.description,
                color = AppTheme.colors.Gray,
                style = AppTheme.textStyles.Default,
                textAlign = TextAlign.Center,
            )

            if (badge.completed) {
                val date = badge.completedOn?.let {
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        .format(Date(it))
                } ?: "Not Completed"

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.badge_completed_on),
                        color = AppTheme.colors.SecondaryTwo,
                        style = AppTheme.textStyles.Default
                    )
                    Text(
                        text = date,
                        color = AppTheme.colors.SecondaryTwo,
                        style = AppTheme.textStyles.Default
                    )
                }
            }
        }
    }
}