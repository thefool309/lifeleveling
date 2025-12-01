package com.lifeleveling.app.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * basic samples of how these functions can be called in a CoroutineScope. This is not best practice to use in production code.
 */
class FirestoreSamples {

    // create a coroutine scope
    private val ioScope = CoroutineScope(Dispatchers.IO)


    // launch a suspend function
    fun launchSuspendFunction() {
        ioScope.launch {
            // call the suspend function here.
        }
    }
}

/**
 * in production android code it is better to use Activities/Fragments (lifecycleScope) or ViewModel (viewModelScope) scopes to launch coroutines so I created a ViewModel() for the sake of showing that
 *
  */

class FirestoreSamplesViewModel : ViewModel() {

    fun launchSuspendFunction() {
        // viewModels come with a scope to launch coroutines. Something I didn't know up to this point or else I would have made some different decisions.
        viewModelScope.launch(Dispatchers.IO) {
            // call the suspend function here
        }
    }
}