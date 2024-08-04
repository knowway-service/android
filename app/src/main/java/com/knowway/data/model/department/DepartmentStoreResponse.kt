package com.knowway.data.model.department

data class DepartmentStoreResponse(
    val departmentStoreId: Long,
    val departmentStoreName: String,
    val departmentStoreBranch: String,
    val departmentStoreFloorResponseList: List<DepartmentStoreFloorResponse>
)