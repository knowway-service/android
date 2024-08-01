package com.knowway.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.knowway.BuildConfig
import com.knowway.data.network.auth.AuthInterceptor

object ApiClient {
    private var retrofitWithToken: Retrofit? = null
    private var retrofitWithoutToken: Retrofit? = null

    fun getClient(withToken: Boolean = false, token: String? = null): Retrofit {
        return if (withToken) {
            getRetrofitWithToken(token)
        } else {
            getRetrofitWithoutToken()
        }
    }

    private fun getRetrofitWithToken(token: String?): Retrofit {
        if (retrofitWithToken == null || token == null) {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(token ?: ""))
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
