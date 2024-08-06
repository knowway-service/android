package com.knowway.data.repository

import com.knowway.data.model.department.DepartmentStoreResponse
import com.knowway.data.network.ApiClient
import com.knowway.data.network.DepartmentStoreApiService
import retrofit2.Response

class DepartmentStoreRepository() : DepartmentStoreApiService {
    private val apiService: DepartmentStoreApiService = ApiClient.getClient().create(DepartmentStoreApiService::class.java)

    override suspend fun getDepartmentStoreByLocation(
        latitude: String,
        longitude: String
    ): Response<List<DepartmentStoreResponse>> {
        return apiService.getDepartmentStoreByLocation(latitude, longitude)
    }

    override suspend fun getDepartmentStoreByBranch(
        departmentStoreBranch: String
    ): Response<List<DepartmentStoreResponse>> {
        return apiService.getDepartmentStoreByBranch(departmentStoreBranch)
    }

}