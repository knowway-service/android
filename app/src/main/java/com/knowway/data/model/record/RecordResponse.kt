package com.knowway.data.model.record

data class RecordResponse(
    val recordId: Long,
    val recordTitle: String,
    val recordLatitude: String,
    val recordLongitude: String,
    val recordPath: String,
    val floorId: Long
)