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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
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
import com.lifeleveling.app.data.LocalNavController
import com.lifeleveling.app.data.LocalUserManager
import com.lifeleveling.app.data.FirestoreRepository
import com.lifeleveling.app.ui.components.CircleButton
import com.lifeleveling.app.ui.components.HighlightCard
import com.lifeleveling.app.ui.components.ScrollFadeEdges
import com.lifeleveling.app.ui.components.SeparatorLine
import com.lifeleveling.app.ui.components.ShadowedIcon
import com.lifeleveling.app.ui.components.UserJourneyToolTip
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.util.AndroidLogger
import com.lifeleveling.app.util.ILogger
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * The User Journey Screen that shows several statistics for the user about their accomplishments so far.
 * Lays out interesting information that has been collected on the backend.
 * Can store progress toward badge materials.
 * @author Elyseia
 */
@Preview
@Composable
fun UserJourneyScreen() {
    val userManager = LocalUserManager.current
    val userState by userManager.uiState.collectAsState()
    val navController = LocalNavController.current

    val scrollState = rememberScrollState()
    val showJourneyTip = remember { mutableStateOf(false) }

    // Extra calculations for some stats
    val profileCreatedDate: String = userState.userBase?.createdAt?.toDate()?.let {date ->
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
    } ?: "Unknown"
    val timeSinceCreated = userManager.calcTimeSinceCreatedDate()
    userManager.userJourneyCalculations()
    val mostCompletedReminder = if (userState.userBase?.mostCompletedReminder?.first == "") stringResource(R.string.no_remiders_completed)
                        else "${userState.userBase?.mostCompletedReminder?.first} ${userState.userBase?.mostCompletedReminder?.second.toString()}"
    val repo = remember{ FirestoreRepository() }
    val logger: ILogger = remember { AndroidLogger() }
    val totalRemindersCompletedState = remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(Unit) {
        totalRemindersCompletedState.value = repo.getTotalReminderCompletions(logger)
    }
    val totalRemindersCompletedDisplay = totalRemindersCompletedState.value?.toString() ?: "0"

    // Statistics to display
    val statistics = listOf(
        // Reminders
        JourneySection(
            title = R.string.myReminders_title2,
            items = listOf(
                JourneyItem(
                    R.string.total_reminders_completed,
                    totalRemindersCompletedDisplay
                )
            )
        ),

        // Streaks
        JourneySection(
          title = R.string.streaks,
            items = listOf(
                // Total of all streaks completed
                JourneyItem(
                    R.string.all_streaks_completed,
                    userState.totalStreaksCompleted.toString()
                ),
                // Total weekly streaks completed
                JourneyItem(
                    R.string.weekly_streaks_completed,
                    userState.userBase?.weekStreaksCompleted.toString()
                ),
                // Total monthly streaks completed
                JourneyItem(
                    R.string.monthly_streaks_completed,
                    userState.userBase?.monthStreaksCompleted.toString()
                )
            )
        ),
        // Badges
        JourneySection(
            title = R.string.badges,
            items = listOf(
                // Total number earned
                JourneyItem(
                    R.string.badges_earned,
                    userState.badgesEarned.toString(),
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
                    userState.allExpEver.toString()
                ),
                // Total coins
                JourneyItem(
                    R.string.total_coins,
                    userState.userBase?.allCoinsEarned.toString()
                ),
                // Coins spent
                JourneyItem(
                    R.string.coins_spent,
                    userState.coinsSpent.toString()
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
                // How old their profile is / how long since creation
                JourneyItem(
                    R.string.account_age,
                    timeSinceCreated
                ),
                // The title of the reminder they completed most and how many times it has been done.
                JourneyItem(
                    R.string.most_completed_reminder,
                    value = mostCompletedReminder
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
            ) {
                // Journey Title
                Text(
                    text = stringResource(R.string.journey_stats),
                    color = AppTheme.colors.SecondaryOne,
                    style = AppTheme.textStyles.HeadingThree.copy(
                        shadow = Shadow(
                            color = AppTheme.colors.DropShadow,
                            offset = Offset(2f, 2f),
                            blurRadius = 2f,
                        )
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.Top)
                )
                Spacer(Modifier.width(8.dp))
                // Info Icon
                ShadowedIcon(
                    imageVector = ImageVector.vectorResource(R.drawable.info),
                    tint = AppTheme.colors.FadedGray,
                    modifier = Modifier
                        .size(20.dp)
//                        .offset(y = 9.74.dp)
                        .clickable {
                            showJourneyTip.value = !showJourneyTip.value
                        }
                        .align(Alignment.Top)
                )
                Spacer(Modifier.width(16.dp))
                CircleButton(
                    modifier = Modifier.align(Alignment.Top),
                    onClick = { navController.popBackStack() },
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
                        .verticalScroll(scrollState),
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
                                        modifier = Modifier.weight(1f)
                                            .align(Alignment.CenterVertically),
                                        text = stringResource(item.name),
                                        color = AppTheme.colors.Gray,
                                        style = AppTheme.textStyles.Default
                                    )
                                    Text(
                                        modifier = Modifier.weight(.45f)
                                            .align(Alignment.CenterVertically),
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

                ScrollFadeEdges(
                    scrollState = scrollState,
                )
            }
        }
    }

    // Show tooltip
    if (showJourneyTip.value) {
        UserJourneyToolTip(showJourneyTip)
    }
}

/**
 * A data class to store statistics to be displayed in the user journey screen.
 * Requires a name of the stat and the value to be displayed for it.
 * @author Elyseia
 */
data class JourneyItem(
    val name: Int,
    val value: String,
)

/**
 * A data class that creates sections with different Journey Items inside
 * Takes in a section name for displaying on the UserJourney screen
 * Takes in a list of Journey Items that will be displayed within that section
 * @author Elyseia
 */
data class JourneySection(
    val title: Int,
    val items: List<JourneyItem>
)