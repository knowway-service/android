package com.knowway.ui.viewmodel.department

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.knowway.data.model.department.LocationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationViewModel(private val fusedLocationClient: FusedLocationProviderClient, private val context: Context): ViewModel() {
    private val _location = MutableStateFlow<LocationResponse?>(null)
    val location: StateFlow<LocationResponse?> get() = _location.asStateFlow()

    fun getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val locationResponse = LocationResponse(location.latitude.toString(), location.longitude.toString())
                        _location.value = locationResponse
                    } else {
                        // 위치를 가져오지 못한 경우
                    }
                }
                .addOnFailureListener {

                }
        }
    }
}