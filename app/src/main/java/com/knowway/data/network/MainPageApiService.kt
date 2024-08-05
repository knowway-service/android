package com.knowway.data.network

import com.knowway.data.model.record.RecordResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MainPageApiService {
    @GET("/records/{deptId}/floors")
    suspend fun getRecordsByDeptFloor(
        @Path("deptId") deptId: Long,
        @Query("floor") deptFloorId: Long) : Response<List<RecordResponse>>
}