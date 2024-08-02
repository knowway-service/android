package com.knowway.ui.viewmodel.department

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knowway.data.model.department.DepartmentStore
import com.knowway.data.repository.DepartmentStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class DepartmentStoreViewModel(private val dataSource: DepartmentStoreRepository) : ViewModel() {
    private val _departmentStores = MutableStateFlow<List<DepartmentStore>>(emptyList())
    val departmentStores: StateFlow<List<DepartmentStore>> get() = _departmentStores

    fun getDepartmentStoresByLocation(size: Int, page: Int, latitude: String, longtitude: String) {
        viewModelScope.launch {
            try {
                val response = dataSource.getDepartmentStoreByLocation(size, page, latitude, longtitude)
                if (response.isSuccessful && response.body() != null) {
                    val resp = response.body()!!
                    _departmentStores.value = resp.content
                } else {
                    Log.d("DepartmentStoreViewModel", "Response not successful: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("DepartmentStoreViewModel", "Exception occurred", e)
            }
        }
    }
}