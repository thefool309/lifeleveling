package com.lifeleveling.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import com.lifeleveling.app.navigation.SplashAnimationOverlay
import com.lifeleveling.app.navigation.TempCalendarScreen
import com.lifeleveling.app.navigation.TempHomeScreen
import com.lifeleveling.app.navigation.TempSettingsScreen
import com.lifeleveling.app.navigation.TempStatsScreen
import com.lifeleveling.app.navigation.TempStreaksScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash Screen to show before app loads
        val splashScreen = installSplashScreen()
        var keepSplash = true
        splashScreen.setKeepOnScreenCondition { keepSplash }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Keep splash until app is ready
            var appReady by remember { mutableStateOf(false) }
            if (!appReady) {
                SplashAnimationOverlay()
            }

            // Setting theme
            val isDarkTheme = remember { mutableStateOf(true) }

            LifelevelingTheme(darkTheme = isDarkTheme.value) {
                val navController = rememberNavController()

                Surface(color = AppTheme.colors.Background) {
                    Scaffold(
                        bottomBar = {
                            BottomNavigatioonBar(navController = navController)
                        }, content = { padding ->
                            NavHostContainer(navController = navController, padding = padding)
                        }
                    )
                }
            }

            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000)
                appReady = true
                keepSplash = false
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
                TempStatsScreen()
            }
            composable("home") {
                TempHomeScreen()
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
fun BottomNavigatioonBar(navController: NavHostController) {
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