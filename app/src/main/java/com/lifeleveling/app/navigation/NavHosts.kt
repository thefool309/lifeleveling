package com.lifeleveling.app.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lifeleveling.app.ui.screens.CalendarScreen
import com.lifeleveling.app.ui.screens.HomeScreen
import com.lifeleveling.app.ui.screens.NotificationScreen
import com.lifeleveling.app.ui.screens.SelfCareScreen
import com.lifeleveling.app.ui.screens.SettingScreen
import com.lifeleveling.app.ui.screens.StatsScreenRoute
import com.lifeleveling.app.ui.screens.StreaksScreen
import com.lifeleveling.app.ui.screens.TermsAndPrivacyScreen
import com.lifeleveling.app.ui.screens.UserJourneyScreen

@Composable
fun MainScreenNavigationHost(
    navController: NavHostController,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    padding: PaddingValues,
    isDarkThemeState: MutableState<Boolean>,
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.Companion.padding(paddingValues = padding),
        builder = {
            composable("calendar") {
                CalendarScreen()
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
                    onSignOut = onSignOut,
                    onDeleteAccount = onDeleteAccount
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