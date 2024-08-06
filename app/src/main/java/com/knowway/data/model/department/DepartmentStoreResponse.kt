package com.knowway.data.model.department

data class DepartmentStoreResponse(
    val departmentStoreId: Long,
    val departmentStoreName: String,
    val departmentStoreBranch: String,
    val departmentStoreLatitude: Double,
    val departmentStoreLongitude: Double,
    val departmentStoreFloorResponseList: List<DepartmentStoreFloorResponse>
)