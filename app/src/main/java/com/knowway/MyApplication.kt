package com.knowway

import android.app.Application

class MyApplication : Application() {
    var currentUserId: Long = 0

    override fun onCreate() {
        super.onCreate()
        currentUserId = 1
    }
}
