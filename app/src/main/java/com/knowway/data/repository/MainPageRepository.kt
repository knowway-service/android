package com.knowway.data.repository

import com.knowway.data.model.record.RecordResponse
import com.knowway.data.network.MainPageApiService
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainPageRepository(private val baseUrl: String) : MainPageApiService {
    private val apiService: MainPageApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(MainPageApiService::class.java)
    }

    override suspend fun getRecordsByDeptFloor(
        deptId: Long,
        deptFloorId: Long
    ): Response<List<RecordResponse>> {
        return apiService.getRecordsByDeptFloor(deptId, deptFloorId)
    }

}