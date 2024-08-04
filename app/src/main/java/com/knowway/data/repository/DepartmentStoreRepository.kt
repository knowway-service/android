package com.knowway.data.repository

import com.knowway.data.model.department.DepartmentStoreResponse
import com.knowway.data.network.DepartmentStoreApiService
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DepartmentStoreRepository(private val baseUrl: String) : DepartmentStoreApiService {
    private val apiService: DepartmentStoreApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(DepartmentStoreApiService::class.java)
    }

    override suspend fun getDepartmentStoreByLocation(
        latitude: String,
        longitude: String
    ): Response<List<DepartmentStoreResponse>> {
        return apiService.getDepartmentStoreByLocation(latitude, longitude)
    }

}