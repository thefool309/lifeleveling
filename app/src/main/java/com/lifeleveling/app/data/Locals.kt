package com.lifeleveling.app.data

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

/**
 * This will provide a variable for the global use of the UserManager
 * @author Elyseia
 */
val LocalUserManager = compositionLocalOf<UserManager> {
    error("No UserManager provided")
}

/**
 * This provides a variable for the global use of the NavController
 * @author Elyseia
 */
val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavController provided")
}