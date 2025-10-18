package com.lifeleveling.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.collections.forEach


/**
 * Makes a bullet list of the list passed in.
 */
@Composable
fun BulletPoints(items: List<AnnotatedString>) {
    Column {
        items.forEach { item ->
            Row {
                Text("•")
                Spacer(modifier = Modifier.width(4.dp))
                Text(item)
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
    val firstLevelTip = buildAnnotatedString {}

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