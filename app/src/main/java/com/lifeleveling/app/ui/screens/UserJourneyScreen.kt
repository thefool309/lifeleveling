package com.lifeleveling.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.components.CircleButton
import com.lifeleveling.app.ui.components.HighlightCard
import com.lifeleveling.app.ui.components.SeparatorLine
import com.lifeleveling.app.ui.components.ShadowedIcon
import com.lifeleveling.app.ui.components.TestUser
import com.lifeleveling.app.ui.components.UserJourneyToolTip
import com.lifeleveling.app.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UserJourneyScreen(
    navController: NavController,
) {
    val showJourneyTip = remember { mutableStateOf(false) }
    val profileCreatedDate = TestUser.profileCreatedDate.let {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            .format(Date(it))
    } ?: "Not Completed"
    val timeSinceCreated = TestUser.getTimeSinceUserCreated()

    // Statistics to display
    val statistics = listOf(
        // Streaks
        JourneySection(
          title = R.string.streaks,
            items = listOf(
                JourneyItem(
                    R.string.all_streaks_completed,
                    TestUser.totalStreaksCompleted.toString()
                ),
                JourneyItem(
                    R.string.weekly_streaks_completed,
                    TestUser.weekStreaksCompleted.toString()
                ),
                JourneyItem(
                    R.string.monthly_streaks_completed,
                    TestUser.monthStreaksCompleted.toString()
                )
            )
        ),
        // Badges
        JourneySection(
            title = R.string.badges,
            items = listOf(
                JourneyItem(
                    R.string.badges_earned,
                    TestUser.badgesEarned.toString(),
                ),
            )
        ),
        // Currency Earnings
        JourneySection(
            title = R.string.total_earnings,
            items = listOf(
                // Total experience
                JourneyItem(
                    R.string.total_exp,
                    TestUser.allExpEver.toString()
                ),
                // Total coins
                JourneyItem(
                    R.string.total_coins,
                    TestUser.allCoinsEarned.toString()
                ),
                // Coins spent
                JourneyItem(
                    R.string.coins_spent,
                    TestUser.coinsSpent.toString()
                ),
            )
        ),
        // User Info
        JourneySection(
            title = R.string.person_stats,
            items = listOf(
                // Joined date
                JourneyItem(
                    R.string.journey_started_on,
                    profileCreatedDate
                ),
                JourneyItem(
                    R.string.account_age,
                    timeSinceCreated
                )
            )
        ),
    )

    // Main body
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.colors.Background)
            .padding(16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Title
            Row(
                modifier = Modifier
                    .align(Alignment.Start),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Journey Title
                Text(
                    text = stringResource(R.string.journey_stats),
                    color = AppTheme.colors.SecondaryOne,
                    style = AppTheme.textStyles.HeadingThree.copy(
                        shadow = Shadow(
                            color = AppTheme.colors.DropShadow,
                            offset = Offset(3f, 4f),
                            blurRadius = 6f,
                        )
                    ),
                )
                // Info Icon
                ShadowedIcon(
                    imageVector = ImageVector.vectorResource(R.drawable.info),
                    tint = AppTheme.colors.FadedGray,
                    modifier = Modifier
                        .size(20.dp)
                        .offset(y = 9.74.dp)
                        .clickable {
                            showJourneyTip.value = !showJourneyTip.value
                        }
                )
                Spacer(Modifier.weight(1f))
                CircleButton(
                    onClick = { navController?.popBackStack() },
                    imageVector = ImageVector.vectorResource(R.drawable.back_arrow),
                    size = 48.dp,
                )
            }

            // Body box
            HighlightCard(
                modifier = Modifier.fillMaxWidth(),
                outerPadding = 0.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Sections
                    statistics.forEachIndexed { index, section ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            // Title
                            Text(
                                text = stringResource(section.title),
                                color = AppTheme.colors.SecondaryThree,
                                style = AppTheme.textStyles.HeadingSix
                            )

                            // Items inside
                            section.items.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ){
                                    Text(
                                        modifier = Modifier.weight(1f),
                                        text = stringResource(item.name),
                                        color = AppTheme.colors.Gray,
                                        style = AppTheme.textStyles.Default
                                    )
                                    Text(
                                        modifier = Modifier.weight(.45f),
                                        text = item.value,
                                        color = AppTheme.colors.Gray,
                                        style = AppTheme.textStyles.Default
                                    )
                                }
                            }
                        }

                        // Separator
                        if (index < statistics.lastIndex){ SeparatorLine() }
                    }
                }
            }
        }
    }

    // Show tooltip
    if (showJourneyTip.value) {
        UserJourneyToolTip(showJourneyTip)
    }
}

data class JourneyItem(
    val name: Int,
    val value: String,
)

data class JourneySection(
    val title: Int,
    val items: List<JourneyItem>
)


@Preview(showBackground = true)
@Composable
fun PreviewUserJourneyScreen() {
    // Create a mock navController
    val navController = rememberNavController()

    UserJourneyScreen(
        navController = navController
    )
}