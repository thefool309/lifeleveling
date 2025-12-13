package com.lifeleveling.app

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import com.lifeleveling.app.data.UserManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Overhead class that controls the application state.
 * MainActivity will refresh with UI load in but application will only go with the apps opening and closing
 * @author Elyseia
 */
class LifeLevelingApplication : Application(), DefaultLifecycleObserver {
    lateinit var userManager: UserManager

    override fun onCreate() {
        super<Application>.onCreate()

        userManager = UserManager()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        CoroutineScope(Dispatchers.IO).launch {
            // Put this in if a save when the application closes is wanted
//            userManager.saveUser()
        }
    }
}