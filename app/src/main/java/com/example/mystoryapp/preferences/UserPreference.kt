package com.example.mystoryapp.preferences

import android.content.Context
import com.example.mystoryapp.data.User

class UserPreference(context: Context) {
    private val preferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun setUser(u: User){
        val editor = preferences.edit()
        editor.putString("name", u.name)
        editor.putString("email", u.email)
        editor.putString("userId", u.userId)
        editor.putString("token", u.token)
        editor.apply()
    }

    fun getUser(): User{
        val user = User()
        user.name = preferences.getString("name", "")
        user.email = preferences.getString("email", "")
        user.userId = preferences.getString("userId", "")
        user.token = preferences.getString("token", "")
        return user
    }
}