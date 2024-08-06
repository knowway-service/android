package com.knowway.ui.viewmodel.mainpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knowway.data.exceptions.mainpage.MainPageApiException
import com.knowway.data.exceptions.mainpage.MainPageException
import com.knowway.data.exceptions.mainpage.MainPageNetworkException
import com.knowway.data.exceptions.mainpage.MainPageUnknownException
import com.knowway.data.model.record.RecordResponse
import com.knowway.data.repository.MainPageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class MainPageViewModel(private val dataSource: MainPageRepository) : ViewModel() {
    private val _records = MutableStateFlow<List<RecordResponse>>(emptyList())
    val recordsResponse: StateFlow<List<RecordResponse>> get() = _records.asStateFlow()

    private val _error = MutableStateFlow<MainPageException?>(null)
    val error: StateFlow<MainPageException?> get() = _error.asStateFlow()

    fun getRecordsByDeptAndFloor(deptId:Long, floorId:Long) {
        viewModelScope.launch {
            try {
                val response = dataSource.getRecordsByDeptFloor(deptId, floorId)
                if (response.isSuccessful && response.body() != null) {
                    val resp = response.body()!!
                    _records.value = resp
                } else {
                    _error.value = MainPageApiException()
                }
            } catch (e: IOException) {
                _error.value = MainPageNetworkException()
            } catch (e: Exception) {
                _error.value = MainPageUnknownException()
            }
        }
    }
}