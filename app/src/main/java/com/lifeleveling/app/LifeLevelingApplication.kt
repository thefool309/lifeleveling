package com.lifeleveling.app

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import com.lifeleveling.app.data.UserManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LifeLevelingApplication : Application(), DefaultLifecycleObserver {
    lateinit var userManager: UserManager

    override fun onCreate() {
        super<Application>.onCreate()

        userManager = UserManager()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        CoroutineScope(Dispatchers.IO).launch {
            userManager.saveUser()
        }
    }
}