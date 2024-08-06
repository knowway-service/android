package com.knowway.ui.viewmodel.department

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knowway.data.exceptions.department.*
import com.knowway.data.model.department.DepartmentStoreResponse
import com.knowway.data.repository.DepartmentStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class DepartmentStoreViewModel(private val dataSource: DepartmentStoreRepository) : ViewModel() {
    private val _departmentStores = MutableStateFlow<List<DepartmentStoreResponse>>(emptyList())
    val departmentStoresResponse: StateFlow<List<DepartmentStoreResponse>> get() = _departmentStores.asStateFlow()

    private val _error = MutableStateFlow<DepartmentStoreException?>(null)
    val error: StateFlow<DepartmentStoreException?> get() = _error.asStateFlow()

    fun getDepartmentStoresByLocation(latitude: String, longitude: String) {
        viewModelScope.launch {
            try {
                val response = dataSource.getDepartmentStoreByLocation(latitude, longitude)
                if (response.isSuccessful && response.body() != null) {
                    val resp = response.body()!!
                    _departmentStores.value = resp
                } else {
                    _error.value = DeptByLocationApiException()
                }
            } catch (e: IOException) {
                _error.value = DeptNetworkException()
            } catch (e: Exception) {
                _error.value = DeptUnknownException()
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
                    _error.value = DeptByBranchApiException()
                }
            } catch (e: IOException) {
                _error.value = DeptNetworkException()
            } catch (e: Exception) {
                _error.value = DeptUnknownException()
            }
        }
    }
}