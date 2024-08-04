package com.knowway.ui.viewmodel.department

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knowway.data.model.department.DepartmentStoreResponse
import com.knowway.data.repository.DepartmentStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class DepartmentStoreViewModel(private val dataSource: DepartmentStoreRepository) : ViewModel() {
    private val _departmentStores = MutableStateFlow<List<DepartmentStoreResponse>>(emptyList())
    val departmentStoresResponse: StateFlow<List<DepartmentStoreResponse>> get() = _departmentStores

    fun getDepartmentStoresByLocation(latitude: String, longitude: String) {
        viewModelScope.launch {
            try {
                val response = dataSource.getDepartmentStoreByLocation(latitude, longitude)
                if (response.isSuccessful && response.body() != null) {
                    val resp = response.body()!!
                    _departmentStores.value = resp
                } else {
                    Log.d("DepartmentStoreViewModel", "Response not successful: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("DepartmentStoreViewModel", "Exception occurred", e)
            }
        }
    }

    fun getDepartmentStoreByBranch(branch: String) {
        viewModelScope.launch {
            try {
                val response = dataSource.getDepartmentStoreByBranch(branch)
                if (response.isSuccessful && response.body() != null) {
                    _departmentStores.value = response.body()!!
                } else {
                    Log.d("DepartmentStoreViewModel", "Response not successful: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("DepartmentStoreViewModel", "Exception occurred", e)
            }
        }
    }
}