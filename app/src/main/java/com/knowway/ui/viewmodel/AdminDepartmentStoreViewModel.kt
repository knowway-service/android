package com.knowway.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.knowway.data.model.department.DepartmentStore
import com.knowway.data.network.AdminDepartmentService
import com.knowway.data.paging.DepartmentStorePagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminDepartmentStoreViewModel : ViewModel() {

    private val service = AdminDepartmentService.create()

    val departmentStores: Flow<PagingData<DepartmentStore>> = Pager(
        config = PagingConfig(
            pageSize = 5,
            enablePlaceholders = false,
            initialLoadSize = 5
        ),
        pagingSourceFactory = { DepartmentStorePagingSource(service) }
    ).flow.cachedIn(viewModelScope)

    private val _searchResults = MutableStateFlow<List<DepartmentStore>>(emptyList())
    val searchResults: StateFlow<List<DepartmentStore>> get() = _searchResults

    fun searchDepartmentStores(branch: String) {
        viewModelScope.launch {
            val response = service.getDepartmentStoreByBranch(branch)
            if (response.isSuccessful) {
                response.body()?.let { store ->
                    _searchResults.value = listOf(store)
                } ?: run {
                    _searchResults.value = emptyList()
                }
            } else {
                _searchResults.value = emptyList()
            }
        }
    }
}
