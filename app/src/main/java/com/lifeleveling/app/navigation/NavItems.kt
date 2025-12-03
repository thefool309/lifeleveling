package com.lifeleveling.app.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.screens.HomeScreen
import com.lifeleveling.app.ui.screens.NotificationScreen
import com.lifeleveling.app.ui.screens.SelfCareScreen
import com.lifeleveling.app.ui.screens.SettingScreen
import com.lifeleveling.app.ui.screens.StatsScreenRoute
import com.lifeleveling.app.ui.screens.StreaksScreen
import com.lifeleveling.app.ui.screens.TermsAndPrivacyScreen
import com.lifeleveling.app.ui.screens.UserJourneyScreen
import com.lifeleveling.app.ui.theme.AppTheme

// Each nav item set up
data class BottomNavItem(
    val icon: Int,
    val route: String,
)

// Group of all items in the menu
object Constants {
    val BottomNavItems = listOf(
        // Calendar screen
        BottomNavItem(
            icon = R.drawable.calendar,
            route = "calendar"
        ),
        // Stats screen
        BottomNavItem(
            icon = R.drawable.bar_graph,
            route = "stats"
        ),
        // Home
        BottomNavItem(
            icon = R.drawable.person,
            route = "home"
        ),
        // Streaks screen
        BottomNavItem(
            icon = R.drawable.award_ribbon,
            route = "streaks"
        ),
        // Settings screen
        BottomNavItem(
            icon = R.drawable.settings_icon,
            route = "settings"
        )
    )
}

@Composable
fun NavHostContainer(
    navController: NavHostController,
    onSignOut: () -> Unit,
    padding: PaddingValues,
    isDarkThemeState: MutableState<Boolean>,
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.Companion.padding(paddingValues = padding),
        builder = {
            composable("calendar") {
                TempCalendarScreen()
            }
            composable("stats") {
                StatsScreenRoute()
            }
            composable("home") {
                HomeScreen()
            }
            composable("streaks") {
                StreaksScreen(navController = navController)
            }
            composable("settings") {
                SettingScreen(
                    navController = navController,
                    isDarkTheme = isDarkThemeState.value,
                    onThemeChange = { newIsDark ->
                        isDarkThemeState.value = newIsDark
                    },
                    onSignOut = onSignOut
                )
            }
            composable("notifications") {
                NotificationScreen(navController = navController)
            }
            composable("selfCare") {
                SelfCareScreen(navController = navController)
            }
            composable("termsAndPrivacy") {
                TermsAndPrivacyScreen(navController = navController)
            }
            composable("journey_stats") {
                UserJourneyScreen(navController = navController)
            }
        }
    )
}

/**
 * New beautiful nav bar. Look at it. LOOK AT IT!
 * @author Elyseia fixed this
 */
@Composable
fun CustomNavBar(
    navController: NavHostController,
    modifier: Modifier = Modifier.Companion,
    height: Dp = 80.dp,
    indicatorSize: Dp = 60.dp,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Background
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(AppTheme.colors.DarkerBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.Companion.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            Constants.BottomNavItems.forEach { navItem ->
                val selected = currentRoute == navItem.route

                Box(
                    contentAlignment = Alignment.Companion.Center,
                    modifier = Modifier.Companion
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            navController.navigate(navItem.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Companion.Center,
                        modifier = Modifier.Companion
                            .size(indicatorSize)
                            .background(
                                color = if (selected) AppTheme.colors.BrandOne else Color.Companion.Transparent,
                                shape = CircleShape
                            )
                    ) {
                        if (selected) {
                            Image(
                                modifier = Modifier.Companion
                                    .clip(CircleShape)
                                    .matchParentSize(),
                                painter = painterResource(R.drawable.circle_button_innerlight),
                                contentDescription = null,
                            )
                        }

                        Icon(
                            imageVector = ImageVector.Companion.vectorResource(navItem.icon),
                            contentDescription = navItem.route,
                            modifier = Modifier.Companion.size(40.dp),
                            tint = if (selected) AppTheme.colors.DarkerBackground else AppTheme.colors.BrandTwo
                        )
                    }
                }
            }
        }
    }
}