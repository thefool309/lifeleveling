package com.lifeleveling.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlin.collections.forEach
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.theme.AppTheme


/**
 * Prints a bulleted list from the list of strings passed in
 * @param items The list of AnnotatedStrings that will be turned into bullet points.
 *
 * @author Elyseia
 */
@Composable
fun BulletPoints(items: List<AnnotatedString>) {
    Column (
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach { item ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "•",
                    style = AppTheme.textStyles.Small,
                    color = AppTheme.colors.Gray,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = item,
                    style = AppTheme.textStyles.Small,
                    color = AppTheme.colors.Gray,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * The tooltip window that pops up with tutorial style information.
 * @param toShow Boolean for showing the dialog
 * @param title Title of tooltip window. Call as R.string.[string name]
 * @param tips Build a list of annotatedStrings to pass in as bullet points
 *
 * @author Elyseia
 */
@Composable
fun Tooltip(
    modifier: Modifier = Modifier,
    toShow: MutableState<Boolean>,
    title: Int,
    tips: List<AnnotatedString>
) {
    CustomDialog(
        toShow = toShow,
        modifier = modifier,
    ) {
        // Content inside card
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Title
            Text(
                stringResource(title),
                color = AppTheme.colors.SecondaryThree,
                style = AppTheme.textStyles.HeadingSix.copy(
                    shadow = Shadow(
                        color = AppTheme.colors.DropShadow,
                        offset = Offset(2f, 2f),
                        blurRadius = 2f,
                    )
                )
            )
            // Tips
            BulletPoints(tips)
        }
    }
}


/**
 * Level and Experience Popup Tool Tip.
 * Explains information about the level and experience of the user
 * @param toShow The boolean to toggle if it is shown
 * @author Elyseia
 */
@Composable
fun LifeExperienceToolTip(toShow: MutableState<Boolean>) {
    // Bullet Points
    val levelTips = listOf(
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.life_tip_one))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.life_tip_two))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.life_tip_three))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.life_tip_four))
            }
        }
    )

    // Dialog window
    Tooltip(
        toShow = toShow,
        title = R.string.life_tip_title,
        tips = levelTips
    )
}

/**
 * Health Tool Tip window
 * Explains the information of the user's health, how to get more, and the difference between fighting and meditating
 * @param toShow The boolean to toggle if it is shown
 * @author Elyseia
 */
@Composable
fun HealthToolTip(toShow: MutableState<Boolean>) {
    // Bullet Points
    val healthTips = listOf(
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.SmallUnderlined.toSpanStyle().copy(color = AppTheme.colors.SecondaryThree)) {
                append(stringResource(R.string.health))
            }
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.health_tip_one))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.health_tip_two))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.health_tip_three))
            }
        }
    )

    // Dialog window
    Tooltip(
        toShow = toShow,
        title = R.string.health_tip_title,
        tips = healthTips
    )
}

/**
 * Streaks Popup Tool Tip
 * Explains what streaks are, the rewards for completing them, and difference between weekly and month streaks.
 * @param toShow The boolean to toggle if it is shown
 * @author Elyseia
 */
@Composable
fun StreaksToolTip(toShow: MutableState<Boolean>) {
    // Bullet Points
    val streaksTips = listOf(
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.streaks_tip_one))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.streaks_tip_two))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.streaks_tip_three))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.streaks_tip_four))
            }
        }
    )

    // Dialog window
    Tooltip(
        toShow = toShow,
        title = R.string.streaks,
        tips = streaksTips
    )
}

/**
 * Badges Popup Tool Tip
 * Explains what badges are and how they appear different between ones achieves and ones still needed.
 * @param toShow The boolean to toggle if it is shown
 * @author Elyseia
 */
@Composable
fun BadgesToolTip(toShow: MutableState<Boolean>) {
    // Bullet Points
    val badgesTips = listOf(
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.badges_tip_one))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.badges_tip_two))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.badges_tip_three))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.badges_tip_four))
            }
        }
    )

    // Dialog window
    Tooltip(
        toShow = toShow,
        title = R.string.badges,
        tips = badgesTips
    )
}

