package com.knowway.data.network

import com.knowway.BuildConfig
import com.knowway.data.model.department.DepartmentStoreResponse
import com.knowway.data.model.department.DepartmentStoreFloorMapResponse
import com.knowway.data.model.department.DepartmentStorePageResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("/depts/{deptId}/floors")
    suspend fun getDepartmentStoreFloorMap(
        @Path("deptId") deptId: Long,
        @Query("floor") floor: String
    ): Response<DepartmentStoreFloorMapResponse>


    companion object {
        fun create(): AdminApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://${BuildConfig.BASE_IP_ADDRESS}:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(AdminApiService::class.java)
        }
    }
}
