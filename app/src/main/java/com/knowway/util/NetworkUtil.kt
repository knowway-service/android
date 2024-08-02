package com.knowway.util

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class NetworkUtil {
    companion object {
        private val client = OkHttpClient()

        suspend fun isServerReachable(urlString: String): Boolean {
            val request = Request.Builder().url(urlString).build()

            return try {
                client.newCall(request).execute().use { response: Response ->
                    response.isSuccessful
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("NetworkUtil", "IOException occurred: ${e.message}")
                false
            }
        }
    }
}