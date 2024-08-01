package com.knowway.data.network.auth

import com.knowway.util.TokenManager
import okhttp3.Interceptor
import okhttp3.Response


class TokenInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val authHeader = response.header("Authorization")
        authHeader?.let {
            val token = it.removePrefix("Bearer ")
            tokenManager.saveToken(token)
        }

        return response
    }
}