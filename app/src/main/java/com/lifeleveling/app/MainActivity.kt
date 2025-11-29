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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.BuildConfig
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lifeleveling.app.data.LocalNavController
import com.lifeleveling.app.data.LocalUserManager
import com.lifeleveling.app.data.UserManager
import com.lifeleveling.app.navigation.AppNavHost
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.theme.LifelevelingTheme
import com.lifeleveling.app.navigation.CustomNavBar
//import com.lifeleveling.app.navigation.MainScreenNavigationHost
import com.lifeleveling.app.ui.theme.SplashAnimationOverlay
import com.lifeleveling.app.ui.screens.*
import com.lifeleveling.app.ui.theme.HideSystemBars
import com.lifeleveling.app.ui.theme.LoadingOverlay
import com.lifeleveling.app.ui.theme.StartLogic
import com.lifeleveling.app.ui.theme.StartLogicFactory
// Temp Check to ensure firebase connection
import com.lifeleveling.app.util.AndroidLogger
import kotlinx.coroutines.launch
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val userManager: UserManager
        get() = (application as LifeLevelingApplication).userManager

//    private lateinit var googleLauncher: ActivityResultLauncher<Intent>
//    private val authVm: com.lifeleveling.app.auth.AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Global edge to edge hides the system bars
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
            val navController = rememberNavController()
            val userState by userManager.uiState.collectAsState()

            // Splash logic startup ---------------------------------------
            val startLogic: StartLogic = viewModel(factory = StartLogicFactory(userManager))
            val isInitialized by startLogic.isInitialized.collectAsState()
            var appReady by remember { mutableStateOf(false) }
            val startTime = remember { System.currentTimeMillis() }
            val minSplashTime = 2000L

            LaunchedEffect(isInitialized) {
                if (isInitialized) {
                    val elapsed = System.currentTimeMillis() - startTime
                    val remaining = minSplashTime - elapsed
                    if (remaining > 0) delay(remaining)
                    appReady = true
                }
            }
            // End splash -------------------------------------------------

            CompositionLocalProvider(
                LocalUserManager provides userManager,
                LocalNavController provides navController,
            ) {
                LifelevelingTheme(
                    darkTheme = userState.userData?.isDarkTheme ?: true
                ) {
                    // System icon change on navigation bars to ensure they are visible when pulled
                    LaunchedEffect(userState.userData?.isDarkTheme) {
                        enableEdgeToEdge(
                            statusBarStyle = if(userState.userData?.isDarkTheme == true) {
                                SystemBarStyle.dark(Color.Transparent.toArgb())
                            } else {
                                SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
                            },
                            navigationBarStyle = if(userState.userData?.isDarkTheme == true) {
                                SystemBarStyle.dark(Color.Transparent.toArgb())
                            } else {
                                SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
                            }
                        )
                    }

                    // Keeps system bars hidden
                    HideSystemBars()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        // Checks if the app is done loading, shows overlay at start
                        if (!appReady) { SplashAnimationOverlay() }
                        else {
                            // Main screens
                            AppNavHost()
                            // Will show a loading wheel if the state is showing it is loading
                            if(userState.isLoading) LoadingOverlay()
                        }
                    }
                }
            }
        }
