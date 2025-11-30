package com.lifeleveling.app

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
import com.lifeleveling.app.ui.screens.CalendarScreen
import com.lifeleveling.app.ui.screens.CreateAccountScreen


import com.lifeleveling.app.ui.screens.HomeScreen
import com.lifeleveling.app.ui.screens.NotificationScreen
import com.lifeleveling.app.ui.screens.SelfCareScreen
import com.lifeleveling.app.ui.screens.SettingScreen
import com.lifeleveling.app.ui.screens.SignIn

import com.lifeleveling.app.ui.screens.StatsScreenRoute
import com.lifeleveling.app.ui.screens.TermsAndPrivacyScreen
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
import android.Manifest
import com.lifeleveling.app.BuildConfig
class MainActivity : ComponentActivity() {


    val logTag = "MainActivity"
    val logger = AndroidLogger()
    private val requestPermissionLauncher = registerForActivityResult( ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            // FCM handles everything here there is nothing else to do, but I added it in case we find something we wish to do here later
        }
        else {
            // a Toast message informing the user they denied permissions and will not recieve notifications.
            // pretty much anywhere you see a Toast message, it is a filler that can be replaced if you wish
            if (BuildConfig.DEBUG) {
                Toast.makeText(applicationContext, R.string.permission_denied_notif, Toast.LENGTH_SHORT).show()
                logger.d("Permissions", getString(R.string.permission_denied_notif))
            }
        }
    }

    /**
     * function for asking the user for notification permission. Will be kept at the top of whatever activity or fragment is being used
     * This function should only be necessary in android TIRAMASU and higher so it is wrapped in a check to see the androidVersion
     * if using a lower version of android than 12 the phone will request notification permissions automatically the first time a notification channel is created
     * MORE INFO: https://firebase.google.com/docs/cloud-messaging/get-started?platform=android#request-permission13
     * @see androidx.core.content.ContextCompat
     * @see android.Manifest
     * @see android.content.pm.PackageManager
     * @see android.content.pm.PackageManager.PERMISSION_GRANTED
     */
    private fun askNotificationPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // this condition is checking if the permission is granted already
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                //FCM SDK (and our app) can post notifications under this condition
                // no action is necessary
            }
            // this if statement compares against a lot of cases.
            //
            else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //  by granting the POST_NOTIFICATION permission.
                //  Should have an "Ok" and "No thanks" button. If the user selects "OK,' directly request the permission.
                //  If the user selects "No Thanks," allow the user to continue without notifications.
                //  The above is paraphrased from the documentation link listed above MORE ON NOTIFICATION PERMISSIONS BEST PRACTICES:

                // the filler solution (Can be commented out. mostly just here for an example )
                Toast.makeText(applicationContext, "We're finna ask you for notifications permissions", Toast.LENGTH_SHORT).show()
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            else {
                // ask for permissions Directly
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    /**
     *  setup emulators is a helper function for the MainActivity to check and see if we need to setup firebase emulators or run on prod
     *  toggle Property useFirebaseEmulators to true to enable the firebase emulators.
     */


    private fun setupEmulators() {
        //toggle this to true if you want to use firebaseEmulators.
        val useFirebaseEmulators = true
        if (useFirebaseEmulators) {
            //separate if for separate error message
            // It is important to do this before any Firebase use
            if (BuildConfig.DEBUG) {
                // 10.0.2.2 is the special IP address to connect to the 'localhost' of
                // the host computer from an Android emulator.
                try{
                    Firebase.firestore.useEmulator("10.0.2.2", 8080)
                    Firebase.auth.useEmulator("10.0.2.2", 9099)
                    logger.d(logTag, "Using Firebase Emulator...")
                }
                catch(ex: Exception) {
                    logger.e(logTag, "Could not connect to Firebase Emulators. error message: ",ex)
                }
            }
            else {
                logger.e(logTag, "Not in a Debug Build. Using the Production dataset...")
            }
        }
        else {
            logger.d(logTag, "useFirebaseEmulator is false. Using the Production dataset...")

        }
    }

    private lateinit var googleLauncher: ActivityResultLauncher<Intent>
    private val authVm: com.lifeleveling.app.auth.AuthViewModel by viewModels()
    private val userManager: UserManager
        get() = (application as LifeLevelingApplication).userManager

//    private lateinit var googleLauncher: ActivityResultLauncher<Intent>
//    private val authVm: com.lifeleveling.app.auth.AuthViewModel by viewModels()

    /**
     *  When the activity enters the Resumed state, it comes to the foreground, and the system invokes the onResume() callback. This is the state in which the app interacts with the user. The app stays in this state until something happens to take focus away from the app, such as the device receiving a phone call, the user navigating to another activity, or the device screen turning off.
     *
     * When the activity moves to the Resumed state, any lifecycle-aware component tied to the activity's lifecycle receives the ON_RESUME event. This is where the lifecycle components can enable any functionality that needs to run while the component is visible and in the foreground, such as starting a camera preview.
     *
     * When an interruptive event occurs, the activity enters the Paused state and the system invokes the onPause() callback.
     *
     * If the activity returns to the Resumed state from the Paused state, the system once again calls the onResume() method. For this reason, implement onResume() to initialize components that you release during onPause() and to perform any other initializations that must occur each time the activity enters the Resumed state.
     * @see ActivityResultLauncher
     * @see ActivityResultContracts
     * @see android.app.Activity
     * @see Intent
     */
    override fun onResume() {
        super.onResume()
    }

    /**
     *  When the activity enters the Started state, the system invokes onStart(). This call makes the activity visible to the user as the app prepares for the activity to enter the foreground and become interactive. For example, this method is where the code that maintains the UI is initialized.
     *
     * When the activity moves to the Started state, any lifecycle-aware component tied to the activity's lifecycle receives the ON_START event.
     *
     * The onStart() method completes quickly and, as with the Created state, the activity does not remain in the Started state. Once this callback finishes, the activity enters the Resumed state and the system invokes the onResume() method.
     * @see ActivityResultLauncher
     * @see ActivityResultContracts
     * @see android.app.Activity
     * @see Intent
     */
    override fun onStart() {
        super.onStart()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()
        setupEmulators()

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

