package com.lifeleveling.app.data

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

val LocalUserManager = compositionLocalOf<UserManager> {
    error("No UserManager provided")
}

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavController provided")
}