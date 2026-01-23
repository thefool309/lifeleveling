package com.lifeleveling.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.R
import com.lifeleveling.app.data.Reminder
import com.lifeleveling.app.data.Streak
import com.lifeleveling.app.data.StreakDraft
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.theme.iconResForName
import com.lifeleveling.app.ui.theme.resolveColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Shows the information of a streak and option to delete it through a dialogue window.
 * Has a switch for what is shown between the streak data or a confirmation message for deletion
 * Confirmation will then launch delete functions.
 * Takes in state managed values to avoid recollecting state.
 * @param toShow A boolean controlling if the window shows or not.
 * @param streak The streak that is being clicked on.
 * @param reminder The reminder that the streak is based on.
 * @param onDelete Logic to implement to delete the streak before the window is closed.
 *
 * @author Elyseia
 */
@Composable
fun ShowStreak(
    toShow: MutableState<Boolean>,
    streak: Streak?,
    reminder: Reminder,
    onDelete: () -> Unit,
) {
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
                        imageVector = ImageVector.vectorResource(iconResForName(reminder.iconName)),
                        tint = if (reminder.colorToken == null) Color.Unspecified
                        else resolveColor(reminder.colorToken),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = reminder.title,
                        style = AppTheme.textStyles.HeadingFour,
                        color = AppTheme.colors.SecondaryThree
                    )
                }
                // Progress bar display
                val percentageCompleted = streak!!.numberCompleted.toFloat() / streak.totalRequired
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
                    text = stringResource(R.string.streak_to_complete, streak.totalRequired),
                    style = AppTheme.textStyles.Default,
                    color = AppTheme.colors.Gray
                )
                if (reminder.daily) {
                    Text(
                        text = stringResource(R.string.streak_daily_count, reminder.timesPerDay),
                        style = AppTheme.textStyles.Default,
                        color = AppTheme.colors.Gray
                    )
                }
                if (streak.repeat) {
                    Text(
                        text = stringResource(R.string.streak_repeat),
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
                                append(reminder.title)
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
                            toShow.value = false
                            onDelete()
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

/**
 * Ask the user for information on which reminder they want to make a streak out of.
 * Will display different messages if there is not a reminder available to use.
 * Creates a streak draft to pass the streak creation function
 * @param toShow Boolean needed to show screen because it is a dialog
 * @param daily Determines if it shows daily reminders or other reminders
 * @param reminders Full list of reminders (daily or not already separated).
 * @param streaksAlreadyCreated List of the streaks already created (either weekly or monthly)
 * @param navigateToAddReminder A navigation call to the add reminder screen.
 * @param onCreate Pass in the function call to create a reminder, gives the Streak draft object to create it out of.
 *
 * @author Elyseia
 */
@Composable
fun AddStreak(
    toShow: MutableState<Boolean>,
    daily: Boolean = true,
    reminders: List<Reminder>,
    streaksAlreadyCreated: List<Streak>,
    navigateToAddReminder: () -> Unit,
    onCreate: (StreakDraft) -> Unit,
    ) {
    var selectedReminderIndex by remember { mutableStateOf(0) }
    val usedIDs = streaksAlreadyCreated.map { it.reminderId }.toSet()
    val remindersAvailableToUse = reminders.filter { reminder ->
        reminder.reminderId !in usedIDs
    }
    var repeat by remember { mutableStateOf(true) }
    val reminderMenu = remember { mutableStateOf(false) }

    CustomDialog(
        toShow = toShow,
        dismissOnInsideClick = false,
        dismissOnOutsideClick = false,
    ) {
        if (remindersAvailableToUse.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ){
                val empty = reminders.isEmpty()
                Text(
                    text = if (empty) stringResource(R.string.no_reminders_for_streaks)
                        else stringResource(R.string.all_reminders_already_streaks),
                    style = AppTheme.textStyles.HeadingFive,
                    color = AppTheme.colors.SecondaryOne,
                    textAlign = TextAlign.Center,
                )
                Text(
                    modifier = Modifier.clickable {
                        toShow.value = false
                        navigateToAddReminder()
                    },
                    text = stringResource(R.string.make_a_new_reminder),
                    style = AppTheme.textStyles.DefaultUnderlined,
                    color = AppTheme.colors.Gray,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                CustomButton(
                    onClick = { toShow.value = false },
                    backgroundColor = AppTheme.colors.Error75,
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = AppTheme.textStyles.HeadingSix,
                        color = AppTheme.colors.Background
                    )
                }
            }
        } else {
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
                ) {
                    Text(
                        text = stringResource(R.string.choose_reminder),
                        style = AppTheme.textStyles.HeadingFive,
                        color = AppTheme.colors.SecondaryThree
                    )
                    DropDownReminderMenu(
                        options = remindersAvailableToUse,
                        selectedIndex = selectedReminderIndex,
                        onSelectedChange = { selectedReminderIndex = it },
                        textStyle = AppTheme.textStyles.HeadingSix,
                        arrowSize = 25.dp,
                        expanded = reminderMenu,
                        backgroundMainColor = AppTheme.colors.DarkerBackground,
                    )
                    Text(
                        modifier = Modifier.clickable {
                            toShow.value = false
                            navigateToAddReminder()
                        },
                        text = stringResource(R.string.need_a_new_reminder),
                        style = AppTheme.textStyles.DefaultUnderlined,
                        color = AppTheme.colors.Gray
                    )
                }

                // Repeat options
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Repeat
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(R.string.ask_repeat),
                            style = AppTheme.textStyles.Default,
                            color = AppTheme.colors.Gray
                        )
                        CustomCheckbox(
                            checked = repeat,
                            onCheckedChange = { repeat = it },
                        )
                    }
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
                            toShow.value = false
                            onCreate(
                                StreakDraft(
                                    reminderId = remindersAvailableToUse[selectedReminderIndex].reminderId,
                                    weekly = daily,
                                    repeat = repeat,
                                )
                            )
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
}

// TODO: Update this after badges created
/**
 * Displays the players badges in a grid of clickable circles.
 * Takes in parameters of the userState to avoid recollecting state.
 * @param columns Number of columns in the grid.
 * @param toShow A boolean for showing a window with badge information inside it.
 * @param badges Pass in the list of badges from the userState for the list that needs to be shown.
 * @param showBadge A variable to store a badge into when it is clicked, will be used for displaying the information.
 * @param scrollState Pass in the scroll state for saving where the user has scrolled in the list to.
 *
 * @author Elyseia
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllBadgesDisplay(
    modifier: Modifier = Modifier,
    columns: Int = 5,
    toShow: MutableState<Boolean>,
    badges: List<TestBadge>,
    showBadge: MutableState<TestBadge>,
    scrollState: LazyGridState,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        state = scrollState,
    ) {
        items(badges) { badge ->
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
                    else resolveColor(badge.color),
                    elevation = 12.dp
                )
            }
        }
    }
}

// TODO: Update this after badges created
/**
 * Shows the information of a single badge in a popup window.
 * @param toShow A boolean to determine if the window shows
 * @param badge The badge item to be displayed
 *
 * @author Elyseia
 */
@Composable
fun SingleBadgeDisplay(
    modifier: Modifier = Modifier,
    badge: TestBadge,
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
                    else resolveColor(badge.color),
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
                            offset = Offset(2f, 2f),
                            blurRadius = 2f,
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
                } ?: "NotCompleted"
//                    ?.toDate()
//                    ?.let {
//                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
//                            .format(it)
//                    } ?: "Not Completed"

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