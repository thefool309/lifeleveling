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
 * Makes a list of the bullets passed in.
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
 * Saving the dialog options with parameters to pass in making tooltips look the same
 * @param toShow Boolean for showing the dialog
 * @param title Title of tooltip window. Call as R.string.[string name]
 * @param tips Build a list of annotatedStrings to pass in as bullet points
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
                        offset = Offset(3f, 4f),
                        blurRadius = 6f,
                    )
                )
            )
            // Tips
            BulletPoints(tips)
        }
    }
}


/**
 * Level and Experience Popup Tool Tip
 * @param toShow The boolean to toggle if it is shown
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
 * HealthToolTip window
 * @param toShow The boolean to toggle if it is shown
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
 * @param toShow The boolean to toggle if it is shown
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
 * Streaks Popup Tool Tip
 * @param toShow The boolean to toggle if it is shown
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