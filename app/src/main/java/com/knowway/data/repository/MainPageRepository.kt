package com.knowway.data.repository

import com.knowway.data.model.record.RecordResponse
import com.knowway.data.network.ApiClient
import com.knowway.data.network.MainPageApiService
import retrofit2.Response

class MainPageRepository() : MainPageApiService {
    private val apiService: MainPageApiService = ApiClient.getClient().create(MainPageApiService::class.java)

    override suspend fun getRecordsByDeptFloor(
        deptId: Long,
        deptFloorId: Long
    ): Response<List<RecordResponse>> {
        return apiService.getRecordsByDeptFloor(deptId, deptFloorId)
    }

}