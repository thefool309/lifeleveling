package com.lifeleveling.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.collections.forEach
import com.lifeleveling.app.R


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
 * Level and Experience Popup Tool Tip
 * @param toShow The boolean to toggle if it is shown
 */
@Composable
fun LifeExperienceToolTip(toShow: MutableState<Boolean>) {
    // Level and Experience tips
    val LevelTips = listOf(
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append(stringResource(R.string.life_tip_one))
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append("The bar shows your experience progress toward your next level.")
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append("You earn experience idly while your character is fighting.")
            }
        },
        buildAnnotatedString {
            withStyle(style = AppTheme.textStyles.Small.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                append("Experience earned is based on your stats.")
            }
        }
    )

    Dialog(
        onDismissRequest = { toShow.value = false },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        // Making a custom dim
        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(color = AppTheme.colors.DarkerBackground.copy(alpha = 0.1f))
                .clickable { toShow.value = false },
        ) {
            // Popup Card shading
            PopupCard(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable { toShow.value = false }
            ) {
                // Content inside card
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Title
                    Text(
                        stringResource(R.string.life_tip_title),
                        color = AppTheme.colors.SecondaryThree,
                        style = AppTheme.textStyles.HeadingSix.copy(
                            shadow = Shadow(
                                color = AppTheme.colors.DropShadow,
                                offset = Offset(3f, 4f),
                                blurRadius = 6f,
                            )
                        )
                    )
                    // Content
                    BulletPoints(LevelTips)
                }
            }
        }
    }
}

@Composable
fun HealthToolTip(toShow: MutableState<Boolean>) {
    // Level and Experience tips
    val firstHealthTip = buildAnnotatedString {}

    Dialog(
        onDismissRequest = { toShow.value = false },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        // Making a custom dim
        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(color = AppTheme.colors.DarkerBackground.copy(alpha = 0.1f))
                .clickable { toShow.value = false },
        ) {
            PopupCard(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable { toShow.value = false }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Example Popup",
                        color = AppTheme.colors.SecondaryThree,
                        style = AppTheme.textStyles.HeadingFour.copy(
                            shadow = Shadow(
                                color = AppTheme.colors.DropShadow,
                                offset = Offset(3f, 4f),
                                blurRadius = 6f,
                            )
                        )
                    )
                    Text(
                        "Testing popup capabilities and text wrapping. Click me to close.",
                        color = AppTheme.colors.Gray,
                        style = AppTheme.textStyles.Default
                    )
                }
            }
        }
    }
}