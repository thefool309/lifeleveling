package com.lifeleveling.app.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.lifeleveling.app.data.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 *  A wrapper around the starting load logic that will run while the splash screen is being shown.
 *  @param userManager The UserManager object that controls if the user is signed in or not
 *
 *  @author Elyseia, fdesouza1992
 */
class StartLogic(
    private val userManager: UserManager,
) : ViewModel() {
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized
    private var hasRun = false

    init {
        viewModelScope.launch {
            if(hasRun) return@launch
            hasRun = true

            _isInitialized.value = false
            try{
                FirebaseAuth.getInstance().currentUser?.reload()?.await()
            } catch(e: Exception) {

            }

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                // TODO: Load in the user
//                userManager.loadUser()
            } else {
                // User not found so set the flag to logged out.
                userManager.setLoggedOut()
            }

            _isInitialized.value = true
        }
    }
}

/**
 * A factory that controls the StartLogic
 * @author Elyseia
 */
class StartLogicFactory(
    private val userManager: UserManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StartLogic(userManager) as T
    }
}