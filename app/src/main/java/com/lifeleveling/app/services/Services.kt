package com.lifeleveling.app.services

import com.lifeleveling.app.data.Users

class Services {
    // TODO: Function to create user and store in firebase
    fun CreateUser(): Users {
        val result = Users();
        return result;
    }
    // TODO: function to edit user in firebase
    fun EditUser(user: Users) {

    }
    // TODO: function to retrieve user information from firebase
    fun GetUser(): Users {
        val result = Users();
        return result;
    }
}