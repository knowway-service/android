package com.knowway.data.network

import com.knowway.data.model.department.DepartmentStore
import com.knowway.data.model.department.DepartmentStoreResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DepartmentStoreApiService {
    @GET("/depts?size=5&page=0")
    suspend fun getDepartmentStores(
        @Query("size") size: Int,
        @Query("page") page: Int
    ): Response<DepartmentStoreResponse>

    @GET("/depts/branch")
    suspend fun getDepartmentStoreByBranch(@Path("branch") branch: String): DepartmentStore

    @GET("/depts/loc")
    suspend fun getDepartmentStoreByLocation(
        @Query("size") size: Int,
        @Query("page") page: Int,
        @Query("latitude") latitude: String,
        @Query("longtitude") longtitude: String) : Response<DepartmentStoreResponse>
}