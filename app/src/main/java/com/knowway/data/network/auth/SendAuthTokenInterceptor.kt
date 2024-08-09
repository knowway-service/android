package com.knowway.data.network.auth

import okhttp3.Interceptor
import okhttp3.Response
/**
 * SendAuthTokenInterceptor
 *
 * @author 구지웅
 * @since 2024.8.1
 * @version 1.0
 */
class SendAuthTokenInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(newRequest)
    }
}
