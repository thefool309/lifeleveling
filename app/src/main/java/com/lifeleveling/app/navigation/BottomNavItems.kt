package com.lifeleveling.app.navigation

import com.lifeleveling.app.R

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