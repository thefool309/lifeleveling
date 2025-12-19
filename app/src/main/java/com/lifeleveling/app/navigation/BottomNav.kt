package com.lifeleveling.app.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.theme.AppTheme

// Each nav item set up
/**
 * The setup information for every navigation item to be passed into the bottom nav bar.
 * @param icon The icon to be displayed
 * @param route The screen that clicking the item should take the user to
 * @author Elyseia
 */
data class BottomNavItem(
    val icon: Int,
    val route: String,
)

// Group of all items in the menu
/**
 * The list of the give main screens that will be showing up on the bottom navigation bar.
 * @author Elyseia
 */
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

/**
 * A custom created bottom navigation bar that follows the applications design themes.
 * Holds the clickable navigation items to direct the user through the app.
 * Will be only be displayed on screens the user can see if they are logged in
 * @param navController The controller that handles the actual routing to change the screens
 * @param height How tall to make the bar at the bottom of the screen
 * @param indicatorSize How large to make the circle indicator that is behind the icons
 * @author Elyseia
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
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Constants.BottomNavItems.forEach { navItem ->
                val selected = currentRoute == navItem.route

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
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
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(indicatorSize)
                            .background(
                                color = if (selected) AppTheme.colors.BrandOne else Color.Transparent,
                                shape = CircleShape
                            )
                    ) {
                        if (selected) {
                            Image(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .matchParentSize(),
                                painter = painterResource(R.drawable.circle_button_innerlight),
                                contentDescription = null,
                            )
                        }

                        Icon(
                            imageVector = ImageVector.vectorResource(navItem.icon),
                            contentDescription = navItem.route,
                            modifier = Modifier.size(40.dp),
                            tint = if (selected) AppTheme.colors.DarkerBackground else AppTheme.colors.BrandTwo
                        )
                    }
                }
            }
        }
    }
}