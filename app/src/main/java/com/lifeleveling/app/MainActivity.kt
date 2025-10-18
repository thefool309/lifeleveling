package com.lifeleveling.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.theme.LifelevelingTheme


// Temp Check to ensure firebase connection
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lifeleveling.app.navigation.Constants
import com.lifeleveling.app.ui.theme.SplashAnimationOverlay
import com.lifeleveling.app.navigation.TempCalendarScreen
import com.lifeleveling.app.navigation.TempSettingsScreen
import com.lifeleveling.app.navigation.TempStatsScreen
import com.lifeleveling.app.navigation.TempStreaksScreen
import com.lifeleveling.app.ui.screens.HomeScreen
import com.lifeleveling.app.ui.theme.HideSystemBars
import com.lifeleveling.app.ui.theme.StartLogic

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var isDarkTheme = true  // TODO: Change to pull on saved preference
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = Color.Transparent.toArgb(),
                darkScrim = Color.Transparent.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = Color.Transparent.toArgb(),
                darkScrim = Color.Transparent.toArgb()
            )
        )

        setContent {
            // Setting theme
            val isDarkThemeState = remember { mutableStateOf(isDarkTheme) }

            // System icon change
            LaunchedEffect(isDarkThemeState.value) {
                enableEdgeToEdge(
                    statusBarStyle = if(isDarkThemeState.value) {
                        SystemBarStyle.dark(Color.Transparent.toArgb())
                    } else {
                        SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
                    },
                    navigationBarStyle = if(isDarkThemeState.value) {
                        SystemBarStyle.dark(Color.Transparent.toArgb())
                    } else {
                        SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
                    }
                )
            }

            // Startup logic and Splash Screen values
            val startLogic: StartLogic = viewModel() // TODO: Go to this file to put in start logic
            val isInitialized by startLogic.isInitialized.collectAsState()
            var appReady by remember { mutableStateOf(false) }
            val startTime = remember { System.currentTimeMillis() }
            val minSplashTime = 2000L // How long Splash shows at a minimum for loading reassurance

            // Splash Screen effect while loading
            LaunchedEffect(isInitialized) {
                if (isInitialized) {
                    val elapsed = System.currentTimeMillis() - startTime
                    val remaining = minSplashTime - elapsed
                    if (remaining > 0) kotlinx.coroutines.delay(remaining)
                    appReady = true
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                // Hide system bars
                HideSystemBars()

                // If app isn't ready, show splash
                if (!appReady) {
                    SplashAnimationOverlay()
                } else {
                    // App is ready, go to ui logic
                    LifelevelingTheme(darkTheme = isDarkThemeState.value) {
                        val navController = rememberNavController()

                        Surface(color = AppTheme.colors.Background) {
                            Scaffold(
                                //contentWindowInsets = WindowInsets(0,0,0,0),  // Add this if bottom nav keeps jumping up
                                bottomBar = {
                                    BottomNavigationBar(navController = navController)
                                }, content = { padding ->
                                    NavHostContainer(navController = navController, padding = padding)
                                }
                            )
                        }
                    }
                }
            }
        }

        // ==== TEMP healthcheck: sign in anonymously, then write a doc ====
        FirebaseAuth.getInstance()
            .signInAnonymously()
            .addOnSuccessListener {
                Log.d("FB", "Anon sign-in OK: uid=${it.user?.uid}")
                Firebase.firestore.collection("healthchecks")
                    .add(mapOf("ts" to Timestamp.now(), "source" to "android"))
                    .addOnSuccessListener { docRef ->
                        Log.d("FB", "Healthcheck doc: ${docRef.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FB", "Healthcheck failed", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FB", "Anon sign-in failed", e)
            }
        // ==== END TEMP ====
    }
}

@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.padding(paddingValues = padding),
        builder = {
            composable("calendar") {
                TempCalendarScreen()
            }
            composable("stats") {
                StatsScreen()
            }
            composable("home") {
                HomeScreen()
            }
            composable("streaks") {
                TempStreaksScreen()
            }
            composable("settings") {
                TempSettingsScreen()
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar(
        containerColor = AppTheme.colors.DarkerBackground,
        modifier = Modifier.height(80.dp)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Constants.BottomNavItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = { navController.navigate(navItem.route) },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(navItem.icon),
                        contentDescription = navItem.route,
                        modifier = Modifier.size(40.dp),
                        )
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppTheme.colors.BrandOne,
                    unselectedIconColor = AppTheme.colors.BrandTwo,
                )
            )
        }
    }
}