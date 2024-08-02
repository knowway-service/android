package com.knowway.data.network

import com.knowway.BuildConfig
import com.knowway.data.model.department.DepartmentStoreResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AdminDepartmentService {
    @GET("/depts")
    suspend fun getDepartmentStores(
        @Query("size") size: Int,
        @Query("page") page: Int
    ): Response<DepartmentStoreResponse>

    companion object {
        fun create(): AdminDepartmentService {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://${BuildConfig.BASE_IP_ADDRESS}:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(AdminDepartmentService::class.java)
        }
    }
}
