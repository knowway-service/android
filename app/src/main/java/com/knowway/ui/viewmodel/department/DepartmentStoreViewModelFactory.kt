package com.knowway.ui.viewmodel.department

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.knowway.data.repository.DepartmentStoreRepository
import java.lang.IllegalArgumentException

class DepartmentStoreViewModelFactory(private val dataSource: DepartmentStoreRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DepartmentStoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DepartmentStoreViewModel(dataSource) as T
        }
        throw IllegalArgumentException("뷰모델이 불일치합니다.")
    }
}