package com.knowway.data.repository

import com.knowway.data.model.admin.AdminRecord
import com.knowway.data.model.department.DepartmentStorePageResponse
import com.knowway.data.model.department.DepartmentStoreResponse
import com.knowway.data.network.AdminApiService
import com.knowway.data.network.ApiClient
import retrofit2.Response

class AdminRepository {
    private val api: AdminApiService = ApiClient.getClient().create(AdminApiService::class.java)

    suspend fun getDepartmentStores(size: Int, page: Int): Response<DepartmentStorePageResponse> {
        return api.getDepartmentStores(size, page)
    }

    suspend fun getDepartmentStoreByBranch(departmentStoreBranch: String): Response<List<DepartmentStoreResponse>> {
        return api.getDepartmentStoreByBranch(departmentStoreBranch)
    }

    suspend fun getRecordsByFloor(departmentStoreFloorId: Long, recordArea: Long, recordIsSelected: Boolean): Response<List<AdminRecord>> {
        return api.getRecordsByFloor(departmentStoreFloorId, recordArea, recordIsSelected)
    }

    suspend fun selectRecord(recordId: Long): Response<Unit> {
        return api.selectRecord(recordId)
    }

    suspend fun updatePoints(requestBody: Map<String, Long>): Response<Unit> {
        return api.updatePoints(requestBody)
    }
}
