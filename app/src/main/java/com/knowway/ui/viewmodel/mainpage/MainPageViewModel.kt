package com.knowway.ui.viewmodel.mainpage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knowway.data.model.record.RecordResponse
import com.knowway.data.repository.MainPageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainPageViewModel(private val dataSource: MainPageRepository) : ViewModel() {
    private val _records = MutableStateFlow<List<RecordResponse>>(emptyList())
    val recordsResponse: StateFlow<List<RecordResponse>> get() = _records

    fun getRecordsByDeptAndFloor(deptId:Long, floorId:Long) {
        viewModelScope.launch {
            try {
                val response = dataSource.getRecordsByDeptFloor(deptId, floorId)
                if (response.isSuccessful && response.body() != null) {
                    val resp = response.body()!!
                    _records.value = resp
                } else {
                    Log.d("MainPageViewModel", "Response not successful: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("MainPageViewModel", "Exception occurred", e)
            }
        }
    }
}