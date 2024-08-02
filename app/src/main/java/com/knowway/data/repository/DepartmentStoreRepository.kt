package com.knowway.data.repository

import com.knowway.data.model.department.DepartmentStore
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

    override suspend fun getDepartmentStores(
        size: Int,
        page: Int
    ): Response<DepartmentStoreResponse> {
        return apiService.getDepartmentStores(size, page)
    }

    override suspend fun getDepartmentStoreByBranch(departmentStoreBranch: String): DepartmentStore {
        return apiService.getDepartmentStoreByBranch(departmentStoreBranch)
    }

    override suspend fun getDepartmentStoreByLocation(
        size: Int,
        page: Int,
        latitude: String,
        longtitude: String
    ): Response<DepartmentStoreResponse> {
        return apiService.getDepartmentStoreByLocation(size, page, latitude, longtitude)
    }

}