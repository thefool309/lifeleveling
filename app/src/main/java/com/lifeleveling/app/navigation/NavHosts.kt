package com.lifeleveling.app.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lifeleveling.app.data.LocalNavController
import com.lifeleveling.app.data.LocalUserManager
import com.lifeleveling.app.ui.screens.CalendarScreen
import com.lifeleveling.app.ui.screens.CreateAccountScreen
import com.lifeleveling.app.ui.screens.HomeScreen
import com.lifeleveling.app.ui.screens.NotificationScreen
import com.lifeleveling.app.ui.screens.SelfCareScreen
import com.lifeleveling.app.ui.screens.SettingScreen
import com.lifeleveling.app.ui.screens.SignIn
import com.lifeleveling.app.ui.screens.StatsScreen
import com.lifeleveling.app.ui.screens.StatsScreenRoute
import com.lifeleveling.app.ui.screens.StreaksScreen
import com.lifeleveling.app.ui.screens.TermsAndPrivacyScreen
import com.lifeleveling.app.ui.screens.UserJourneyScreen

/**
 * Holds the navigation logic.
 * Listens for if the user is logged in and only shows inside screens with the navigation bar if the user is authenticated and logged in.
 * Add screens to be navigated too here within the NavHost
 * @author Elyseia
 */
@Composable
fun AppNavHost() {
    val navController = LocalNavController.current
    val userManager = LocalUserManager.current
    val userState by userManager.uiState.collectAsState()

    // Shows loading screen as it looks if the user is logged in
    if (userState.isLoading) {

    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val hideBottomBar = listOf("signIn", "createAccount", "forgotPassword")
    val showBottomBar = currentRoute !in hideBottomBar

    LaunchedEffect(userState.isLoggedIn) {
        if (!userState.isLoggedIn) {
            navController.navigate("signIn") {
                popUpTo(0)
            }
        }
    }

    Scaffold(
        bottomBar = {
            if(showBottomBar) {
                CustomNavBar(navController)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (userState.isLoggedIn) "home" else "signIn",
            modifier = Modifier.padding(padding)
        ) {
            // Auth Screens
            composable("signIn") { SignIn() }
            composable("createAccount") { CreateAccountScreen() }
//            composable("forgotPassword") { ForgotPasswordScreen() }

            // Main Screens
            composable("home") { HomeScreen() }
            composable("calendar") { CalendarScreen() }
            composable("stats") { TempStatsScreen()/*StatsScreen()*/ }
            composable("streaks") { StreaksScreen() }
            composable("settings") { SettingScreen() }

            composable("notifications") { NotificationScreen() }
            composable("selfCare") { SelfCareScreen() }
            composable("termsAndPrivacy") { TermsAndPrivacyScreen() }
            composable("journeyStats") { UserJourneyScreen() }
        }
    }
}


//@Composable
//fun MainScreenNavigationHost(
//    navController: NavHostController,
//    onSignOut: () -> Unit,
//    onDeleteAccount: () -> Unit,
//    padding: PaddingValues,
//    isDarkThemeState: MutableState<Boolean>,
//) {
//    NavHost(
//        navController = navController,
//        startDestination = "home",
//        modifier = Modifier.Companion.padding(paddingValues = padding),
//        builder = {
//            composable("calendar") {
//                CalendarScreen()
//            }
//            composable("stats") {
//                StatsScreenRoute()
//            }
//            composable("home") {
//                HomeScreen()
//            }
//            composable("streaks") {
//                StreaksScreen(navController = navController)
//            }
//            composable("settings") {
//                SettingScreen(
//                    navController = navController,
//                    isDarkTheme = isDarkThemeState.value,
//                    onThemeChange = { newIsDark ->
//                        isDarkThemeState.value = newIsDark
//                    },
//                    onSignOut = onSignOut,
//                    onDeleteAccount = onDeleteAccount
//                )
//            }
//            composable("notifications") {
//                NotificationScreen(navController = navController)
//            }
//            composable("selfCare") {
//                SelfCareScreen(navController = navController)
//            }
//            composable("termsAndPrivacy") {
//                TermsAndPrivacyScreen(navController = navController)
//            }
//            composable("journey_stats") {
//                UserJourneyScreen(navController = navController)
//            }
//        }
//    )
//}