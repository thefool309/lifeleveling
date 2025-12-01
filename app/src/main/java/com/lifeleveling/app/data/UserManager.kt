package com.lifeleveling.app.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifeleveling.app.auth.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserManager(val user: Users = Users(),
                  val authViewModel: AuthViewModel = AuthViewModel(),
                  val firestoreRepository: FirestoreRepository = FirestoreRepository())
    : ViewModel() {
    private val mutableUserState = MutableStateFlow<UserState>(UserState(user = user))
    private val UserState: StateFlow<UserState> = mutableUserState.asStateFlow()

    init {

    }

    fun getUserObject() : Users {
        return user
    }


}