/**
 * Stats popup tool tip
 * Explains the difference between the stats and how they affect the experience and coin gains
 * @param toShow Boolean that controls if the popup window will be displayed
 *
 * @author StephenC1993
 */
@Composable
fun StatsToolTip(toShow: MutableState<Boolean>) {
    // Bullet Points
    val statTips = listOf(
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.StatTipOne))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.SmallUnderlined.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.Life_Points))
            }
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.StatTipTwo))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.SmallUnderlined.toSpanStyle().copy(color = AppTheme.colors.BrandOne)) {
                append(stringResource(R.string.strength))
            }
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.StatTipThree))
            }
            withStyle(style = AppTheme.textStyles.SmallUnderlined.toSpanStyle().copy(color = AppTheme.colors.BrandTwo)) {
                append(stringResource(R.string.defense))
            }
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.StatTipFour))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.SmallUnderlined.toSpanStyle().copy(color = AppTheme.colors.SecondaryOne)) {
                append(stringResource(R.string.intelligence))
            }
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.StatTipThree))
            }
            withStyle(style = AppTheme.textStyles.SmallUnderlined.toSpanStyle().copy(color = AppTheme.colors.SecondaryTwo)) {
                append(stringResource(R.string.agility))
            }
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.StatTipFive))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.SmallUnderlined.toSpanStyle().copy(color = AppTheme.colors.SecondaryThree)) {
                append(stringResource(R.string.health))
            }
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.health_tip_one))
            }
        },
    )

    // Dialog window
    Tooltip(
        toShow = toShow,
        title = R.string.stats,
        tips = statTips
    )
}

/**
 * User Journey tool tip window
 * Explains the stats displayed within the window as the user's personal achievements
 * @param toShow Boolean controlling if the popup window will be displayed
 *
 * @author Elyseia
 */
@Composable
fun UserJourneyToolTip(toShow: MutableState<Boolean>) {
    // Bullet Points
    val levelTips = listOf(
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.journey_tips_one))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.journey_tips_two))
            }
        },
    )

    // Dialog window
    Tooltip(
        toShow = toShow,
        title = R.string.journey_stats_title,
        tips = levelTips
    )
}

/**
 * MyReminders Popup Tool Tip
 * @param toShow The boolean to toggle if it is shown
 */
@Composable
fun MyRemindersToolTip(toShow: MutableState<Boolean>) {
    // Bullet Points
    val myRemindersTips = listOf(
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.myReminders_tooltip1))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.myReminders_tooltip2))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.myReminders_tooltip3))
            }
        },

    )

    // Dialog window
    Tooltip(
        toShow = toShow,
        title = R.string.myReminders_tipstitle,
        tips = myRemindersTips
    )
}

/**
 * CreateReminders Popup Tool Tip
 * @param toShow The boolean to toggle if it is shown
 */
@Composable
fun CreateRemindersToolTip(toShow: MutableState<Boolean>) {
    // Bullet Points
    val addRemindersTips = listOf(
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.title_tooltip))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.icon_tooltip))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.starting_at_tooltip))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.remind_me_every_tooltip))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.repeat_for_tooltip))
            }
        },
    )

    // Dialog window
    Tooltip(
        toShow = toShow,
        title = R.string.reminders_tooptip,
        tips = addRemindersTips
    )
}

/**
 * Calendar Popup Tool Tip
 * @param toShow The boolean to toggle if it is shown
 */
@Composable
fun CalendarToolTip(toShow: MutableState<Boolean>) {
    // Bullet Points
    val calendarTips = listOf(
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.calendar_tooptip1))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.calendar_tooptip2))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.calendar_tooptip3))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.calendar_tip_jumpto))
            }
        },
    )

    // Dialog window
    Tooltip(
        toShow = toShow,
        title = R.string.calendar_tooptiptitle,
        tips = calendarTips
    )
}