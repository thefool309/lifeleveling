package com.lifeleveling.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.theme.HighlightCard
import com.lifeleveling.app.ui.theme.ShadowedIcon

@Preview
@Composable
fun StreaksScreen() {
    // Bools for pop up tips
    val showStreaksTip = remember { mutableStateOf(false) }
    val showBadgesTip = remember { mutableStateOf(false) }

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
            // Streaks title
            Row(
                modifier = Modifier
                    .align(Alignment.Start),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Streaks",
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

            // Streaks window
            HighlightCard(
                modifier = Modifier,
                outerPadding = 0.dp,

            ) {
                Column (
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ){
                    // Title
                    Text(
                        text = "This Week",
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

                    // Add in reminders display

                    // Add goal

                }
            }
        }
    }
}