//
//        // It is important to do this before any Firebase use
//        if (BuildConfig.DEBUG) {
//            Firebase.firestore.useEmulator("10.0.2.2", 8080)
//            Firebase.auth.useEmulator("10.0.2.2", 9099)
//        }
//
//
//        var isDarkTheme = true  // TODO: Change to pull on saved preference
//        enableEdgeToEdge(
//            statusBarStyle = SystemBarStyle.auto(
//                lightScrim = Color.Transparent.toArgb(),
//                darkScrim = Color.Transparent.toArgb()
//            ),
//            navigationBarStyle = SystemBarStyle.auto(
//                lightScrim = Color.Transparent.toArgb(),
//                darkScrim = Color.Transparent.toArgb()
//            )
//        )
//
//
//        // Register the launcher and forward the result to AuthViewModel
//        googleLauncher = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { result ->
//            authVm.handleGoogleResultIntent(result.data)
//        }
//
//        setContent {
//            // Setting theme
//            val isDarkThemeState = remember { mutableStateOf(isDarkTheme) }
//
//            // System icon change
//            LaunchedEffect(isDarkThemeState.value) {
//                enableEdgeToEdge(
//                    statusBarStyle = if(isDarkThemeState.value) {
//                        SystemBarStyle.dark(Color.Transparent.toArgb())
//                    } else {
//                        SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
//                    },
//                    navigationBarStyle = if(isDarkThemeState.value) {
//                        SystemBarStyle.dark(Color.Transparent.toArgb())
//                    } else {
//                        SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
//                    }
//                )
//            }
//
//            // Startup logic and Splash Screen values
//            val startLogic: StartLogic = viewModel()
//            val isInitialized by startLogic.isInitialized.collectAsState()
//            var appReady by remember { mutableStateOf(false) }
//            val startTime = remember { System.currentTimeMillis() }
//            val minSplashTime = 2000L // How long Splash shows at a minimum for loading reassurance
//
//            // Auth state from VM
//            val authState by authVm.ui.collectAsState()
//
//            // Splash Screen effect while loading
//            LaunchedEffect(isInitialized) {
//                if (isInitialized) {
//                    val elapsed = System.currentTimeMillis() - startTime
//                    val remaining = minSplashTime - elapsed
//                    if (remaining > 0) kotlinx.coroutines.delay(remaining)
//                    appReady = true
//                }
//            }
//
//            Box(modifier = Modifier.fillMaxSize()) {
//                // Hide system bars
//                HideSystemBars()
//
//                // If app isn't ready, show splash
//                if (!appReady) {
//                    SplashAnimationOverlay()
//                } else {
//                    // Show SignIn when not authenticated; show your app when signed in
//                    LifelevelingTheme(darkTheme = isDarkThemeState.value) {
//                        if (authState.user == null) {
//
//                            val preAuthNav = rememberNavController()
//                            NavHost(navController = preAuthNav, startDestination = "signIn") {
//                                composable("signIn") {
//                                    // -------- Sign In UI --------
//                                    val email = remember { mutableStateOf("") }
//                                    val password = remember { mutableStateOf("") }
//                                    val logger : ILogger = AndroidLogger()
//                                    val scope = rememberCoroutineScope()
//                                    SignIn(
//                                        // Auth using email and password
//                                        onLogin = {
//                                            scope.launch {
//                                                try {
//                                                    authVm.signInWithEmailPassword(email.value, password.value, logger)
//                                                } catch (e: FirebaseAuthInvalidCredentialsException) {
//                                                    logger.e(
//                                                        "FB",
//                                                        "createUserWithEmailAndPassword failed due to Invalid Credentials: ",
//                                                        e
//                                                    )
//                                                }
//                                            }
//
//                                            /* email/password auth */
//                                        },
//
//                                        // Auth with Google Sign In
//                                        onGoogleLogin = {
//                                            authVm.beginGoogleSignIn()
//                                            val intent = authVm.googleClient(this@MainActivity).signInIntent
//                                            googleLauncher.launch(intent)
//                                        },
//
//                                        // Create account screen
//                                        onCreateAccount = {
//                                            preAuthNav.navigate("createAccount") {
//                                                //launchSingleTop = false
//                                            }
//                                        },
//                                        email,
//                                        password,
//                                        authState = authState,
//                                        onDismissError = {authVm.clearError()}
//                                    )
//                                }
//                                composable("createAccount") {
//                                    val email = remember { mutableStateOf("") }
//                                    val password = remember {mutableStateOf("")}
//                                    val logger : ILogger = AndroidLogger()
//                                    val scope = rememberCoroutineScope()
//                                    CreateAccountScreen(
//                                        onJoin = {/*TODO: Handle sign-up logic*/
//                                            scope.launch {
//                                                try {
//                                                    authVm.createUserWithEmailAndPassword(email.value, password.value, logger)
//                                                }
//                                                catch (e: FirebaseAuthInvalidCredentialsException) {
//                                                    logger.e("FB", "createUserWithEmailAndPassword failed due to Invalid Credentials: ", e)
//                                                }
//                                            }
//                                                 },
//                                        onGoogleLogin = {
//                                            authVm.beginGoogleSignIn()
//                                            val intent = authVm.googleClient(this@MainActivity).signInIntent
//                                            googleLauncher.launch(intent)
//                                        },
//                                        onLog = {
//                                            preAuthNav.navigate("signIn") // Back to Sign-In
//                                        },
//                                        email,
//                                        password
//                                    )
//                                }
//                            }
//                        } else {
//
//                            // Main App UI
//                            val navController = rememberNavController()
//                            Surface(color = AppTheme.colors.Background) {
//                                Scaffold(
//                                    bottomBar = { CustomNavBar(navController = navController) },
//                                ) { padding ->
//                                    MainScreenNavigationHost(
//                                        navController = navController,
//                                        padding = padding,
//                                        isDarkThemeState = isDarkThemeState,
//                                        onSignOut = { authVm.signOut(this@MainActivity) },
//                                        onDeleteAccount = {
//                                            val logger = AndroidLogger()
//                                            authVm.deleteAccount(logger)
//                                        }
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
}

