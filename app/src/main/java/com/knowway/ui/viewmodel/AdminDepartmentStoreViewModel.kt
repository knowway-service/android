package com.knowway.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.knowway.data.model.department.DepartmentStoreResponse
import com.knowway.data.paging.DepartmentStorePagingSource
import com.knowway.data.repository.AdminRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminDepartmentStoreViewModel : ViewModel() {

    private val repository: AdminRepository = AdminRepository()

    val departmentStoresResponse: Flow<PagingData<DepartmentStoreResponse>> = Pager(
        config = PagingConfig(
            pageSize = 5,
            enablePlaceholders = false,
            initialLoadSize = 5
        ),
        pagingSourceFactory = { DepartmentStorePagingSource(repository) }
    ).flow.cachedIn(viewModelScope)

    private val _searchResults = MutableStateFlow<List<DepartmentStoreResponse>>(emptyList())
    val searchResults: StateFlow<List<DepartmentStoreResponse>> get() = _searchResults

    fun searchDepartmentStores(branch: String) {
        viewModelScope.launch {
            val response = repository.getDepartmentStoreByBranch(branch)
            if (response.isSuccessful) {
                response.body()?.let { stores ->
                    _searchResults.value = stores
                } ?: run {
                    _searchResults.value = emptyList()
                }
            } else {
                _searchResults.value = emptyList()
            }
        }
    }
}