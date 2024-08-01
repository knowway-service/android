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

class AdminDepartmentStoreViewModel : ViewModel() {

    private val service = AdminDepartmentService.create()

    val departmentStores: Flow<PagingData<DepartmentStore>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        pagingSourceFactory = { DepartmentStorePagingSource(service) }
    ).flow.cachedIn(viewModelScope)
}
