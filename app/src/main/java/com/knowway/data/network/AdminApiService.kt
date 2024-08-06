package com.knowway.data.network

import com.knowway.BuildConfig
import com.knowway.data.model.admin.AdminRecord
import com.knowway.data.model.department.DepartmentStoreFloorMapResponse
import com.knowway.data.model.department.DepartmentStorePageResponse
import com.knowway.data.model.department.DepartmentStoreResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface AdminApiService {
    @GET("/depts")
    suspend fun getDepartmentStores(
        @Query("size") size: Int,
        @Query("page") page: Int
    ): Response<DepartmentStorePageResponse>

    @GET("/depts/branch")
    suspend fun getDepartmentStoreByBranch(
        @Query("departmentStoreBranch") departmentStoreBranch: String
    ): Response<List<DepartmentStoreResponse>>

    @GET("/admin/records")
    suspend fun getRecordsByFloor(
        @Query("departmentStoreFloorId") departmentStoreFloorId: Long,
        @Query("recordArea") recordArea: Long,
        @Query("recordIsSelected") recordIsSelected: Boolean
    ): Response<List<AdminRecord>>

    @PATCH("/admin/records/{recordId}")
    suspend fun selectRecord(
        @Path("recordId") recordId: Long
    ): Response<Unit>

    @POST("/admin/points")
    suspend fun updatePoints(
        @Body requestBody: Map<String, Long>
    ): Response<Unit>
}
