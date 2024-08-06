package com.knowway.data.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.knowway.Constants.url
import com.knowway.data.network.auth.ExpiredTokenInterceptor
import com.knowway.data.network.auth.SendAuthTokenInterceptor
import com.knowway.data.network.auth.TokenInterceptor
import com.knowway.util.TokenManager
import retrofit2.converter.scalars.ScalarsConverterFactory

object ApiClient {

    private lateinit var tokenManager: TokenManager
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
        tokenManager = TokenManager(appContext)
        tokenManager.clearToken()
    }

    fun getClient(): Retrofit = createRetrofit()

    private fun createRetrofit(): Retrofit {
        val okHttpClientBuilder = OkHttpClient.Builder()

        okHttpClientBuilder.addInterceptor(TokenInterceptor(tokenManager))
        okHttpClientBuilder.addInterceptor(ExpiredTokenInterceptor(appContext))

        tokenManager.getToken()?.let { token ->
            okHttpClientBuilder.addInterceptor(SendAuthTokenInterceptor(token))
        }

        val okHttpClient = okHttpClientBuilder.build()

        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
