package com.knowway.ui.viewmodel.department

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knowway.data.model.department.DepartmentStore
import com.knowway.data.repository.DepartmentStoreRemoteDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class DepartmentStoreViewModel(private val dataSource: DepartmentStoreRemoteDataSource) : ViewModel() {
    private val _departmentStores = MutableStateFlow<List<DepartmentStore>>(emptyList())
    val departmentStores: StateFlow<List<DepartmentStore>> get() = _departmentStores

    fun getDepartmentStores() {
        viewModelScope.launch {
            try {
                val stores = dataSource.getDepartmentStores()
                _departmentStores.value = stores
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}