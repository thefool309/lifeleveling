package com.lifeleveling.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.ktx.auth
import com.lifeleveling.app.BuildConfig
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.theme.LifelevelingTheme
import com.lifeleveling.app.navigation.Constants
import com.lifeleveling.app.ui.theme.SplashAnimationOverlay
import com.lifeleveling.app.navigation.TempCalendarScreen
import com.lifeleveling.app.ui.screens.CalendarScreen
import com.lifeleveling.app.ui.screens.CreateAccountScreen


import com.lifeleveling.app.ui.screens.HomeScreen
import com.lifeleveling.app.ui.screens.NotificationScreen
import com.lifeleveling.app.ui.screens.SelfCareScreen
import com.lifeleveling.app.ui.screens.SettingScreen
import com.lifeleveling.app.ui.screens.SignIn

import com.lifeleveling.app.ui.screens.StatsScreenRoute
import com.lifeleveling.app.ui.screens.TermsAndPrivacyScreen
import com.lifeleveling.app.ui.theme.HideSystemBars
import com.lifeleveling.app.ui.theme.StartLogic
// Temp Check to ensure firebase connection
import com.lifeleveling.app.ui.screens.StreaksScreen
import com.lifeleveling.app.util.AndroidLogger
import kotlinx.coroutines.launch
import com.lifeleveling.app.ui.screens.UserJourneyScreen
import com.lifeleveling.app.util.ILogger

class MainActivity : ComponentActivity() {

    private lateinit var googleLauncher: ActivityResultLauncher<Intent>
    private val authVm: com.lifeleveling.app.auth.AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // It is important to do this before any Firebase use
        if (BuildConfig.DEBUG) {
            // 10.0.2.2 is the special IP address to connect to the 'localhost' of
            // the host computer from an Android emulator.
            Firebase.firestore.useEmulator("10.0.2.2", 8080)
            Firebase.auth.useEmulator("10.0.2.2", 9099)
        }


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


        // Register the launcher and forward the result to AuthViewModel
        googleLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            authVm.handleGoogleResultIntent(result.data)
        }

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
            val startLogic: StartLogic = viewModel()
            val isInitialized by startLogic.isInitialized.collectAsState()
            var appReady by remember { mutableStateOf(false) }
            val startTime = remember { System.currentTimeMillis() }
            val minSplashTime = 2000L // How long Splash shows at a minimum for loading reassurance

            // Auth state from VM
            val authState by authVm.ui.collectAsState()

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
                    // Show SignIn when not authenticated; show your app when signed in
                    LifelevelingTheme(darkTheme = isDarkThemeState.value) {
                        if (authState.user == null) {

                            val preAuthNav = rememberNavController()
                            NavHost(navController = preAuthNav, startDestination = "signIn") {
                                composable("signIn") {
                                    // -------- Sign In UI --------
                                    val email = remember { mutableStateOf("") }
                                    val password = remember { mutableStateOf("") }
                                    val logger : ILogger = AndroidLogger()
                                    val scope = rememberCoroutineScope()
                                    SignIn(
                                        // Auth using email and password
                                        onLogin = {
                                            scope.launch {
                                                try {
                                                    authVm.signInWithEmailPassword(email.value, password.value, logger)
                                                } catch (e: FirebaseAuthInvalidCredentialsException) {
                                                    logger.e(
                                                        "FB",
                                                        "createUserWithEmailAndPassword failed due to Invalid Credentials: ",
                                                        e
                                                    )
                                                }
                                            }

                                            /* email/password auth */
                                        },

                                        // Auth with Google Sign In
                                        onGoogleLogin = {
                                            authVm.beginGoogleSignIn()
                                            val intent = authVm.googleClient(this@MainActivity).signInIntent
                                            googleLauncher.launch(intent)
                                        },

                                        // Create account screen
                                        onCreateAccount = {
                                            preAuthNav.navigate("createAccount") {
                                                //launchSingleTop = false
                                            }
                                        },
                                        email,
                                        password,
                                        authState = authState,
                                        onDismissError = {authVm.clearError()}
                                    )
                                }
                                composable("createAccount") {
                                    val email = remember { mutableStateOf("") }
                                    val password = remember {mutableStateOf("")}
                                    val logger : ILogger = AndroidLogger()
                                    val scope = rememberCoroutineScope()
                                    CreateAccountScreen(
                                        onJoin = {/*TODO: Handle sign-up logic*/
                                            scope.launch {
                                                try {
                                                    authVm.createUserWithEmailAndPassword(email.value, password.value, logger)
                                                }
                                                catch (e: FirebaseAuthInvalidCredentialsException) {
                                                    logger.e("FB", "createUserWithEmailAndPassword failed due to Invalid Credentials: ", e)
                                                }
                                            }
                                                 },
                                        onGoogleLogin = {
                                            authVm.beginGoogleSignIn()
                                            val intent = authVm.googleClient(this@MainActivity).signInIntent
                                            googleLauncher.launch(intent)
                                        },
                                        onLog = {
                                            preAuthNav.navigate("signIn") // Back to Sign-In
                                        },
                                        email,
                                        password
                                    )
                                }
                            }
                        } else {

                            // Main App UI
                            val navController = rememberNavController()
                            Surface(color = AppTheme.colors.Background) {
                                Scaffold(
                                    bottomBar = { BottomNavigationBar(navController = navController) },
                                ) { padding ->
                                    NavHostContainer(
                                        navController = navController,
                                        padding = padding,
                                        isDarkThemeState = isDarkThemeState,
                                        onSignOut = {authVm.signOut(this@MainActivity)},
                                        onDeleteAccount = {
                                            val logger = AndroidLogger()
                                            authVm.deleteAccount(logger)}
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavHostContainer(
    navController: NavHostController,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    padding: PaddingValues,
    isDarkThemeState: MutableState<Boolean>,
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.padding(paddingValues = padding),
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
            composable ("notifications"){
                NotificationScreen(navController = navController)
            }
            composable ("selfCare"){
                SelfCareScreen(navController = navController)
            }
            composable ("termsAndPrivacy") {
                TermsAndPrivacyScreen(navController = navController)
            }
            composable ("journey_stats") {
                UserJourneyScreen(navController = navController)
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