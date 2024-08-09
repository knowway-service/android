package com.knowway.util

import android.content.Context
import android.content.SharedPreferences


/**
 * TokenManager
 *
 * @author 구지웅
 * @since 2024.8.1
 * @version 1.0
 */
class TokenManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "auth_token"

        private var instance: TokenManager? = null

        fun init(context: Context) {
            instance = TokenManager(context)
        }

        fun getInstance(): TokenManager {
            return instance ?: throw IllegalStateException("TokenManager not initialized")
        }

        fun getToken(): String? {
            return getInstance().getToken()
        }

        fun saveToken(token: String) {
            getInstance().saveToken(token)
        }

        fun clearToken() {
            getInstance().clearToken()
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
