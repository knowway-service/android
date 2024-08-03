package com.knowway.ui.viewmodel.department

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import java.lang.IllegalArgumentException

class LocationViewModelFactory(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationViewModel(fusedLocationClient, context) as T
        }
        throw IllegalArgumentException("뷰모델이 불일치합니다.")
    }
}