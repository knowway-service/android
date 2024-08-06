package com.knowway.data.network.auth

import android.app.AuthenticationRequiredException
import android.content.Context
import android.content.Intent
import com.knowway.ui.activity.user.LoginActivity
import com.knowway.util.TokenManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ExpiredTokenInterceptor(private val context: Context) : Interceptor {

    @Throws(AuthenticationRequiredException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        if (response.code == 401) {
            val errorBody = response.body?.string()
            if (errorBody?.contains("Expired") == true) {
                TokenManager.clearToken()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                response.close()
            }
        }

        return response
    }
}
