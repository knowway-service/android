package com.knowway.data.repository

import com.knowway.BuildConfig
import com.knowway.data.model.department.DepartmentStore
import com.knowway.data.model.department.DepartmentStoreResponse
import com.knowway.data.network.DepartmentStoreApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DepartmentStoreRemoteDataSource(private val baseUrl: String) : DepartmentStoreApiService {
    private val apiService: DepartmentStoreApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(DepartmentStoreApiService::class.java)
    }

    override suspend fun getDepartmentStores(): List<DepartmentStore> {
        return apiService.getDepartmentStores()
    }

    override suspend fun getDepartmentStoreByBranch(departmentStoreBranch: String): DepartmentStore {
        return apiService.getDepartmentStoreByBranch(departmentStoreBranch)
    }

}