package com.example.mystoryapp.preferences

import android.content.Context
import com.example.mystoryapp.data.User

class UserPreference(context: Context) {
    companion object{
        private const val PREFS_NAME = "user_prefs"
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val USER_ID = "userId"
        private const val TOKEN = "token"
    }

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUser(u: User){
        val editor = preferences.edit()
        editor.putString(NAME, u.name)
        editor.putString(EMAIL, u.email)
        editor.putString(USER_ID, u.userId)
        editor.putString(TOKEN, u.token)
        editor.apply()
    }

    fun getUser(): User{
        val user = User()
        user.name = preferences.getString(NAME, "")
        user.email = preferences.getString(EMAIL, "")
        user.userId = preferences.getString(USER_ID, "")
        user.token = preferences.getString(TOKEN, "")
        return user
    }
}