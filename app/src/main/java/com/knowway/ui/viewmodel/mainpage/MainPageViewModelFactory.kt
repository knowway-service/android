package com.knowway.ui.viewmodel.mainpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.knowway.data.repository.MainPageRepository
import java.lang.IllegalArgumentException

class MainPageViewModelFactory(private val dataSource: MainPageRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainPageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainPageViewModel(dataSource) as T
        }
        throw IllegalArgumentException("뷰모델이 불일치합니다.")
    }
}