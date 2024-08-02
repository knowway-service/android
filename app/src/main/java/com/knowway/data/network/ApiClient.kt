package com.knowway.data.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.knowway.BuildConfig
import com.knowway.data.network.auth.AuthInterceptor
import com.knowway.util.TokenManager

object ApiClient {
    private var retrofitWithToken: Retrofit? = null
    private var retrofitWithoutToken: Retrofit? = null

    fun init(context: Context) {
        TokenManager.init(context)
    }

    fun getClient(): Retrofit {
        val token = TokenManager.getToken()
        return if (token != null && token.isNotEmpty()) {
            getRetrofitWithToken(token)
        } else {
            getRetrofitWithoutToken()
        }
    }

    private fun getRetrofitWithToken(token: String): Retrofit {
        if (retrofitWithToken == null) {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(token))
                .build()

            retrofitWithToken = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_IP_ADDRESS)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofitWithToken!!
    }

    private fun getRetrofitWithoutToken(): Retrofit {
        if (retrofitWithoutToken == null) {
            val okHttpClient = OkHttpClient.Builder().build()

            retrofitWithoutToken = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_IP_ADDRESS)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofitWithoutToken!!
    }
}