package com.knowway.util

import android.content.Context
import android.content.SharedPreferences


class TokenManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "auth_token"

        private var instance: TokenManager? = null

        fun init(context: Context) {
            instance = TokenManager(context)
        }

        fun getToken(): String? {
            return instance?.getToken()
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }

    fun clearToken() {
        sharedPreferences.edit().remove(TOKEN_KEY).apply()
    }
}
