package com.knowway.data.model.user

data class UserRecord(
    val recordId: String,
    val recordUrl: String,
    val isSelectedByAdmin: Boolean,
    val departmentName: String,
    val departmentLocationName: String,
    val floor: String
)

data class UserRecordResponse(
    val userRecords: List<UserRecord>
)
