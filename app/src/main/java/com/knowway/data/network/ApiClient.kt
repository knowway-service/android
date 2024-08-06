package com.knowway.data.network

import android.content.Context
import com.knowway.Constants.url
import com.knowway.data.network.auth.AuthInterceptor
import com.knowway.data.network.auth.TokenInterceptor
import com.knowway.util.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private lateinit var tokenManager: TokenManager

    fun init(context: Context) {
        tokenManager = com.knowway.util.TokenManager(context)
        tokenManager.clearToken()

    }

    fun getClient(): Retrofit = createRetrofit()

    private fun createRetrofit(): Retrofit {
        val okHttpClientBuilder = OkHttpClient.Builder()

        okHttpClientBuilder.addInterceptor(TokenInterceptor(tokenManager))

        tokenManager.getToken()?.let { token ->
            okHttpClientBuilder.addInterceptor(AuthInterceptor(token))
        }

        val okHttpClient = okHttpClientBuilder.build()

        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}