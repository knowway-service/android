package com.knowway.data.network

import com.knowway.data.model.department.DepartmentStoreResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DepartmentStoreApiService {
    @GET("/depts/loc")
    suspend fun getDepartmentStoreByLocation(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String) : Response<List<DepartmentStoreResponse>>

    @GET("/depts/branch")
    suspend fun getDepartmentStoreByBranch(
        @Query("departmentStoreBranch") departmentStoreBranch: String
    ): Response<List<DepartmentStoreResponse>>
}