package com.lifeleveling.app.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StartLogic : ViewModel() {
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

    init {
        viewModelScope.launch {
            // TODO: Put startup logic here: calls to database, loading local settings
            delay(3000) // Simulated loading, delete this
            _isInitialized.value = true
        }
    }
}