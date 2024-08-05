package com.knowway.data.model.user

data class UserRecord(
    val recordId: Long,
    var isExpanded: Boolean = false,
    val recordTitle : String,
    val recordUrl: String,
    val isSelectedByAdmin: Boolean,
    val departmentName: String,
    val departmentLocationName: String,
    val floor: String
)

data class UserRecordResponse(
    val userRecords: List<UserRecord>
)
