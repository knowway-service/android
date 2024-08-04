package com.knowway.data.model.record

data class Record(
    val memberId: Long,
    val departmentStoreFloorId: Long,
    val departmentStoreId: Long,
    val recordTitle: String,
    val recordLatitude: String,
    val recordLongitude: String,
)
