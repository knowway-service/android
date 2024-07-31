package com.knowway.data.network

import com.knowway.data.model.department.DepartmentStore
import com.knowway.data.model.department.DepartmentStoreResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DepartmentStoreApiService {
    @GET("/depts")
    suspend fun getDepartmentStores(): List<DepartmentStore>

    @GET("/depts/branch")
    suspend fun getDepartmentStoreByBranch(@Path("branch") branch: String): DepartmentStore
}